package com.universidad.biblioteca.model;

import java.time.LocalDate;

/**
 * Representa un préstamo, con datos opcionales de devolución y multa.
 */
public class Prestamo {
    private final int idPrestamo;
    private final String codigoUsuario;
    private final String isbn;
    private final String titulo;
    private final LocalDate fechaPrestamo;
    private final LocalDate fechaDevolucion;  // null si aún no devuelto
    private final double multa;                // 0 si no hay multa

    /** 
     * Constructor para préstamos activos (sin fechaDevolucion ni multa). 
     */
    public Prestamo(int idPrestamo,
                    String codigoUsuario,
                    String isbn,
                    String titulo,
                    LocalDate fechaPrestamo) {
        this(idPrestamo, codigoUsuario, isbn, titulo, fechaPrestamo, null, 0.0);
    }

    /**
     * Constructor completo para historial (con fechaDevolucion y multa).
     */
    public Prestamo(int idPrestamo,
                    String codigoUsuario,
                    String isbn,
                    String titulo,
                    LocalDate fechaPrestamo,
                    LocalDate fechaDevolucion,
                    double multa) {
        this.idPrestamo     = idPrestamo;
        this.codigoUsuario  = codigoUsuario;
        this.isbn           = isbn;
        this.titulo         = titulo;
        this.fechaPrestamo  = fechaPrestamo;
        this.fechaDevolucion= fechaDevolucion;
        this.multa          = multa;
    }

    // Getters
    public int getIdPrestamo()           { return idPrestamo; }
    public String getCodigoUsuario()     { return codigoUsuario; }
    public String getIsbn()              { return isbn; }
    public String getTitulo()            { return titulo; }
    public LocalDate getFechaPrestamo()  { return fechaPrestamo; }
    public LocalDate getFechaDevolucion(){ return fechaDevolucion; }
    public double getMulta()             { return multa; }
}
