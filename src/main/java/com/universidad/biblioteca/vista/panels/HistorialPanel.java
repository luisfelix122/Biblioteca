package com.universidad.biblioteca.vista.panels;

import com.universidad.biblioteca.controlador.PrestamoDAO;
import com.universidad.biblioteca.modelo.Prestamo;
import com.universidad.biblioteca.modelo.Usuario;
import com.universidad.biblioteca.vista.main.MainView;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HistorialPanel extends JPanel {

    private static final Color BACKGROUND_COLOR = new Color(243, 244, 246);
    private static final Color FOREGROUND_COLOR = new Color(55, 65, 81);
    private static final Color BORDER_COLOR = new Color(209, 213, 219);
    private static final Font TEXT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    private static final String[] TABLE_HEADERS = {"ID Préstamo", "Libro", "Fecha Préstamo", "Fecha Devolución", "Multa", "Devuelto"};
    private final MainView mainView;
    private final PrestamoDAO prestamoDAO;
    private final Usuario usuarioLogueado;

    private DefaultTableModel modeloHistorial;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField campoBusqueda;
    private JComboBox<String> comboFiltroDevuelto;

    public HistorialPanel(MainView mainView, PrestamoDAO prestamoDAO, Usuario usuarioLogueado) {
        this.mainView = mainView;
        this.prestamoDAO = prestamoDAO;
        this.usuarioLogueado = usuarioLogueado;

        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(BACKGROUND_COLOR);

        initUI();
        cargarDatosHistorial();
    }

    private void initUI() {
        add(createFilterPanel(), BorderLayout.NORTH);
        add(new JScrollPane(createTable()), BorderLayout.CENTER);
    }

    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        filterPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(0, 0, 0, 15);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        filterPanel.add(new JLabel("Buscar por libro:"), gbc);

        gbc.gridx = 1;
        campoBusqueda = createStyledTextField(30);
        filterPanel.add(campoBusqueda, gbc);

        gbc.gridx = 2;
        filterPanel.add(new JLabel("Estado:"), gbc);

        gbc.gridx = 3;
        comboFiltroDevuelto = createStyledComboBox(new String[]{"Todos", "Devueltos", "No Devueltos"});
        filterPanel.add(comboFiltroDevuelto, gbc);

        gbc.gridx = 4;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        JButton botonFiltrar = createStyledButton("Filtrar", new Color(67, 56, 202), Color.WHITE);
        botonFiltrar.addActionListener(e -> aplicarFiltros());
        filterPanel.add(botonFiltrar, gbc);

        return filterPanel;
    }

    private JTable createTable() {
        modeloHistorial = new DefaultTableModel(TABLE_HEADERS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(modeloHistorial);
        sorter = new TableRowSorter<>(modeloHistorial);
        table.setRowSorter(sorter);
        styleTable(table);
        return table;
    }

    public void cargarDatosHistorial() {
        try {
            modeloHistorial.setRowCount(0);
            List<Prestamo> prestamos = prestamoDAO.obtenerPrestamosPorUsuario(usuarioLogueado.getCodigo());
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
        header.setFont(BOLD_FONT);
        header.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
    }

    private JTextField createStyledTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setFont(TEXT_FONT);
        textField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDER_COLOR, 10),
                new EmptyBorder(10, 15, 10, 15)
        ));
        textField.setBackground(Color.WHITE);
        textField.setForeground(FOREGROUND_COLOR);
        return textField;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(TEXT_FONT);
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(FOREGROUND_COLOR);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDER_COLOR, 10),
                new EmptyBorder(5, 10, 5, 10)
        ));
        return comboBox;
    }

    private JButton createStyledButton(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(12, 25, 12, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void aplicarFiltros() {
        RowFilter<DefaultTableModel, Object> rf = null;
        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        // Filtro de búsqueda por texto
        String textoBusqueda = campoBusqueda.getText().trim();
        if (!textoBusqueda.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + textoBusqueda, 1)); // Columna de Título del Libro
        }

        // Filtro de estado (devuelto/no devuelto)
        String filtroDevuelto = (String) comboFiltroDevuelto.getSelectedItem();
        if (filtroDevuelto != null && !filtroDevuelto.equals("Todos")) {
            String valor = filtroDevuelto.equals("Devueltos") ? "Sí" : "No";
            filters.add(RowFilter.regexFilter("^" + valor + "$", 5)); // Columna de Devuelto
        }

        if (!filters.isEmpty()) {
            rf = RowFilter.andFilter(filters);
        }

        sorter.setRowFilter(rf);
    }
}