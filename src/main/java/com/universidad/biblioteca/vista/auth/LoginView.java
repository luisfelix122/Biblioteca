package com.universidad.biblioteca.vista.auth;



 import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;


public class LoginView extends JFrame {

    private JTextField txtCodigo;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegister;

    public LoginView() {
        System.out.println("Creando LoginView...");
        init();
    }

    private void init() {
        setTitle("Sistema de Biblioteca Universitaria");
        setResizable(false);
        setSize(500, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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

        JLabel footer = createLabel("© 2025 Luis Gonzales - Todos los derechos reservados", 12, new Color(255, 255, 255, 150));

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

        btnLogin = createButton("INICIAR SESIÓN", Color.WHITE, new Color(0, 102, 204));
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

        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validateField(field);
            }
        });

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
                validateField(field);
            }
        });
    }



    private void validateField(JTextComponent field) {
        String text = "";
        if (field instanceof JTextField) {
            text = ((JTextField) field).getText();
        }
        if (field instanceof JPasswordField) {
            text = new String(((JPasswordField) field).getPassword());
        }

        if (text.trim().isEmpty() || text.equals("Código Universitario") || text.equals("Contraseña")) {
            field.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(Color.RED, 1, true),
                    new EmptyBorder(12, 15, 12, 15)));
        } else {
            field.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(0, 102, 204), 1, true),
                    new EmptyBorder(12, 15, 12, 15)));
        }
    }

    private JButton createButton(String text, Color foreground, Color background) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(12, 0, 12, 0));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    public String getCodigo() {
        return txtCodigo.getText();
    }

    public String getPassword() {
        return new String(txtPassword.getPassword());
    }

    public JButton getBtnLogin() {
        return btnLogin;
    }

    public JButton getBtnRegister() {
        return btnRegister;
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

}