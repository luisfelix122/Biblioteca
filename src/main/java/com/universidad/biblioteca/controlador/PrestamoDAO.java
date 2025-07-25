package com.universidad.biblioteca.controlador;

import com.universidad.biblioteca.config.ConexionBD;
import com.universidad.biblioteca.modelo.Libro;
import com.universidad.biblioteca.modelo.Prestamo;
import com.universidad.biblioteca.modelo.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {

    private static final String SELECT_ALL_PRESTAMOS = "SELECT p.*, u.nombre as nombreUsuario FROM Prestamo p JOIN Usuario u ON p.codigoUsuario = u.codigo";
    private static final String SELECT_PRESTAMO_BY_ID = "SELECT * FROM Prestamo WHERE idPrestamo = ?";
    private static final String INSERT_PRESTAMO = "INSERT INTO Prestamo (codigoUsuario, isbn, fechaPrestamo, fechaDevolucion, multa, devuelto) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_DEVOLUCION = "UPDATE Prestamo SET devuelto = 1, multa = ? WHERE idPrestamo = ?";
    private static final String SELECT_PRESTAMOS_BY_USUARIO = "SELECT * FROM Prestamo WHERE codigoUsuario = ?";

    private static final String COL_ID_PRESTAMO = "idPrestamo";
    private static final String COL_FECHA_PRESTAMO = "fechaPrestamo";
    private static final String COL_FECHA_DEVOLUCION = "fechaDevolucion";
    private static final String COL_MULTA = "multa";
    private static final String COL_DEVUELTO = "devuelto";

    private static final String COL_ISBN = "isbn";
     private static final String COL_CODIGO_USUARIO = "codigoUsuario";

    private final LibroDAO libroDAO;
    private final UsuarioDAO usuarioDAO;

    public PrestamoDAO() {
        this.libroDAO = new LibroDAO();
        this.usuarioDAO = new UsuarioDAO();
    }

    public List<Prestamo> obtenerTodosLosPrestamos() throws SQLException {
        List<Prestamo> prestamos = new ArrayList<>();
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_PRESTAMOS);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                prestamos.add(construirPrestamo(conn, rs));
            }
        }
        return prestamos;
    }

    public Prestamo obtenerPrestamoPorId(int idPrestamo) throws SQLException {
        try (Connection conn = ConexionBD.obtenerConexion()) {
            return obtenerPrestamoPorId(conn, idPrestamo);
        }
    }

    public Prestamo obtenerPrestamoPorId(Connection conn, int idPrestamo) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_PRESTAMO_BY_ID)) {
            stmt.setInt(1, idPrestamo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return construirPrestamo(conn, rs);
                }
            }
        }
        return null;
    }

    public boolean registrarPrestamo(Prestamo prestamo) throws SQLException {
        try (Connection conn = ConexionBD.obtenerConexion()) {
            return registrarPrestamo(conn, prestamo);
        }
    }

    public boolean registrarPrestamo(Connection conn, Prestamo prestamo) throws SQLException {
        // Registrar el préstamo
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_PRESTAMO)) {
            stmt.setString(1, prestamo.getUsuario().getCodigo());
            stmt.setString(2, prestamo.getLibro().getIsbn());
            stmt.setTimestamp(3, new java.sql.Timestamp(prestamo.getFechaPrestamo().getTime()));
            stmt.setTimestamp(4, new java.sql.Timestamp(prestamo.getFechaDevolucion().getTime()));
            stmt.setDouble(5, prestamo.getMulta());
            stmt.setBoolean(6, prestamo.isDevuelto());
            stmt.executeUpdate();
        }

        // Actualizar disponibilidad del libro
        Libro libro = prestamo.getLibro();
        libro.setDisponible(false);
        libroDAO.actualizar(conn, libro);
        return true;
    }

    public boolean marcarComoDevuelto(int idPrestamo, double multa) throws SQLException {
        try (Connection conn = ConexionBD.obtenerConexion()) {
            return marcarComoDevuelto(conn, idPrestamo, multa);
        }
    }

    public boolean marcarComoDevuelto(Connection conn, int idPrestamo, double multa) throws SQLException {
        // Obtener el préstamo para conocer el libro
        Prestamo prestamo = obtenerPrestamoPorId(conn, idPrestamo);
        if (prestamo == null) {
            return false;
        }

        // Marcar el préstamo como devuelto
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_DEVOLUCION)) {
            stmt.setDouble(1, multa);
            stmt.setInt(2, idPrestamo);
            stmt.executeUpdate();
        }

        // Actualizar disponibilidad del libro
        Libro libro = prestamo.getLibro();
        libro.setDisponible(true);
        libroDAO.actualizar(conn, libro);
        return true;
    }

    public List<Prestamo> obtenerPrestamosPorUsuario(String codigoUsuario) throws SQLException {
        try (Connection conn = ConexionBD.obtenerConexion()) {
            return obtenerPrestamosPorUsuario(conn, codigoUsuario);
        }
    }

    public List<Prestamo> obtenerPrestamosPorUsuario(Connection conn, String codigoUsuario) throws SQLException {
        List<Prestamo> prestamos = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_PRESTAMOS_BY_USUARIO)) {
            stmt.setString(1, codigoUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    prestamos.add(construirPrestamo(conn, rs));
                }
            }
        }
        return prestamos;
    }

    private Prestamo construirPrestamo(Connection conn, ResultSet rs) throws SQLException {
        Prestamo prestamo = new Prestamo();
        prestamo.setId(rs.getInt(COL_ID_PRESTAMO));
        prestamo.setFechaPrestamo(rs.getTimestamp(COL_FECHA_PRESTAMO));
        prestamo.setFechaDevolucion(rs.getTimestamp(COL_FECHA_DEVOLUCION));
        prestamo.setMulta(rs.getDouble(COL_MULTA));
        prestamo.setDevuelto(rs.getBoolean(COL_DEVUELTO));

        Libro libro = this.libroDAO.obtenerPorId(conn, rs.getString(COL_ISBN));
        prestamo.setLibro(libro);

        Usuario usuario = new Usuario();
        usuario.setCodigo(rs.getString(COL_CODIGO_USUARIO));
        if (rs.getMetaData().getColumnCount() > 7) { // Heurística para saber si viene el join
            usuario.setNombre(rs.getString("nombreUsuario"));
        } else {
            Usuario u = this.usuarioDAO.obtenerPorCodigo(rs.getString(COL_CODIGO_USUARIO));
            if (u != null) {
                usuario.setNombre(u.getNombre());
            }
        }
        prestamo.setUsuario(usuario);

        return prestamo;
    }
}
