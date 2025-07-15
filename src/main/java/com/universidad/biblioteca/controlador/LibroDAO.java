package com.universidad.biblioteca.controlador;

import com.universidad.biblioteca.modelo.Libro;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {

    private static final String INSERT_LIBRO = "INSERT INTO Libro (titulo, autor, anioPublicacion, disponible) VALUES (?, ?, ?, ?)";
    private static final String SELECT_ALL_LIBROS = "SELECT * FROM Libro";
    private static final String SELECT_LIBRO_BY_ID = "SELECT * FROM Libro WHERE isbn = ?";
    private static final String UPDATE_LIBRO = "UPDATE Libro SET titulo = ?, autor = ?, anioPublicacion = ? WHERE isbn = ?";
    private static final String DELETE_LIBRO = "DELETE FROM Libro WHERE isbn = ?";
    private static final String SEARCH_LIBROS = "SELECT * FROM Libro WHERE titulo LIKE ? OR autor LIKE ?";

    private static final String COL_ISBN = "isbn";
    private static final String COL_TITULO = "titulo";
    private static final String COL_AUTOR = "autor";
    private static final String COL_CATEGORIA = "categoria";
    private static final String COL_EDITORIAL = "editorial";
    private static final String COL_ANIO = "anioPublicacion";
    private static final String COL_DISPONIBLES = "disponibles";

    private final Connection conexion;

    public LibroDAO(Connection conexion) {
        this.conexion = conexion;
    }

    public List<Libro> obtenerTodosLosLibros() throws SQLException {
        List<Libro> libros = new ArrayList<>();
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_LIBROS)) {
            while (rs.next()) {
                libros.add(mapResultSetToLibro(rs));
            }
        }
        return libros;
    }

    public Libro obtenerPorId(int isbn) throws SQLException {
        try (PreparedStatement stmt = conexion.prepareStatement(SELECT_LIBRO_BY_ID)) {
            stmt.setInt(1, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLibro(rs);
                }
            }
        }
        return null;
    }

    public void insertar(Libro libro) throws SQLException {
        try (PreparedStatement stmt = conexion.prepareStatement(INSERT_LIBRO, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, libro.getTitulo());
            stmt.setString(2, libro.getAutor());
            stmt.setInt(3, libro.getAnioPublicacion());
            stmt.setBoolean(4, libro.isDisponible());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    libro.setIsbn(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void actualizar(Libro libro) throws SQLException {
        try (PreparedStatement stmt = conexion.prepareStatement(UPDATE_LIBRO)) {
            stmt.setString(1, libro.getTitulo());
            stmt.setString(2, libro.getAutor());
            stmt.setInt(3, libro.getAnioPublicacion());
            stmt.setInt(4, libro.getIsbn());
            stmt.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        try (PreparedStatement stmt = conexion.prepareStatement(DELETE_LIBRO)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Libro> buscarLibros(String termino) throws SQLException {
        List<Libro> libros = new ArrayList<>();
        try (PreparedStatement stmt = conexion.prepareStatement(SEARCH_LIBROS)) {
            String likeTerm = "%" + termino + "%";
            stmt.setString(1, likeTerm);
            stmt.setString(2, likeTerm);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    libros.add(mapResultSetToLibro(rs));
                }
            }
        }
        return libros;
    }

    private Libro mapResultSetToLibro(ResultSet rs) throws SQLException {
        Libro libro = new Libro();
        libro.setIsbn(rs.getInt(COL_ISBN));
        libro.setTitulo(rs.getString(COL_TITULO));
        libro.setAutor(rs.getString(COL_AUTOR));
        libro.setCategoria(rs.getString(COL_CATEGORIA));
        libro.setEditorial(rs.getString(COL_EDITORIAL));
        libro.setAnioPublicacion(rs.getInt(COL_ANIO));
        libro.setDisponible(rs.getInt(COL_DISPONIBLES) > 0);
        return libro;
    }
}
