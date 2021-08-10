package com.dgteam.servimovildg.usuario;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.dgteam.servimovildg.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;

public class Maps_service extends Fragment {

    //Variables asignadas para la geolocalización
    private GoogleMap mMap;

    private Marker marcador; // -> Nos permite generar los marcadores en el mapa.
    private Marker marcador2; // -> Nos permite generar los marcadores en el mapa.
    double lat = 0.0;// -> variable de latitud de la ubicacion actual.
    double lng = 0.0;// -> variable de longitud de la ubicacion actual.
    int REQUEST_CODE = 200;
    View vista;
    private String estado;
    private FirebaseFirestore db;
    private DatabaseReference data;
    Location locat;
    private String register;
    private String type;
    private ArrayList<Marker> almacenar = new ArrayList<>();
    private ArrayList<Marker> actual = new ArrayList<>();
    private EditText cel,ref,det;
    private String nombre,apellido,name,last;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            miUbicacion();
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) { return; }
            //mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        vista = inflater.inflate(R.layout.fragment_maps_search, container, false);
        db = FirebaseFirestore.getInstance();
        insercion(lat,lng);
        verificar_cuestionario();
        return vista;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    //Metodo para agregar marcadores en el mapa segun la longitud y latitud asignada.
    private void agregarMarcador(double lat, double lng, String nomape) {
        LatLng coordenadas = new LatLng(lat, lng); // coordenadas de la ubicacion.
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 16); // centrar la camara del celular en la posición del marcador.
        if (marcador != null) marcador.remove(); // eliminamos el marcador si cumple la condicion.
        marcador = mMap.addMarker(new MarkerOptions()
                .position(coordenadas) //posición del marcador
                .title("Usuario")// agregamos titulo al marcador
                .snippet(nomape)
                //.icon(bitmapDescriptorFromVector(getActivity(),R.drawable.ic_baseline_person_pin_circle_24))
        );
        mMap.animateCamera(miUbicacion); // mover camara del celular de una posicion a otra.
    }

    //Metodo para renderizar los iconos de googlemaps.
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    //Metodo que nos permitira obtener la latitud y longitud actual del celular.
    private void actualizarUbicacion(Location location) {
        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                nombre = documentSnapshot.getString("nombre");
                apellido = documentSnapshot.getString("apellido");
                String nomape = nombre+" "+apellido;
                if (location != null) {
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                    agregarMarcador(lat, lng,nomape);
                    actualizar(lat, lng);
                    marcadores();
                }
            }
        });
    }

    LocationListener locListener = new LocationListener() {
        //onLocationChanged nos permitira recibir la actualizacion de cambio de localidad dado por el GPS y refrescar la posición actual.
        @Override
        public void onLocationChanged(Location location) {
            actualizarUbicacion(location);
        }
    };

    //Metodo para obtener servicios del dispositivo.
    private void miUbicacion() {

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE); //Pregunta al usuario sobre el acceso al permiso.
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); //Ultima posición conocida transmitida por el GPS del celular.
        actualizarUbicacion(location); // LLamamos el metodo actualizarubicacion.
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 0, locListener); //Actualizaciones de posicion entregadas por el GPS por cada 5 segundos.
        //Log.d("id", "Ubicacion es : " + variables );
    }

    //Metodo para insertar la latitud y longitud del logueado.
    public void insercion(double lat, double lng){
        //Obtenemos el usuario logueado
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference df = db.collection("locations").document(user.getUid());
        DocumentReference us = db.collection("users").document(user.getUid());
        us.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                type = documentSnapshot.getString("userType");
                name = documentSnapshot.getString("nombre");
                last = documentSnapshot.getString("apellido");

                Map<String, Object> loc = new HashMap<>();
                loc.put("userUid", user.getUid());
                loc.put("userType",type);
                loc.put("latitud", lat);
                loc.put("longitud", lng);
                loc.put("nombre",name+" "+last);
                df.set(loc);
            }
        });
    }

    //Metodo para actualizar la localización del usuario logueado.
    public void actualizar(double lat, double lng){
        //Obtenemos el usuario logueado
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference df = db.collection("locations").document(user.getUid());
        Map data = new HashMap<>();
        data.put("userUid", user.getUid());
        data.put("latitud", lat);
        data.put("longitud", lng);
        df.update(data);
    }

    //Funcion para agregar los marcadores en la base de datos y en el maps.
    public void marcadores(){
        CollectionReference data = db.collection("locations");

        Query query = data.whereEqualTo("userType","mecanico");

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for(Marker marker:actual){
                        marker.remove();
                    }
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        Log.d("TAG", doc.getId() + " => " + doc.getData());
                        double lati = doc.getDouble("latitud");
                        double longi = doc.getDouble("longitud");
                        String nombre = doc.getString("nombre");
                        LatLng coordenadas = new LatLng(lati,longi);
                        marcador2 = mMap.addMarker(new MarkerOptions()
                                .position(coordenadas)
                                .title("Mecanico")
                                .snippet(nombre)
                                /*.icon(bitmapDescriptorFromVector(getActivity(),R.drawable.ic_baseline_engineering_24))*/);
                        almacenar.add(marcador2);
                    }
                    actual.clear();
                    actual.addAll(almacenar);
                }else{
                    Log.d("TAG", " Error " , task.getException());
                }
            }
        });

    }

    public void request_automotor(){ startActivity(new Intent(getActivity(), request_form.class)); }

    public void Mensaje(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialogo)
                .setTitle(R.string.welcome)
                .setIcon(R.drawable.ic_undraw_mobile_application_mr4r)
                .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request_automotor();
                        visto();
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(),"Continua con la aplicación",Toast.LENGTH_SHORT).show();
            }
        }).setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void Mensaje2(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.pregunta)
                .setIcon(R.drawable.ic_undraw_cloud_sync_re_02p1)
                .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        validar_registro();
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(),"Continua con la aplicación",Toast.LENGTH_SHORT).show();
            }
        }).setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //verificar a donde redireccionar al usuario logueado.
    public void verificar_cuestionario(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference us = db.collection("MensajeP").document(user.getUid());
        us.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                estado = documentSnapshot.getString("estado");
                if(estado == null) {
                    Mensaje();
                }else {
                    if (estado.equals("visto")) {
                        Mensaje2();
                    } else {
                        Mensaje();
                    }
                }
            }
        });
    }

    //validar que haya visualizado el mensaje primario.
    public void visto(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference df = db.collection("MensajeP").document(user.getUid());
        Map<String, Object> loc = new HashMap<>();
        loc.put("userUid", user.getUid());
        loc.put("estado","visto");
        df.set(loc);
    }

    //Insercion cuando haya visualizado el mensaje primario.
    public void validar_registro(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("solicitudes")
                .whereEqualTo("userUid",user.getUid())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> snapshots = queryDocumentSnapshots.getDocuments();
                for(DocumentSnapshot snapshot: snapshots){
                    register = snapshot.getString("estado");
                }

                //Validación de solicitud pues cada usuario puede hacer una solicitud siempre y cuando
                //No tenga una solicitud pendiente.
                if(register==null ){
                    request_automotor();
                }else if(register.equals("espera")){
                    verificar();
                }else if(register.equals("activo")){
                    request_automotor();
                }else if(register.equals("terminado")){
                    request_automotor();
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d("TAG","ALGO FALLO");
            }
        });

    }

    //Solicitud para evitar que ingrese otra solicitud diferente.
    public void verificar(){
            //Agregamos el listview en el alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(true);
            builder.setIcon(R.drawable.ic_baseline_cancel_24);
            builder.setMessage("Tu tienes una solicitud pendiente\npor favor espera a que se elimine");
            builder.setTitle(R.string.cancelar);
            final AlertDialog dialog = builder.create();
            dialog.show();
    }
}
