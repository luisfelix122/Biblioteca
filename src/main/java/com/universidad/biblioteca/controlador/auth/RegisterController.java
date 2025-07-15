package com.universidad.biblioteca.controlador.auth;

import com.universidad.biblioteca.controlador.UsuarioDAO;
import com.universidad.biblioteca.modelo.Usuario;
import com.universidad.biblioteca.vista.auth.RegisterView;

import javax.swing.*;
import java.sql.SQLException;

public class RegisterController {

    private final RegisterView view;
    private final UsuarioDAO usuarioDAO;

    public RegisterController(RegisterView view, UsuarioDAO usuarioDAO) {
        this.view = view;
        this.usuarioDAO = usuarioDAO;
        initController();
    }

    private void initController() {
        view.getBtnRegister().addActionListener(e -> registerUser());
    }

    private void registerUser() {
        String username = view.getTxtUsuario().getText();
        String password = new String(view.getTxtPassword().getPassword());
        String confirmPassword = new String(view.getTxtConfirmPassword().getPassword());

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || username.equals("Usuario") || password.equals("Contraseña") || confirmPassword.equals("Confirmar Contraseña")) {
            JOptionPane.showMessageDialog(view, "Por favor, complete todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(view, "Las contraseñas no coinciden.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (usuarioDAO.buscarPorUsername(username) != null) {
                JOptionPane.showMessageDialog(view, "El nombre de usuario ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Usuario newUser = new Usuario();
            newUser.setCodigo(username);
            newUser.setContrasena(password);
            newUser.setRol("ESTUDIANTE"); // Rol por defecto

            if (usuarioDAO.insertar(newUser)) {
                JOptionPane.showMessageDialog(view, "Usuario registrado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                view.dispose();
            } else {
                JOptionPane.showMessageDialog(view, "No se pudo registrar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Error en la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}