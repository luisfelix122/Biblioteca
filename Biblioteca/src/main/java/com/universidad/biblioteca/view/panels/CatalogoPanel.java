package com.universidad.biblioteca.view.panels;

import com.universidad.biblioteca.controller.LibroDAO;
import com.universidad.biblioteca.controller.PrestamoDAO;
import com.universidad.biblioteca.model.Libro;
import com.universidad.biblioteca.model.Prestamo;
import com.universidad.biblioteca.model.Usuario;
import com.universidad.biblioteca.view.main.MainView;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CatalogoPanel extends JPanel {

    private final MainView mainView;
    private final LibroDAO libroDAO;
    private final PrestamoDAO prestamoDAO;
    private final Usuario usuarioLogueado;

    private JTable tablaCatalogo;
    private DefaultTableModel modeloCatalogo;
    private JTextField campoBusqueda;
    private JButton botonSolicitar;

    public CatalogoPanel(MainView mainView, LibroDAO libroDAO, PrestamoDAO prestamoDAO, Usuario usuarioLogueado) {
        this.mainView = mainView;
        this.libroDAO = libroDAO;
        this.prestamoDAO = prestamoDAO;
        this.usuarioLogueado = usuarioLogueado;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initUI();
        cargarDatosCatalogo();
    }

    private void initUI() {
        JPanel panelSuperior = new JPanel(new BorderLayout(10, 0));
        campoBusqueda = new JTextField();
        JButton botonBuscar = new JButton("Buscar");
        JButton botonExportar = new JButton("Exportar a Excel");
        panelSuperior.add(campoBusqueda, BorderLayout.CENTER);
        panelSuperior.add(botonBuscar, BorderLayout.EAST);
        panelSuperior.add(botonExportar, BorderLayout.WEST);
        add(panelSuperior, BorderLayout.NORTH);

        modeloCatalogo = new DefaultTableModel(new String[]{"ID", "Título", "Autor", "Año", "Disponible"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaCatalogo = new JTable(modeloCatalogo);
        add(new JScrollPane(tablaCatalogo), BorderLayout.CENTER);

        botonSolicitar = new JButton("Solicitar Libro Seleccionado");
        botonSolicitar.setEnabled(false);
        add(botonSolicitar, BorderLayout.SOUTH);

        botonBuscar.addActionListener(e -> buscarLibros());
        botonExportar.addActionListener(e -> exportarCatalogoAExcel());
        botonSolicitar.addActionListener(e -> solicitarLibro());

        tablaCatalogo.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                botonSolicitar.setEnabled(tablaCatalogo.getSelectedRow() != -1);
            }
        });
    }

    public void cargarDatosCatalogo() {
        try {
            modeloCatalogo.setRowCount(0);
            List<Libro> libros = libroDAO.obtenerTodosLosLibros();
            for (Libro libro : libros) {
                modeloCatalogo.addRow(new Object[]{
                        libro.getId(), libro.getTitulo(), libro.getAutor(),
                        libro.getAnioPublicacion(), libro.isDisponible() ? "Sí" : "No"
                });
            }
        } catch (SQLException e) {
            mainView.mostrarError("Error al cargar el catálogo: " + e.getMessage());
        }
    }

    private void buscarLibros() {
        try {
            String termino = campoBusqueda.getText().trim();
            modeloCatalogo.setRowCount(0);
            List<Libro> libros = libroDAO.buscarLibros(termino);
            for (Libro libro : libros) {
                modeloCatalogo.addRow(new Object[]{
                        libro.getId(), libro.getTitulo(), libro.getAutor(),
                        libro.getAnioPublicacion(), libro.isDisponible() ? "Sí" : "No"
                });
            }
        } catch (SQLException e) {
            mainView.mostrarError("Error al buscar libros: " + e.getMessage());
        }
    }

    private void solicitarLibro() {
        int filaSeleccionada = tablaCatalogo.getSelectedRow();
        if (filaSeleccionada == -1) return;

        int idLibro = (int) modeloCatalogo.getValueAt(filaSeleccionada, 0);
        boolean disponible = modeloCatalogo.getValueAt(filaSeleccionada, 4).equals("Sí");

        if (!disponible) {
            mainView.mostrarError("El libro seleccionado no está disponible.");
            return;
        }

        try {
            Libro libro = libroDAO.obtenerLibroPorId(idLibro);
            if (libro != null && libro.isDisponible()) {
                Prestamo prestamo = new Prestamo();
                prestamo.setLibro(libro);
                prestamo.setUsuario(usuarioLogueado);
                prestamo.setFechaPrestamo(new Date());

                Calendar c = Calendar.getInstance();
                c.setTime(new Date());
                c.add(Calendar.DATE, 15); // 15 días de préstamo
                prestamo.setFechaDevolucion(c.getTime());

                if (prestamoDAO.crear(prestamo)) {
                    libro.setDisponible(false);
                    libroDAO.actualizar(libro);
                    mainView.mostrarMensaje("Libro solicitado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarDatosCatalogo();
                    mainView.getMisPrestamosPanel().cargarDatosMisPrestamos();
                } else {
                    mainView.mostrarError("No se pudo solicitar el libro.");
                }
            }
        } catch (SQLException e) {
            mainView.mostrarError("Error al solicitar el libro: " + e.getMessage());
        }
    }

    private void exportarCatalogoAExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar como");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de Excel", "xlsx"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Catalogo de Libros");

                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < modeloCatalogo.getColumnCount(); i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(modeloCatalogo.getColumnName(i));
                }

                for (int i = 0; i < modeloCatalogo.getRowCount(); i++) {
                    Row row = sheet.createRow(i + 1);
                    for (int j = 0; j < modeloCatalogo.getColumnCount(); j++) {
                        Cell cell = row.createCell(j);
                        Object value = modeloCatalogo.getValueAt(i, j);
                        cell.setCellValue(value != null ? value.toString() : "");
                    }
                }

                try (FileOutputStream fileOut = new FileOutputStream(fileChooser.getSelectedFile().getAbsolutePath() + ".xlsx")) {
                    workbook.write(fileOut);
                }
                mainView.mostrarMensaje("Catálogo exportado a Excel con éxito.", "Exportación Exitosa", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {
                mainView.mostrarError("Error al exportar a Excel: " + e.getMessage());
            }
        }
    }
}