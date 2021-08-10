package com.dgteam.servimovildg.mecanico;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.dgteam.servimovildg.R;
import com.dgteam.servimovildg.Terminado;
import com.dgteam.servimovildg.solicitudes;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class cashing_mechanic extends Fragment {
    private View vista;
    private TextView ref,ini,fin,cos;
    private EditText observa,detal;
    private Spinner usu;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment_cashing_mechanic, container, false);

        ref = vista.findViewById(R.id.txtref);
        ini = vista.findViewById(R.id.txtfechI);
        fin = vista.findViewById(R.id.txtfechF);
        cos = vista.findViewById(R.id.txtcosto);

        observa = vista.findViewById(R.id.observamechanic);
        detal = vista.findViewById(R.id.txtcaso);

        usu = vista.findViewById(R.id.spin);

        resultado();

        return vista;
    }

    public void resultado(){
        final List<Terminado> solicita = new ArrayList<>();
        db.collection("solicitudes")
                .whereEqualTo("mechanicUid",user.getUid())
                .whereEqualTo("estado","terminado")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snapshots = queryDocumentSnapshots.getDocuments();
                        for(DocumentSnapshot snapshot: snapshots){
                            String nombre = snapshot.getString("nombre");
                            String ref = snapshot.getString("ref_vehiculo");
                            String costo = snapshot.getString("Costo");
                            String det = snapshot.getString("detalle");
                            String fech = snapshot.getString("fecha");
                            String fechF = snapshot.getString("Fecha_fin");
                            String obser = snapshot.getString("Observaciones");
                            solicita.add(new Terminado(nombre,det,costo,fech,fechF,obser,ref));
                        }
                        ArrayAdapter<Terminado> adapter = new ArrayAdapter<>(getActivity(),
                                android.R.layout.simple_dropdown_item_1line,
                                solicita);
                        usu.setAdapter(adapter);
                        usu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                detal.setText(solicita.get(position).getDetalle());
                                fin.setText(solicita.get(position).getFechafin());
                                ini.setText(solicita.get(position).getFecha());
                                ref.setText(solicita.get(position).getReferencia());
                                observa.setText(solicita.get(position).getObserva());
                                cos.setText(solicita.get(position).getCosto());
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                });
    }
}