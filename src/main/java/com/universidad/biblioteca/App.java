package com.universidad.biblioteca;

import com.universidad.biblioteca.controlador.LoginController;
import com.universidad.biblioteca.controlador.UsuarioDAO;
import com.universidad.biblioteca.config.ConexionBD;
import com.universidad.biblioteca.vista.auth.LoginView;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Connection connection = null;
            try {
                connection = ConexionBD.obtenerConexion();
                LoginView loginView = new LoginView();
                UsuarioDAO usuarioDAO = new UsuarioDAO(connection);
                new LoginController(loginView, usuarioDAO);
                loginView.setVisible(true);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos: " + e.getMessage(), "Error de Conexión", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } finally {   
            }
        });
    }
}