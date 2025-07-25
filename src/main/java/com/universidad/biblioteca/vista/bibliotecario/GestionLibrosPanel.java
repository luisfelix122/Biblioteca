package com.universidad.biblioteca.vista.bibliotecario;
import com.universidad.biblioteca.modelo.Libro;
import com.universidad.biblioteca.modelo.Usuario;
import com.universidad.biblioteca.controlador.LibroDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GestionLibrosPanel extends JPanel {

        private DefaultTableModel tableModel;
    private JTable librosTable;
    private JTextField isbnField, tituloField, autorField, anioField, estadoField;
    private JButton addButton, editButton, deleteButton;



    private List<Libro> libros = new ArrayList<>();
    private LibroDAO libroDAO;

    public GestionLibrosPanel(Usuario usuario) {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 240, 240)); // Color de fondo similar al resto del sistema

        libroDAO = new LibroDAO();

        // Panel de formulario de entrada
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        formPanel.setBackground(new Color(240, 240, 240));

        formPanel.add(new JLabel("ISBN:"));
        isbnField = new JTextField();
        formPanel.add(isbnField);

        formPanel.add(new JLabel("Título:"));
        tituloField = new JTextField();
        formPanel.add(tituloField);

        formPanel.add(new JLabel("Autor:"));
        autorField = new JTextField();
        formPanel.add(autorField);

        formPanel.add(new JLabel("Año:"));
        anioField = new JTextField();
        formPanel.add(anioField);

        formPanel.add(new JLabel("Estado:"));
        estadoField = new JTextField();
        formPanel.add(estadoField);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(240, 240, 240));

        addButton = new JButton("Agregar");
        editButton = new JButton("Editar");
        deleteButton = new JButton("Eliminar");

        styleButton(addButton);
        styleButton(editButton);
        styleButton(deleteButton);

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        // Añadir listeners a los botones
        addButton.addActionListener(this::agregarLibro);
        editButton.addActionListener(this::editarLibro);
        deleteButton.addActionListener(this::eliminarLibro);

        // Tabla de libros
        String[] columnNames = {"ISBN", "Título", "Autor", "Año", "Estado"};
        tableModel = new DefaultTableModel(columnNames, 0);
        librosTable = new JTable(tableModel);
        librosTable.setFillsViewportHeight(true);
        librosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        librosTable.getTableHeader().setBackground(new Color(85, 65, 118)); // Color de cabecera
        librosTable.getTableHeader().setForeground(Color.WHITE);
        librosTable.setRowHeight(25);
        librosTable.setGridColor(new Color(200, 200, 200));

        // Add a ListSelectionListener to the table
        librosTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // Ensure the event is not a partial adjustment
                cargarLibroSeleccionado();
            }
        });

        JScrollPane scrollPane = new JScrollPane(librosTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        // Añadir componentes al panel principal
        add(formPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        // Cargar algunos datos de ejemplo
        cargarLibrosDesdeBD();
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(85, 65, 118)); // Color de fondo
        button.setForeground(Color.WHITE); // Color de texto
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    }

    private void cargarLibrosDesdeBD() {
        try {
            libros = libroDAO.obtenerTodosLosLibros();
            actualizarTabla();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar libros desde la base de datos: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void actualizarTabla() {
        tableModel.setRowCount(0); // Limpiar tabla
        for (Libro libro : libros) {
            tableModel.addRow(new Object[]{libro.getIsbn(), libro.getTitulo(), libro.getAutor(), libro.getAnioPublicacion(), libro.isDisponible() ? "Disponible" : "Prestado"});
        }
    }

    private void limpiarCampos() {
        isbnField.setText("");
        tituloField.setText("");
        autorField.setText("");
        anioField.setText("");
        estadoField.setText("");
    }

    private void agregarLibro(java.awt.event.ActionEvent e) {
        String isbn = isbnField.getText();
        String titulo = tituloField.getText();
        String autor = autorField.getText();
        String anioStr = anioField.getText();
        String estado = estadoField.getText();

        if (!isbn.isEmpty() && !titulo.isEmpty() && !autor.isEmpty() && !anioStr.isEmpty() && !estado.isEmpty()) {
            try {
                int anio = Integer.parseInt(anioStr);
                Libro nuevoLibro = new Libro();
                nuevoLibro.setIsbn(isbn);
                nuevoLibro.setTitulo(titulo);
                nuevoLibro.setAutor(autor);
                nuevoLibro.setAnioPublicacion(anio);
                nuevoLibro.setDisponible("Disponible".equalsIgnoreCase(estado));

                // Check if book with ISBN already exists
                if (libroDAO.obtenerPorId(isbn) != null) {
                    JOptionPane.showMessageDialog(this, "Ya existe un libro con el ISBN: " + isbn, "Error de ISBN Duplicado", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                libroDAO.insertar(nuevoLibro);

                libros.add(nuevoLibro);
                actualizarTabla();
                limpiarCampos();
                JOptionPane.showMessageDialog(this, "Libro agregado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El año debe ser un número válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al agregar el libro a la base de datos: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarLibro(java.awt.event.ActionEvent e) {
        int selectedRow = librosTable.getSelectedRow();
        if (selectedRow >= 0) {
            String isbn = isbnField.getText();
            String titulo = tituloField.getText();
            String autor = autorField.getText();
            String anioStr = anioField.getText();
            String estado = estadoField.getText();

            if (!isbn.isEmpty() && !titulo.isEmpty() && !autor.isEmpty() && !anioStr.isEmpty() && !estado.isEmpty()) {
                Libro libro = libros.get(selectedRow);
                libro.setIsbn(isbn);
                libro.setTitulo(titulo);
                libro.setAutor(autor);
                try {
                    libro.setAnioPublicacion(Integer.parseInt(anioStr));
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "El año debe ser un número válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                libro.setDisponible("Disponible".equalsIgnoreCase(estado));
                try {
                    libroDAO.actualizar(libro);
                    actualizarTabla();
                    limpiarCampos();
                    JOptionPane.showMessageDialog(this, "Libro actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error al actualizar el libro en la base de datos: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un libro para editar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void eliminarLibro(java.awt.event.ActionEvent e) {
        int selectedRow = librosTable.getSelectedRow();
        if (selectedRow >= 0) {
            String isbn = (String) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea eliminar este libro?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    libroDAO.eliminar(isbn);
                    libros.remove(selectedRow);
                    actualizarTabla();
                    limpiarCampos();
                    JOptionPane.showMessageDialog(this, "Libro eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error al eliminar el libro de la base de datos: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un libro para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Método para cargar los datos del libro seleccionado en los campos de texto
    private void cargarLibroSeleccionado() {
        int selectedRow = librosTable.getSelectedRow();
        if (selectedRow >= 0) {
            isbnField.setText(tableModel.getValueAt(selectedRow, 0).toString());
            tituloField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            autorField.setText(tableModel.getValueAt(selectedRow, 2).toString());
            anioField.setText(tableModel.getValueAt(selectedRow, 3).toString());
            estadoField.setText(tableModel.getValueAt(selectedRow, 4).toString());
        }
    }
}