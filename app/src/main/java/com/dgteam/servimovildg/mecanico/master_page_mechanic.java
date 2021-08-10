package com.dgteam.servimovildg.mecanico;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

import com.dgteam.servimovildg.R;
import com.dgteam.servimovildg.logueo.MainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class master_page_mechanic extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private TextView nombre;
    View hview;
    private String uid,nom,apellido;
    private Button logout;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Instanciamos el firebaseAuth.
        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_master_page_mechanic);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.mechanicListFragment, R.id.maps_search, R.id.acercademechanic, R.id.cashing_mechanic,R.id.service_finish,R.id.perfil_mechanic)
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

        //Obtenemos el header de navigation drawer.
        hview = navigationView.getHeaderView(0);


        //Instancia de caja de texto.
        nombre = (TextView)hview.findViewById(R.id.Nombre_m);


        //Instancia de boton de cerrar sesion
        logout = (Button)hview.findViewById(R.id.btnsalir);

        //Funcion Para cerrar sesion.
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
                startActivity(new Intent(master_page_mechanic.this,MainActivity.class));
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