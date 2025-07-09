package com.universidad.biblioteca.view;

import com.universidad.biblioteca.controller.LoginController;
import com.universidad.biblioteca.model.Usuario;
import com.universidad.biblioteca.config.ConexionBD;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class LoginView extends javax.swing.JFrame {

    private JLabel lblCodigo;
    private JTextField txtCodigo;
    private JLabel lblPassword;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegister;

    public LoginView() {
        initComponents();
    }

    private void initComponents() {
        // Configuración de la ventana principal
        setTitle("Sistema de Biblioteca Universitaria");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(500, 700);
        setLocationRelativeTo(null);
        
        // Panel principal con gradiente animado
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradiente de fondo más suave
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(67, 56, 202),
                    0, getHeight(), new Color(139, 92, 246)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Efectos decorativos
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.fillOval(-100, -100, 300, 300);
                g2d.fillOval(getWidth() - 200, getHeight() - 200, 300, 300);
            }
        };
        mainPanel.setLayout(new BorderLayout());
        
        // Panel del formulario
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(40, 50, 40, 50));
        
        // Icono de biblioteca grande
        JLabel iconLabel = createLibraryIcon();
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Título principal con mejor tipografía
        JLabel titleLabel = new JLabel("BIBLIOTECA");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Sistema de Gestión Universitaria");
        subtitleLabel.setFont(new Font("Segoe UI", Font.LIGHT, 16));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Panel de login con diseño moderno
        JPanel loginPanel = createLoginPanel();
        loginPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Agregar componentes al panel principal
        formPanel.add(Box.createVerticalStrut(30));
        formPanel.add(iconLabel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(subtitleLabel);
        formPanel.add(Box.createVerticalStrut(40));
        formPanel.add(loginPanel);
        formPanel.add(Box.createVerticalGlue());
        
        // Footer con información adicional
        JLabel footerLabel = new JLabel("© 2024 Universidad - Todos los derechos reservados");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerLabel.setForeground(new Color(255, 255, 255, 150));
        footerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(footerLabel);
        formPanel.add(Box.createVerticalStrut(20));
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }
    
    private JLabel createLibraryIcon() {
        // Crear un icono de biblioteca personalizado
        JLabel iconLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int size = 80;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                
                // Fondo circular
                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.fillOval(x, y, size, size);
                
                // Libro
                g2d.setColor(Color.WHITE);
                g2d.fillRect(x + 20, y + 25, 40, 30);
                
                // Líneas del libro
                g2d.setColor(new Color(67, 56, 202));
                g2d.setStroke(new BasicStroke(2));
                for (int i = 0; i < 4; i++) {
                    g2d.drawLine(x + 25, y + 30 + i * 5, x + 55, y + 30 + i * 5);
                }
            }
        };
        iconLabel.setPreferredSize(new Dimension(100, 100));
        return iconLabel;
    }
    
    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fondo con bordes redondeados y sombra
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.fill(new RoundRectangle2D.Double(5, 5, getWidth() - 10, getHeight() - 10, 20, 20));
                
                g2d.setColor(Color.WHITE);
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 10, getHeight() - 10, 20, 20));
            }
        };
        
        loginPanel.setOpaque(false);
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        loginPanel.setMaximumSize(new Dimension(380, 350));
        
        // Título del formulario
        JLabel formTitle = new JLabel("Iniciar Sesión");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        formTitle.setForeground(new Color(51, 51, 51));
        formTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Campo de código con icono
        JPanel codigoPanel = createFieldPanel("👤", "Código Universitario");
        txtCodigo = (JTextField) codigoPanel.getComponent(1);
        
        // Campo de contraseña con icono
        JPanel passwordPanel = createPasswordPanel("🔒", "Contraseña");
        txtPassword = (JPasswordField) passwordPanel.getComponent(1);
        
        // Botones estilizados
        btnLogin = createModernButton("INICIAR SESIÓN", new Color(67, 56, 202), Color.WHITE);
        btnRegister = createModernButton("CREAR CUENTA", new Color(249, 250, 251), new Color(67, 56, 202));
        
        // Agregar componentes
        loginPanel.add(formTitle);
        loginPanel.add(Box.createVerticalStrut(30));
        loginPanel.add(codigoPanel);
        loginPanel.add(Box.createVerticalStrut(20));
        loginPanel.add(passwordPanel);
        loginPanel.add(Box.createVerticalStrut(30));
        loginPanel.add(btnLogin);
        loginPanel.add(Box.createVerticalStrut(15));
        loginPanel.add(btnRegister);
        
        return loginPanel;
    }
    
    private JPanel createFieldPanel(String icon, String placeholder) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        iconLabel.setBorder(new EmptyBorder(0, 15, 0, 10));
        
        JTextField field = new JTextField();
        styleModernTextField(field, placeholder);
        
        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createPasswordPanel(String icon, String placeholder) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        iconLabel.setBorder(new EmptyBorder(0, 15, 0, 10));
        
        JPasswordField field = new JPasswordField();
        styleModernPasswordField(field, placeholder);
        
        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void styleModernTextField(JTextField field, String placeholder) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(229, 231, 235), 1, true),
            new EmptyBorder(12, 15, 12, 15)
        ));
        field.setBackground(new Color(249, 250, 251));
        field.setForeground(new Color(55, 65, 81));
        
        // Placeholder effect
        field.setText(placeholder);
        field.setForeground(new Color(156, 163, 175));
        
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(new Color(55, 65, 81));
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(67, 56, 202), 2, true),
                    new EmptyBorder(11, 14, 11, 14)
                ));
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(new Color(156, 163, 175));
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(229, 231, 235), 1, true),
                    new EmptyBorder(12, 15, 12, 15)
                ));
            }
        });
    }
    
    private void styleModernPasswordField(JPasswordField field, String placeholder) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(229, 231, 235), 1, true),
            new EmptyBorder(12, 15, 12, 15)
        ));
        field.setBackground(new Color(249, 250, 251));
        field.setForeground(new Color(55, 65, 81));
        field.setEchoChar((char) 0);
        field.setText(placeholder);
        field.setForeground(new Color(156, 163, 175));
        
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (String.valueOf(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setEchoChar('•');
                    field.setForeground(new Color(55, 65, 81));
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(67, 56, 202), 2, true),
                    new EmptyBorder(11, 14, 11, 14)
                ));
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getPassword().length == 0) {
                    field.setEchoChar((char) 0);
                    field.setText(placeholder);
                    field.setForeground(new Color(156, 163, 175));
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(229, 231, 235), 1, true),
                    new EmptyBorder(12, 15, 12, 15)
                ));
            }
        });
    }
    
    private JButton createModernButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(bgColor.brighter());
                } else {
                    g2d.setColor(bgColor);
                }
                
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                
                // Texto
                g2d.setColor(textColor);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(text)) / 2;
                int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(text, textX, textY);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(textColor);
        button.setBackground(bgColor);
        button.setBorder(new EmptyBorder(15, 25, 15, 25));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        return button;
    }

    public String getCodigo() {
        String text = txtCodigo.getText().trim();
        return text.equals("Código Universitario") ? "" : text;
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

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        EventQueue.invokeLater(() -> {
            try {
                // Obtener conexión usando la clase ConexionBD
                Connection conexion = ConexionBD.obtenerConexion();
                
                if (conexion != null) {
                    LoginView view = new LoginView();
                    LoginController controller = new LoginController(conexion);
                    
                    // Agregar listeners a los botones
                    view.getBtnLogin().addActionListener(e -> {
                        String codigo = view.getCodigo();
                        String password = view.getPassword();
                        
                        if (codigo.isEmpty() || password.isEmpty()) {
                            view.showMessage("Por favor, complete todos los campos");
                            return;
                        }
                        
                        Usuario usuario = controller.verificarCredenciales(codigo, password);
                        if (usuario != null) {
                            view.showMessage("Login exitoso");
                            // Abrir MainView con el código del usuario
                            view.dispose();
                            new MainView(codigo).setVisible(true);
                        } else {
                            view.showMessage("Credenciales incorrectas");
                        }
                    });
                    
                    view.getBtnRegister().addActionListener(e -> {
                        view.showMessage("Función de registro no implementada");
                    });
                    
                    view.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, 
                        "No se pudo conectar a la base de datos",
                        "Error de Conexión", 
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, 
                    "Error al inicializar la aplicación: " + e.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE
                );
                e.printStackTrace();
            }
        });
    }
}
