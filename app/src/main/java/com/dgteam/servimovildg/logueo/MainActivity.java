package com.dgteam.servimovildg.logueo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.dgteam.servimovildg.R;
import com.dgteam.servimovildg.mecanico.details;
import com.dgteam.servimovildg.mecanico.master_page_mechanic;
import com.dgteam.servimovildg.registro.SelectUserType;
import com.dgteam.servimovildg.usuario.master_page_user;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //Declaración de variables de la interfaz
    private EditText usu,con;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    int REQUEST_CODE = 200;


    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Condición para pedir el permiso de gps y llamadas en el smarphone.
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE); //Pregunta al usuario sobre el acceso al permiso.
        }

        //Desplegar la interfaz de logueo.
        setContentView(R.layout.activity_main);

        //Eliminar la barra superior de la aplicación.
        getSupportActionBar().hide();

        //Instancias de la base de datos firebase.
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        //Variables para llamar cajas de la interfaz
        usu = (EditText)findViewById(R.id.Correo);
        con = (EditText)findViewById(R.id.Contraseña);

    }

    // La funcion redirige a la siguiente activity donde el usuario debe ingresar los demas datos
    // del regitro
    // DMR
    public void goToRegistro(View V) {
        Intent irAregistro2 = new Intent(
                MainActivity.this,  SelectUserType.class);
        startActivity(irAregistro2);
    }


    //La función se encarga de verificar que las cajas de texto no esten vacias.
    public boolean verificar() {
        String correo = usu.getText().toString();
        String contraseña = con.getText().toString();
        boolean verif=true;
        if(correo.length()==0){
            Toast notification = Toast.makeText(this,
                    "El Correo no puede quedar vacio",
                    Toast.LENGTH_SHORT);
            notification.show();
            verif =false;

        }else if(contraseña.length()==0){
            Toast notification = Toast.makeText(this,
                    "La contraseña no puede quedar vacia",
                    Toast.LENGTH_SHORT);
            notification.show();
            verif =false;
        }
        return verif;
    }

    // Metodo del loguin en donde se hace la comparacion en la base de datos.
    public void login (View view){
        if(verificar()==true) {
            mAuth.signInWithEmailAndPassword(usu.getText().toString(), con.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    String UID = authResult.getUser().getUid();
                    Typeuser(UID);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Credenciales no validas", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    //Función que nos permite determinar con que usuario se esta logueando (usuario o mecanico).
    public void Typeuser(String uid){
        Log.d("id", "ESTE ES EL UID: " + uid);
        DocumentReference df = db.collection("users").document(uid);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String tipo = documentSnapshot.getString("userType");

                //Traer token cuando recien se esta logueando
                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (!task.isSuccessful()) {
                                    Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                                    return;
                                }

                                // Get new FCM registration token
                                String token = task.getResult();

                                onNewToken(token, uid);
                            }
                        });

                if(tipo.equals("usuario")){
                    Intent user = new Intent(
                            MainActivity.this,
                            master_page_user.class);
                    startActivity(user);

                }
                if(tipo.equals("mecanico")){
                    Intent user = new Intent(
                            MainActivity.this,
                            master_page_mechanic.class);
                    startActivity(user);
                }
            }
        });
    }

    //Actualizar el token del usuario cada vez que inice sesion
    public void onNewToken(String token, String uid)
    {
        Log.d("TokenDelUsuario", token);
        Log.d("IdUsuarioLogueado", uid);

        DocumentReference df = db.collection("users").document(uid);
        Map data = new HashMap<>();
        data.put("Token", token);
        df.update(data);
    }
}

