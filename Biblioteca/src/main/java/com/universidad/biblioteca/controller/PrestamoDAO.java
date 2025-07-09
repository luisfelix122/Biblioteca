package com.universidad.biblioteca.controller;

import com.universidad.biblioteca.model.Libro;
import com.universidad.biblioteca.model.Prestamo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {

    private final Connection conexion;

    public PrestamoDAO() {
        this.conexion = ConexionBD.obtenerConexion();
    }

    public List<Prestamo> obtenerPrestamosPorUsuario(String codigoUsuario) throws SQLException {
        List<Prestamo> lista = new ArrayList<>();
        String sql = "SELECT p.*, l.* FROM prestamos p JOIN libros l ON p.id_libro = l.id WHERE p.codigo_usuario = ? AND p.fecha_devolucion IS NULL";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, codigoUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearPrestamo(rs));
                }
            }
        }

        return lista;
    }

    public List<Prestamo> obtenerHistorialPorUsuario(String codigoUsuario) throws SQLException {
        List<Prestamo> lista = new ArrayList<>();
        String sql = "SELECT p.*, l.* FROM prestamos p JOIN libros l ON p.id_libro = l.id WHERE p.codigo_usuario = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, codigoUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearPrestamo(rs));
                }
            }
        }

        return lista;
    }

    public boolean crearPrestamo(String codigoUsuario, int idLibro) throws SQLException {
        String sql = "INSERT INTO prestamos (codigo_usuario, id_libro, fecha_prestamo, estado, multa) VALUES (?, ?, GETDATE(), 'Activo', 0)";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, codigoUsuario);
            stmt.setInt(2, idLibro);

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                actualizarDisponibilidadLibro(idLibro, false);
                return true;
            }
        }

        return false;
    }

    public boolean devolverLibro(int idPrestamo) throws SQLException {
        String sql = "UPDATE prestamos SET fecha_devolucion = GETDATE(), estado = 'Devuelto' WHERE id = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idPrestamo);

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                // Recuperar el libro del préstamo
                int idLibro = obtenerIdLibroPorPrestamo(idPrestamo);
                if (idLibro != -1) {
                    actualizarDisponibilidadLibro(idLibro, true);
                }
                return true;
            }
        }

        return false;
    }

    private int obtenerIdLibroPorPrestamo(int idPrestamo) throws SQLException {
        String sql = "SELECT id_libro FROM prestamos WHERE id = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idPrestamo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_libro");
                }
            }
        }
        return -1;
    }

    private void actualizarDisponibilidadLibro(int idLibro, boolean disponible) throws SQLException {
        String sql = "UPDATE libros SET disponible = ? WHERE id = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setBoolean(1, disponible);
            stmt.setInt(2, idLibro);
            stmt.executeUpdate();
        }
    }

    private Prestamo mapearPrestamo(ResultSet rs) throws SQLException {
        Libro libro = new Libro(
                rs.getInt("id_libro"),
                rs.getString("titulo"),
                rs.getString("autor"),
                rs.getInt("anio_publicacion"),
                rs.getBoolean("disponible")
        );

        Prestamo prestamo = new Prestamo();
        prestamo.setId(rs.getInt("id"));
        prestamo.setLibro(libro);
        prestamo.setFechaPrestamo(rs.getDate("fecha_prestamo"));
        prestamo.setFechaDevolucion(rs.getDate("fecha_devolucion"));
        prestamo.setMulta(rs.getDouble("multa"));
        prestamo.setEstado(rs.getString("estado"));

        return prestamo;
    }
}
