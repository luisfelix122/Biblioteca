package com.universidad.biblioteca.controlador;

import com.universidad.biblioteca.modelo.Usuario;
import com.universidad.biblioteca.vista.main.MainView;
import com.universidad.biblioteca.vista.auth.LoginView;
import com.universidad.biblioteca.controlador.auth.RegisterController;
import com.universidad.biblioteca.vista.auth.RegisterView;

import java.sql.SQLException;

public class LoginController {

    private final LoginView view;
    private final UsuarioDAO usuarioDAO;

    public LoginController(LoginView view, UsuarioDAO usuarioDAO) {
        this.view = view;
        this.usuarioDAO = usuarioDAO;
        initController();
    }

    private void initController() {
        view.getBtnLogin().addActionListener(e -> login());
        view.getBtnRegister().addActionListener(e -> register());
    }

    private void login() {
        String codigo = view.getCodigo();
        String pass = view.getPassword();

        if (codigo.isEmpty() || pass.isEmpty()) {
            view.showMessage("Por favor, complete todos los campos");
            return;
        }

        try {
            Usuario u = usuarioDAO.verificarCredenciales(codigo, pass);

            if (u != null) {
                view.showMessage("Login exitoso");
                view.dispose();
                new MainView(u).setVisible(true);
            } else {
                view.showMessage("Credenciales incorrectas");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            view.showMessage("Error al verificar credenciales: " + e.getMessage());
        }
    }

    private void register() {
        RegisterView registerView = new RegisterView();
        new RegisterController(registerView, usuarioDAO);
        registerView.setVisible(true);
    }
}
