package com.dgteam.servimovildg.mecanico;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dgteam.servimovildg.R;
import com.dgteam.servimovildg.usuario.master_page_user;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;


public class Perfil_mechanic extends Fragment {
    private FirebaseFirestore db;
    View vista;
    private EditText corr,nomb,apel,idenf;
    private EditText talle,ni,espe,direc;
    private Button habilitar,Actualizar;
    String uid;
    String cor,nom,ape,iden,taller,nit,especiali,direccio;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Reemplazamos el frame en el container para ilustrarlo.
        vista = inflater.inflate(R.layout.fragment_perfil_mechanic, container, false);

        //Instanciamos el firestore.
        db = FirebaseFirestore.getInstance();

        //Obtenemos el usuario logueado
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();



        if(user != null) {
            //Obtenemos el Uid del usuario logueado
            uid = user.getUid();
            Log.d("id", "ESTE ES EL UID: " + uid);//Prueba de consola.

            //Instanciamos la colección del firebase.
            DocumentReference df = db.collection("users").document(uid);
            //Siendo la instancia de forma exitosa, obtenemos los valores respectivos del usuario logueado.
            df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    cor = documentSnapshot.getString("email");
                    nom = documentSnapshot.getString("nombre");
                    ape = documentSnapshot.getString("apellido");
                    iden = documentSnapshot.getString("identificacion");
                    taller = documentSnapshot.getString("nombreTaller");
                    nit = documentSnapshot.getString("nit");
                    especiali = documentSnapshot.getString("especialidad");
                    direccio = documentSnapshot.getString("direccion");

                    nomb.setText(nom);
                    apel.setText(ape);
                    idenf.setText(iden);
                    corr.setText(cor);
                    talle.setText(taller);
                    ni.setText(nit);
                    espe.setText(especiali);
                    direc.setText(direccio);
                }
            });

        }

        //Instanciamos las cajas de texto de la vista.
        corr = vista.findViewById(R.id.correo_mechanic);
        nomb = vista.findViewById(R.id.name_mechanic);
        apel = vista.findViewById(R.id.lastname_mechanic);
        idenf = vista.findViewById(R.id.iden_mechanic);
        talle = vista.findViewById(R.id.txttaller);
        ni = vista.findViewById(R.id.txtnit);
        espe = vista.findViewById(R.id.txtespecial);
        direc = vista.findViewById(R.id.txtdireccion);


        //Cajas de texto inhabilitadas
        nomb.setEnabled(false);
        corr.setEnabled(false);
        idenf.setEnabled(false);
        apel.setEnabled(false);
        talle.setEnabled(false);
        ni.setEnabled(false);
        espe.setEnabled(false);
        direc.setEnabled(false);


        // instancia del boton habilitar
        habilitar = vista.findViewById(R.id.btnhabilitar_mechanic);

        //Funcion al presionar el boton
        habilitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nomb.setEnabled(true);
                idenf.setEnabled(true);
                apel.setEnabled(true);
                talle.setEnabled(true);
                espe.setEnabled(true);
                direc.setEnabled(true);
            }
        });

        Actualizar = vista.findViewById(R.id.btnactualizar_mechanic);

        //instancia del boton Actualizar mecanico
        Actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mapeamos los datos que vamos a ingresar
                Map data = new HashMap<>();

                //Los ingresamos en la instancia creada.
                data.put("nombre",nomb.getText().toString());
                data.put("apellido",apel.getText().toString());
                data.put("identificacion",idenf.getText().toString());
                data.put("nombreTaller",talle.getText().toString());
                data.put("especialidad",espe.getText().toString());
                data.put("direccion",direc.getText().toString());

                //Realizamos la actualizacion en el firestore.
                db.collection("users").document(uid).update(data).addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Toast notifica = Toast.makeText(getActivity(),"Datos actualizados",Toast.LENGTH_SHORT);
                        notifica.show();
                        Intent user = new Intent(
                                getActivity(),
                                master_page_mechanic.class);
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
                                master_page_mechanic.class);
                        startActivity(user);
                        getActivity().onBackPressed();
                    }
                });
            }
        });


        return vista;
    }
}