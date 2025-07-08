package com.universidad.biblioteca.model;

public class Libro {

    private String isbn;
    private String titulo;
    private String autor;
    private String categoria;
    private String editorial;
    private int anioPublicacion;
    private int totalEjemplares;
    private int disponibles;

    public Libro() {
    }

    public Libro(String isbn, String titulo, String autor, String categoria,
            String editorial, int anioPublicacion, int totalEjemplares, int disponibles) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.categoria = categoria;
        this.editorial = editorial;
        this.anioPublicacion = anioPublicacion;
        this.totalEjemplares = totalEjemplares;
        this.disponibles = disponibles;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public int getAnioPublicacion() {
        return anioPublicacion;
    }

    public void setAnioPublicacion(int anioPublicacion) {
        this.anioPublicacion = anioPublicacion;
    }

    public int getTotalEjemplares() {
        return totalEjemplares;
    }

    public void setTotalEjemplares(int totalEjemplares) {
        this.totalEjemplares = totalEjemplares;
    }

    public int getDisponibles() {
        return disponibles;
    }

    public void setDisponibles(int disponibles) {
        this.disponibles = disponibles;
    }

}
