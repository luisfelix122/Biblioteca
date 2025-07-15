package com.universidad.biblioteca.vista.panels;

import com.universidad.biblioteca.modelo.Usuario;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MiPerfilPanel extends JPanel {

    private static final Color BACKGROUND_COLOR = new Color(243, 244, 246);
    private static final Color FOREGROUND_COLOR = new Color(55, 65, 81);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font TEXT_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    private final Usuario usuario;

    public MiPerfilPanel(Usuario usuario) {
        this.usuario = usuario;

        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(40, 60, 40, 60));
        setBackground(BACKGROUND_COLOR);

        initUI();
    }

    private void initUI() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Labels
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createLabel("Nombre:"), gbc);



        gbc.gridy = 1;
        formPanel.add(createLabel("Código:"), gbc);

        gbc.gridy = 2;
        formPanel.add(createLabel("Rol:"), gbc);

        // Fields (as labels, not editable)
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(createValueLabel(usuario.getNombre()), gbc);



        gbc.gridy = 1;
        formPanel.add(createValueLabel(usuario.getCodigo()), gbc);

        gbc.gridy = 2;
        formPanel.add(createValueLabel(usuario.getRol()), gbc);

        // Change Password Button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(30, 10, 10, 10);
        JButton changePasswordButton = createStyledButton("Cambiar Contraseña", new Color(67, 56, 202), Color.WHITE);
        changePasswordButton.addActionListener(e -> changePassword());
        formPanel.add(changePasswordButton, gbc);

        add(formPanel, BorderLayout.CENTER);
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