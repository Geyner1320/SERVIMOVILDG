package com.dgteam.servimovildg.services;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dgteam.servimovildg.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterMessages extends RecyclerView.Adapter<HolderMenssage> {

    private List<Message> listMensaje = new ArrayList<>();
    private Context c;

    public AdapterMessages(Context c) {
        this.c = c;
    }

    public void addMessage(Message m){
        listMensaje.add(m);
        notifyItemInserted(listMensaje.size());
    }

    @NonNull
    @Override
    public HolderMenssage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.card_view_mensajes,parent,false);
        return new HolderMenssage(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderMenssage holder, int position) {
        holder.getNombre().setText(listMensaje.get(position).getNombre());
        holder.getMensaje().setText(listMensaje.get(position).getMensaje());
        holder.getHora().setText(listMensaje.get(position).getHora());
    }

    @Override
    public int getItemCount() {
        return listMensaje.size();
    }
}
