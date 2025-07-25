package com.universidad.biblioteca.controlador;

import com.universidad.biblioteca.config.ConexionBD;
import com.universidad.biblioteca.modelo.Sugerencia;
import com.universidad.biblioteca.modelo.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SugerenciaDAO {

    private static final String INSERT_SUGERENCIA = "INSERT INTO Sugerencia (codigoUsuario, titulo, descripcion, fechaSugerencia) VALUES (?, ?, ?, ?)";
    private static final String SELECT_ALL_SUGERENCIAS = "SELECT s.*, u.nombre as nombreUsuario FROM Sugerencia s JOIN Usuario u ON s.codigoUsuario = u.codigo ORDER BY s.fechaSugerencia DESC";

    private static final String COL_ID_SUGERENCIA = "idSugerencia";
    private static final String COL_CODIGO_USUARIO = "codigoUsuario";
    private static final String COL_TITULO = "titulo";
    private static final String COL_DESCRIPCION = "descripcion";
    private static final String COL_FECHA_SUGERENCIA = "fechaSugerencia";

    private final UsuarioDAO usuarioDAO;

    public SugerenciaDAO() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public boolean agregarSugerencia(Sugerencia sugerencia) throws SQLException {
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SUGERENCIA)) {
            stmt.setString(1, sugerencia.getUsuario().getCodigo());
            stmt.setString(2, sugerencia.getTitulo());
            stmt.setString(3, sugerencia.getDescripcion());
            stmt.setTimestamp(4, new Timestamp(sugerencia.getFechaSugerencia().getTime()));
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public List<Sugerencia> obtenerTodasLasSugerencias() throws SQLException {
        List<Sugerencia> sugerencias = new ArrayList<>();
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SUGERENCIAS);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                sugerencias.add(construirSugerencia(conn, rs));
            }
        }
        return sugerencias;
    }

    private Sugerencia construirSugerencia(Connection conn, ResultSet rs) throws SQLException {
        Sugerencia sugerencia = new Sugerencia();
        sugerencia.setId(rs.getInt(COL_ID_SUGERENCIA));
        sugerencia.setTitulo(rs.getString(COL_TITULO));
        sugerencia.setDescripcion(rs.getString(COL_DESCRIPCION));
        sugerencia.setFechaSugerencia(rs.getTimestamp(COL_FECHA_SUGERENCIA));

        Usuario usuario = new Usuario();
        usuario.setCodigo(rs.getString(COL_CODIGO_USUARIO));
        // Check if user name is available from join, otherwise fetch it
        try {
            rs.findColumn("nombreUsuario"); // Check if column exists
            usuario.setNombre(rs.getString("nombreUsuario"));
        } catch (SQLException e) {
            // Column not found, fetch user details
            Usuario u = usuarioDAO.obtenerPorCodigo(rs.getString(COL_CODIGO_USUARIO));
            if (u != null) {
                usuario.setNombre(u.getNombre());
            }
        }
        sugerencia.setUsuario(usuario);

        return sugerencia;
    }
}