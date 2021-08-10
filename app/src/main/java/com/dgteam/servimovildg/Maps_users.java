package com.dgteam.servimovildg;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.dgteam.servimovildg.mecanico.details;
import com.dgteam.servimovildg.mecanico.master_page_mechanic;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.dgteam.servimovildg.databinding.ActivityMapsUsersBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Maps_users extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsUsersBinding binding;
    private String doc, tel;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    ;
    private FirebaseUser mech = FirebaseAuth.getInstance().getCurrentUser();
    private Marker marcador;
    private ImageButton check, call;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        informacion();
        check = findViewById(R.id.checkfinish);
        call = findViewById(R.id.calluser);

        tel = getIntent().getStringExtra("cel");
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mensaje();
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel));
                startActivity(i);
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) { return; }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        marcadores();
    }

    public void marcadores(){
        doc = getIntent().getStringExtra("UID");
        Log.d("TAG",doc);
                db.
                collection("locations")
                .document(doc)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        double lat = documentSnapshot.getDouble("latitud");
                        double lon = documentSnapshot.getDouble("longitud");
                        String nombre = documentSnapshot.getString("nombre");
                        LatLng coordenadas = new LatLng(lat, lon); // coordenadas de la ubicacion.
                        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 16);
                        if(marcador != null) marcador.remove();
                        marcador = mMap.addMarker(new MarkerOptions()
                        .position(coordenadas)
                        .title("Ubicación del usuario")
                        .snippet(nombre));
                        mMap.animateCamera(miUbicacion);
                    }
                });

    }


    public void  mensaje(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Maps_users.this);
        builder.setTitle("¿Llegaste al destino?");
        builder.setCancelable(false);
        builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                exito();
            }
        }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Maps_users.this);
                builder.setCancelable(true);
                builder.setIcon(R.drawable.ic_baseline_run_circle_24);
                builder.setMessage("Vamos continua con tu camino!!");
                builder.setTitle(R.string.sucess);
                final AlertDialog dialo = builder.create();
                dialo.show();
            }
        });
        final AlertDialog dialo = builder.create();
        dialo.show();

    }

    public void exito(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Maps_users.this);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_baseline_add_location_alt_24);
        builder.setMessage("Nos alegra mucho continua con el registro de tu servicio.");
        builder.setTitle(R.string.suces);
        builder.setPositiveButton("CONTINUAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Maps_users.this, master_page_mechanic.class));
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void informacion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Maps_users.this);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_baseline_add_location_alt_24);
        builder.setMessage("Acontinuación encontraras el marcador del usuario correspondiente\n" +
                "para trazar la ruta debes dar click encima del marcador y en la parte inferior\n" +
                "te ilustrara un boton de googlemaps el cual daras click para continuar\n" +
                "luego das un  click en iniciar y listo sigues tu camino; BUENA SUERTE!!!");
        builder.setTitle(R.string.sucesss);
        builder.setPositiveButton("CONTINUAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                builder.setCancelable(true);
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }
}