package com.universidad.biblioteca.controller;

import com.universidad.biblioteca.model.Prestamo;
import com.universidad.biblioteca.model.Libro;
import com.universidad.biblioteca.model.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {
    private Connection conexion;

    public PrestamoDAO(Connection conexion) {
        this.conexion = conexion;
    }

    public List<Prestamo> obtenerPrestamosActivosPorUsuario(String codigoUsuario) throws SQLException {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT * FROM Prestamo WHERE codigoUsuario = ? AND devuelto = 0";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, codigoUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Prestamo prestamo = construirPrestamo(rs);
                    prestamos.add(prestamo);
                }
            }
        }
        return prestamos;
    }

    public List<Prestamo> obtenerHistorialPrestamosPorUsuario(String codigoUsuario) throws SQLException {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT * FROM Prestamo WHERE codigoUsuario = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, codigoUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Prestamo prestamo = construirPrestamo(rs);
                    prestamos.add(prestamo);
                }
            }
        }
        return prestamos;
    }

    public Prestamo obtenerPrestamoPorId(int idPrestamo) throws SQLException {
        String sql = "SELECT * FROM Prestamo WHERE idPrestamo = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idPrestamo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return construirPrestamo(rs);
                }
            }
        }
        return null;
    }

    public boolean registrarPrestamo(Prestamo prestamo) throws SQLException {
        String sql = "INSERT INTO Prestamo (codigoUsuario, isbn, fechaPrestamo, fechaDevolucion, multa, devuelto) VALUES (?, ?, ?, ?, ?, 0)";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, prestamo.getUsuario().getCodigo());
            stmt.setInt(2, prestamo.getLibro().getId());
            stmt.setTimestamp(3, new java.sql.Timestamp(prestamo.getFechaPrestamo().getTime()));
            stmt.setTimestamp(4, new java.sql.Timestamp(prestamo.getFechaDevolucion().getTime()));
            stmt.setDouble(5, prestamo.getMulta());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean marcarComoDevuelto(int idPrestamo, double multa) throws SQLException {
        String sql = "UPDATE Prestamo SET devuelto = 1, multa = ? WHERE idPrestamo = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDouble(1, multa);
            stmt.setInt(2, idPrestamo);
            return stmt.executeUpdate() > 0;
        }
    }

    private Prestamo construirPrestamo(ResultSet rs) throws SQLException {
        Prestamo prestamo = new Prestamo();
        prestamo.setId(rs.getInt("idPrestamo"));
        prestamo.setFechaPrestamo(rs.getTimestamp("fechaPrestamo"));
        prestamo.setFechaDevolucion(rs.getTimestamp("fechaDevolucion"));
        prestamo.setMulta(rs.getDouble("multa"));

        LibroDAO libroDAO = new LibroDAO(this.conexion);
        // La columna es 'isbn'
        Libro libro = libroDAO.obtenerPorId(rs.getInt("isbn"));
        prestamo.setLibro(libro);

        Usuario usuario = new Usuario();
        usuario.setCodigo(rs.getString("codigoUsuario"));
        prestamo.setUsuario(usuario);

        return prestamo;
    }
}
