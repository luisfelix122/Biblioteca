package com.universidad.biblioteca.vista.bibliotecario;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.plot.PiePlot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import javax.swing.JFileChooser;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.universidad.biblioteca.modelo.Usuario;
import com.universidad.biblioteca.modelo.Libro;
import com.universidad.biblioteca.modelo.Prestamo;
import com.universidad.biblioteca.controlador.LibroDAO;
import com.universidad.biblioteca.controlador.PrestamoDAO;

import java.util.List;
import java.sql.SQLException;

public class ReportesPanel extends JPanel {

    private JButton generatePdfButton, exportExcelButton;

    public ReportesPanel(Usuario usuario) {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 240, 240));

        // Panel para los gráficos
        JPanel chartsContainer = new JPanel(new GridLayout(1, 2, 10, 10));
        chartsContainer.setBackground(new Color(240, 240, 240));

        // Añadir gráfico de barras
        chartsContainer.add(createBarChartPanel());

        // Añadir gráfico circular
        chartsContainer.add(createPieChartPanel());

        // Panel de botones de acción
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(240, 240, 240));

        generatePdfButton = new JButton("Generar Reporte PDF");
        exportExcelButton = new JButton("Exportar Excel");

        styleButton(generatePdfButton);
        styleButton(exportExcelButton);

        generatePdfButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generatePdfReport();
            }
        });

        exportExcelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportExcelReport();
            }
        });

        buttonPanel.add(generatePdfButton);
        buttonPanel.add(exportExcelButton);

        // Añadir componentes al panel principal
        add(chartsContainer, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private ChartPanel createBarChartPanel() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int[] monthlyLoans = {50, 70, 60, 90, 80, 100}; // Datos de ejemplo
        String[] months = {"Ene", "Feb", "Mar", "Abr", "May", "Jun"};

        for (int i = 0; i < monthlyLoans.length; i++) {
            dataset.addValue(monthlyLoans[i], "Préstamos", months[i]);
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Préstamos por Mes",
                "Mes",
                "Número de Préstamos",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);

        barChart.setBackgroundPaint(new Color(240, 240, 240));
        barChart.getTitle().setPaint(Color.BLACK);
        // Customize bar colors
        CategoryPlot plot = barChart.getCategoryPlot();
        plot.setRenderer(new org.jfree.chart.renderer.category.BarRenderer());
        plot.getRenderer().setSeriesPaint(0, new Color(85, 65, 118)); // Use a color similar to buttons

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(400, 300));
        chartPanel.setBackground(new Color(240, 240, 240));
        return chartPanel;
    }

    @SuppressWarnings("unchecked")
    private JPanel createPieChartPanel() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        int[] bookPercentages = {30, 25, 20, 15, 10}; // Datos de ejemplo
        String[] bookTitles = {"Ficción", "Ciencia", "Historia", "Arte", "Otros"};

        for (int i = 0; i < bookPercentages.length; i++) {
            dataset.setValue(bookTitles[i] + " (" + bookPercentages[i] + "%)", bookPercentages[i]);
        }

        JFreeChart pieChart = ChartFactory.createPieChart(
                "Libros más Leídos",
                dataset,
                true, true, false);

        pieChart.setBackgroundPaint(new Color(240, 240, 240));
        pieChart.getTitle().setPaint(Color.BLACK);
        // Customize pie section colors
        PiePlot<String> plot = (PiePlot<String>) pieChart.getPlot();
        plot.setSectionPaint("Ficción (30%)", new Color(255, 102, 102)); // Light Red
        plot.setSectionPaint("Ciencia (25%)", new Color(102, 178, 255)); // Light Blue
        plot.setSectionPaint("Historia (20%)", new Color(255, 204, 102)); // Light Orange
        plot.setSectionPaint("Arte (15%)", new Color(178, 102, 255)); // Light Purple
        plot.setSectionPaint("Otros (10%)", new Color(102, 255, 178)); // Light Green
        plot.setExplodePercent((String) "Ficción (30%)", 0.10); // Explode a slice for emphasis

        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanel.setPreferredSize(new Dimension(400, 300));
        chartPanel.setBackground(new Color(240, 240, 240));
        return chartPanel;
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(85, 65, 118));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    }

    private void generatePdfReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte PDF");
        fileChooser.setSelectedFile(new File("ReporteBiblioteca.pdf"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String path = fileToSave.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".pdf")) {
                path += ".pdf";
            }

            PdfWriter pdfWriter = null;
            try {
                pdfWriter = new PdfWriter(new FileOutputStream(path));
                PdfDocument pdfDocument = new PdfDocument(pdfWriter);
                Document document = new Document(pdfDocument);

                document.add(new Paragraph("Reporte de la Biblioteca").setFontSize(20).setBold());
                document.add(new Paragraph("\n")); // Add some space

                // Add data from LibroDAO
                try {
                    LibroDAO libroDAO = new LibroDAO();
                    List<Libro> libros = libroDAO.obtenerTodosLosLibros();

                    document.add(new Paragraph("Listado de Libros").setFontSize(16).setBold());
                    Table libroTable = new Table(com.itextpdf.layout.property.UnitValue.createPercentArray(new float[]{1, 3, 3, 2, 1}));
                    libroTable.setWidth(com.itextpdf.layout.property.UnitValue.createPercentValue(100));
                    libroTable.addHeaderCell(new Paragraph("ISBN").setBold());
                    libroTable.addHeaderCell(new Paragraph("Título").setBold());
                    libroTable.addHeaderCell(new Paragraph("Autor").setBold());
                    libroTable.addHeaderCell(new Paragraph("Año").setBold());
                    libroTable.addHeaderCell(new Paragraph("Disponible").setBold());

                    for (Libro libro : libros) {
                        libroTable.addCell(new Paragraph(libro.getIsbn()));
                        libroTable.addCell(new Paragraph(libro.getTitulo()));
                        libroTable.addCell(new Paragraph(libro.getAutor()));
                        libroTable.addCell(new Paragraph(String.valueOf(libro.getAnioPublicacion())));
                        libroTable.addCell(new Paragraph(libro.isDisponible() ? "Sí" : "No"));
                    }
                    document.add(libroTable);
                    document.add(new Paragraph("\n")); // Add some space

                } catch (SQLException ex) {
                    document.add(new Paragraph("Error al cargar datos de libros: " + ex.getMessage()).setItalic().setFontColor(com.itextpdf.kernel.colors.ColorConstants.RED));
                    ex.printStackTrace();
                }

                // Add data from PrestamoDAO
                try {
                    PrestamoDAO prestamoDAO = new PrestamoDAO();
                    List<Prestamo> prestamos = prestamoDAO.obtenerTodosLosPrestamos();

                    document.add(new Paragraph("Listado de Préstamos").setFontSize(16).setBold());
                    Table prestamoTable = new Table(com.itextpdf.layout.property.UnitValue.createPercentArray(new float[]{2, 2, 2, 2, 1, 1}));
                    prestamoTable.setWidth(com.itextpdf.layout.property.UnitValue.createPercentValue(100));
                    prestamoTable.addHeaderCell(new Paragraph("Usuario").setBold());
                    prestamoTable.addHeaderCell(new Paragraph("ISBN Libro").setBold());
                    prestamoTable.addHeaderCell(new Paragraph("Fecha Préstamo").setBold());
                    prestamoTable.addHeaderCell(new Paragraph("Fecha Devolución").setBold());
                    prestamoTable.addHeaderCell(new Paragraph("Multa").setBold());
                    prestamoTable.addHeaderCell(new Paragraph("Devuelto").setBold());

                    for (Prestamo prestamo : prestamos) {
                        prestamoTable.addCell(new Paragraph(prestamo.getUsuario().getCodigo()));
                        prestamoTable.addCell(new Paragraph(prestamo.getLibro().getIsbn()));
                        prestamoTable.addCell(new Paragraph(prestamo.getFechaPrestamo().toString()));
                        prestamoTable.addCell(new Paragraph(prestamo.getFechaDevolucion() != null ? prestamo.getFechaDevolucion().toString() : "N/A"));
                        prestamoTable.addCell(new Paragraph(String.valueOf(prestamo.getMulta())));
                        prestamoTable.addCell(new Paragraph(prestamo.isDevuelto() ? "Sí" : "No"));
                    }
                    document.add(prestamoTable);
                    document.add(new Paragraph("\n")); // Add some space

                } catch (SQLException ex) {
                    document.add(new Paragraph("Error al cargar datos de préstamos: " + ex.getMessage()).setItalic().setFontColor(com.itextpdf.kernel.colors.ColorConstants.RED));
                    ex.printStackTrace();
                }

                // Add charts as images (example - you'd render them to byte arrays)
                // For simplicity, this example assumes you have a way to get image data from JFreeChart
                // You would typically render the chart to a BufferedImage and then to a byte array.
                // For now, I'll add a placeholder text.
                document.add(new Paragraph("Gráficos (implementación de imagen pendiente)").setItalic());

                document.close();
                JOptionPane.showMessageDialog(this, "Reporte PDF generado exitosamente en: " + path, "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error al generar el reporte PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } catch (Exception e) { // Catch any other exceptions during PDF generation
                JOptionPane.showMessageDialog(this, "Error inesperado al generar el reporte PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } finally {
                if (pdfWriter != null) {
                    try {
                        pdfWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void exportExcelReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte Excel");
        fileChooser.setSelectedFile(new File("ReporteLibros.xlsx"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String path = fileToSave.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".xlsx")) {
                path += ".xlsx";
            }

            try (Workbook workbook = new XSSFWorkbook();
                 FileOutputStream outputStream = new FileOutputStream(path)) {

                // Exportar datos de Libros
                Sheet libroSheet = workbook.createSheet("Libros");
                LibroDAO libroDAO = new LibroDAO();
                List<Libro> libros = libroDAO.obtenerTodosLosLibros();

                // Encabezados de Libros
                Row headerRowLibros = libroSheet.createRow(0);
                String[] libroHeaders = {"ISBN", "Título", "Autor", "Año Publicación", "Disponible"};
                for (int i = 0; i < libroHeaders.length; i++) {
                    Cell cell = headerRowLibros.createCell(i);
                    cell.setCellValue(libroHeaders[i]);
                }

                // Datos de Libros
                int rowNum = 1;
                for (Libro libro : libros) {
                    Row row = libroSheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(libro.getIsbn());
                    row.createCell(1).setCellValue(libro.getTitulo());
                    row.createCell(2).setCellValue(libro.getAutor());
                    row.createCell(3).setCellValue(libro.getAnioPublicacion());
                    row.createCell(4).setCellValue(libro.isDisponible() ? "Sí" : "No");
                }

                // Exportar datos de Préstamos
                Sheet prestamoSheet = workbook.createSheet("Préstamos");
                PrestamoDAO prestamoDAO = new PrestamoDAO();
                List<Prestamo> prestamos = prestamoDAO.obtenerTodosLosPrestamos();

                // Encabezados de Préstamos
                Row headerRowPrestamos = prestamoSheet.createRow(0);
                String[] prestamoHeaders = {"Código Usuario", "ISBN Libro", "Fecha Préstamo", "Fecha Devolución", "Multa", "Devuelto"};
                for (int i = 0; i < prestamoHeaders.length; i++) {
                    Cell cell = headerRowPrestamos.createCell(i);
                    cell.setCellValue(prestamoHeaders[i]);
                }

                // Datos de Préstamos
                rowNum = 1;
                for (Prestamo prestamo : prestamos) {
                    Row row = prestamoSheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(prestamo.getUsuario().getCodigo());
                    row.createCell(1).setCellValue(prestamo.getLibro().getIsbn());
                    row.createCell(2).setCellValue(prestamo.getFechaPrestamo().toString());
                    row.createCell(3).setCellValue(prestamo.getFechaDevolucion() != null ? prestamo.getFechaDevolucion().toString() : "N/A");
                    row.createCell(4).setCellValue(prestamo.getMulta());
                    row.createCell(5).setCellValue(prestamo.isDevuelto() ? "Sí" : "No");
                }

                workbook.write(outputStream);
                JOptionPane.showMessageDialog(this, "Reporte Excel generado exitosamente en: " + path, "Éxito", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException | SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al generar el reporte Excel: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}