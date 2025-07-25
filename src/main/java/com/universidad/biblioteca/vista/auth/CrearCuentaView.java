package com.universidad.biblioteca.vista.auth;

import com.formdev.flatlaf.FlatLightLaf;
import org.jdesktop.swingx.prompt.PromptSupport;

import javax.swing.*;
import java.awt.*;

public class CrearCuentaView extends JFrame {

    private JTextField txtNuevoUsuario;
    private JTextField txtEmail;
    private JPasswordField txtClave;
    private JPasswordField txtConfirmar;
    private JComboBox<String> cbRol;
    private JButton btnRegistrar;
    private JButton btnVolver;

    public CrearCuentaView() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        setTitle("Crear Nueva Cuenta");
        setSize(980, 730);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        if (getContentPane() instanceof JComponent) {
            ((JComponent) getContentPane()).setOpaque(false);
        }

        GradientPanel backgroundPanel = new GradientPanel(new Color(0x4B0082), new Color(0x8000FF));
        backgroundPanel.setLayout(new GridBagLayout());
        add(backgroundPanel, BorderLayout.CENTER);

        RoundedShadowPanel registerCard = new RoundedShadowPanel();
        registerCard.setLayout(new BoxLayout(registerCard, BoxLayout.Y_AXIS));
        registerCard.setPreferredSize(new Dimension(380, 400));
        registerCard.setMaximumSize(new Dimension(380, 400));
        registerCard.setBackground(Color.WHITE);
        registerCard.setOpaque(false);

        registerCard.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel cardTitle = new JLabel("Crear Cuenta");
        cardTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        cardTitle.setForeground(new Color(0x333333));
        cardTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerCard.add(cardTitle);

        registerCard.add(Box.createRigidArea(new Dimension(0, 20)));

        txtNuevoUsuario = new JTextField();
        setupTextField(txtNuevoUsuario, "Usuario");
        registerCard.add(txtNuevoUsuario);

        registerCard.add(Box.createRigidArea(new Dimension(0, 15)));

        txtEmail = new JTextField();
        setupTextField(txtEmail, "Correo");
        registerCard.add(txtEmail);

        registerCard.add(Box.createRigidArea(new Dimension(0, 15)));

        txtClave = new JPasswordField();
        setupTextField(txtClave, "Contraseña");
        registerCard.add(txtClave);

        registerCard.add(Box.createRigidArea(new Dimension(0, 15)));

        txtConfirmar = new JPasswordField();
        setupTextField(txtConfirmar, "Confirmar contraseña");
        registerCard.add(txtConfirmar);

        registerCard.add(Box.createRigidArea(new Dimension(0, 20)));

        btnRegistrar = new JButton("REGISTRAR");
        setupButton(btnRegistrar, new Color(0x007BFF));
        registerCard.add(btnRegistrar);

        registerCard.add(Box.createRigidArea(new Dimension(0, 15)));

        btnVolver = new JButton("VOLVER");
        setupButton(btnVolver, new Color(0x7200FF));
        registerCard.add(btnVolver);

        registerCard.add(Box.createRigidArea(new Dimension(0, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        backgroundPanel.add(registerCard, gbc);

        // Action Listeners
        btnRegistrar.addActionListener(e -> {
            if (txtNuevoUsuario.getText().isBlank() ||
                txtEmail.getText().isBlank() ||
                !new String(txtClave.getPassword()).equals(new String(txtConfirmar.getPassword())) ||
                new String(txtClave.getPassword()).isBlank()) {
                JOptionPane.showMessageDialog(this, "Verifica los datos ingresados. La contraseña no puede estar vacía y debe coincidir.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // TODO: llamar al DAO para insertar nuevo usuario
            JOptionPane.showMessageDialog(this, "Cuenta creada exitosamente, inicia sesión.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new LoginView().setVisible(true);
        });

        btnVolver.addActionListener(e -> {
            dispose();
            new LoginView().setVisible(true);
        });
    }

    private void setupTextField(JTextField field, String placeholder) {
        field.setPreferredSize(new Dimension(300, 44));
        field.setMaximumSize(new Dimension(300, 44));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xCCCCCC), 1, true),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        PromptSupport.setPrompt(placeholder, field);
        PromptSupport.setForeground(new Color(0x9E9E9E), field);
    }

    private void setupButton(JButton button, Color background) {
        button.setMaximumSize(new Dimension(300, 44));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(background.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(background);
            }
        });
    }
}