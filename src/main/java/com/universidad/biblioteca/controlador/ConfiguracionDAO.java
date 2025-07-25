package com.universidad.biblioteca.controlador;

import com.universidad.biblioteca.config.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ConfiguracionDAO {

    public Map<String, String> cargarConfiguracion() throws SQLException {
        Map<String, String> config = new HashMap<>();
        String sql = "SELECT clave, valor FROM ConfiguracionSistema";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                config.put(rs.getString("clave"), rs.getString("valor"));
            }
        }
        return config;
    }

    public void guardarConfiguracion(Map<String, String> config) throws SQLException {
        String sql = "UPDATE ConfiguracionSistema SET valor = ? WHERE clave = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Map.Entry<String, String> entry : config.entrySet()) {
                stmt.setString(1, entry.getValue());
                stmt.setString(2, entry.getKey());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }
}