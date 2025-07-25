package com.universidad.biblioteca.modelo;

import java.util.Date;

public class Sugerencia {
    private int id;
    private Usuario usuario;
    private String titulo;
    private String descripcion;
    private Date fechaSugerencia;

    // Constructor
    public Sugerencia() {
    }

    public Sugerencia(Usuario usuario, String titulo, String descripcion, Date fechaSugerencia) {
        this.usuario = usuario;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaSugerencia = fechaSugerencia;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaSugerencia() {
        return fechaSugerencia;
    }

    public void setFechaSugerencia(Date fechaSugerencia) {
        this.fechaSugerencia = fechaSugerencia;
    }
}