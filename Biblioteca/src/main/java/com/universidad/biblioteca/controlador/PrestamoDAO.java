package com.universidad.biblioteca.controlador;

import com.universidad.biblioteca.modelo.Libro;
import com.universidad.biblioteca.modelo.Prestamo;
import com.universidad.biblioteca.modelo.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {

    private static final String SELECT_ALL_PRESTAMOS = "SELECT * FROM Prestamo";
    private static final String SELECT_PRESTAMO_BY_ID = "SELECT * FROM Prestamo WHERE idPrestamo = ?";
    private static final String INSERT_PRESTAMO = "INSERT INTO Prestamo (codigoUsuario, isbn, fechaPrestamo, fechaDevolucion, multa, devuelto) VALUES (?, ?, ?, ?, ?, 0)";
    private static final String UPDATE_DEVOLUCION = "UPDATE Prestamo SET devuelto = 1, multa = ? WHERE idPrestamo = ?";
    private static final String SELECT_PRESTAMOS_BY_USUARIO = "SELECT * FROM Prestamo WHERE codigoUsuario = ?";

    private static final String COL_ID_PRESTAMO = "idPrestamo";
    private static final String COL_FECHA_PRESTAMO = "fechaPrestamo";
    private static final String COL_FECHA_DEVOLUCION = "fechaDevolucion";
    private static final String COL_MULTA = "multa";
    private static final String COL_DEVUELTO = "devuelto";
    private static final String COL_ISBN = "isbn";
    private static final String COL_CODIGO_USUARIO = "codigoUsuario";

    private final Connection conexion;
    private final LibroDAO libroDAO;
    private final UsuarioDAO usuarioDAO;

    public PrestamoDAO(Connection conexion) {
        this.conexion = conexion;
        this.libroDAO = new LibroDAO(conexion);
        this.usuarioDAO = new UsuarioDAO(conexion);
    }

    public List<Prestamo> obtenerTodos() throws SQLException {
        List<Prestamo> prestamos = new ArrayList<>();
        try (PreparedStatement stmt = conexion.prepareStatement(SELECT_ALL_PRESTAMOS);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                prestamos.add(construirPrestamo(rs));
            }
        }
        return prestamos;
    }

    public Prestamo obtenerPrestamoPorId(int idPrestamo) throws SQLException {
        try (PreparedStatement stmt = conexion.prepareStatement(SELECT_PRESTAMO_BY_ID)) {
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
        try (PreparedStatement stmt = conexion.prepareStatement(INSERT_PRESTAMO)) {
            stmt.setString(1, prestamo.getUsuario().getCodigo());
            stmt.setInt(2, prestamo.getLibro().getId());
            stmt.setTimestamp(3, new java.sql.Timestamp(prestamo.getFechaPrestamo().getTime()));
            stmt.setTimestamp(4, new java.sql.Timestamp(prestamo.getFechaDevolucion().getTime()));
            stmt.setDouble(5, prestamo.getMulta());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean marcarComoDevuelto(int idPrestamo, double multa) throws SQLException {
        try (PreparedStatement stmt = conexion.prepareStatement(UPDATE_DEVOLUCION)) {
            stmt.setDouble(1, multa);
            stmt.setInt(2, idPrestamo);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Prestamo> obtenerPrestamosPorUsuario(String codigoUsuario) throws SQLException {
        List<Prestamo> prestamos = new ArrayList<>();
        try (PreparedStatement stmt = conexion.prepareStatement(SELECT_PRESTAMOS_BY_USUARIO)) {
            stmt.setString(1, codigoUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    prestamos.add(construirPrestamo(rs));
                }
            }
        }
        return prestamos;
    }

    private Prestamo construirPrestamo(ResultSet rs) throws SQLException {
        Prestamo prestamo = new Prestamo();
        prestamo.setId(rs.getInt(COL_ID_PRESTAMO));
        prestamo.setFechaPrestamo(rs.getTimestamp(COL_FECHA_PRESTAMO));
        prestamo.setFechaDevolucion(rs.getTimestamp(COL_FECHA_DEVOLUCION));
        prestamo.setMulta(rs.getDouble(COL_MULTA));
        prestamo.setDevuelto(rs.getBoolean(COL_DEVUELTO));

        Libro libro = libroDAO.obtenerPorId(rs.getInt(COL_ISBN));
        prestamo.setLibro(libro);

        Usuario usuario = usuarioDAO.obtenerPorCodigo(rs.getString(COL_CODIGO_USUARIO));
        prestamo.setUsuario(usuario);

        return prestamo;
    }
}
