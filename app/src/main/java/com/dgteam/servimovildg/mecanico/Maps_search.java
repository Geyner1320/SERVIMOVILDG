package com.dgteam.servimovildg.mecanico;

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
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.Map;

public class Maps_search extends Fragment {

    //Variables asignadas para la geolocalización
    private GoogleMap mMap;
    private Marker marcador; // -> Nos permite generar los marcadores en el mapa.
    private Marker marcador2; // -> Nos permite generar los marcadores en el mapa.
    double lat = 0.0;// -> variable de latitud de la ubicacion actual.
    double lng = 0.0;// -> variable de longitud de la ubicacion actual.
    private FirebaseFirestore db;
    private String type;
    View vista;
    private String estado;
    private String nombre;
    private String id ;
    private ListView list;
    private String nom,apellido,name,last;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ArrayAdapter<String> adapter;;
    private ArrayList<Marker> almacenar = new ArrayList<>();
    private ArrayList<Marker> actual = new ArrayList<>();

    //Función encargada de replicar las acciones en el maps.
    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            miUbicacion();
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment_maps_search, container, false);
        db = FirebaseFirestore.getInstance();
        list = new ListView(getActivity());
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_expandable_list_item_1,new ArrayList<String>());
        list.setAdapter(adapter);
        insercion(lat,lng);
        return vista;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        verificar_cuestionario();
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

    }

    //Metodo para agregar marcadores en el mapa segun la longitud y latitud asignada.
    private void agregarMarcador(double lat, double lng,String nomape) {
        LatLng coordenadas = new LatLng(lat, lng); // coordenadas de la ubicacion.
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 16); // centrar la camara del celular en la posición del marcador.
        if (marcador != null) marcador.remove(); // eliminamos el marcador si cumple la condicion.
        marcador = mMap.addMarker(new MarkerOptions()
                .position(coordenadas) //posición del marcador
                .title("Mecanico")// agregamos titulo al marcador
                .snippet(nomape)
                //.icon(bitmapDescriptorFromVector(getActivity(),R.drawable.ic_baseline_engineering_24))
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
        db
                .collection("users")
                .document(user.getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                nombre = documentSnapshot.getString("nombre");
                apellido = documentSnapshot.getString("apellido");
                String nomape = nombre+" "+apellido;

                if (location != null) {
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                    agregarMarcador(lat, lng,nomape);
                    actualizar(lat,lng);
                    marcadores();
                }
            }
        });
    }

    LocationListener locListener = new LocationListener() {
        //onLocationChanged nos permitira recibir la actualizacion de cambio de localidad dado por el GPS y refrescar la posición actual.
        @Override
        public void onLocationChanged( Location location) {
            actualizarUbicacion(location);
        }
    };

    //Metodo para obtener servicios del dispositivo.
    private void miUbicacion() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); //Ultima posición conocida transmitida por el GPS del celular.
        actualizarUbicacion(location);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,15000,0,locListener); //Actualizaciones de posicion entregadas por el GPS por cada 15 segundos.
    }

    //metodo para insertar los valores de latitud y longitud de la persona logueada.
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

    //metodo para actualizar el usuario logueado.
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


    //Metodo que permite agregar marcadores en la base de datos y en el maps.
    public void marcadores(){
        CollectionReference data = db.collection("locations");

        Query query = data.whereEqualTo("userType","usuario");

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for(Marker marker:actual){
                        marker.remove();
                    }
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        Log.d("TAG", doc.getId() + " => " + doc.getData());
                        double lati = doc.getDouble("latitud");
                        double longi = doc.getDouble("longitud");
                        String uid = doc.getString("userUid");
                        String nombre = doc.getString("nombre");
                        Log.d("TAG","VALOR:"+uid);
                        LatLng coordenadas = new LatLng(lati, longi);
                        marcador2 = mMap.addMarker(new MarkerOptions()
                                .position(coordenadas)
                                .title("Usuario")
                                .snippet(nombre)
                                /*.icon(bitmapDescriptorFromVector(getActivity(),R.drawable.ic_baseline_person_pin_circle_24))*/);
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

    //Mensaje primario y solo aparece una vez.
    public void Mensaje(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialogo2)
                .setTitle(R.string.welcome)
                .setIcon(R.drawable.ic_undraw_mobile_inbox_re_ciwq)
                .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestlist();
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

    public void requestlist(){ startActivity(new Intent(getActivity(), mechanic_list.class)); }

    //Mensaje secundario y constante la aplicación.
    public void Mensaje2(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.preguntaM)
                .setIcon(R.drawable.ic_undraw_cloud_sync_re_02p1)
                .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestlist();
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

    //Permite verificar si ya se visualizo el mensaje primario de la aplicación.
    public void visto(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference df = db.collection("MensajeP").document(user.getUid());
        Map<String, Object> loc = new HashMap<>();
        loc.put("userUid", user.getUid());
        loc.put("estado","visto");
        df.set(loc);
    }

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
}