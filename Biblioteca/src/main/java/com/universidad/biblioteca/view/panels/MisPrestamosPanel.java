package com.universidad.biblioteca.view.panels;

import com.universidad.biblioteca.controller.LibroDAO;
import com.universidad.biblioteca.controller.PrestamoDAO;
import com.universidad.biblioteca.model.Libro;
import com.universidad.biblioteca.model.Prestamo;
import com.universidad.biblioteca.model.Usuario;
import com.universidad.biblioteca.view.main.MainView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
        modeloMisPrestamos = new DefaultTableModel(new String[]{"ID Préstamo", "Título", "Fecha Préstamo",
                "Fecha Devolución", "Días Restantes", "Multa"}, 0) {
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
                botonDevolver.setEnabled(tablaMisPrestamos.getSelectedRow() != -1);
            }
        });
    }

    public void cargarDatosMisPrestamos() {
        try {
            modeloMisPrestamos.setRowCount(0);
            List<Prestamo> prestamos = prestamoDAO.obtenerPrestamosActivosPorUsuario(usuarioLogueado.getCodigo());
            for (Prestamo prestamo : prestamos) {
                long diasRestantes = calcularDiasRestantes(prestamo.getFechaDevolucion());
                modeloMisPrestamos.addRow(new Object[]{
                        prestamo.getId(),
                        prestamo.getLibro() != null ? prestamo.getLibro().getTitulo() : "",
                        prestamo.getFechaPrestamo(),
                        prestamo.getFechaDevolucion(),
                        diasRestantes < 0 ? "Vencido" : diasRestantes,
                        String.format("%.2f", prestamo.getMulta())
                });
            }
        } catch (Exception e) {
            mainView.mostrarError("Error al cargar los préstamos: " + e.getMessage());
        }
    }

    private void devolverLibro() {
        int filaSeleccionada = tablaMisPrestamos.getSelectedRow();
        if (filaSeleccionada == -1) return;

        int idPrestamo = (int) modeloMisPrestamos.getValueAt(filaSeleccionada, 0);

        try {
            Prestamo prestamo = prestamoDAO.obtenerPrestamoPorId(idPrestamo);
            if (prestamo != null) {
                double multa = 0;
                long diasRestantes = calcularDiasRestantes(prestamo.getFechaDevolucion());
                if (diasRestantes < 0) {
                    multa = Math.abs(diasRestantes) * 1.5; // Ejemplo de multa
                }

                if (prestamoDAO.marcarComoDevuelto(idPrestamo, multa)) {
                    Libro libro = prestamo.getLibro();
                    libro.setDisponible(true);
                    libroDAO.actualizar(libro);
                    mainView.mostrarMensaje("Libro devuelto con éxito. Multa aplicada: S/ " + String.format("%.2f", multa),
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarDatosMisPrestamos();
                    mainView.cargarDatosHistorial();
                    mainView.getCatalogoPanel().cargarDatosCatalogo();
                } else {
                    mainView.mostrarError("No se pudo procesar la devolución.");
                }
            }
        } catch (SQLException e) {
            mainView.mostrarError("Error al devolver el libro: " + e.getMessage());
        }
    }

    private long calcularDiasRestantes(Date fechaDevolucion) {
        if (fechaDevolucion == null) return 0;
        long diff = fechaDevolucion.getTime() - new Date().getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }
}