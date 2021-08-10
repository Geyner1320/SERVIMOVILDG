package com.dgteam.servimovildg.mecanico;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dgteam.servimovildg.Maps_users;
import com.dgteam.servimovildg.R;
import com.dgteam.servimovildg.logueo.MainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class details extends AppCompatActivity {

    //Variables declaradas de manera global para la gestión del activity.
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView nombre,referencia,telefo;
    private EditText detal;
    private String no,re,de,te,id,name,uid;
    private Button service,call;
    private String number;
    private int REQUEST_CODE = 200;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(ActivityCompat.checkSelfPermission(details.this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE);
        }

        //Despliegue de la activity de detalles de la solicitud.
        setContentView(R.layout.activity_details);

        //Asignación de variables a los diferentes componentes del activity.
        nombre = findViewById(R.id.nom);
        referencia = findViewById(R.id.ref);
        telefo = findViewById(R.id.tel);
        detal = findViewById(R.id.des);
        service = findViewById(R.id.btnservice);
        call = findViewById(R.id.btncall);

        //Asignación a una variable sobre un valor que se encuentra en otro activity.
        name = getIntent().getStringExtra("name");

        //Llamado de función generada en la clase.java
        request();

        call.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+telefo.getText()));
                Log.d("TAG","Numero de celular "+telefo.getText());
                startActivity(i);
            }
        });

        //Captura de evento para el boton servicio.
        service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Agregamos un alert dialog para confirmación de decisión tomada.
                AlertDialog.Builder builder = new AlertDialog.Builder(details.this);
                //Le aceptamos la cancelación de la ventana emergente.
                builder.setCancelable(true);
                //asignación de icono en la ventana emergente.
                builder.setIcon(R.drawable.ic_undraw_mobile_inbox_re_ciwq);
                //asignación de titulo en la ventana emergente.
                builder.setTitle(R.string.confirmacion);
                //En el caso que presione el boton aceptar ocurrira una tarea.
                builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        try {
                            //instancia del usuario logueado.
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            //Obtención de la colección solicitudes con un documento en especifico.
                            DocumentReference df = db.collection("solicitudes").document(id);
                            Task<DocumentSnapshot> mech = db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    String nombre = documentSnapshot.getString("nombre");
                                    String apellido = documentSnapshot.getString("apellido");
                                    String nomape = nombre+" "+apellido;
                                    //Arreglo de mapeo.
                                    Map data = new HashMap<>();
                                    //agregar valores al arreglo.
                                    data.put("mechanicUid", user.getUid());
                                    data.put("estado", "activo");
                                    data.put("nombre_mechanic",nomape);
                                    //actualizamos la base de datos.
                                    df.update(data);
                                    //Despliegue de función determinada en la clase.java
                                    aceptado();
                                }
                            });
                        }
                        catch (Exception e){
                            error();
                        }
                    }
                    //Capturar evencto de Cancelar.
                }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Desplegar un aviso de servicio no establecido.
                        Toast.makeText(details.this,"Servicio no tomado.",Toast.LENGTH_SHORT).show();
                    }
                });
                //Construimos la ventana emergente.
                final AlertDialog dialog = builder.create();
                //Desplegamos la ventana emergente.
                dialog.show();
            }
        });
    }

    //Función que permitira entender la confirmación
    public void aceptado(){
        View vista = getLayoutInflater().inflate(R.layout.success,null);

        //Agregamos el listview en el alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(details.this);
        builder.setView(vista);
        builder.setCancelable(false);
        builder.setPositiveButton("CONTINUAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               Intent nuevo = new Intent(details.this,Maps_users.class);
                nuevo.putExtra("UID",uid);
                nuevo.putExtra("cel",te);
                startActivity(nuevo);
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Función que permitira entender la confirmación
    public void error(){
        View vista = getLayoutInflater().inflate(R.layout.error,null);

        //Agregamos el listview en el alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(details.this);
        builder.setView(vista);
        builder.setCancelable(false);
        builder.setPositiveButton("REGRESAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(details.this,master_page_mechanic.class));
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Metodo para mostrar el boton de acción.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuactions,menu);
        return true;
    }

    //Metodo para agregar las acciones de nuestro botones.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.back){
            finish();
        }
        return true;
    }

    //Función para setear la información del usuario seleccionado.
    public void request(){
        db.collection("solicitudes")
                .whereEqualTo("estado","espera")
                .whereEqualTo("nombre",name)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snapshots = queryDocumentSnapshots.getDocuments();
                        for(DocumentSnapshot snapshot: snapshots){
                            id = snapshot.getId();
                            no = snapshot.getString("nombre");
                            re = snapshot.getString("ref_vehiculo");
                            de = snapshot.getString("detalle");
                            te = snapshot.getString("telefono");
                            uid = snapshot.getString("userUid");
                            nombre.setText(no);
                            referencia.setText(re);
                            telefo.setText(te);
                            detal.setText(de);
                            detal.setEnabled(false);
                        }
                    }
                });
        }

    }
