package com.universidad.biblioteca.vista.panels;

import com.universidad.biblioteca.controlador.UsuarioDAO;
import com.universidad.biblioteca.modelo.Usuario;
import com.universidad.biblioteca.vista.main.MainView;

import javax.swing.*;
import java.awt.*;

public class PerfilPanel extends JPanel {

    private final MainView mainView;
    private final UsuarioDAO usuarioDAO;
    private final Usuario usuarioLogueado;

    private JTextField campoCodigo, campoNombre, campoCorreo, campoTelefono;
    private JPasswordField campoContrasena;

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

        campoCodigo = createFormField("Código:", 0, gbc);
        campoCodigo.setEditable(false);
        campoNombre = createFormField("Nombre:", 1, gbc);
        campoCorreo = createFormField("Correo:", 2, gbc);
        campoTelefono = createFormField("Teléfono:", 3, gbc);

        gbc.gridx = 0; gbc.gridy = 4; add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; campoContrasena = new JPasswordField(20); add(campoContrasena, gbc);

        JButton botonActualizarPerfil = new JButton("Actualizar Perfil");
        gbc.gridx = 1; gbc.gridy = 5; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        add(botonActualizarPerfil, gbc);
        botonActualizarPerfil.addActionListener(e -> actualizarPerfil());
    }

    private JTextField createFormField(String label, int y, GridBagConstraints gbc) {
        gbc.gridx = 0; gbc.gridy = y;
        add(new JLabel(label), gbc);
        gbc.gridx = 1;
        JTextField textField = new JTextField(20);
        add(textField, gbc);
        return textField;
    }

    public void cargarDatosPerfil() {
        campoCodigo.setText(usuarioLogueado.getCodigo());
        campoNombre.setText(usuarioLogueado.getNombre());
        campoCorreo.setText(usuarioLogueado.getCorreo());
        campoTelefono.setText(usuarioLogueado.getTelefono());
    }

    private void actualizarPerfil() {
        String nombre = campoNombre.getText().trim();
        String correo = campoCorreo.getText().trim();
        String telefono = campoTelefono.getText().trim();
        String contrasena = new String(campoContrasena.getPassword());

        if (nombre.isEmpty() || correo.isEmpty() || telefono.isEmpty()) {
            mainView.mostrarError("Todos los campos son obligatorios, excepto la contraseña.");
            return;
        }

        usuarioLogueado.setNombre(nombre);
        usuarioLogueado.setCorreo(correo);
        usuarioLogueado.setTelefono(telefono);
        if (!contrasena.isEmpty()) {
            usuarioLogueado.setContrasena(contrasena);
        }

        if (usuarioDAO.actualizarPerfil(usuarioLogueado)) {
            mainView.mostrarMensaje("Perfil actualizado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            if (!contrasena.isEmpty()) {
                campoContrasena.setText("");
            }
        } else {
            mainView.mostrarError("No se pudo actualizar el perfil.");
        }
    }
}