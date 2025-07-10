package com.universidad.biblioteca.controller;

import com.universidad.biblioteca.model.Usuario;
import com.universidad.biblioteca.view.MainView;
import com.universidad.biblioteca.view.auth.LoginView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    private final LoginView view;
    private final Connection conexion;

    public LoginController(LoginView view, Connection conexion) {
        this.view = view;
        this.conexion = conexion;
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

        Usuario u = verificarCredenciales(codigo, pass);

        if (u != null) {
            view.showMessage("Login exitoso");
            view.dispose();
            new MainView(u).setVisible(true);
        } else {
            view.showMessage("Credenciales incorrectas");
        }
    }

    private void register() {
        view.showMessage("Función de registro no implementada");
    }

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
