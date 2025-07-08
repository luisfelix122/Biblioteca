package com.universidad.biblioteca.controller;

import com.universidad.biblioteca.model.Usuario;
import com.universidad.biblioteca.view.LoginView;
import com.universidad.biblioteca.view.MainView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class LoginController {
    private final LoginView view;
    private final UsuarioDAO usuarioDAO;

    public LoginController(LoginView view) {
        this.view = view;
        this.usuarioDAO = new UsuarioDAO();
        this.view.getBtnLogin().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
    }

    private void login() {
        String codigo = view.getCodigo();
        String pass   = view.getPassword();
        try {
            Usuario user = usuarioDAO.obtenerPorCodigo(codigo);
            if (user == null) {
                view.showMessage("Usuario no encontrado.");
            } else if (!user.getPassword().equals(pass)) {
                view.showMessage("Contraseña incorrecta.");
            } else {
                view.showMessage("¡Bienvenido, " + user.getNombre() + "!");
                view.dispose();
                // Abrir MainView pasándole el código del usuario
                new MainView(user.getCodigo()).setVisible(true);
            }
        } catch (SQLException ex) {
            view.showMessage("Error al conectarse: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
