package com.universidad.biblioteca.controller;

import com.universidad.biblioteca.model.Libro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {

    private final Connection conexion;

    public LibroDAO() {
        this.conexion = ConexionBD.obtenerConexion();
    }

    public List<Libro> obtenerTodosLosLibros() throws SQLException {
        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT * FROM libros";

        try (PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearLibro(rs));
            }
        }

        return lista;
    }

    public List<Libro> buscarLibros(String termino) throws SQLException {
        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT * FROM libros WHERE titulo LIKE ? OR autor LIKE ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            String busqueda = "%" + termino + "%";
            stmt.setString(1, busqueda);
            stmt.setString(2, busqueda);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearLibro(rs));
                }
            }
        }

        return lista;
    }

    public Libro obtenerLibroPorTitulo(String titulo) throws SQLException {
        String sql = "SELECT * FROM libros WHERE titulo = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, titulo);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearLibro(rs);
                }
            }
        }

        return null;
    }

    private Libro mapearLibro(ResultSet rs) throws SQLException {
        return new Libro(
            rs.getInt("id"),
            rs.getString("titulo"),
            rs.getString("autor"),
            rs.getInt("anio_publicacion"),
            rs.getBoolean("disponible")
        );
    }
}
