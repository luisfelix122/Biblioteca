package com.universidad.biblioteca.vista.estudiante;

import com.universidad.biblioteca.controlador.SugerenciaDAO;
import com.universidad.biblioteca.modelo.Sugerencia;
import com.universidad.biblioteca.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SugerenciasPanel extends JPanel {
    private Usuario usuarioActual;
    private JTextField txtTitulo;
    private JTextArea txtDescripcion;
    private DefaultTableModel tableModel;
    private JTable sugerenciasTable;

    public SugerenciasPanel(Usuario usuario) {
        this.usuarioActual = usuario;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        if (usuarioActual != null && "Estudiante".equals(usuarioActual.getRol().getNombre())) {
            // Title
            JLabel titleLabel = new JLabel("Sugerencias y Comentarios", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            add(titleLabel, BorderLayout.NORTH);

            // Form Panel
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBorder(BorderFactory.createTitledBorder("Enviar Nueva Sugerencia"));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            gbc.gridx = 0;
            gbc.gridy = 0;
            formPanel.add(new JLabel("Título:"), gbc);

            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            txtTitulo = new JTextField(30);
            formPanel.add(txtTitulo, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.weightx = 0;
            formPanel.add(new JLabel("Descripción:"), gbc);

            gbc.gridx = 1;
            gbc.gridy = 1;
            gbc.weightx = 1.0;
            txtDescripcion = new JTextArea(5, 30);
            txtDescripcion.setLineWrap(true);
            txtDescripcion.setWrapStyleWord(true);
            JScrollPane scrollPaneDesc = new JScrollPane(txtDescripcion);
            formPanel.add(scrollPaneDesc, gbc);

            gbc.gridx = 1;
            gbc.gridy = 2;
            gbc.anchor = GridBagConstraints.EAST;
            JButton btnEnviar = new JButton("Enviar Sugerencia");
            btnEnviar.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    enviarSugerencia();
                }
            });
            formPanel.add(btnEnviar, gbc);

            add(formPanel, BorderLayout.CENTER);

            // Suggestions List Panel
            JPanel listPanel = new JPanel(new BorderLayout());
            listPanel.setBorder(BorderFactory.createTitledBorder("Sugerencias Enviadas"));
            String[] columnNames = {"ID", "Usuario", "Título", "Descripción", "Fecha"};
            tableModel = new DefaultTableModel(columnNames, 0);
            sugerenciasTable = new JTable(tableModel);
            sugerenciasTable.setFillsViewportHeight(true);
            JScrollPane scrollPaneTable = new JScrollPane(sugerenciasTable);
            listPanel.add(scrollPaneTable, BorderLayout.CENTER);

            add(listPanel, BorderLayout.SOUTH);

            cargarSugerencias();

        } else {
            mostrarAccesoDenegado();
        }
    }

    private void enviarSugerencia() {
        String titulo = txtTitulo.getText().trim();
        String descripcion = txtDescripcion.getText().trim();

        if (titulo.isEmpty() || descripcion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos para enviar una sugerencia.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Sugerencia nuevaSugerencia = new Sugerencia(
                usuarioActual,
                titulo,
                descripcion,
                new Date()
        );

        SugerenciaDAO sugerenciaDAO = new SugerenciaDAO();
        try {
            if (sugerenciaDAO.agregarSugerencia(nuevaSugerencia)) {
                JOptionPane.showMessageDialog(this, "Sugerencia enviada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                txtTitulo.setText("");
                txtDescripcion.setText("");
                cargarSugerencias(); // Reload suggestions after adding a new one
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo enviar la sugerencia.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al enviar la sugerencia: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void cargarSugerencias() {
        // Clear existing data
        tableModel.setRowCount(0);

        SugerenciaDAO sugerenciaDAO = new SugerenciaDAO();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        try {
            List<Sugerencia> sugerencias = sugerenciaDAO.obtenerTodasLasSugerencias();
            for (Sugerencia sug : sugerencias) {
                Object[] rowData = {
                        sug.getId(),
                        sug.getUsuario().getNombre(), // Assuming Usuario object has 'nombre' field populated
                        sug.getTitulo(),
                        sug.getDescripcion(),
                        dateFormat.format(sug.getFechaSugerencia())
                };
                tableModel.addRow(rowData);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar sugerencias: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
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
}