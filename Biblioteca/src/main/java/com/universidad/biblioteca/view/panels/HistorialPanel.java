package com.universidad.biblioteca.view.panels;

import com.universidad.biblioteca.controller.PrestamoDAO;
import com.universidad.biblioteca.model.Prestamo;
import com.universidad.biblioteca.model.Usuario;
import com.universidad.biblioteca.view.main.MainView;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class HistorialPanel extends JPanel {

    private final MainView mainView;
    private final PrestamoDAO prestamoDAO;
    private final Usuario usuarioLogueado;

    private JTable tablaHistorial;
    private DefaultTableModel modeloHistorial;
    private JButton botonExportar;

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
        modeloHistorial = new DefaultTableModel(new String[]{"ID Préstamo", "Título", "Fecha Préstamo", "Fecha Devolución", "Multa"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaHistorial = new JTable(modeloHistorial);
        add(new JScrollPane(tablaHistorial), BorderLayout.CENTER);

        botonExportar = new JButton("Exportar a Excel");
        add(botonExportar, BorderLayout.SOUTH);
        botonExportar.addActionListener(e -> exportarHistorialAExcel());
    }

    public void cargarDatosHistorial() {
        try {
            modeloHistorial.setRowCount(0);
            List<Prestamo> prestamos = prestamoDAO.obtenerHistorialPrestamosPorUsuario(usuarioLogueado.getCodigo());
            for (Prestamo prestamo : prestamos) {
                modeloHistorial.addRow(new Object[]{
                        prestamo.getId(),
                        prestamo.getLibro() != null ? prestamo.getLibro().getTitulo() : "N/A",
                        prestamo.getFechaPrestamo(),
                        prestamo.getFechaDevolucionReal(),
                        String.format("%.2f", prestamo.getMulta())
                });
            }
        } catch (Exception e) {
            mainView.mostrarError("Error al cargar el historial: " + e.getMessage());
        }
    }

    private void exportarHistorialAExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar como");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de Excel", "xlsx"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Historial de Préstamos");

                // Crear encabezado
                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < modeloHistorial.getColumnCount(); i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(modeloHistorial.getColumnName(i));
                }

                // Llenar datos
                for (int i = 0; i < modeloHistorial.getRowCount(); i++) {
                    Row row = sheet.createRow(i + 1);
                    for (int j = 0; j < modeloHistorial.getColumnCount(); j++) {
                        Cell cell = row.createCell(j);
                        Object value = modeloHistorial.getValueAt(i, j);
                        cell.setCellValue(value != null ? value.toString() : "");
                    }
                }

                // Guardar archivo
                try (FileOutputStream fileOut = new FileOutputStream(fileChooser.getSelectedFile().getAbsolutePath() + ".xlsx")) {
                    workbook.write(fileOut);
                }
                mainView.mostrarMensaje("Historial exportado a Excel con éxito.", "Exportación Exitosa", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {
                mainView.mostrarError("Error al exportar a Excel: " + e.getMessage());
            }
        }
    }
}