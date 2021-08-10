package com.dgteam.servimovildg.services;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dgteam.servimovildg.R;

public class HolderMenssage extends RecyclerView.ViewHolder {

    private TextView nombre;
    private TextView mensaje;
    private TextView hora;


    public HolderMenssage(@NonNull View itemView) {

        super(itemView);

        nombre = (TextView) itemView.findViewById(R.id.nombreMensaje);
        mensaje = (TextView) itemView.findViewById(R.id.mensaje);
        hora = (TextView) itemView.findViewById(R.id.horaMensaje);
    }

    public TextView getNombre() {
        return nombre;
    }

    public TextView getMensaje() {
        return mensaje;
    }

    public TextView getHora() {
        return hora;
    }

    public void setNombre(TextView nombre) {
        this.nombre = nombre;
    }

    public void setMensaje(TextView mensaje) {
        this.mensaje = mensaje;
    }

    public void setHora(TextView hora) {
        this.hora = hora;
    }
}
