package com.dgteam.servimovildg.usuario;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dgteam.servimovildg.R;
import com.dgteam.servimovildg.logueo.MainActivity;
import com.dgteam.servimovildg.mecanico.master_page_mechanic;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class master_page_user extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private TextView nombre;
    View hview;
    MenuItem item;
    private Button logout;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String uid,apellido,nom;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_master_page_user);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.user_list, R.id.maps_service, R.id.acercadeuser,R.id.pays,R.id.my_perfil)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //Instanciamos el firestore.
        db = FirebaseFirestore.getInstance();

        //Obtenemos el usuario logueado
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null) {

            //Obtenemos el Uid del usuario logueado
            uid = user.getUid();


            //Instanciamos la colección del firebase.
            DocumentReference df = db.collection("users").document(uid);

            //Siendo la instancia de forma exitosa, obtenemos los valores respectivos del usuario logueado.
            df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    //Asignamos esos valores en las variables por medio del documentSnapshot.
                    nom = documentSnapshot.getString("nombre");
                    apellido = documentSnapshot.getString("apellido");

                    //Set de variables de la interfaz
                    nombre.setText(nom.toUpperCase()+" "+apellido.toUpperCase());

                }
            });

        }


        //Obtenemos el header del navigaton drawer.
        hview = navigationView.getHeaderView(0);

        logout = (Button)hview.findViewById(R.id.btnsalir_user);


        //Variables asignadas para la interfaz.
        nombre = (TextView)hview.findViewById(R.id.Nombre);


        //Onclick del boton cerrar sesión
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(master_page_user.this,MainActivity.class));
            }
        });



    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}