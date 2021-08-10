package com.dgteam.servimovildg.registro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dgteam.servimovildg.R;
import com.dgteam.servimovildg.logueo.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistroTallerActivity extends AppCompatActivity {
    //Declarar los campos de texto
    private EditText etNombreTaller, etNit, etEspecialidad, etDireccion;
    private Button btnSave;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Instancias
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_taller);
        getSupportActionBar().hide();

        //Declaración de variables para traer datos del formulario.
        etNombreTaller = (EditText) findViewById(R.id.etNameTaller);
        etNit= (EditText) findViewById(R.id.etNit);
        etEspecialidad = (EditText) findViewById(R.id.etEspecialidad);
        etDireccion = (EditText) findViewById(R.id.etUbicacion);
        btnSave = (Button) findViewById(R.id.btnNext);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyData(mAuth);
            }
        });
    }

    // La funcion verifica los datos ingresados para poder guardarlos o pasarlos
    // para la siguiente vista según el tipo de usuario
    // DMR
    public void verifyData(FirebaseAuth mAuth) {
        String nombreTaller = etNombreTaller.getText().toString();
        String nit = etNit.getText().toString();
        String especialidad = etEspecialidad.getText().toString();
        String direccion = etDireccion.getText().toString();

        // Confirmar que los campos no estén vacios
        if (nombreTaller.isEmpty() || nit.isEmpty() || especialidad.isEmpty() || direccion.isEmpty()) {
            Toast notification = Toast.makeText(this,
                    "Los campos no pueden quedar vacíos",
                    Toast.LENGTH_LONG);
            notification.show();
        } else {
                // Me traigo las variables que se han pasado por la el intent
                String nombre = getIntent().getStringExtra("nombre");
                String apellido = getIntent().getStringExtra("apellido");
                String identificacion = getIntent().getStringExtra("identificacion");
                String correo = getIntent().getStringExtra("correo");
                String contrasena = getIntent().getStringExtra("contrasena");

                //Si es usuario, guardará la información en la base de datos

                    mAuth.createUserWithEmailAndPassword(correo, contrasena)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("TAG", "createUserWithEmail:success");
                                        DocumentReference df = db.collection("users").document(user.getUid());
                                        Map<String, Object> userInfo = new HashMap<>();
                                        userInfo.put("userUid", user.getUid());
                                        userInfo.put("nombre", nombre);
                                        userInfo.put("email", correo);
                                        userInfo.put("apellido", apellido);
                                        userInfo.put("identificacion", identificacion);
                                        userInfo.put("nombreTaller", nombreTaller);
                                        userInfo.put("nit", nit);
                                        userInfo.put("especialidad", especialidad);
                                        userInfo.put("direccion", direccion);
                                        userInfo.put("userType", "mecanico");
                                        df.set(userInfo);
                                        Log.d("TAG", "createUserWithEmail:SUCCESS");
                                        goToLogin();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.d("TAG", "createUserWithEmail:failure");
                                        showToast();
                                    }
                                }
                            });
                    //saveUserInfo(nombre, apellido,identificacion,correo,contrasena);
                }
            }


    // La funcion redirije al loin luego de ingresar la informacion.
    public void goToLogin() {
        Intent goLogin = new Intent(
                RegistroTallerActivity.this,
                MainActivity.class);
        startActivity(goLogin);
    }

    public void showToast(){
        Toast pass = Toast.makeText(this,
                "No se ha podido crear el usuario",
                Toast.LENGTH_LONG);
        pass.show();
    }
}
