package com.dgteam.servimovildg.usuario;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dgteam.servimovildg.R;
import com.dgteam.servimovildg.logueo.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Perfil extends Fragment {
    private FirebaseFirestore db;
    View frame;
    private EditText correo,nombre,apellido,idenficacion;
    private Button habilitar, Actualizar;
    String uid;
    String cor,nom,ape,iden;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Reemplazamos el frame en el container para ilustrarlo.
        frame = inflater.inflate(R.layout.fragment_perfil, container, false);

        //Instanciamos el firestore.
        db = FirebaseFirestore.getInstance();

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

                    //Asignamos los valores en las cajas de texto.
                    nombre.setText(nom);
                    apellido.setText(ape);
                    idenficacion.setText(iden);
                    correo.setText(cor);
                }
            });

        }

                //Instancias de cajas de texto.
                nombre = frame.findViewById(R.id.txtname);
                correo = frame.findViewById(R.id.txtemail);
                idenficacion = frame.findViewById(R.id.txtiden);
                apellido = frame.findViewById(R.id.txtlastname);


                //Cajas de texto inhabilitadas
                nombre.setEnabled(false);
                correo.setEnabled(false);
                idenficacion.setEnabled(false);
                apellido.setEnabled(false);


                // instancia del boton habilitar
                habilitar = frame.findViewById(R.id.btnhabilitar);

                //Funcion al presionar el boton
                habilitar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nombre.setEnabled(true);
                        idenficacion.setEnabled(true);
                        apellido.setEnabled(true);
                    }
                });

                Actualizar = frame.findViewById(R.id.btnactualizar);

                //instancia del boton Actualizar
                Actualizar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Mapeamos los datos por medio de la instancia map.
                        Map data = new HashMap<>();

                        //Ingresamos los datos en la instancia creada.
                        data.put("nombre",nombre.getText().toString());
                        data.put("apellido",apellido.getText().toString());
                        data.put("identificacion",idenficacion.getText().toString());

                        //Generemos la actualización en el firestore.
                        db.collection("users").document(uid).update(data).addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                Toast notifica = Toast.makeText(getActivity(),"Datos actualizados",Toast.LENGTH_SHORT);
                                notifica.show();
                                Intent user = new Intent(
                                        getActivity(),
                                        master_page_user.class);
                                startActivity(user);
                                getActivity().onBackPressed();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                Toast notifica = Toast.makeText(getActivity(),"Datos NO actualizados",Toast.LENGTH_SHORT);
                                notifica.show();
                                Intent user = new Intent(
                                        getActivity(),
                                        master_page_user.class);
                                startActivity(user);
                                getActivity().onBackPressed();
                            }
                        });
                    }
                });

        return frame;
    }




}