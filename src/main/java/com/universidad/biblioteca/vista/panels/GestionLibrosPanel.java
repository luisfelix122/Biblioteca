package com.universidad.biblioteca.vista.panels;
import com.universidad.biblioteca.controlador.LibroDAO;
import com.universidad.biblioteca.modelo.Libro;
import com.universidad.biblioteca.vista.main.MainView;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;
public class GestionLibrosPanel extends JPanel {
    private static final Color BACKGROUND_COLOR = new Color(243, 244, 246);
    private static final Color FOREGROUND_COLOR = new Color(55, 65, 81);
    private static final Color BORDER_COLOR = new Color(209, 213, 219);
    private static final Font TEXT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private final MainView mainView;
    private final LibroDAO libroDAO;
    private JTable tablaLibros;
    private DefaultTableModel modeloTabla;

    public GestionLibrosPanel(MainView mainView, LibroDAO libroDAO) {
        this.mainView = mainView;
        this.libroDAO = libroDAO;
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(BACKGROUND_COLOR);
        initUI();
        cargarLibros();
    }

    private void initUI() {
        // Panel de Acciones (Norte)
        add(createActionsPanel(), BorderLayout.NORTH);

        // Tabla de Libros (Centro)
        JScrollPane scrollPane = new JScrollPane(createTable());
        add(scrollPane, BorderLayout.CENTER);
    }

    private JTable createTable() {
        modeloTabla = new DefaultTableModel(new String[]{"ID", "Título", "Autor", "Año", "Disponible"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaLibros = new JTable(modeloTabla);
        styleTable(tablaLibros);
        return tablaLibros;
    }

    private JPanel createActionsPanel() {
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionsPanel.setBackground(BACKGROUND_COLOR);

        JButton btnAgregar = createStyledButton("Agregar Libro", new Color(67, 56, 202), Color.WHITE);
        btnAgregar.addActionListener(e -> agregarLibro());

        JButton btnEditar = createStyledButton("Editar Libro", new Color(249, 115, 22), Color.WHITE);
        btnEditar.addActionListener(e -> editarLibro());

        JButton btnEliminar = createStyledButton("Eliminar Libro", new Color(239, 68, 68), Color.WHITE);
        btnEliminar.addActionListener(e -> eliminarLibro());

        actionsPanel.add(btnAgregar);
        actionsPanel.add(btnEditar);
        actionsPanel.add(btnEliminar);
        return actionsPanel;
    }

    private void cargarLibros() {
        try {
            modeloTabla.setRowCount(0);
            for (Libro libro : libroDAO.obtenerTodosLosLibros()) {
                modeloTabla.addRow(new Object[]{libro.getIsbn(), libro.getTitulo(), libro.getAutor(), libro.getAnioPublicacion(), libro.isDisponible() ? "Sí" : "No"});
            }
        } catch (SQLException e) {
            mainView.mostrarError("Error al cargar los libros: " + e.getMessage());
        }
    }

    private void agregarLibro() {
        JPanel panel = createFormPanel(null);
        int option = JOptionPane.showConfirmDialog(this, panel, "Agregar Nuevo Libro", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            try {
                Libro libro = new Libro();
                libro.setTitulo(((JTextField) panel.getComponent(1)).getText());
                libro.setAutor(((JTextField) panel.getComponent(3)).getText());
                libro.setAnioPublicacion(Integer.parseInt(((JTextField) panel.getComponent(5)).getText()));
                libro.setDisponible(true);
                libroDAO.insertar(libro);
                cargarLibros();
                mainView.getCatalogoPanel().cargarDatosCatalogo();
            } catch (NumberFormatException ex) {
                mainView.mostrarError("El año debe ser un número válido.");
            } catch (SQLException ex) {
                mainView.mostrarError("Error al agregar el libro: " + ex.getMessage());
            }
        }
    }

    private void editarLibro() {
        int selectedRow = tablaLibros.getSelectedRow();
        if (selectedRow == -1) {
            mainView.mostrarMensaje("Por favor, seleccione un libro para editar.");
            return;
        }

        int libroId = (int) modeloTabla.getValueAt(selectedRow, 0);
        try {
            Libro libro = libroDAO.obtenerPorId(libroId);
            if (libro != null) {
                JPanel panel = createFormPanel(libro);
                int option = JOptionPane.showConfirmDialog(this, panel, "Editar Libro", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (option == JOptionPane.OK_OPTION) {
                    libro.setTitulo(((JTextField) panel.getComponent(1)).getText());
                    libro.setAutor(((JTextField) panel.getComponent(3)).getText());
                    libro.setAnioPublicacion(Integer.parseInt(((JTextField) panel.getComponent(5)).getText()));
                    libroDAO.actualizar(libro);
                    cargarLibros();
                    mainView.getCatalogoPanel().cargarDatosCatalogo();
                }
            }
        } catch (NumberFormatException ex) {
            mainView.mostrarError("El año debe ser un número válido.");
        } catch (SQLException ex) {
            mainView.mostrarError("Error al editar el libro: " + ex.getMessage());
        }
    }

    private void eliminarLibro() {
        int selectedRow = tablaLibros.getSelectedRow();
        if (selectedRow == -1) {
            mainView.mostrarMensaje("Por favor, seleccione un libro para eliminar.");
            return;
        }

        int libroId = (int) modeloTabla.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea eliminar este libro?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                libroDAO.eliminar(libroId);
                cargarLibros();
                mainView.getCatalogoPanel().cargarDatosCatalogo();
            } catch (SQLException ex) {
                mainView.mostrarError("Error al eliminar el libro: " + ex.getMessage());
            }
        }
    }

    private JPanel createFormPanel(Libro libro) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBackground(BACKGROUND_COLOR);

        JTextField tituloField = createStyledTextField(libro != null ? libro.getTitulo() : "");
        JTextField autorField = createStyledTextField(libro != null ? libro.getAutor() : "");
        JTextField anioField = createStyledTextField(libro != null ? String.valueOf(libro.getAnioPublicacion()) : "");

        panel.add(new JLabel("Título:"));
        panel.add(tituloField);
        panel.add(new JLabel("Autor:"));
        panel.add(autorField);
        panel.add(new JLabel("Año de Publicación:"));
        panel.add(anioField);

        return panel;
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

    private JTextField createStyledTextField(String text) {
        JTextField textField = new JTextField(text);
        textField.setFont(TEXT_FONT);
        textField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDER_COLOR, 10),
                new EmptyBorder(10, 15, 10, 15)
        ));
        textField.setBackground(Color.WHITE);
        textField.setForeground(FOREGROUND_COLOR);
        return textField;
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
}