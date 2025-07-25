package com.universidad.biblioteca.controlador;

import com.universidad.biblioteca.config.ConexionBD;
import com.universidad.biblioteca.modelo.Auditoria;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AuditoriaDAO {

    public List<Auditoria> obtenerTodosLosRegistros() throws SQLException {
        List<Auditoria> registros = new ArrayList<>();
        String sql = "SELECT * FROM Auditoria ORDER BY fecha DESC";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Auditoria registro = new Auditoria();
                registro.setId(rs.getInt("id"));
                registro.setIdUsuario(rs.getInt("idUsuario"));
                registro.setAccion(rs.getString("accion"));
                registro.setFecha(rs.getTimestamp("fecha"));
                registros.add(registro);
            }
        }
        return registros;
    }
}