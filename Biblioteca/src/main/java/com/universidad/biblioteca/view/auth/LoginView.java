package com.universidad.biblioteca.view.auth;

import com.universidad.biblioteca.config.ConexionBD;
import com.universidad.biblioteca.controller.LoginController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;

public class LoginView extends JFrame {

    private JTextField txtCodigo;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegister;

    public LoginView() {
        setTitle("Sistema de Biblioteca Universitaria");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setSize(500, 700);
        setLocationRelativeTo(null);
        setContentPane(createMainPanel());
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(67, 56, 202), 0, getHeight(), new Color(139, 92, 246));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(createFormPanel(), BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(40, 50, 40, 50));

        JLabel titleLabel = createLabel("BIBLIOTECA", 36, Color.WHITE);
        JLabel subtitleLabel = createLabel("Sistema de Gestión Universitaria", 16, new Color(255, 255, 255, 200));

        JPanel loginPanel = createLoginPanel();

        JLabel footer = createLabel("© 2024 Universidad - Todos los derechos reservados", 12, new Color(255, 255, 255, 150));

        formPanel.add(Box.createVerticalStrut(60));
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(subtitleLabel);
        formPanel.add(Box.createVerticalStrut(40));
        formPanel.add(loginPanel);
        formPanel.add(Box.createVerticalGlue());
        formPanel.add(footer);
        formPanel.add(Box.createVerticalStrut(20));

        return formPanel;
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255));
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
            }
        };

        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));
        panel.setMaximumSize(new Dimension(380, 350));

        JLabel formTitle = createLabel("Iniciar Sesión", 24, new Color(51, 51, 51));

        txtCodigo = createTextField("Código Universitario");
        txtPassword = createPasswordField("Contraseña");

        btnLogin = createButton("INICIAR SESIÓN", new Color(67, 56, 202), Color.WHITE);
        btnRegister = createButton("CREAR CUENTA", new Color(249, 250, 251), new Color(67, 56, 202));

        panel.add(formTitle);
        panel.add(Box.createVerticalStrut(30));
        panel.add(txtCodigo);
        panel.add(Box.createVerticalStrut(20));
        panel.add(txtPassword);
        panel.add(Box.createVerticalStrut(30));
        panel.add(btnLogin);
        panel.add(Box.createVerticalStrut(15));
        panel.add(btnRegister);

        return panel;
    }

    private JLabel createLabel(String text, int fontSize, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        label.setForeground(color);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField(placeholder);
        styleField(field, placeholder);
        return field;
    }

    private JPasswordField createPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField(placeholder);
        styleField(field, placeholder);
        field.setEchoChar((char) 0);
        return field;
    }

    private void styleField(JTextComponent field, String placeholder) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(229, 231, 235), 1, true),
                new EmptyBorder(12, 15, 12, 15)));
        field.setBackground(new Color(249, 250, 251));
        field.setForeground(new Color(156, 163, 175));

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(new Color(55, 65, 81));
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar('•');
                    }
                }
            }

            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(new Color(156, 163, 175));
                    field.setText(placeholder);
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar((char) 0);
                    }
                }
            }
        });
    }

    private JButton createButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(fg);
        button.setBackground(bg);
        button.setBorder(new EmptyBorder(15, 25, 15, 25));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    public String getCodigo() {
        return txtCodigo.getText().equals("Código Universitario") ? "" : txtCodigo.getText().trim();
    }

    public String getPassword() {
        String text = String.valueOf(txtPassword.getPassword());
        return text.equals("Contraseña") ? "" : text;
    }

    public JButton getBtnLogin() {
        return btnLogin;
    }

    public JButton getBtnRegister() {
        return btnRegister;
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        EventQueue.invokeLater(() -> {
            Connection conexion = ConexionBD.obtenerConexion();
            if (conexion != null) {
                LoginView view = new LoginView();
                new LoginController(view, conexion);
                view.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo conectar a la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}