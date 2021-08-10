package com.dgteam.servimovildg.mecanico;

public class MecanicosModel {

    String nombre, apellidos, uuid, usuarioAsignado;

    public MecanicosModel() {
    }

    public MecanicosModel(String nombre, String apellidos, String uuid) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.uuid = uuid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return nombre + " " + apellidos;
    }
}
