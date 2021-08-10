package com.dgteam.servimovildg.usuario;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dgteam.servimovildg.services.AdapterMessages;
import com.dgteam.servimovildg.services.Message;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import com.dgteam.servimovildg.R;


public class inbox extends Fragment {

    View vista;
    //Instanciar los elementos
    private TextView nombre;
    private RecyclerView rvMensajes;
    private EditText txtMensaje;
    private Button btnEnviar;

    //Instanciamos los objetos de la base de datos
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    public inbox() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private AdapterMessages adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Crear la instancia de la vista para que reconozca los elmentos
        vista = inflater.inflate(R.layout.fragment_inbox, container, false);


        nombre = (TextView) vista.findViewById(R.id.nameSenderMessage);
        rvMensajes = (RecyclerView) vista.findViewById(R.id.rvMensajes);
        txtMensaje = (EditText) vista.findViewById(R.id.txtMensaje);
        btnEnviar = (Button) vista.findViewById(R.id.btnEnviar);

        //Inicializar componentes de firebase
        database = FirebaseDatabase.getInstance();
        // Sala de chat
        databaseReference = database.getReference("chat");

        adapter = new AdapterMessages(this.getContext());
        LinearLayoutManager l = new LinearLayoutManager(this.getContext());
        rvMensajes.setLayoutManager(l);
        rvMensajes.setAdapter(adapter);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //adapter.addMessage(new Message(txtMensaje.getText().toString(),nombre.getText().toString(),"00:00"));
                databaseReference.push().setValue(new Message(txtMensaje.getText().toString(),
                        nombre.getText().toString(),
                        "00:00"));
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

        return vista;
    }

    private void setScrollbar(){
        rvMensajes.scrollToPosition(adapter.getItemCount() - 1);
    }

}

