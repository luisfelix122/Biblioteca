package com.universidad.biblioteca.vista.bibliotecario;

import com.universidad.biblioteca.controlador.LibroDAO;
import com.universidad.biblioteca.modelo.Libro;
import com.universidad.biblioteca.modelo.Usuario;
import com.universidad.biblioteca.vista.utils.RoundedBorder;


import javax.swing.*;
import javax.swing.border.EmptyBorder;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

import java.sql.SQLException;
import java.util.List;

public class CatalogoPanelBibliotecario extends JPanel {

    private static final Color BACKGROUND_COLOR = new Color(243, 244, 246);
    private static final Color FOREGROUND_COLOR = new Color(55, 65, 81);
    private static final Color BORDER_COLOR = new Color(209, 213, 219);
    private static final Font TEXT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    private static final Color SECONDARY_BUTTON_COLOR = new Color(249, 115, 22);

    private final LibroDAO libroDAO;
    private final Usuario usuarioLogueado;

    private JTable tablaCatalogo;
    private DefaultTableModel modeloCatalogo;
    private int totalPaginas = 1;
    private JTextField campoBusquedaTitulo;
    private JTextField campoBusquedaAutor;
    private JComboBox<String> comboDisponibilidad;
    private static final String[] TABLE_HEADERS = {"ISBN", "Título", "Autor", "Año", "Disponible"};
    private int paginaActual = 1;
    private static final int FILAS_POR_PAGINA = 15;
    private JLabel labelPaginacion;
    private JButton botonPrimera, botonAnterior, botonSiguiente, botonUltima;


