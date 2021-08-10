package com.dgteam.servimovildg.registro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dgteam.servimovildg.R;
import com.dgteam.servimovildg.logueo.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.core.FirestoreClient;

import java.util.HashMap;
import java.util.Map;

public class RegistroCorreoPassActivity extends AppCompatActivity {

    //Declarar los campos de texto
    private EditText etCorreo, etContrasena, etConfirmarContrasena;
    private Button btnNextOrFinish;
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Configuracion de firebase

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_correo_pass);
        getSupportActionBar().hide();

        //VARIABLES DE DEBUG, PARA SABER COMO LLEGAN A LA VISTA
        /*Log.d("name", "onName: " + getIntent().getStringExtra("nombre"));
        Log.d("lastName", "onLastName: " + getIntent().getStringExtra("apellidos"));*/
        Log.d("id", "onUserType: " + getIntent().getStringExtra("userType"));

        //Declaración de variables para traer datos del formulario.

        etCorreo = (EditText) findViewById(R.id.etCorreo);
        etContrasena = (EditText) findViewById(R.id.etContrasena);
        etConfirmarContrasena = (EditText) findViewById(R.id.etConfirmarContrasena);
        btnNextOrFinish = (Button) findViewById(R.id.btnNext);

        // Si el parametro de llegada es "Usuario" setea el boton, con el texto
        // finalizar por que del usuario no se necesita mas informacion,
        if (getIntent().getStringExtra("userType").equals("User")) {
            btnNextOrFinish.setText("Finalizar");
            btnNextOrFinish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    verifyData(mAuth);
                }
            });
            //Si no es usuario es mecanico, entonces el mecanico debera ingresar más informacion
        } else {
            btnNextOrFinish.setText("Siguiente");
            btnNextOrFinish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    verifyData(mAuth);
                }
            });
        }
    }

    // La funcion verifica los datos ingresados para poder guardarlos o pasarlos
    // para la siguiente vista según el tipo de usuario
    // DMR
    public void verifyData(FirebaseAuth mAuth) {
        String correo = etCorreo.getText().toString();
        Log.d("id", "onCorreo:" + correo);
        String contrasena = etContrasena.getText().toString();
        String confirmarContrasena = etConfirmarContrasena.getText().toString();
        // Confirmar que los campos no estén vacios
        if (correo.isEmpty() || contrasena.isEmpty() || confirmarContrasena.isEmpty()) {
            Toast notification = Toast.makeText(this,
                    "Los campos no pueden quedar vacíos",
                    Toast.LENGTH_LONG);
            notification.show();
        } else {
            //Confirmar que la contraseña tenga más de 6 carcteres
            if (contrasena.length() < 6) {
                Toast pass = Toast.makeText(this,
                        "La contraseña debe tener más de 6 caracteres",
                        Toast.LENGTH_LONG);
                pass.show();
            } else if (contrasena.equals(confirmarContrasena)) {
                // Me traigo las variables que se han pasado por la el intent
                String nombre = getIntent().getStringExtra("nombre");
                String apellido = getIntent().getStringExtra("apellidos");
                String identificacion = getIntent().getStringExtra("identificacion");

                //Si es usuario, guardará la información en la base de datos
                if (getIntent().getStringExtra("userType").equals("User")) {
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
                                        userInfo.put("userType", "usuario");
                                        df.set(userInfo);
                                        goToLogin();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.d("TAG", "createUserWithEmail:failure");
                                    }
                                }
                            });
                    //saveUserInfo(nombre, apellido,identificacion,correo,contrasena);
                } else {
                    //Si no es usuario deberá registrar la información del taller
                    goToRegTaller(nombre, apellido, identificacion, correo, contrasena);
                }
            } else {
                Toast pass = Toast.makeText(this,
                        "Las contraseñas no coinciden",
                        Toast.LENGTH_LONG);
                pass.show();
            }
        }
    }

    public void goToRegTaller(String nombre, String apellido, String identificacion, String correo, String contrasena) {
        Intent irAregistro2 = new Intent(
                RegistroCorreoPassActivity.this,
                RegistroTallerActivity.class);
        irAregistro2.putExtra("nombre", nombre);
        irAregistro2.putExtra("apellido", apellido);
        irAregistro2.putExtra("identificacion", identificacion);
        irAregistro2.putExtra("correo", correo);
        irAregistro2.putExtra("contrasena", contrasena);
        startActivity(irAregistro2);
    }

    // La funcion redirije al loin luego de ingresar la informacion.
    public void goToLogin() {
        Intent goLogin = new Intent(
                RegistroCorreoPassActivity.this,
                MainActivity.class);
        startActivity(goLogin);
    }

}