package com.universidad.biblioteca.vista.estudiante;

import com.universidad.biblioteca.controlador.LibroDAO;
import com.universidad.biblioteca.controlador.PrestamoDAO;
import com.universidad.biblioteca.modelo.Libro;
import com.universidad.biblioteca.modelo.Prestamo;
import com.universidad.biblioteca.modelo.Usuario;

import com.universidad.biblioteca.vista.utils.RoundedBorder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
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
    private static final Color PRIMARY_BUTTON_COLOR = new Color(34, 197, 94);
    private static final Color SECONDARY_BUTTON_COLOR = new Color(249, 115, 22);

    private final com.universidad.biblioteca.vista.main.MainView mainView;
    private final LibroDAO libroDAO;
    private final PrestamoDAO prestamoDAO;
    private final Usuario usuarioLogueado;

    private JTable tablaCatalogo;
    private DefaultTableModel modeloCatalogo;
    private int totalPaginas = 1;
    private JTextField campoBusquedaTitulo;
    private JTextField campoBusquedaAutor;
    private JComboBox<String> comboDisponibilidad;
    private JButton botonSolicitar;
    private static final String[] TABLE_HEADERS = {"ISBN", "Título", "Autor", "Año", "Disponible"};
    private int paginaActual = 1;
    private static final int FILAS_POR_PAGINA = 15;
    private JLabel labelPaginacion;
    private JButton botonPrimera, botonAnterior, botonSiguiente, botonUltima;


    public CatalogoPanel(com.universidad.biblioteca.vista.main.MainView mainView, LibroDAO libroDAO, PrestamoDAO prestamoDAO, Usuario usuarioLogueado) {
        this.mainView = mainView;
        this.libroDAO = libroDAO;
        this.prestamoDAO = prestamoDAO;
        this.usuarioLogueado = usuarioLogueado;

        // Initialize pagination components here
        labelPaginacion = new JLabel("Página 1");
        botonPrimera = createStyledButton("|<", SECONDARY_BUTTON_COLOR, Color.WHITE);
        botonAnterior = createStyledButton("<", SECONDARY_BUTTON_COLOR, Color.WHITE);
        botonSiguiente = createStyledButton(">", SECONDARY_BUTTON_COLOR, Color.WHITE);
        botonUltima = createStyledButton(">|", SECONDARY_BUTTON_COLOR, Color.WHITE);

        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(BACKGROUND_COLOR);

        if (usuarioLogueado != null && ("Estudiante".equals(usuarioLogueado.getRol().getNombre()) || "Bibliotecario".equals(usuarioLogueado.getRol().getNombre()) || "Administrador".equals(usuarioLogueado.getRol().getNombre()))) {
            initUI();

        } else {
            mostrarAccesoDenegado();
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
        // 1. Tabla de Catálogo (Centro)
        add(createTablePanel(), BorderLayout.CENTER);

        // 2. Panel de Filtros y Búsqueda (Superior)
        add(createFilterPanel(), BorderLayout.NORTH);

        // 3. Panel de Acciones (Inferior)
        add(createActionsPanel(), BorderLayout.SOUTH);
    }

    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBackground(BACKGROUND_COLOR);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Filtros y Búsqueda"),
                new EmptyBorder(10, 10, 10, 10)
        ));
        // Apply RoundedBorder to the filter panel
        filterPanel.setBorder(new RoundedBorder(BORDER_COLOR, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fila 1: Título y Autor
        gbc.gridx = 0;
        gbc.gridy = 0;
        filterPanel.add(new JLabel("Título:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        campoBusquedaTitulo = createStyledTextField("Buscar por título...");
        filterPanel.add(campoBusquedaTitulo, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.0;
        filterPanel.add(new JLabel("Autor:"), gbc);

        gbc.gridx = 3;
        gbc.weightx = 1.0;
        campoBusquedaAutor = createStyledTextField("Buscar por autor...");
        filterPanel.add(campoBusquedaAutor, gbc);

        // Fila 2: Disponibilidad y botón de búsqueda
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        filterPanel.add(new JLabel("Disponibilidad:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        comboDisponibilidad = new JComboBox<>(new String[]{"Todos", "Disponibles", "No Disponibles"});
        styleComboBox(comboDisponibilidad);
        filterPanel.add(comboDisponibilidad, gbc);

        // Listeners para búsqueda en tiempo real
        campoBusquedaTitulo.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrarCatalogo(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrarCatalogo(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrarCatalogo(); }
        });
        campoBusquedaAutor.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrarCatalogo(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrarCatalogo(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrarCatalogo(); }
        });
        comboDisponibilidad.addActionListener(_ -> filtrarCatalogo());

        addFocusListener(campoBusquedaTitulo, "Buscar por título...");
        addFocusListener(campoBusquedaAutor, "Buscar por autor...");

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
        styleTable(tablaCatalogo);

        tablaCatalogo.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                actualizarEstadoBotonSolicitar();
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaCatalogo);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        return scrollPane;
    }

    private JPanel createActionsPanel() {
        // Panel principal de acciones con BorderLayout
        JPanel panelContenedor = new JPanel(new BorderLayout());
        panelContenedor.setBackground(BACKGROUND_COLOR);
        panelContenedor.setBorder(new EmptyBorder(10, 0, 0, 0));

        // Panel para los botones de la derecha (Solicitar, Exportar)
        JPanel panelBotonesDerecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotonesDerecha.setBackground(BACKGROUND_COLOR);

        botonSolicitar = createStyledButton("Solicitar Libro", PRIMARY_BUTTON_COLOR, Color.WHITE);
        botonSolicitar.setEnabled(false);
        botonSolicitar.addActionListener(_ -> solicitarLibro());

        if (!"Bibliotecario".equals(usuarioLogueado.getRol().getNombre()) && !"Administrador".equals(usuarioLogueado.getRol().getNombre())) {
            panelBotonesDerecha.add(botonSolicitar);
        }

        JButton botonExportar = createStyledButton("Exportar a Excel", SECONDARY_BUTTON_COLOR, Color.WHITE);
        botonExportar.addActionListener(_ -> exportarCatalogoAExcel());
        panelBotonesDerecha.add(botonExportar);

        // Panel para la paginación a la izquierda
        JPanel panelPaginacion = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelPaginacion.setBackground(BACKGROUND_COLOR);

        labelPaginacion.setFont(TEXT_FONT);

        botonPrimera.addActionListener(_ -> {
            paginaActual = 1;
            actualizarCatalogo();
        });
        botonAnterior.addActionListener(_ -> {
            if (paginaActual > 1) {
                paginaActual--;
                actualizarCatalogo();
            }
        });
        botonSiguiente.addActionListener(_ -> {
            if (paginaActual < totalPaginas) {
                paginaActual++;
                actualizarCatalogo();
            }
        });
        botonUltima.addActionListener(_ -> {
            paginaActual = totalPaginas;
            actualizarCatalogo();
        });

        panelPaginacion.add(botonPrimera);
        panelPaginacion.add(botonAnterior);
        panelPaginacion.add(labelPaginacion);
        panelPaginacion.add(botonSiguiente);
        panelPaginacion.add(botonUltima);

        // Añadir los paneles al contenedor principal
        panelContenedor.add(panelPaginacion, BorderLayout.WEST);
        panelContenedor.add(panelBotonesDerecha, BorderLayout.EAST);

        return panelContenedor;
    }

    public void cargarDatosCatalogo() {
        paginaActual = 1;
        actualizarCatalogo();
    }

    private void actualizarCatalogo() {
        java.sql.Connection conn = null;
        try {
            conn = com.universidad.biblioteca.config.ConexionBD.obtenerConexion();
            conn.setAutoCommit(false);

            String titulo = campoBusquedaTitulo.getText().trim();
            String autor = campoBusquedaAutor.getText().trim();
            String disponibilidad = (String) comboDisponibilidad.getSelectedItem();

            if (titulo.equals("Buscar por título...")) {
                titulo = "";
            }
            if (autor.equals("Buscar por autor...")) {
                autor = "";
            }

            int totalLibros = libroDAO.contarLibros(conn, titulo, autor, disponibilidad);
            actualizarControlesPaginacion(totalLibros);

            List<Libro> libros = libroDAO.buscarLibrosPaginado(conn, titulo, autor, disponibilidad, (paginaActual - 1) * FILAS_POR_PAGINA, FILAS_POR_PAGINA);
            poblarTabla(libros);
            conn.commit();
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                mainView.mostrarError("Error al intentar revertir la transacción: " + ex.getMessage());
            }
            mainView.mostrarError("Error al cargar el catálogo: " + e.getMessage());
        }
    }

    private void actualizarControlesPaginacion(int totalLibros) {
        totalPaginas = (int) Math.ceil((double) totalLibros / FILAS_POR_PAGINA);
        if (totalPaginas == 0) {
            totalPaginas = 1;
        }

        labelPaginacion.setText("Página " + paginaActual + " de " + totalPaginas);

        if (botonPrimera == null) {
            System.out.println("DEBUG: botonPrimera is null in actualizarControlesPaginacion");
        }
        botonPrimera.setEnabled(paginaActual > 1);
        botonAnterior.setEnabled(paginaActual > 1);
        botonSiguiente.setEnabled(paginaActual < totalPaginas);
        botonUltima.setEnabled(paginaActual < totalPaginas);
    }

    private void filtrarCatalogo() {
        paginaActual = 1;
        actualizarCatalogo();
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

    private void addFocusListener(JTextField textField, String placeholder) {
        textField.setForeground(Color.GRAY);
        textField.setText(placeholder);

        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(FOREGROUND_COLOR);
                }
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(placeholder);
                }
            }
        });
    }

    private void solicitarLibro() {
        int filaSeleccionada = tablaCatalogo.getSelectedRow();
        if (filaSeleccionada == -1) return;

        String idLibro = (String) modeloCatalogo.getValueAt(tablaCatalogo.convertRowIndexToModel(filaSeleccionada), 0);

        java.sql.Connection conn = null;
        try {
            conn = com.universidad.biblioteca.config.ConexionBD.obtenerConexion();
            conn.setAutoCommit(false);

            Libro libro = libroDAO.obtenerPorId(conn, idLibro);
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

                prestamoDAO.registrarPrestamo(conn, prestamo);
                libro.setDisponible(false);
                libroDAO.actualizar(conn, libro);

                conn.commit();
            mainView.mostrarMensaje("Libro \"" + libro.getTitulo() + "\" solicitado con éxito.");
            cargarDatosCatalogo(); // Refrescar la tabla
            mainView.getMisPrestamosPanel().cargarDatosMisPrestamos(); // Actualizar la vista de Mis Préstamos

            } else {
                mainView.mostrarError("Libro no encontrado.");
            }
        } catch (SQLException ex) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                mainView.mostrarError("Error al intentar revertir la transacción: " + rollbackEx.getMessage());
            }
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