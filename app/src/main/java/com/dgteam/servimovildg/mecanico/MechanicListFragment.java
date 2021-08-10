package com.dgteam.servimovildg.mecanico;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.dgteam.servimovildg.R;
import com.dgteam.servimovildg.usuario.UserModel;
import com.dgteam.servimovildg.usuario.inbox_user;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MechanicListFragment extends Fragment {

    View vista;
    //Conector a firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //Variables del mecanico
    String nombre, apellido, uuid;
    private ListView listView;
    private ArrayList<String> names;
    ArrayList<UserModel> usuarios;
    private FrameLayout fl;

    //Obtener el id del usuario que esta registrado
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    //id del mecanico que se va ha enviar a la nueva vista
    String mecanicoId;
    String nombresApellidos;
    String uid;
    String cor,nom,ape,iden;

    public MechanicListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment_user_list, container, false);

        return vista;
    }

    @Override
    public void onActivityCreated(Bundle state) {

        super.onActivityCreated(state);

        //Definir la lista en donde se mostraran los datos
        listView = (ListView)getView().findViewById(R.id.user_listview);

        //Declarar arreglo en donde se almacenaran los nombres
        names = new ArrayList<String>();

        //Declarar un arreglo de usuarios
        usuarios = new ArrayList<UserModel>();


        //Consultar en firestore todos los usuarios que sean mecanicos
        db.collection("users")
                .whereEqualTo("userType","usuario")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                //Obtenemos el usuario logueado
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if(user != null) {

                    //Obtenemos el Uid del usuario logueado
                    uid = user.getUid();

                    //Prueba de consola.
                    Log.d("id", "ESTE ES EL UID: " + uid);

                    //Instanciamos la colección del firebase.
                    DocumentReference df = db.collection("users").document(uid);

                    //Siendo la instancia de forma exitosa, obtenemos los valores respectivos del usuario logueado.
                    df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            //Asignamos esos valores en las variables por medio del documentSnapshot.
                            cor = documentSnapshot.getString("email");
                            nom = documentSnapshot.getString("nombre");
                            ape = documentSnapshot.getString("apellido");
                            iden = documentSnapshot.getString("identificacion");


                        }
                    });

                }
                List<DocumentSnapshot> snapshots = queryDocumentSnapshots.getDocuments();
                for(DocumentSnapshot snapshot: snapshots){
                    nombre = snapshot.getString("nombre");
                    apellido = snapshot.getString("apellido");
                    uuid = snapshot.getString("userUid");
                    Log.d("Tag","Nombres: " + nombre);
                    Log.d("TAG","idMecanico: " + uuid);
                    names.add(nombresApellidos);
                    usuarios.add(new UserModel(nombre, apellido, uuid));
                }

                ArrayAdapter<UserModel> adapter = new ArrayAdapter<UserModel>(
                        getActivity().getApplicationContext(),
                        android.R.layout.simple_expandable_list_item_1,
                        usuarios);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mecanicoId = String.valueOf(usuarios.get(position).getUuid());
                        nombresApellidos = String.valueOf(usuarios.get(position).toString());

                        Intent goToChat = new Intent(
                                getActivity(),
                                inbox_user.class
                        );
                        goToChat.putExtra("nombres", nombresApellidos);
                        goToChat.putExtra("uuid", mecanicoId);
                        goToChat.putExtra("actualUser", nom + " " + ape);

                        startActivity(goToChat);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d("TAG","Error: "+ e.getMessage());
            }
        });
    }
}