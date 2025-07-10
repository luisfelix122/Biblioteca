package com.universidad.biblioteca.view.panels;

import com.universidad.biblioteca.controller.UsuarioDAO;
import com.universidad.biblioteca.model.Usuario;
import com.universidad.biblioteca.view.main.MainView;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class PerfilPanel extends JPanel {

    private final MainView mainView;
    private final UsuarioDAO usuarioDAO;
    private final Usuario usuarioLogueado;

    private JTextField campoCodigo, campoNombres, campoApellidos, campoDni;
    private JPasswordField campoContrasena;
    private JButton botonActualizarPerfil;

    public PerfilPanel(MainView mainView, UsuarioDAO usuarioDAO, Usuario usuarioLogueado) {
        this.mainView = mainView;
        this.usuarioDAO = usuarioDAO;
        this.usuarioLogueado = usuarioLogueado;

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initUI();
        cargarDatosPerfil();
    }

    private void initUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("Código:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; campoCodigo = new JTextField(20); campoCodigo.setEditable(false); add(campoCodigo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; add(new JLabel("Nombres:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; campoNombres = new JTextField(20); add(campoNombres, gbc);

        gbc.gridx = 0; gbc.gridy = 2; add(new JLabel("Apellidos:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; campoApellidos = new JTextField(20); add(campoApellidos, gbc);

        gbc.gridx = 0; gbc.gridy = 3; add(new JLabel("DNI:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; campoDni = new JTextField(20); add(campoDni, gbc);

        gbc.gridx = 0; gbc.gridy = 4; add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; campoContrasena = new JPasswordField(20); add(campoContrasena, gbc);

        botonActualizarPerfil = new JButton("Actualizar Perfil");
        gbc.gridx = 1; gbc.gridy = 5; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        add(botonActualizarPerfil, gbc);
        botonActualizarPerfil.addActionListener(e -> actualizarPerfil());
    }

    private void cargarDatosPerfil() {
        campoCodigo.setText(usuarioLogueado.getCodigo());
        campoNombres.setText(usuarioLogueado.getNombres());
        campoApellidos.setText(usuarioLogueado.getApellidos());
        campoDni.setText(usuarioLogueado.getDni());
    }

    private void actualizarPerfil() {
        String nombres = campoNombres.getText();
        String apellidos = campoApellidos.getText();
        String dni = campoDni.getText();
        String contrasena = new String(campoContrasena.getPassword());

        if (nombres.isEmpty() || apellidos.isEmpty() || dni.isEmpty()) {
            mainView.mostrarError("Todos los campos son obligatorios, excepto la contraseña.");
            return;
        }

        usuarioLogueado.setNombres(nombres);
        usuarioLogueado.setApellidos(apellidos);
        usuarioLogueado.setDni(dni);
        if (!contrasena.isEmpty()) {
            usuarioLogueado.setContrasena(contrasena);
        }

        try {
            if (usuarioDAO.actualizar(usuarioLogueado)) {
                mainView.mostrarMensaje("Perfil actualizado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                if (!contrasena.isEmpty()) {
                    campoContrasena.setText("");
                }
            } else {
                mainView.mostrarError("No se pudo actualizar el perfil.");
            }
        } catch (SQLException e) {
            mainView.mostrarError("Error al actualizar el perfil: " + e.getMessage());
        }
    }
}