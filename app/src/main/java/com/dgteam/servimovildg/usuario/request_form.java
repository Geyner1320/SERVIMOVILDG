package com.dgteam.servimovildg.usuario;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dgteam.servimovildg.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import dmax.dialog.SpotsDialog;

public class request_form extends AppCompatActivity {
    private EditText cel, det, ref;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button btn;
    private android.app.AlertDialog builder;
    private String esta;
    private String documento;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_form);
        cel = (EditText) findViewById(R.id.cel);
        det = (EditText) findViewById(R.id.detalle);
        ref = (EditText) findViewById(R.id.referencia);
        btn = (Button) findViewById(R.id.btn_request);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request();
            }
        });

    }

    //Metodo para insertar valores en la base de datos.
    public void request() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String dateToStr = dateFormat.format(date);
        DocumentReference df = db.collection("solicitudes").document();
        DocumentReference us = db.collection("users").document(user.getUid());
        us.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String nom = documentSnapshot.getString("nombre");
                String apellido = documentSnapshot.getString("apellido");
                String nombreC = nom + " " + apellido;

                if (verificar() == true) {
                    Map<String, Object> loc = new HashMap<>();
                    loc.put("nombre", nombreC);
                    loc.put("userUid", user.getUid());
                    loc.put("ref_vehiculo", ref.getText().toString());
                    loc.put("telefono", cel.getText().toString());
                    loc.put("detalle", det.getText().toString());
                    loc.put("fecha", dateToStr);
                    loc.put("mechanicUid", "ninguno");
                    loc.put("estado", "espera");
                    loc.put("documento",df.getId());
                    df.set(loc);


                    View vista = getLayoutInflater().inflate(R.layout.exito, null);

                    //Agregamos el listview en el alert dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(request_form.this);
                    builder.setView(vista);
                    builder.setCancelable(false);
                    builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            espera();
                            metodo();
                        }
                    });
                    final AlertDialog dialog = builder.create();
                    dialog.show();


                }
            }
        });
    }

    //Metodo para verificar que los campos no entren vacios en la base de datos.
    public boolean verificar() {
        String celular = cel.getText().toString();
        String detalle = det.getText().toString();
        String referencia = ref.getText().toString();
        boolean verif = true;
        if (celular.length() == 0) {
            Toast.makeText(this, "Debes colocar un numero de celular", Toast.LENGTH_SHORT).show();
            verif = false;

        } else if (detalle.length() == 0) {
            Toast.makeText(this, "Debes colocar una descripción", Toast.LENGTH_SHORT).show();
            verif = false;
        } else if (referencia.length() == 0) {
            Toast.makeText(this, "Debes colocar una referencia", Toast.LENGTH_SHORT).show();
            verif = false;
        }
        return verif;
    }


    //Metodo para mostrar el boton de acción.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuactions, menu);
        return true;
    }

    //Metodo para agregar las acciones de nuestro botones.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.back) {
            finish();
        }
        return true;
    }

    //Funcion de visualizacion de la ventana emergente de espera con tiempo para deshabilitar.
    public void espera() {
        View vista = getLayoutInflater().inflate(R.layout.activity_esperando, null);
        //Agregamos el listview en el alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(request_form.this);
        builder.setView(vista);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(dialog.isShowing()){
                    dialog.dismiss();
                }
            }
        },50000);
    }

    //Metodo de mensaje de solicitud aceptada.
    public void mensaje(){
        //Agregamos el listview en el alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_baseline_add_location_alt_24);
        builder.setMessage("EL MECANICO TENDRA COMUNICACIÓN CONTIGO, " +
                "SE DIRIGIRA AL LUGAR EN DONDE TE ENCUENTRAS, POR FAVOR PRESIONA " +
                "CONTINUAR PARA SEGUIR INTERACTUANDO CON LA APLICACIÓN.");
        builder.setTitle(R.string.mecanico);
        builder.setPositiveButton("CONTINUAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Metodo de mensaje de exito.
    public void inicio(){
        View vista = getLayoutInflater().inflate(R.layout.good, null);
        //Agregamos el listview en el alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(request_form.this);
        builder.setView(vista);
        builder.setCancelable(false);
        builder.setPositiveButton("CONTINUAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mensaje();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Metodo de mensaje de error
    public void fin(){
        View vista = getLayoutInflater().inflate(R.layout.error, null);
        //Agregamos el listview en el alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(request_form.this);
        builder.setView(vista);
        builder.setCancelable(false);
        builder.setPositiveButton("CONTINUAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mensaje2();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    //funcion de mensaje secundario
    public void mensaje2(){
        //Agregamos el listview en el alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_baseline_gpp_bad_24);
        builder.setMessage("SE ACABO EL TIEMPO DE ESPERA " +
                "REALIZA DE NUEVO UNA SOLICITUD; POR FAVOR PRESIONA CONTINUAR PARA " +
                "SEGUIR INTERACTUANDO CON LA APLICACIÓN.");
        builder.setTitle(R.string.mecanico1);
        builder.setPositiveButton("CONTINUAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    //metodo para obtener el UID del documento.
    public void metodo(){
        //variable de tiempo declarada para el intent de pagina al finalizar el SplashScreen
        db.collection("solicitudes").whereEqualTo("userUid",user.getUid())
                .whereEqualTo("estado","espera")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snapshots = queryDocumentSnapshots.getDocuments();
                        for(DocumentSnapshot snapshot: snapshots){
                            documento = snapshot.getId();
                        }
                        Log.d("TAG","DOCUMENTO:"+documento);
                        metodo2(documento);
                    }
                });
    }

    //Metodo de verificación y asignación de tiempo para la solicitud.
    public void metodo2(String doc){
        TimerTask tarea = new TimerTask() {
            @Override
            public void run() {
                db.collection("solicitudes").document(doc)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                esta = documentSnapshot.getString("estado");
                                if(esta.equals("activo") ){
                                    inicio();
                                }else if(esta.equals("espera")){
                                    eliminar();
                                    fin();
                                }
                            }
                        });
            }
        };

        //Tiempo establecido para el SplashScreen
        Timer tiempo = new Timer();
        tiempo.schedule(tarea,50000);
    }

    // Metodo para eliminar registro de solicitudes.
    public void eliminar(){
        db.collection("solicitudes").document(documento).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(request_form.this,
                                "ELIMINADO", Toast.LENGTH_SHORT);
                    }
                });
    }
}