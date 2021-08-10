package com.dgteam.servimovildg.mecanico;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dgteam.servimovildg.R;
import com.dgteam.servimovildg.solicitudes;
import com.dgteam.servimovildg.usuario.request_form;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class service_finish extends Fragment {
    private View vista;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private Spinner btn;
    private String selectnombre="";
    private TextView nam,det,fech,ref,txtid;
    private Button btncos;
    private EditText observa,costo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment_service_finish, container, false);
        btn = vista.findViewById(R.id.btnspinner);
        fech = vista.findViewById(R.id.txtfech);
        nam = vista.findViewById(R.id.txtuser);
        ref = vista.findViewById(R.id.txtref);
        det = vista.findViewById(R.id.txtdetail);
        fech = vista.findViewById(R.id.txtfech);
        txtid = vista.findViewById(R.id.id_doc);
        observa = vista.findViewById(R.id.editobserva);
        costo = vista.findViewById(R.id.editcosto);
        loadspinner();


        btncos = vista.findViewById(R.id.btncosto);
        btncos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrar();
            }
        });

        return vista;
    }

    public void loadspinner(){
        final List<solicitudes> solicita = new ArrayList<>();
        db.collection("solicitudes")
                .whereEqualTo("mechanicUid",user.getUid())
                .whereEqualTo("estado","activo")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snapshots = queryDocumentSnapshots.getDocuments();
                        for(DocumentSnapshot snapshot: snapshots){
                            String name = snapshot.getString("nombre");
                            String uid = snapshot.getString("userUid");
                            String id = snapshot.getId();
                            String ref = snapshot.getString("ref_vehiculo");
                            String det = snapshot.getString("detalle");
                            String fech = snapshot.getString("fecha");
                            solicita.add(new solicitudes(name,uid,id,ref,det,fech));
                        }
                        ArrayAdapter<solicitudes> adapter = new ArrayAdapter<>(getActivity(),
                                android.R.layout.simple_dropdown_item_1line,
                                solicita);
                        btn.setAdapter(adapter);
                        btn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                selectnombre = String.valueOf(parent.getItemAtPosition(position));
                                nam.setText(selectnombre);
                                det.setText(solicita.get(position).getDetalle());
                                fech.setText(solicita.get(position).getFecha());
                                ref.setText(solicita.get(position).getReferencia());
                                txtid.setText(solicita.get(position).getId());
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                });
    }

    public void registrar(){
        if(verificar()==true) {
            //Obtenemos el usuario logueado
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            String dateToStr = dateFormat.format(date);
            String documento = txtid.getText().toString();
            DocumentReference df = db.collection("solicitudes").document(documento);
            Map data = new HashMap<>();
            data.put("estado", "terminado");
            data.put("Observaciones", observa.getText().toString());
            data.put("Costo", costo.getText().toString());
            data.put("Fecha_fin",dateToStr);
            df.update(data);
            success();
        }else{
            Toast.makeText(getActivity(), "Cumple con los datos solicitados", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean verificar() {
        String observaciones = observa.getText().toString();
        String cost = costo.getText().toString();
        boolean verif = true;
        if (observaciones.length() == 0) {
            Toast.makeText(getActivity(), "Debes llenar el campo observaciones", Toast.LENGTH_SHORT).show();
            verif = false;

        } else if (cost.length() == 0) {
            Toast.makeText(getActivity(), "Debes colocar un costo ", Toast.LENGTH_SHORT).show();
            verif = false;
        }
        return verif;
    }

    public void success(){
        //Agregamos el listview en el alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_baseline_add_location_alt_24);
        builder.setMessage("REGISTRO COMPLETADO CON EXITO!");
        builder.setTitle(R.string.suce);
        final AlertDialog dialog = builder.create();
        dialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(dialog.isShowing()){
                    dialog.dismiss();
                    startActivity(new Intent(getActivity(),master_page_mechanic.class));
                }
            }
        },10000);
    }


}