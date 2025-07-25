package com.universidad.biblioteca.controlador;

import com.universidad.biblioteca.config.ConexionBD;
import com.universidad.biblioteca.modelo.Role;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RolDAO {

    public Role obtenerRolPorNombre(String nombre) throws SQLException {
        String sql = "SELECT * FROM Rol WHERE nombre = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Role rol = new Role();
                    rol.setId(rs.getInt("id"));
                    rol.setNombre(rs.getString("nombre"));
                    return rol;
                }
            }
        }
        return null;
    }
}