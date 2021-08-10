package com.dgteam.servimovildg.usuario;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dgteam.servimovildg.R;
import com.dgteam.servimovildg.services.AdapterMessages;
import com.dgteam.servimovildg.services.Message;
import com.dgteam.servimovildg.utiles.Fechas;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class inbox_user extends AppCompatActivity {

    //Instanciar los elementos
    private TextView nombre;
    private RecyclerView rvMensajes;
    private EditText txtMensaje;
    private Button btnEnviar;
    String actualUser;

    //Instanciamos los objetos de la base de datos
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    //Traer el nombre del usuario Logueado
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db;


    public inbox_user(){ }

    private AdapterMessages adapter;

    String fechaHora;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);


        nombre = (TextView) findViewById(R.id.nameSenderMessage);
        rvMensajes = (RecyclerView) findViewById(R.id.rvMensajes);
        txtMensaje = (EditText) findViewById(R.id.txtMensaje);
        btnEnviar = (Button) findViewById(R.id.btnEnviar);

        //Obtener parametros que llegan de la otra pantalla
        String nombreMecanico = getIntent().getStringExtra("nombres");
        String actualUser = getIntent().getStringExtra("actualUser");
        this.setTitle("Chat con: " + nombreMecanico);
        nombre.setText(getIntent().getStringExtra("actualUser"));

        //Inicializar componentes de firebase
        database = FirebaseDatabase.getInstance();
        chat();
    }

    //Metodo para mostrar el boton de acción
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuactions,menu);
        return true;
    }

    //Metodo para agregar las acciones de nuestro botones


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.back){
            finish();
            //startActivity(new Intent(this,user_list.class));
        }
        return true;
    }

    public void chat(){
        // Sala de chat
        databaseReference = database.getReference("chat");

        adapter = new AdapterMessages(this);
        LinearLayoutManager l = new LinearLayoutManager(this);
        rvMensajes.setLayoutManager(l);
        rvMensajes.setAdapter(adapter);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Log.d("Hora", Fechas.obtenerHoraActual("America/Bogota").toString());
               Log.d("Fecha", Fechas.obtenerFechaActual("America/Bogota").toString());
                fechaHora = Fechas.obtenerHoraActual("America/Bogota").toString() +
                        "-" +
                        Fechas.obtenerFechaActual("America/Bogota").toString();

                //adapter.addMessage(new Message(txtMensaje.getText().toString(),nombre.getText().toString(),"00:00"));
                databaseReference.push().setValue(new Message(txtMensaje.getText().toString(),
                        getIntent().getStringExtra("actualUser"),
                        fechaHora));
                txtMensaje.setText("");
            }
        });

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                setScrollbar();
            }
        });


        //Hijo de la referencia de la base de datos
        databaseReference.addChildEventListener(new ChildEventListener() {

            // Cuando se agrege un dato a la bd se guarda en la lista
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message m = dataSnapshot.getValue(Message.class);
                adapter.addMessage(m);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setScrollbar(){
        rvMensajes.scrollToPosition(adapter.getItemCount() - 1);
    }
}