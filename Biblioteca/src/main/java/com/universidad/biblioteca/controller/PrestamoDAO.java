// 📦 Paquete controller
package com.universidad.biblioteca.controller;

import com.universidad.biblioteca.model.Prestamo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {

    private Connection conexion;

    public PrestamoDAO(Connection conexion) {
        this.conexion = conexion;
    }

    // Obtener todos los préstamos
    public List<Prestamo> obtenerTodos() {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT * FROM prestamos";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Prestamo prestamo = new Prestamo(
                    rs.getInt("id"),
                    rs.getInt("idLibro"),
                    rs.getInt("idUsuario"),
                    rs.getString("fechaPrestamo"),
                    rs.getString("fechaDevolucion"),
                    rs.getBoolean("devuelto")
                );
                prestamos.add(prestamo);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return prestamos;
    }

    // Insertar un nuevo préstamo
    public boolean insertar(Prestamo prestamo) {
        String sql = "INSERT INTO prestamos (idLibro, idUsuario, fechaPrestamo, fechaDevolucion, devuelto) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, prestamo.getIdLibro());
            stmt.setInt(2, prestamo.getIdUsuario());
            stmt.setString(3, prestamo.getFechaPrestamo());
            stmt.setString(4, prestamo.getFechaDevolucion());
            stmt.setBoolean(5, prestamo.isDevuelto());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Actualizar un préstamo
    public boolean actualizar(Prestamo prestamo) {
        String sql = "UPDATE prestamos SET idLibro = ?, idUsuario = ?, fechaPrestamo = ?, fechaDevolucion = ?, devuelto = ? WHERE id = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, prestamo.getIdLibro());
            stmt.setInt(2, prestamo.getIdUsuario());
            stmt.setString(3, prestamo.getFechaPrestamo());
            stmt.setString(4, prestamo.getFechaDevolucion());
            stmt.setBoolean(5, prestamo.isDevuelto());
            stmt.setInt(6, prestamo.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Eliminar un préstamo
    public boolean eliminar(int id) {
        String sql = "DELETE FROM prestamos WHERE id = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Obtener un préstamo por ID
    public Prestamo obtenerPorId(int id) {
        String sql = "SELECT * FROM prestamos WHERE id = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Prestamo(
                    rs.getInt("id"),
                    rs.getInt("idLibro"),
                    rs.getInt("idUsuario"),
                    rs.getString("fechaPrestamo"),
                    rs.getString("fechaDevolucion"),
                    rs.getBoolean("devuelto")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Marcar devolución
    public boolean marcarComoDevuelto(int id) {
        String sql = "UPDATE prestamos SET devuelto = 1 WHERE id = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
