package com.universidad.biblioteca.model;

public class Libro {
    private int id;
    private String titulo;
    private String autor;
    private String categoria;
    private String editorial;
    private int anioPublicacion;
    private boolean disponible;

    // Constructor vacío
    public Libro() {}

    // Constructor completo
    public Libro(int id, String titulo, String autor, String categoria, String editorial, int anioPublicacion, boolean disponible) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.categoria = categoria;
        this.editorial = editorial;
        this.anioPublicacion = anioPublicacion;
        this.disponible = disponible;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    // Opcional: método para mostrar información básica (útil para debug)
    @Override
    public String toString() {
        return "Libro{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", categoria='" + categoria + '\'' +
                ", editorial='" + editorial + '\'' +
                ", anioPublicacion=" + anioPublicacion +
                ", disponible=" + disponible +
                '}';
    }
}
