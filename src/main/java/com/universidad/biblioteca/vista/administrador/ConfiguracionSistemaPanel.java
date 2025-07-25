package com.universidad.biblioteca.vista.administrador;

import com.universidad.biblioteca.modelo.Usuario;
import com.universidad.biblioteca.vista.utils.RoundedBorder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import com.universidad.biblioteca.controlador.ConfiguracionDAO;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ConfiguracionSistemaPanel extends JPanel {

    private static final Color BACKGROUND_COLOR = new Color(243, 244, 246);
    private static final Color FOREGROUND_COLOR = new Color(55, 65, 81);
    private static final Color BORDER_COLOR = new Color(209, 213, 219);
    private static final Font TEXT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Color PRIMARY_BUTTON_COLOR = new Color(34, 197, 94);

    private JTextField txtMaxLibrosPrestamo;
    private JTextField txtDuracionPrestamo;
    private JTextField txtMultaPorDia;
    private final ConfiguracionDAO configuracionDAO;

    public ConfiguracionSistemaPanel(Usuario usuario) {
        this.configuracionDAO = new ConfiguracionDAO();
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        if (usuario == null || (!"Administrador".equalsIgnoreCase(usuario.getRol().getNombre().trim()) && !"Admin".equalsIgnoreCase(usuario.getRol().getNombre().trim()))) {
            mostrarAccesoDenegado();
        } else {
            initUI();
            cargarConfiguracion();
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
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createTitledBorder("Parámetros del Sistema"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Fila 1: Máximo de libros por préstamo
        JLabel lblMaxLibros = createStyledLabel("Máximo de libros por préstamo:");
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(lblMaxLibros, gbc);
        txtMaxLibrosPrestamo = createStyledTextField(10);
        gbc.gridx = 1; gbc.gridy = 0; formPanel.add(txtMaxLibrosPrestamo, gbc);

        // Fila 2: Duración del préstamo en días
        JLabel lblDuracion = createStyledLabel("Duración del préstamo (días):");
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(lblDuracion, gbc);
        txtDuracionPrestamo = createStyledTextField(10);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(txtDuracionPrestamo, gbc);

        // Fila 3: Multa por día de retraso
        JLabel lblMulta = createStyledLabel("Multa por día de retraso ($):");
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(lblMulta, gbc);
        txtMultaPorDia = createStyledTextField(10);
        gbc.gridx = 1; gbc.gridy = 2; formPanel.add(txtMultaPorDia, gbc);

        add(formPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        JButton btnGuardar = createStyledButton("Guardar Cambios", PRIMARY_BUTTON_COLOR, Color.WHITE);
        btnGuardar.addActionListener(this::guardarConfiguracion);
        buttonPanel.add(btnGuardar);

        add(buttonPanel, BorderLayout.CENTER);
    }

    private void cargarConfiguracion() {
        try {
            Map<String, String> config = configuracionDAO.cargarConfiguracion();
            txtMaxLibrosPrestamo.setText(config.getOrDefault("max_libros_prestamo", "3"));
            txtDuracionPrestamo.setText(config.getOrDefault("dias_prestamo", "15"));
            txtMultaPorDia.setText(config.getOrDefault("multa_por_dia", "0.50"));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar la configuración: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarConfiguracion(ActionEvent e) {
        try {
            int maxLibros = Integer.parseInt(txtMaxLibrosPrestamo.getText());
            int duracion = Integer.parseInt(txtDuracionPrestamo.getText());
            double multa = Double.parseDouble(txtMultaPorDia.getText());

            if (maxLibros <= 0 || duracion <= 0 || multa < 0) {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese valores válidos y positivos.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Map<String, String> config = new HashMap<>();
            config.put("max_libros_prestamo", String.valueOf(maxLibros));
            config.put("dias_prestamo", String.valueOf(duracion));
            config.put("multa_por_dia", String.valueOf(multa));

            configuracionDAO.guardarConfiguracion(config);

            JOptionPane.showMessageDialog(this, "La configuración se ha guardado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese solo números válidos en los campos.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar la configuración: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Métodos de estilo ---

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(FOREGROUND_COLOR);
        return label;
    }

    private JTextField createStyledTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setFont(TEXT_FONT);
        textField.setForeground(FOREGROUND_COLOR);
        textField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDER_COLOR, 10),
                new EmptyBorder(5, 10, 5, 10)
        ));
        return textField;
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}