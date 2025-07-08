package com.universidad.biblioteca.controller;

import com.universidad.biblioteca.model.Usuario;
import com.universidad.biblioteca.model.Role;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class UsuarioDAO {

    private static final String URL
            = "jdbc:sqlserver://localhost:1433;"
            + "databaseName=BibliotecaDB;"
            + "encrypt=true;"
            + "trustServerCertificate=true;";

    private static final String USER = "sa";
    private static final String PASS = "Informatica1!";

    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    /**
     * Busca un Usuario en la base por su código.
     *
     * @param codigo El código universitario.
     * @return Usuario si existe; null si no.
     * @throws SQLException si hay error en la consulta.
     */
    /**
     * Devuelve el usuario por su código.
     */
    public Usuario obtenerPorCodigo(String codigo) throws SQLException {
        String sql = "SELECT codigo, contrasena, nombre, email, telefono, rol "
                + "FROM dbo.Usuario WHERE codigo = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getString("codigo"),
                            rs.getString("contrasena"),
                            rs.getString("nombre"),
                            rs.getString("email"),
                            rs.getString("telefono"),
                            rs.getString("rol")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Actualiza nombre, email y teléfono de un usuario.
     */
    public void actualizarPerfil(Usuario u) throws SQLException {
        String sql = "UPDATE dbo.Usuario SET nombre = ?, email = ?, telefono = ? "
                + "WHERE codigo = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getTelefono());
            ps.setString(4, u.getCodigo());
            ps.executeUpdate();
        }
    }

}
