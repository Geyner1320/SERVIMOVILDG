package com.dgteam.servimovildg.mecanico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dgteam.servimovildg.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class mechanic_list extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String nombre;
    private ListView list;
    private String cadena;
    private String id_doc;
    private String uid;
    private String sol = "Nueva solicitud\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_list);
        list =(ListView)findViewById(R.id.list_request);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,new ArrayList<String>());
        list.setAdapter(adapter);

        //agregar datos al listview
        db.collection("solicitudes")
                .whereEqualTo("estado","espera").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<String> data = new ArrayList<>();
                List<DocumentSnapshot> snapshots = queryDocumentSnapshots.getDocuments();
                for(DocumentSnapshot snapshot: snapshots){
                    nombre = snapshot.getString("nombre");
                    id_doc = snapshot.getId();
                    uid = snapshot.getString("userUid");
                    Log.d("Tag","Valor id del documento: "+snapshot.getId());
                    cadena = sol+nombre;
                    data.add(cadena);
                }
                adapter.clear();
                //      Creamos un adaptador de arreglos
                adapter.addAll(data);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d("TAG","Error: "+ e.getMessage());
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String valor = (String)list.getAdapter().getItem(position);
                String contenido = new String(valor.getBytes());
                String[] parts = contenido.split("Nueva solicitud\n");
                String nombre = parts[1];
                Intent activity = new Intent(mechanic_list.this, details.class);
                activity.putExtra("name",nombre);
                startActivity(activity);
            }
        });
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
            startActivity(new Intent(mechanic_list.this,master_page_mechanic.class));
        }
        return true;
    }

}