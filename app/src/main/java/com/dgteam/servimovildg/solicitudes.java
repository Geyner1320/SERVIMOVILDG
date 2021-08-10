package com.dgteam.servimovildg;

public class solicitudes {

    String nombre;
    String userUid;
    String id;
    String referencia;
    String detalle;
    String fecha;

    public solicitudes(String nombre, String userUid, String id, String referencia, String detalle,String fecha) {
        this.nombre = nombre;
        this.userUid = userUid;
        this.id = id;
        this.referencia = referencia;
        this.detalle = detalle;
        this.fecha = fecha;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
