package com.universidad.biblioteca.vista.bibliotecario;

import com.universidad.biblioteca.controlador.PrestamoDAO;
import com.universidad.biblioteca.modelo.Prestamo;
import com.universidad.biblioteca.modelo.Usuario;
import com.universidad.biblioteca.vista.utils.RoundedBorder;

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

    private DefaultTableModel modeloHistorial;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField campoBusqueda;
    private JComboBox<String> comboFiltroDevuelto;

    private PrestamoDAO prestamoDAO;

    private static final Color BACKGROUND_COLOR = new Color(243, 244, 246);
    private static final Color FOREGROUND_COLOR = new Color(55, 65, 81);
    private static final Color BORDER_COLOR = new Color(209, 213, 219);
    private static final Color PRIMARY_BUTTON_COLOR = new Color(67, 56, 202);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font TEXT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    private static final String[] TABLE_HEADERS = {"ID Préstamo", "Libro", "Usuario", "Fecha Préstamo", "Fecha Devolución", "Multa", "Devuelto"};



    public HistorialPanel(Usuario usuario) {
        this.prestamoDAO = new PrestamoDAO();

        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(BACKGROUND_COLOR);

        String rol = usuario.getRol().getNombre();
        if (!rol.equals("Bibliotecario") && !rol.equals("Administrador")) {
            JOptionPane.showMessageDialog(this, "Acceso denegado. No tienes permiso para acceder a esta sección.", "Error de acceso", JOptionPane.ERROR_MESSAGE);
            add(new JLabel("Acceso denegado"));
        } else {
            initUI();
            cargarDatosHistorial();
        }
    }

    private void initUI() {
        // Título
        JLabel titleLabel = new JLabel("Historial de Préstamos", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(FOREGROUND_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Panel de contenido que incluye filtros y tabla
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.add(createFilterPanel(), BorderLayout.NORTH);
        contentPanel.add(new JScrollPane(createTable()), BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 5));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(15, 20, 15, 20)
        ));

        filterPanel.add(new JLabel("Buscar por libro:"));
        campoBusqueda = createStyledTextField(25);
        filterPanel.add(campoBusqueda);

        filterPanel.add(new JLabel("Estado:"));
        comboFiltroDevuelto = createStyledComboBox(new String[]{"Todos", "Devueltos", "No Devueltos"});
        JPanel comboPanel = new JPanel(new BorderLayout());
        comboPanel.add(comboFiltroDevuelto, BorderLayout.CENTER);
        filterPanel.add(comboPanel);

        JButton botonFiltrar = createStyledButton("Filtrar", PRIMARY_BUTTON_COLOR, Color.WHITE);
        botonFiltrar.addActionListener(_ -> aplicarFiltros());
        filterPanel.add(botonFiltrar);

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
            List<Prestamo> prestamos = prestamoDAO.obtenerTodosLosPrestamos();
            for (Prestamo prestamo : prestamos) {
                modeloHistorial.addRow(new Object[]{
                        prestamo.getId(),
                        prestamo.getLibro() != null ? prestamo.getLibro().getTitulo() : "(Libro no disponible)",
                        prestamo.getUsuario() != null ? prestamo.getUsuario().getNombre() : "(Usuario no disponible)",
                        prestamo.getFechaPrestamo(),
                        prestamo.getFechaDevolucion(),
                        prestamo.getMulta(),
                        prestamo.isDevuelto() ? "Sí" : "No"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar el historial de préstamos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    private JTextField createStyledTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setFont(TEXT_FONT);
        textField.setBackground(Color.WHITE);
        textField.setForeground(FOREGROUND_COLOR);
        textField.setBorder(new RoundedBorder(BORDER_COLOR, 10));
        textField.setPreferredSize(new Dimension(textField.getPreferredSize().width, 35));
        return textField;
    }

    private <T> JComboBox<T> createStyledComboBox(T[] items) {
        JComboBox<T> comboBox = new JComboBox<>(items);
        comboBox.setFont(TEXT_FONT);
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(FOREGROUND_COLOR);
        // comboBox.setBorder(new RoundedBorder(BORDER_COLOR, 10));
        comboBox.setPreferredSize(new Dimension(150, 35));
        comboBox.setMinimumSize(new Dimension(150, 35));
        comboBox.setMaximumSize(new Dimension(150, 35));
        comboBox.setEditable(false);
        return comboBox;
    }

    private JButton createStyledButton(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setBorder(new RoundedBorder(background, 10));
        button.setPreferredSize(new Dimension(button.getPreferredSize().width, 35));
        return button;
    }
}