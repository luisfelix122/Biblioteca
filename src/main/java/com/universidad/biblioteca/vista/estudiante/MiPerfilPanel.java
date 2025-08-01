package com.universidad.biblioteca.vista.estudiante;

import com.universidad.biblioteca.modelo.Usuario;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MiPerfilPanel extends JPanel {

    private static final Color BACKGROUND_COLOR = new Color(243, 244, 246);
    private static final Color FOREGROUND_COLOR = new Color(55, 65, 81);
    private static final Color PRIMARY_BUTTON_COLOR = new Color(67, 56, 202);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font TEXT_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    private final Usuario usuario;

    public MiPerfilPanel(Usuario usuario) {
        this.usuario = usuario;

        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(40, 60, 40, 60));
        setBackground(BACKGROUND_COLOR);

        if (usuario != null && ("Estudiante".equals(usuario.getRol().getNombre()) || "Bibliotecario".equals(usuario.getRol().getNombre()) || "Administrador".equals(usuario.getRol().getNombre()))) {
            initUI();
        } else {
            mostrarAccesoDenegado();
        }
    }

    private void mostrarAccesoDenegado() {
        removeAll();
        setLayout(new GridBagLayout());
        JLabel label = new JLabel("Acceso denegado. No tienes permiso para acceder a esta sección.");
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(Color.RED);
        add(label);
        revalidate();
        repaint();
    }

    private void initUI() {
        // Panel principal que contendrá todo
        JPanel mainPanel = new JPanel(new BorderLayout(20, 30));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 100, 20, 100)); // Margen más amplio

        // Título
        JLabel titleLabel = new JLabel("Mi Perfil", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(FOREGROUND_COLOR);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Panel del formulario con la información
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219), 1, true),
                new EmptyBorder(30, 40, 30, 40)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Labels
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createLabel("Nombre:"), gbc);

        gbc.gridy = 1;
        formPanel.add(createLabel("Código:"), gbc);

        gbc.gridy = 2;
        formPanel.add(createLabel("Rol:"), gbc);

        // Fields
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(createValueLabel(usuario.getNombre()), gbc);

        gbc.gridy = 1;
        formPanel.add(createValueLabel(usuario.getCodigo()), gbc);

        gbc.gridy = 2;
        formPanel.add(createValueLabel(usuario.getRol().getNombre()), gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        JButton changePasswordButton = createStyledButton("Cambiar Contraseña", PRIMARY_BUTTON_COLOR, Color.WHITE);
        changePasswordButton.addActionListener(_ -> changePassword());
        buttonPanel.add(changePasswordButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Añadir el panel principal al panel de la clase
        add(mainPanel, BorderLayout.CENTER);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(FOREGROUND_COLOR);
        return label;
    }

    private JLabel createValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(TEXT_FONT);
        label.setForeground(new Color(107, 114, 128));
        label.setBorder(new EmptyBorder(5, 10, 5, 10));
        label.setOpaque(true);
        label.setBackground(new Color(249, 250, 251));
        return label;
    }

    private JButton createStyledButton(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                new com.universidad.biblioteca.vista.utils.RoundedBorder(new Color(209, 213, 219), 10),
                new EmptyBorder(12, 25, 12, 25)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void changePassword() {
        // Placeholder for change password logic
        JOptionPane.showMessageDialog(this, "Funcionalidad para cambiar contraseña aún no implementada.", "En Construcción", JOptionPane.INFORMATION_MESSAGE);
    }
}