package com.universidad.biblioteca.vista.panels;

import com.universidad.biblioteca.controlador.PrestamoDAO;
import com.universidad.biblioteca.modelo.Prestamo;
import com.universidad.biblioteca.modelo.Usuario;
import com.universidad.biblioteca.vista.main.MainView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class HistorialPanel extends JPanel {

    private final MainView mainView;
    private final PrestamoDAO prestamoDAO;
    private final Usuario usuarioLogueado;

    private JTable tablaHistorial;
    private DefaultTableModel modeloHistorial;

    public HistorialPanel(MainView mainView, PrestamoDAO prestamoDAO, Usuario usuarioLogueado) {
        this.mainView = mainView;
        this.prestamoDAO = prestamoDAO;
        this.usuarioLogueado = usuarioLogueado;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initUI();
        cargarDatosHistorial();
    }

    private void initUI() {
        modeloHistorial = new DefaultTableModel(new String[]{"ID Préstamo", "Libro", "Fecha Préstamo", "Fecha Devolución", "Multa", "Devuelto"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaHistorial = new JTable(modeloHistorial);
        add(new JScrollPane(tablaHistorial), BorderLayout.CENTER);
    }

    public void cargarDatosHistorial() {
        try {
            modeloHistorial.setRowCount(0);
            List<Prestamo> prestamos = prestamoDAO.obtenerTodos(); // Idealmente, filtrar por usuario si no es admin
            for (Prestamo prestamo : prestamos) {
                modeloHistorial.addRow(new Object[]{
                        prestamo.getId(),
                        prestamo.getLibro() != null ? prestamo.getLibro().getTitulo() : "(Libro no disponible)",
                        prestamo.getFechaPrestamo(),
                        prestamo.getFechaDevolucion(),
                        prestamo.getMulta(),
                        prestamo.isDevuelto() ? "Sí" : "No"
                });
            }
        } catch (SQLException e) {
            mainView.mostrarError("Error al cargar el historial de préstamos: " + e.getMessage());
        }
    }
}