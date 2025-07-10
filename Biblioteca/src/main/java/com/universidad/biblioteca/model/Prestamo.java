package com.universidad.biblioteca.model;

import java.util.Date;

public class Prestamo {
    private int id;
    private Usuario usuario;
    private Libro libro;
    private Date fechaPrestamo;
    private Date fechaDevolucion;
    private double multa;

    // Constructor vacío
    public Prestamo() {}

    // Constructor completo
    public Prestamo(int id, Usuario usuario, Libro libro, Date fechaPrestamo, Date fechaDevolucion, double multa) {
        this.id = id;
        this.usuario = usuario;
        this.libro = libro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
        this.multa = multa;
    }

    // Getters y Setters
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

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public Date getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(Date fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public Date getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(Date fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public double getMulta() {
        return multa;
    }

    public void setMulta(double multa) {
        this.multa = multa;
    }

    // Opcional: para debug
    @Override
    public String toString() {
        return "Prestamo{" +
                "id=" + id +
                ", usuario=" + (usuario != null ? usuario.getCodigo() : "null") +
                ", libro=" + (libro != null ? libro.getId() : "null") +
                ", fechaPrestamo=" + fechaPrestamo +
                ", fechaDevolucion=" + fechaDevolucion +
                ", multa=" + multa +
                '}';
    }
}
