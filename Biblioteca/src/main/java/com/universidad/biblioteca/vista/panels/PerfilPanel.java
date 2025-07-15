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

        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initUI();
        cargarDatosPerfil();
    }

    private void initUI() {
        // Panel principal para el contenido del formulario
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        // Sección de Información Personal
        formPanel.add(createPersonalInfoPanel());
        formPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Espaciador

        // Sección de Seguridad
        formPanel.add(createSecurityPanel());

        // Panel de Avatar
        add(createAvatarPanel(), BorderLayout.WEST);

        // Panel de Formulario
        add(formPanel, BorderLayout.CENTER);

        // Panel de Acciones
        add(createActionsPanel(), BorderLayout.SOUTH);
    }

    private JPanel createPersonalInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Información Personal"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Fila 1: Código
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Código:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; campoCodigo = new JTextField(20); campoCodigo.setEditable(false); panel.add(campoCodigo, gbc);

        // Fila 2: Nombre
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; campoNombre = new JTextField(20); panel.add(campoNombre, gbc);

        // Fila 3: Correo
        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Correo:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; campoCorreo = new JTextField(20); panel.add(campoCorreo, gbc);

        // Fila 4: Teléfono
        gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; campoTelefono = new JTextField(20); panel.add(campoTelefono, gbc);

        return panel;
    }

    private JPanel createSecurityPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Seguridad"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Fila 1: Contraseña
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Nueva Contraseña (opcional):"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; campoContrasena = new JPasswordField(20); panel.add(campoContrasena, gbc);

        return panel;
    }

    private JPanel createAvatarPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Foto de Perfil"));
        JLabel avatarLabel;
        java.net.URL imgURL = getClass().getResource("/images/default-avatar.png");
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            Image image = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            avatarLabel = new JLabel(new ImageIcon(image));
        } else {
            avatarLabel = new JLabel("Imagen no encontrada");
        }
        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(avatarLabel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createActionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton botonActualizarPerfil = new JButton("Actualizar Perfil");
        botonActualizarPerfil.addActionListener(e -> actualizarPerfil());
        panel.add(botonActualizarPerfil);
        return panel;
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

        if (!validarCampos(nombre, correo, telefono)) {
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

    private boolean validarCampos(String nombre, String correo, String telefono) {
        if (nombre.isEmpty() || correo.isEmpty() || telefono.isEmpty()) {
            mainView.mostrarError("Los campos de nombre, correo y teléfono son obligatorios.");
            return false;
        }

        // Validación de formato de correo electrónico (simple)
        if (!correo.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            mainView.mostrarError("El formato del correo electrónico no es válido.");
            return false;
        }

        return true;
    }
}