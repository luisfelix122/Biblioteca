package com.universidad.biblioteca.controller;

import com.universidad.biblioteca.model.Usuario;

import java.sql.*;

public class UsuarioDAO {

    private final Connection conexion;

    public UsuarioDAO() {
        this.conexion = ConexionBD.obtenerConexion();
    }

    public Usuario obtenerUsuarioPorCodigo(String codigo) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE codigo = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, codigo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setCodigo(rs.getString("codigo"));
                    u.setNombre(rs.getString("nombre"));
                    u.setEmail(rs.getString("email"));
                    u.setTelefono(rs.getString("telefono"));
                    u.setPassword(rs.getString("password"));
                    return u;
                }
            }
        }

        return null;
    }
    
    // Método alias para compatibilidad con LoginController
    public Usuario obtenerPorCodigo(String codigo) throws SQLException {
        return obtenerUsuarioPorCodigo(codigo);
    }

    public boolean actualizarUsuario(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios SET nombre = ?, email = ?, telefono = ? WHERE codigo = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getTelefono());
            stmt.setString(4, usuario.getCodigo());

            return stmt.executeUpdate() > 0;
        }
    }
}
