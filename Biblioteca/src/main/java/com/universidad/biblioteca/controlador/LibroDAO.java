package com.universidad.biblioteca.controlador;

import com.universidad.biblioteca.modelo.Libro;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {
    private Connection conexion;

    public LibroDAO(Connection conexion) {
        this.conexion = conexion;
    }

    // Obtener todos los libros
    public List<Libro> obtenerTodosLosLibros() throws SQLException {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM Libro";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Libro libro = new Libro();
                libro.setId(rs.getInt("isbn"));  // Cambiado de "id" a "isbn"
                libro.setTitulo(rs.getString("titulo"));
                libro.setAutor(rs.getString("autor"));
                libro.setCategoria(rs.getString("categoria"));
                libro.setEditorial(rs.getString("editorial"));
                libro.setAnioPublicacion(rs.getInt("anioPublicacion"));
                libro.setDisponible(rs.getInt("disponibles") > 0);
                libros.add(libro);
            }
        }
        return libros;
    }

    // Obtener libro por isbn
    public Libro obtenerPorId(int isbn) throws SQLException {
        String sql = "SELECT * FROM Libro WHERE isbn = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Libro libro = new Libro();
                    libro.setId(rs.getInt("isbn")); // Cambiado de "id" a "isbn"
                    libro.setTitulo(rs.getString("titulo"));
                    libro.setAutor(rs.getString("autor"));
                    libro.setCategoria(rs.getString("categoria"));
                    libro.setEditorial(rs.getString("editorial"));
                    libro.setAnioPublicacion(rs.getInt("anioPublicacion"));
                    libro.setDisponible(rs.getInt("disponibles") > 0);
                    return libro;
                }
            }
        }
        return null;
    }

    // Actualizar disponibilidad del libro
    public boolean actualizar(Libro libro) throws SQLException {
        String sql = "UPDATE Libro SET disponible = ? WHERE isbn = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, libro.isDisponible() ? 1 : 0);
            stmt.setInt(2, libro.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    // Buscar libros por título o autor
    public List<Libro> buscarLibros(String termino) throws SQLException {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM Libro WHERE titulo LIKE ? OR autor LIKE ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            String likeTerm = "%" + termino + "%";
            stmt.setString(1, likeTerm);
            stmt.setString(2, likeTerm);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Libro libro = new Libro();
                    libro.setId(rs.getInt("isbn")); // Cambiado de "id" a "isbn"
                    libro.setTitulo(rs.getString("titulo"));
                    libro.setAutor(rs.getString("autor"));
                    libro.setCategoria(rs.getString("categoria"));
                    libro.setEditorial(rs.getString("editorial"));
                    libro.setAnioPublicacion(rs.getInt("anioPublicacion"));
                    libro.setDisponible(rs.getInt("disponibles") > 0);
                    libros.add(libro);
                }
            }
        }
        return libros;
    }
}
