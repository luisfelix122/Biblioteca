package com.universidad.biblioteca.vista.panels;

import com.universidad.biblioteca.controlador.LibroDAO;
import com.universidad.biblioteca.controlador.PrestamoDAO;
import com.universidad.biblioteca.modelo.Prestamo;
import com.universidad.biblioteca.modelo.Usuario;
import com.universidad.biblioteca.vista.main.MainView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class MisPrestamosPanel extends JPanel {

    private final MainView mainView;
    private final PrestamoDAO prestamoDAO;
    private final LibroDAO libroDAO;
    private final Usuario usuarioLogueado;

    private JTable tablaMisPrestamos;
    private DefaultTableModel modeloMisPrestamos;
    private JButton botonDevolver;

    public MisPrestamosPanel(MainView mainView, PrestamoDAO prestamoDAO, LibroDAO libroDAO, Usuario usuarioLogueado) {
        this.mainView = mainView;
        this.prestamoDAO = prestamoDAO;
        this.libroDAO = libroDAO;
        this.usuarioLogueado = usuarioLogueado;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initUI();
        cargarDatosMisPrestamos();
    }

    private void initUI() {
        modeloMisPrestamos = new DefaultTableModel(new String[]{"ID Préstamo", "Libro", "Fecha Préstamo", "Fecha Devolución", "Multa", "Devuelto"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaMisPrestamos = new JTable(modeloMisPrestamos);
        add(new JScrollPane(tablaMisPrestamos), BorderLayout.CENTER);

        botonDevolver = new JButton("Devolver Libro Seleccionado");
        botonDevolver.setEnabled(false);
        add(botonDevolver, BorderLayout.SOUTH);

        botonDevolver.addActionListener(e -> devolverLibro());

        tablaMisPrestamos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tablaMisPrestamos.getSelectedRow();
                if (selectedRow != -1) {
                    boolean devuelto = (boolean) modeloMisPrestamos.getValueAt(selectedRow, 5);
                    botonDevolver.setEnabled(!devuelto);
                } else {
                    botonDevolver.setEnabled(false);
                }
            }
        });
    }

    public void cargarDatosMisPrestamos() {
        try {
            modeloMisPrestamos.setRowCount(0);
            List<Prestamo> prestamos = prestamoDAO.obtenerPrestamosPorUsuario(usuarioLogueado.getCodigo());
            for (Prestamo prestamo : prestamos) {
                modeloMisPrestamos.addRow(new Object[]{
                        prestamo.getId(),
                        prestamo.getLibro() != null ? prestamo.getLibro().getTitulo() : "(Libro no disponible)",
                        prestamo.getFechaPrestamo(),
                        prestamo.getFechaDevolucion(),
                        prestamo.getMulta(),
                        prestamo.isDevuelto()
                });
            }
        } catch (SQLException e) {
            mainView.mostrarError("Error al cargar mis préstamos: " + e.getMessage());
        }
    }

    private void devolverLibro() {
        int filaSeleccionada = tablaMisPrestamos.getSelectedRow();
        if (filaSeleccionada == -1) return;

        int idPrestamo = (int) modeloMisPrestamos.getValueAt(filaSeleccionada, 0);
        boolean devuelto = (boolean) modeloMisPrestamos.getValueAt(filaSeleccionada, 5);

        if (devuelto) {
            mainView.mostrarMensaje("Este libro ya ha sido devuelto.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            Prestamo prestamo = prestamoDAO.obtenerPrestamoPorId(idPrestamo);
            if (prestamo != null) {
                // Calcular multa si aplica (ejemplo simple: 1 día de retraso = 1 unidad de multa)
                double multa = 0.0;
                if (new java.util.Date().after(prestamo.getFechaDevolucion())) {
                    long diff = new java.util.Date().getTime() - prestamo.getFechaDevolucion().getTime();
                    long diffDays = diff / (24 * 60 * 60 * 1000);
                    multa = diffDays * 1.0; // 1 unidad de multa por día de retraso
                }

                if (prestamoDAO.marcarComoDevuelto(idPrestamo, multa)) {
                    // Actualizar disponibilidad del libro
                    prestamo.getLibro().setDisponible(true);
                    libroDAO.actualizar(prestamo.getLibro());

                    mainView.mostrarMensaje("Libro devuelto con éxito. Multa: " + String.format("%.2f", multa), "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarDatosMisPrestamos();
                    mainView.getCatalogoPanel().cargarDatosCatalogo();
                    mainView.cargarDatosHistorial();
                } else {
                    mainView.mostrarError("No se pudo marcar el préstamo como devuelto.");
                }
            }
        } catch (SQLException e) {
            mainView.mostrarError("Error al devolver el libro: " + e.getMessage());
        }
    }
}