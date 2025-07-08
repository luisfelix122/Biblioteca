package com.universidad.biblioteca.controller;

import com.universidad.biblioteca.model.Prestamo;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gestionar préstamos.
 */
public class PrestamoDAO {

    private static final String URL =
        "jdbc:sqlserver://localhost:1433;"
      + "databaseName=BibliotecaDB;"
      + "encrypt=false;"
      + "trustServerCertificate=true;";
    private static final String USER = "sa";               // Ajustar usuario
    private static final String PASS = "Informatica1!";    // Ajustar contraseña

    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /** Abre y devuelve una conexión a la base de datos. */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    /**
     * Inserta un nuevo préstamo.
     */
    public void registrarPrestamo(String codigoUsuario, String isbn) throws SQLException {
        String sql = "INSERT INTO dbo.Prestamo(codigoUsuario, isbn) VALUES(?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codigoUsuario);
            ps.setString(2, isbn);
            ps.executeUpdate();
        }
    }

    /**
     * Marca un préstamo como devuelto, fija fechaDevolucion y calcula multa.
     *
     * @param idPrestamo   el ID del préstamo
     * @param tarifaPorDia multa fija por día de retraso
     */
    public void registrarDevolucion(int idPrestamo, double tarifaPorDia) throws SQLException {
        String sql =
            "UPDATE dbo.Prestamo " +
            "SET fechaDevolucion = GETDATE(), " +
            "    multa = CASE " +
            "      WHEN DATEDIFF(day, fechaPrestamo, GETDATE()) > 15 " +
            "      THEN (DATEDIFF(day, fechaPrestamo, GETDATE()) - 15) * ? " +
            "      ELSE 0 END " +
            "WHERE idPrestamo = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, tarifaPorDia);
            ps.setInt(2, idPrestamo);
            ps.executeUpdate();
        }
    }

    /**
     * Devuelve los préstamos activos (fechaDevolucion IS NULL) de un usuario.
     */
    public List<Prestamo> listarPorUsuario(String codigoUsuario) throws SQLException {
        String sql =
            "SELECT p.idPrestamo, p.isbn, l.titulo, p.fechaPrestamo " +
            "FROM dbo.Prestamo p " +
            "JOIN dbo.Libro l ON p.isbn = l.isbn " +
            "WHERE p.codigoUsuario = ? AND p.fechaDevolucion IS NULL";
        List<Prestamo> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codigoUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Prestamo p = new Prestamo(
                        rs.getInt("idPrestamo"),
                        codigoUsuario,
                        rs.getString("isbn"),
                        rs.getString("titulo"),
                        rs.getTimestamp("fechaPrestamo")
                          .toLocalDateTime()
                          .toLocalDate()
                    );
                    lista.add(p);
                }
            }
        }
        return lista;
    }

    /**
     * Devuelve todos los préstamos (activos y devueltos) de un usuario.
     */
    public List<Prestamo> listarHistorialPorUsuario(String codigoUsuario) throws SQLException {
        String sql =
            "SELECT p.idPrestamo, p.isbn, l.titulo, p.fechaPrestamo, p.fechaDevolucion, p.multa " +
            "FROM dbo.Prestamo p " +
            "JOIN dbo.Libro l ON p.isbn = l.isbn " +
            "WHERE p.codigoUsuario = ?";
        List<Prestamo> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codigoUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Prestamo p = new Prestamo(
                        rs.getInt("idPrestamo"),
                        codigoUsuario,
                        rs.getString("isbn"),
                        rs.getString("titulo"),
                        rs.getTimestamp("fechaPrestamo")
                          .toLocalDateTime()
                          .toLocalDate(),
                        rs.getTimestamp("fechaDevolucion") != null
                          ? rs.getTimestamp("fechaDevolucion")
                                .toLocalDateTime()
                                .toLocalDate()
                          : null,
                        rs.getDouble("multa")
                    );
                    lista.add(p);
                }
            }
        }
        return lista;
    }
}
