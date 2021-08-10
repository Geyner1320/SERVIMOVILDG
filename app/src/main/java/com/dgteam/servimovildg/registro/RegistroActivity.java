package com.dgteam.servimovildg.registro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dgteam.servimovildg.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class RegistroActivity extends AppCompatActivity {

    //Declarar los campos de texto
    private EditText etNombre, etApelllidos, etIdentificacion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        getSupportActionBar().hide();

        //Declaración de variables para traer datos del formulario.

        etNombre = (EditText) findViewById(R.id.etNombres);
        etApelllidos = (EditText) findViewById(R.id.etApellidos);
        etIdentificacion = (EditText) findViewById(R.id.etIdentificacion);
        Log.d("id", "onUserType: " + getIntent().getStringExtra("userType"));
    }

    // La funcion redirige a la siguiente activity donde el usuario debe ingresar los demas datos
    // del regitro
    // DMR
    public void goToRegMailPass(View V) {

        String nombre = etNombre.getText().toString();
        String apellidos = etApelllidos.getText().toString();
        String identificacion = etIdentificacion.getText().toString();
        String userType = getIntent().getStringExtra("userType");

        if (nombre.isEmpty() || apellidos.isEmpty() || identificacion.isEmpty()) {
            Toast notification = Toast.makeText(this,
                    "Los campos no pueden quedar vacíos",
                    Toast.LENGTH_LONG);
            notification.show();
        }else{
            Intent irAregistro2 = new Intent(
                    RegistroActivity.this,
                    RegistroCorreoPassActivity.class);
            irAregistro2.putExtra("nombre", nombre);
            irAregistro2.putExtra("apellidos", apellidos);
            irAregistro2.putExtra("identificacion", identificacion);
            irAregistro2.putExtra("userType", userType);
            startActivity(irAregistro2);
        }
    }
}