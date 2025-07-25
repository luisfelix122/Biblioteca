package com.universidad.biblioteca.controlador.auth;

import com.universidad.biblioteca.vista.auth.LoginView;
import com.universidad.biblioteca.controlador.UsuarioDAO;
import com.universidad.biblioteca.modelo.Usuario;
import java.sql.SQLException;
import com.universidad.biblioteca.vista.main.MainView;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginController implements ActionListener {

    private LoginView view;
    private UsuarioDAO usuarioDAO;

    public LoginController(LoginView view) {
        this.view = view;
        this.usuarioDAO = new UsuarioDAO();
        this.view.getBtnLogin().addActionListener(this);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.getBtnLogin()) {
        String username = view.getUsernameField().getText();
        String password = new String(view.getPasswordField().getPassword());
        String hashedPassword = com.universidad.biblioteca.utils.PasswordHasher.hashPassword(password);
        System.out.println("LoginController: Attempting login for username: " + username + " with hashed password: " + hashedPassword);
        Usuario usuario = null;
         try {
             usuario = usuarioDAO.verificarCredenciales(username, hashedPassword);
         } catch (SQLException ex) {
             ex.printStackTrace();
             // Optionally, show an error message to the user
             view.mostrarMensaje("Error de base de datos: " + ex.getMessage());
         }
        if (usuario != null) {
            System.out.println("Authentication successful for user: " + usuario.getUsername());
            view.dispose(); // Close the login window
            MainView mainView = new MainView(usuario);
            mainView.setVisible(true);
        } else {
            System.out.println("Authentication failed for username: " + username);
            JOptionPane.showMessageDialog(view, "Usuario o contraseña incorrectos", "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
        }
    }
}
}
