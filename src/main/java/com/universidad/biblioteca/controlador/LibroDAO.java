package com.universidad.biblioteca.controlador;
import com.universidad.biblioteca.config.ConexionBD;
import com.universidad.biblioteca.modelo.Libro;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {

    private static final String INSERT_LIBRO = "INSERT INTO Libro (isbn, titulo, autor, anioPublicacion, disponible) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_ALL_LIBROS = "SELECT * FROM Libro";
    private static final String SELECT_LIBRO_BY_ID = "SELECT * FROM Libro WHERE isbn = ?";
    private static final String UPDATE_LIBRO = "UPDATE Libro SET titulo = ?, autor = ?, anioPublicacion = ?, disponible = ? WHERE isbn = ?";
    private static final String DELETE_LIBRO = "DELETE FROM Libro WHERE isbn = ?";
    private static final String SEARCH_LIBROS = "SELECT * FROM Libro WHERE titulo LIKE ? OR autor LIKE ?";

    private static final String COL_ISBN = "isbn";
    private static final String COL_TITULO = "titulo";
    private static final String COL_AUTOR = "autor";

    private static final String COL_ANIO = "anioPublicacion";
    private static final String COL_DISPONIBLE = "disponible";



    public List<Libro> obtenerTodosLosLibros() throws SQLException {
        List<Libro> libros = new ArrayList<>();
        try (Connection conn = ConexionBD.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_LIBROS)) {
            while (rs.next()) {
                libros.add(mapResultSetToLibro(rs));
            }
        }
        return libros;
    }

    public void actualizar(Libro libro) throws SQLException {
        try (Connection conn = ConexionBD.obtenerConexion()) {
            actualizar(conn, libro);
        }
    }

    public void actualizar(Connection conn, Libro libro) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_LIBRO)) {
            stmt.setString(1, libro.getTitulo());
            stmt.setString(2, libro.getAutor());
            stmt.setInt(3, libro.getAnioPublicacion());
            stmt.setBoolean(4, libro.isDisponible());
            stmt.setString(5, libro.getIsbn());
            stmt.executeUpdate();
        }
    }

    public Libro obtenerPorId(String isbn) throws SQLException {
        try (Connection conn = ConexionBD.obtenerConexion()) {
            return obtenerPorId(conn, isbn);
        }
    }

    public Libro obtenerPorId(Connection conn, String isbn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_LIBRO_BY_ID)) {
            stmt.setString(1, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLibro(rs);
                }
            }
        }
        return null;
    }

    public void insertar(Libro libro) throws SQLException {
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(INSERT_LIBRO)) {
            stmt.setString(1, libro.getIsbn());
            stmt.setString(2, libro.getTitulo());
            stmt.setString(3, libro.getAutor());
            stmt.setInt(4, libro.getAnioPublicacion());
            stmt.setBoolean(5, libro.isDisponible());
            stmt.executeUpdate();
        }
    }

    public void eliminar(String isbn) throws SQLException {
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(DELETE_LIBRO)) {
            stmt.setString(1, isbn);
            stmt.executeUpdate();
        }
    }

    public List<Libro> buscarLibros(String titulo, String autor) throws SQLException {
        List<Libro> libros = new ArrayList<>();
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(SEARCH_LIBROS)) {
            stmt.setString(1, "%" + titulo + "%");
            stmt.setString(2, "%" + autor + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    libros.add(mapResultSetToLibro(rs));
                }
            }
        }
        return libros;
    }

    public int contarLibros(String titulo, String autor, String disponibilidad) throws SQLException {
        try (Connection conn = ConexionBD.obtenerConexion()) {
            return contarLibros(conn, titulo, autor, disponibilidad);
        }
    }

    public int contarLibros(Connection conn, String titulo, String autor, String disponibilidad) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Libro WHERE 1=1");
        if (titulo != null && !titulo.isEmpty()) {
            sql.append(" AND titulo LIKE ?");
        }
        if (autor != null && !autor.isEmpty()) {
            sql.append(" AND autor LIKE ?");
        }
        if (disponibilidad != null && !disponibilidad.isEmpty() && !disponibilidad.equals("Todos")) {
            sql.append(" AND disponible = ?");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int i = 1;
            if (titulo != null && !titulo.isEmpty()) {
                stmt.setString(i++, "%" + titulo + "%");
            }
            if (autor != null && !autor.isEmpty()) {
                stmt.setString(i++, "%" + autor + "%");
            }
            if (disponibilidad != null && !disponibilidad.isEmpty() && !disponibilidad.equals("Todos")) {
                stmt.setBoolean(i++, disponibilidad.equals("Disponibles"));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public List<Libro> buscarLibrosPaginado(String titulo, String autor, String disponibilidad, int offset, int limit) throws SQLException {
        try (Connection conn = ConexionBD.obtenerConexion()) {
            return buscarLibrosPaginado(conn, titulo, autor, disponibilidad, offset, limit);
        }
    }

    public List<Libro> buscarLibrosPaginado(Connection conn, String titulo, String autor, String disponibilidad, int offset, int limit) throws SQLException {
        List<Libro> libros = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM Libro WHERE 1=1");
        if (titulo != null && !titulo.isEmpty()) {
            sql.append(" AND titulo LIKE ?");
        }
        if (autor != null && !autor.isEmpty()) {
            sql.append(" AND autor LIKE ?");
        }
        if (disponibilidad != null && !disponibilidad.isEmpty() && !disponibilidad.equals("Todos")) {
            sql.append(" AND disponible = ?");
        }
        sql.append(" ORDER BY isbn OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            System.out.println("SQL Query: " + sql.toString()); // Debug print
            int i = 1;
            if (titulo != null && !titulo.isEmpty()) {
                stmt.setString(i++, "%" + titulo + "%");
                System.out.println("Param " + (i - 1) + ": titulo = " + titulo); // Debug print
            }
            if (autor != null && !autor.isEmpty()) {
                stmt.setString(i++, "%" + autor + "%");
                System.out.println("Param " + (i - 1) + ": autor = " + autor); // Debug print
            }
            if (disponibilidad != null && !disponibilidad.isEmpty() && !disponibilidad.equals("Todos")) {
                stmt.setBoolean(i++, disponibilidad.equals("Disponibles"));
                System.out.println("Param " + (i - 1) + ": disponibilidad = " + disponibilidad); // Debug print
            }
            stmt.setInt(i++, offset);
            System.out.println("Param " + (i - 1) + ": offset = " + offset); // Debug print
            stmt.setInt(i++, limit);
            System.out.println("Param " + (i - 1) + ": limit = " + limit); // Debug print

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
        libro.setIsbn(rs.getString(COL_ISBN));
        libro.setTitulo(rs.getString(COL_TITULO));
        libro.setAutor(rs.getString(COL_AUTOR));
        libro.setAnioPublicacion(rs.getInt(COL_ANIO));
        libro.setDisponible(rs.getBoolean(COL_DISPONIBLE));
        return libro;
    }
}
