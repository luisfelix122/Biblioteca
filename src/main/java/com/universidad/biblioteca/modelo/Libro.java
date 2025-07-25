package com.universidad.biblioteca.modelo;

public class Libro {
    private String isbn;
    private String titulo;
    private String autor;

    private int anioPublicacion;
    private boolean disponible;

    // Constructor vacío
    public Libro() {}

    // Constructor completo
    public Libro(String isbn, String titulo, String autor, int anioPublicacion, boolean disponible) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;

        this.anioPublicacion = anioPublicacion;
        this.disponible = disponible;
    }

    // Getters y Setters
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
                "isbn=" + isbn +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +

                ", anioPublicacion=" + anioPublicacion +
                ", disponible=" + disponible +
                '}';
    }
}
