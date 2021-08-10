package com.dgteam.servimovildg;

public class Terminado {
    private String detalle;
    private String costo;
    private String fecha;
    private String fechafin;
    private String observa;
    private String referencia;
    private String nombre;

    public Terminado(String nombre,String detalle, String costo, String fecha, String fechafin, String observa, String referencia) {
        this.nombre = nombre;
        this.detalle = detalle;
        this.costo = costo;
        this.fecha = fecha;
        this.fechafin = fechafin;
        this.observa = observa;
        this.referencia = referencia;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getCosto() {
        return costo;
    }

    public void setCosto(String costo) {
        this.costo = costo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getFechafin() {
        return fechafin;
    }

    public void setFechafin(String fechafin) {
        this.fechafin = fechafin;
    }

    public String getObserva() {
        return observa;
    }

    public void setObserva(String observa) {
        this.observa = observa;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
