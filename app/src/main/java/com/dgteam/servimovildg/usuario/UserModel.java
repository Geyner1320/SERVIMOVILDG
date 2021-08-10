package com.dgteam.servimovildg.usuario;

public class UserModel {
    String nombre, apellidos, uuid, mecanicoAsignado;

    public UserModel() {
    }

    public UserModel(String nombre, String apellidos, String uuid) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.uuid = uuid;
        this.mecanicoAsignado = mecanicoAsignado;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return  nombre + " " + apellidos ;
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

    public String getMecanicoAsignado() {
        return mecanicoAsignado;
    }

    public void setMecanicoAsignado(String mecanicoAsignado) {
        this.mecanicoAsignado = mecanicoAsignado;
    }
}
