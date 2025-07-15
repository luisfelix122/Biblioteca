package com.universidad.biblioteca.controlador;

import com.universidad.biblioteca.modelo.Usuario;
import java.sql.*;

public class UsuarioDAO {
    private Connection conexion;

    public UsuarioDAO(Connection conexion) {
        this.conexion = conexion;
    }

    public Usuario verificarCredenciales(String codigo, String contrasena) throws SQLException {
        String sql = "SELECT * FROM Usuario WHERE codigo = ? AND contrasena = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, codigo);
            stmt.setString(2, contrasena);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setCodigo(rs.getString("codigo"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setCorreo(rs.getString("email"));
                usuario.setTelefono(rs.getString("telefono"));
                usuario.setContrasena(rs.getString("contrasena"));
                usuario.setRol(rs.getString("rol"));
                usuario.setFechaRegistro(rs.getDate("fechaRegistro"));
                return usuario;
            }
        }
        return null;
    }

    public Usuario obtenerPorCodigo(String codigo) throws SQLException {
        String sql = "SELECT * FROM Usuario WHERE codigo = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, codigo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setCodigo(rs.getString("codigo"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setCorreo(rs.getString("email"));
                    usuario.setTelefono(rs.getString("telefono"));
                    usuario.setContrasena(rs.getString("contrasena"));
                    return usuario;
                }
            }
        }
        return null;
    }

    public Usuario buscarPorUsername(String username) throws SQLException {
        String sql = "SELECT * FROM Usuario WHERE codigo = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setCodigo(rs.getString("codigo"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setCorreo(rs.getString("email"));
                usuario.setTelefono(rs.getString("telefono"));
                usuario.setContrasena(rs.getString("contrasena"));
                usuario.setRol(rs.getString("rol"));
                usuario.setFechaRegistro(rs.getDate("fechaRegistro"));
                return usuario;
            }
        }
        return null;
    }

    public boolean insertar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO Usuario (codigo, contrasena, rol, fechaRegistro) VALUES (?, ?, ?, NOW())";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, usuario.getCodigo());
            stmt.setString(2, usuario.getContrasena());
            stmt.setString(3, usuario.getRol());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean actualizarPerfil(Usuario usuario) {
        String sql = "UPDATE Usuario SET nombre = ?, email = ?, telefono = ?, contrasena = ? WHERE codigo = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getCorreo());
            stmt.setString(3, usuario.getTelefono());
            stmt.setString(4, usuario.getContrasena());
            stmt.setString(5, usuario.getCodigo());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