    public CatalogoPanelBibliotecario(Usuario usuarioLogueado) {
        this.libroDAO = new LibroDAO(); // Initialize LibroDAO here
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
        cargarLibros(paginaActual);
    }

    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBackground(BACKGROUND_COLOR);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Filtros y Búsqueda"),
                new EmptyBorder(10, 10, 10, 10)
        ));
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
        campoBusquedaTitulo = createStyledTextField("");
        filterPanel.add(campoBusquedaTitulo, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.0;
        filterPanel.add(new JLabel("Autor:"), gbc);

        gbc.gridx = 3;
        gbc.weightx = 1.0;
        campoBusquedaAutor = createStyledTextField("");
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
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                System.out.println("Autor field insert: " + campoBusquedaAutor.getText());
                filtrarCatalogo();
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                System.out.println("Autor field remove: " + campoBusquedaAutor.getText());
                filtrarCatalogo();
            }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                System.out.println("Autor field changed: " + campoBusquedaAutor.getText());
                filtrarCatalogo();
            }
        });
        campoBusquedaAutor.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrarCatalogo(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrarCatalogo(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrarCatalogo(); }
        });
        comboDisponibilidad.addActionListener(_ -> filtrarCatalogo());



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

        JScrollPane scrollPane = new JScrollPane(tablaCatalogo);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        return scrollPane;
    }

    private JPanel createActionsPanel() {
        // Panel principal de acciones con BorderLayout
        JPanel panelContenedor = new JPanel(new BorderLayout());
        panelContenedor.setBackground(BACKGROUND_COLOR);
        panelContenedor.setBorder(new EmptyBorder(10, 0, 0, 0));

        // Panel para los botones de la derecha (Exportar)
        JPanel panelBotonesDerecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotonesDerecha.setBackground(BACKGROUND_COLOR);

        // Botón de solicitar libro (solo para estudiantes)
        if (usuarioLogueado != null && "Estudiante".equals(usuarioLogueado.getRol().getNombre())) {
            JButton botonSolicitarLibro = createStyledButton("Solicitar Libro", new Color(52, 152, 219), Color.WHITE);
            panelBotonesDerecha.add(botonSolicitarLibro);
            botonSolicitarLibro.addActionListener(_ -> {
                int selectedRow = tablaCatalogo.getSelectedRow();
                if (selectedRow != -1) {
                    String isbn = (String) modeloCatalogo.getValueAt(selectedRow, 0);
                    String titulo = (String) modeloCatalogo.getValueAt(selectedRow, 1);
                    JOptionPane.showMessageDialog(this, "Solicitar libro: " + titulo + " (ISBN: " + isbn + ")", "Solicitar Libro", JOptionPane.INFORMATION_MESSAGE);
                    // Further logic for requesting the book would go here
                } else {
                    JOptionPane.showMessageDialog(this, "Por favor, selecciona un libro de la tabla para solicitar.", "Error", JOptionPane.WARNING_MESSAGE);
                }
            });
        }

        // Panel de paginación (izquierda)
        JPanel panelPaginacion = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelPaginacion.setBackground(BACKGROUND_COLOR);
        panelPaginacion.add(botonPrimera);
        panelPaginacion.add(botonAnterior);
        panelPaginacion.add(labelPaginacion);
        panelPaginacion.add(botonSiguiente);
        panelPaginacion.add(botonUltima);

        // Añadir listeners a los botones de paginación
        botonPrimera.addActionListener(_ -> irAPagina(1));
        botonAnterior.addActionListener(_ -> irAPagina(paginaActual - 1));
        botonSiguiente.addActionListener(_ -> irAPagina(paginaActual + 1));
        botonUltima.addActionListener(_ -> irAPagina(totalPaginas));

        panelContenedor.add(panelPaginacion, BorderLayout.WEST);
        panelContenedor.add(panelBotonesDerecha, BorderLayout.EAST);

        return panelContenedor;
    }

    public void cargarLibros(int pagina) {
        try {
            String titulo = campoBusquedaTitulo.getText().trim();
            String autor = campoBusquedaAutor.getText().trim();
            String disponibilidad = (String) comboDisponibilidad.getSelectedItem();

            System.out.println("Cargando libros con: Titulo='" + titulo + "', Autor='" + autor + "', Disponibilidad='" + disponibilidad + "'");

            int offset = (pagina - 1) * FILAS_POR_PAGINA;
            List<Libro> libros = libroDAO.buscarLibrosPaginado(titulo, autor, disponibilidad, offset, FILAS_POR_PAGINA);
            int totalLibros = libroDAO.contarLibros(titulo, autor, disponibilidad);
            totalPaginas = (int) Math.ceil((double) totalLibros / FILAS_POR_PAGINA);

            modeloCatalogo.setRowCount(0); // Limpiar tabla
            for (Libro libro : libros) {
                modeloCatalogo.addRow(new Object[]{
                        libro.getIsbn(),
                        libro.getTitulo(),
                        libro.getAutor(),
                        libro.getAnioPublicacion(),
                        libro.isDisponible() ? "Sí" : "No"
                });
            }
            paginaActual = pagina;
            labelPaginacion.setText("Página " + paginaActual + " de " + totalPaginas);
            actualizarEstadoBotonesPaginacion();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar libros: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void filtrarCatalogo() {
        irAPagina(1);
    }

    private void irAPagina(int pagina) {
        if (pagina >= 1 && pagina <= totalPaginas) {
            cargarLibros(pagina);
        }
    }

    private void actualizarEstadoBotonesPaginacion() {
        botonPrimera.setEnabled(paginaActual > 1);
        botonAnterior.setEnabled(paginaActual > 1);
        botonSiguiente.setEnabled(paginaActual < totalPaginas);
        botonUltima.setEnabled(paginaActual < totalPaginas);
    }







    private JTextField createStyledTextField(String placeholder) {
        JTextField textField = new JTextField(placeholder);
        textField.setFont(TEXT_FONT);
        textField.setForeground(FOREGROUND_COLOR);
        textField.setBackground(Color.WHITE);
        textField.setBorder(new RoundedBorder(BORDER_COLOR, 8));
        textField.setPreferredSize(new Dimension(250, 35));
        return textField;
    }

    private JButton createStyledButton(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setBorder(new RoundedBorder(background, 8));
        button.setPreferredSize(new Dimension(150, 35));
        return button;
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(TEXT_FONT);
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(FOREGROUND_COLOR);
        comboBox.setBorder(new RoundedBorder(BORDER_COLOR, 8));
        comboBox.setPreferredSize(new Dimension(450, 40));
        comboBox.setMaximumSize(new Dimension(450, 35));
        comboBox.getEditor().getEditorComponent().setFont(TEXT_FONT);
    }

    private void styleTable(JTable table) {
        table.setFont(TEXT_FONT);
        table.setBackground(Color.WHITE);
        table.setForeground(FOREGROUND_COLOR);
        table.setGridColor(BORDER_COLOR);
        table.setRowHeight(25);
        table.setSelectionBackground(new Color(209, 213, 219));
        table.setSelectionForeground(FOREGROUND_COLOR);

        JTableHeader header = table.getTableHeader();
        header.setFont(BUTTON_FONT);
        header.setBackground(new Color(229, 231, 235));
        header.setForeground(FOREGROUND_COLOR);
        header.setPreferredSize(new Dimension(header.getWidth(), 30));
    }
}