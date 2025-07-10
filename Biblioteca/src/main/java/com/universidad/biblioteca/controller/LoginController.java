package com.universidad.biblioteca.controller;

import com.universidad.biblioteca.model.Usuario;
import java.sql.*;

public class LoginController {

    private Connection conexion;

    public LoginController(Connection conexion) {
        this.conexion = conexion;
    }

    // Login por código de usuario, no correo
    public Usuario verificarCredenciales(String codigo, String contrasena) {
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
                return usuario;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
