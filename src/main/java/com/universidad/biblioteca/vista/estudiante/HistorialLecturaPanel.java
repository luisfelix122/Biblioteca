package com.universidad.biblioteca.vista.estudiante;

import javax.swing.JPanel;

import com.universidad.biblioteca.controlador.PrestamoDAO;
import com.universidad.biblioteca.modelo.Libro;
import com.universidad.biblioteca.modelo.Prestamo;
import com.universidad.biblioteca.modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class HistorialLecturaPanel extends JPanel {
    public HistorialLecturaPanel(Usuario usuario) {
        setLayout(new BorderLayout());

        if (usuario != null && "Estudiante".equals(usuario.getRol().getNombre())) {
            JLabel titleLabel = new JLabel("Historial de Lectura", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            add(titleLabel, BorderLayout.NORTH);

            String[] columnNames = {"ISBN", "Título", "Autor", "Fecha Préstamo", "Fecha Devolución", "Multa"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);
            JTable historialTable = new JTable(model);
            historialTable.setFillsViewportHeight(true);

            JScrollPane scrollPane = new JScrollPane(historialTable);
            add(scrollPane, BorderLayout.CENTER);

            cargarHistorialLectura(usuario.getCodigo(), model);
        } else {
            mostrarAccesoDenegado();
        }
    }

    private void cargarHistorialLectura(String codigoUsuario, DefaultTableModel model) {
        PrestamoDAO prestamoDAO = new PrestamoDAO();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {
            List<Prestamo> prestamos = prestamoDAO.obtenerPrestamosPorUsuario(codigoUsuario);
            for (Prestamo prestamo : prestamos) {
                // Only show returned books as part of reading history
                if (prestamo.isDevuelto()) {
                    Libro libro = prestamo.getLibro();
                    Object[] rowData = {
                            libro.getIsbn(),
                            libro.getTitulo(),
                            libro.getAutor(),
                            dateFormat.format(prestamo.getFechaPrestamo()),
                            dateFormat.format(prestamo.getFechaDevolucion()),
                            String.format("%.2f", prestamo.getMulta())
                    };
                    model.addRow(rowData);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar el historial de lectura: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
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