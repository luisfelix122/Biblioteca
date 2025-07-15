package com.universidad.biblioteca.vista.estudiante;

import com.universidad.biblioteca.controlador.LibroDAO;
import com.universidad.biblioteca.controlador.PrestamoDAO;
import com.universidad.biblioteca.modelo.Libro;
import com.universidad.biblioteca.modelo.Prestamo;
import com.universidad.biblioteca.modelo.Usuario;
import com.universidad.biblioteca.vista.main.MainView;
import com.universidad.biblioteca.vista.utils.RoundedBorder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CatalogoPanel extends JPanel {

    private static final Color BACKGROUND_COLOR = new Color(243, 244, 246);
    private static final Color FOREGROUND_COLOR = new Color(55, 65, 81);
    private static final Color BORDER_COLOR = new Color(209, 213, 219);
    private static final Font TEXT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    private final MainView mainView;
    private final LibroDAO libroDAO;
    private final PrestamoDAO prestamoDAO;
    private final Usuario usuarioLogueado;

    private JTable tablaCatalogo;
    private DefaultTableModel modeloCatalogo;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField campoBusquedaTitulo;
    private JTextField campoBusquedaAutor;
    private JComboBox<String> comboDisponibilidad;
    private JButton botonSolicitar;
    private static final String[] TABLE_HEADERS = {"ISBN", "Título", "Autor", "Año", "Disponible"};


    public CatalogoPanel(MainView mainView, LibroDAO libroDAO, PrestamoDAO prestamoDAO, Usuario usuarioLogueado) {
        this.mainView = mainView;
        this.libroDAO = libroDAO;
        this.prestamoDAO = prestamoDAO;
        this.usuarioLogueado = usuarioLogueado;

        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(BACKGROUND_COLOR);

        initUI();
        cargarDatosCatalogo();
    }

    private void initUI() {
        // 1. Panel de Filtros y Búsqueda (Superior)
        add(createFilterPanel(), BorderLayout.NORTH);

        // 2. Tabla de Catálogo (Centro)
        add(createTablePanel(), BorderLayout.CENTER);

        // 3. Panel de Acciones (Inferior)
        add(createActionsPanel(), BorderLayout.SOUTH);
    }

    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.X_AXIS));
        filterPanel.setBackground(BACKGROUND_COLOR);
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filtros y Búsqueda"));

        campoBusquedaTitulo = createStyledTextField("Buscar por título...");
        addPlaceholderStyle(campoBusquedaTitulo);
        addFocusListener(campoBusquedaTitulo);

        campoBusquedaAutor = createStyledTextField("Buscar por autor...");
        addPlaceholderStyle(campoBusquedaAutor);
        addFocusListener(campoBusquedaAutor);
        comboDisponibilidad = new JComboBox<>(new String[]{"Todos", "Disponibles", "No Disponibles"});
        styleComboBox(comboDisponibilidad);

        // Listener para búsqueda en tiempo real
        DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrarCatalogo();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarCatalogo();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarCatalogo();
            }
        };

        campoBusquedaTitulo.getDocument().addDocumentListener(documentListener);
        campoBusquedaAutor.getDocument().addDocumentListener(documentListener);
        comboDisponibilidad.addActionListener(e -> filtrarCatalogo());

        filterPanel.add(campoBusquedaTitulo);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(campoBusquedaAutor);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(comboDisponibilidad);

        return filterPanel;
    }

    private JScrollPane createTablePanel() {
        modeloCatalogo = new DefaultTableModel(TABLE_HEADERS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaCatalogo = new JTable(modeloCatalogo);
        sorter = new TableRowSorter<>(modeloCatalogo);
        tablaCatalogo.setRowSorter(sorter);

        styleTable(tablaCatalogo);

        tablaCatalogo.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarEstadoBotonSolicitar();
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaCatalogo);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        return scrollPane;
    }

    private JPanel createActionsPanel() {
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setBackground(BACKGROUND_COLOR);

        botonSolicitar = createStyledButton("Solicitar Libro", new Color(34, 197, 94), Color.WHITE);
        botonSolicitar.setEnabled(false);
        botonSolicitar.addActionListener(e -> solicitarLibro());

        if (!"BIBLIOTECARIO".equals(usuarioLogueado.getRol())) {
            actionsPanel.add(botonSolicitar);
        }

        JButton botonExportar = createStyledButton("Exportar a Excel", new Color(249, 115, 22), Color.WHITE);
        botonExportar.addActionListener(e -> exportarCatalogoAExcel());
        actionsPanel.add(botonExportar);

        return actionsPanel;
    }

    public void cargarDatosCatalogo() {
        try {
            List<Libro> libros = libroDAO.obtenerTodosLosLibros();
            poblarTabla(libros);
        } catch (SQLException e) {
            mainView.mostrarError("Error al cargar el catálogo: " + e.getMessage());
        }
    }

    private void filtrarCatalogo() {
        String titulo = campoBusquedaTitulo.getText().trim();
        String autor = campoBusquedaAutor.getText().trim();
        String disponibilidad = (String) comboDisponibilidad.getSelectedItem();

        // Si los campos de texto tienen el texto de placeholder, se consideran vacíos
        if (titulo.equals("Buscar por título...")) {
            titulo = "";
        }
        if (autor.equals("Buscar por autor...")) {
            autor = "";
        }

        try {
            List<RowFilter<Object, Object>> filters = new java.util.ArrayList<>();

            if (!titulo.isEmpty()) {
                filters.add(RowFilter.regexFilter("(?i)" + titulo, 1));
            }
            if (!autor.isEmpty()) {
                filters.add(RowFilter.regexFilter("(?i)" + autor, 2));
            }
            if (disponibilidad != null && !disponibilidad.equals("Todos")) {
                String filtroDisponibilidad = disponibilidad.equals("Disponibles") ? "Sí" : "No";
                filters.add(RowFilter.regexFilter(filtroDisponibilidad, 4));
            }

            if (filters.isEmpty()) {
                sorter.setRowFilter(null); // Si no hay filtros, muestra toda la tabla
            } else {
                sorter.setRowFilter(RowFilter.andFilter(filters));
            }
        } catch (java.util.regex.PatternSyntaxException e) {
            mainView.mostrarError("Error en el formato de búsqueda.");
        }
    }

    private void poblarTabla(List<Libro> libros) {
        modeloCatalogo.setRowCount(0);
        for (Libro libro : libros) {
            modeloCatalogo.addRow(new Object[]{
                    libro.getIsbn(), libro.getTitulo(), libro.getAutor(),
                    libro.getAnioPublicacion(), libro.isDisponible() ? "Sí" : "No"
            });
        }
    }

    private void actualizarEstadoBotonSolicitar() {
        int selectedRow = tablaCatalogo.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = tablaCatalogo.convertRowIndexToModel(selectedRow);
            boolean disponible = modeloCatalogo.getValueAt(modelRow, 4).equals("Sí");
            botonSolicitar.setEnabled(disponible);
        } else {
            botonSolicitar.setEnabled(false);
        }
    }



    private void styleTable(JTable table) {
        table.setBackground(Color.WHITE);
        table.setForeground(FOREGROUND_COLOR);
        table.setSelectionBackground(new Color(199, 210, 254));
        table.setSelectionForeground(FOREGROUND_COLOR);
        table.setFont(TEXT_FONT);
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(229, 231, 235));
        header.setForeground(new Color(107, 114, 128));
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField textField = new JTextField(placeholder);
        textField.setFont(TEXT_FONT);
        textField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDER_COLOR, 10),
                new EmptyBorder(10, 15, 10, 15)
        ));
        textField.setBackground(Color.WHITE);
        textField.setForeground(FOREGROUND_COLOR);
        return textField;
    }

    private void addPlaceholderStyle(JTextField textField) {
        textField.setForeground(Color.GRAY);
        textField.setFont(new Font("Segoe UI", Font.ITALIC, 14));
    }

    private void addFocusListener(JTextField textField) {
        String placeholder = textField.getText();
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(FOREGROUND_COLOR);
                    textField.setFont(TEXT_FONT);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    addPlaceholderStyle(textField);
                    textField.setText(placeholder);
                }
            }
        });
    }

    private void solicitarLibro() {
        int filaSeleccionada = tablaCatalogo.getSelectedRow();
        if (filaSeleccionada == -1) return;

        int idLibro = (int) modeloCatalogo.getValueAt(tablaCatalogo.convertRowIndexToModel(filaSeleccionada), 0);

        try {
            Libro libro = libroDAO.obtenerPorId(idLibro);
            if (libro != null) {
                if (!libro.isDisponible()) {
                    mainView.mostrarError("El libro seleccionado ya no está disponible. Refresca el catálogo.");
                    return;
                }

                Prestamo prestamo = new Prestamo();
                prestamo.setLibro(libro);
                prestamo.setUsuario(usuarioLogueado);

                prestamo.setFechaPrestamo(new Date());

                Calendar c = Calendar.getInstance();
                c.setTime(new Date());
                c.add(Calendar.DAY_OF_MONTH, 7); // Fecha de devolución 7 días después
                prestamo.setFechaDevolucion(c.getTime());
                prestamo.setMulta(0.0);
                prestamo.setDevuelto(false);

                prestamoDAO.registrarPrestamo(prestamo);
                libro.setDisponible(false);
                libroDAO.actualizar(libro);

                mainView.mostrarMensaje("Libro \"" + libro.getTitulo() + "\" solicitado con éxito.");
                cargarDatosCatalogo(); // Refrescar la tabla
                mainView.cargarMisPrestamos(); // Actualizar la vista de Mis Préstamos

            } else {
                mainView.mostrarError("Libro no encontrado.");
            }
        } catch (SQLException ex) {
            mainView.mostrarError("Error al solicitar el libro: " + ex.getMessage());
        }
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(TEXT_FONT);
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(FOREGROUND_COLOR);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDER_COLOR, 10),
                new EmptyBorder(5, 5, 5, 5)
        ));
    }

    private JButton createStyledButton(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
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