package com.universidad.biblioteca.vista.auth;

import com.formdev.flatlaf.FlatLightLaf;
import com.universidad.biblioteca.controlador.auth.LoginController;
import org.jdesktop.swingx.prompt.PromptSupport;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class LoginView extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton btnLogin;
    private JButton btnCrear;
    private JLabel appTitle;
    private GridBagConstraints gbc;
    private JPanel backgroundPanel;

    public LoginView() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        setTitle("Sistema de Biblioteca Universitaria");
        setSize(980, 730);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Garantiza transparencia del contenedor
        if (getContentPane() instanceof JComponent) {
            ((JComponent) getContentPane()).setOpaque(false);
        }

        // Panel raíz con fondo degradado
        backgroundPanel = new GradientPanel(new Color(0x4B0082), new Color(0x8000FF));
        backgroundPanel.setLayout(new BorderLayout());
        add(backgroundPanel, BorderLayout.CENTER);

        // Copyright
        JLabel copyrightLabel = new JLabel("© 2025 PROGRAM – Todos los derechos reservados");
        copyrightLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        copyrightLabel.setForeground(Color.WHITE);
        copyrightLabel.setHorizontalAlignment(SwingConstants.CENTER);
        copyrightLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // airecito
        backgroundPanel.add(copyrightLabel, BorderLayout.SOUTH);

        // --- Contenedor Principal ---
        JPanel mainContainer = new JPanel();
        mainContainer.setOpaque(false);
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));

        // --- Encabezado ---
        appTitle = new JLabel("BIBLIOTECA");
        appTitle.setFont(new Font("SansSerif", Font.BOLD, 64));
        appTitle.setForeground(Color.WHITE);
        appTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel appSubtitle = new JLabel("Sistema de Gestión Universitaria");
        appSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 24));
        appSubtitle.setForeground(Color.WHITE);
        appSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainContainer.add(appTitle);
        mainContainer.add(appSubtitle);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 20))); // Margen inferior

        // --- Tarjeta de Login ---
        RoundedShadowPanel loginCard = new RoundedShadowPanel();
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
        loginCard.setPreferredSize(new Dimension(380, 340));
        loginCard.setMaximumSize(new Dimension(380, 340));
        loginCard.setBackground(Color.WHITE);
        loginCard.setOpaque(false);

        loginCard.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel cardTitle = new JLabel("Iniciar Sesión");
        cardTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        cardTitle.setForeground(new Color(0x333333));
        cardTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginCard.add(cardTitle);

        loginCard.add(Box.createRigidArea(new Dimension(0, 20)));

        usernameField = new JTextField();
        setupTextField(usernameField, "Usuario");
        loginCard.add(usernameField);

        loginCard.add(Box.createRigidArea(new Dimension(0, 20)));

        passwordField = new JPasswordField();
        setupTextField(passwordField, "Contraseña");
        loginCard.add(passwordField);

        loginCard.add(Box.createRigidArea(new Dimension(0, 20)));

        btnLogin = new JButton("INICIAR SESIÓN");
        setupButton(btnLogin, new Color(0x007BFF));
        loginCard.add(btnLogin);

        loginCard.add(Box.createRigidArea(new Dimension(0, 20)));

        btnCrear = new JButton("CREAR CUENTA");
        setupButton(btnCrear, new Color(0x7200FF));
        loginCard.add(btnCrear);

        btnCrear.addActionListener(e -> {
            CrearCuentaView registro = new CrearCuentaView();
            registro.setLocationRelativeTo(null);
            registro.setVisible(true);
            this.dispose();
        });

        loginCard.add(Box.createRigidArea(new Dimension(0, 20)));

        mainContainer.add(loginCard);

        // Wrapper panel to center mainContainer in the BorderLayout
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        gbc = new GridBagConstraints();
        centerWrapper.add(mainContainer, gbc);
        backgroundPanel.add(centerWrapper, BorderLayout.CENTER);

        // --- Lógica de Responsividad ---
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int size = Math.max(36, Math.min(64, w / 12));
                appTitle.setFont(appTitle.getFont().deriveFont((float) size));

                if (getHeight() < 600) {
                    gbc.insets = new Insets(20, 0, 20, 0);
                } else {
                    gbc.insets = new Insets(0, 0, 0, 0);
                }
                backgroundPanel.revalidate();
                backgroundPanel.repaint();
            }
        });

        setVisible(true);
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

    public void setLoginController(LoginController controller) {
        btnLogin.addActionListener(controller);
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    public JTextField getUsernameField() {
        return usernameField;
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }

    public JButton getBtnLogin() {
        return btnLogin;
    }

    public JButton getBtnCrear() {
        return btnCrear;
    }
}