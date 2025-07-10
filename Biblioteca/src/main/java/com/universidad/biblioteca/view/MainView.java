package com.universidad.biblioteca.view;

import com.universidad.biblioteca.config.ConexionBD;
import com.universidad.biblioteca.controller.LibroDAO;
import com.universidad.biblioteca.controller.PrestamoDAO;
import com.universidad.biblioteca.controller.UsuarioDAO;
import com.universidad.biblioteca.model.Libro;
import com.universidad.biblioteca.model.Prestamo;
import com.universidad.biblioteca.model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class MainView extends JFrame {

    private final Usuario usuarioLogueado;
    private final LibroDAO libroDAO;
    private final PrestamoDAO prestamoDAO;
    private final UsuarioDAO usuarioDAO;

    private JTable tablaCatalogo, tablaMisPrestamos, tablaHistorial;
    private DefaultTableModel modeloCatalogo, modeloMisPrestamos, modeloHistorial;
    private JTextField campoBusqueda, perfilNombre, perfilEmail, perfilTelefono;
    private JPasswordField perfilContrasena;
    private JButton botonSolicitar, botonDevolver;

    public MainView(Usuario usuario) {
        this.usuarioLogueado = usuario;
        Connection conn = ConexionBD.obtenerConexion();
        this.libroDAO = new LibroDAO(conn);
        this.prestamoDAO = new PrestamoDAO(conn);
        this.usuarioDAO = new UsuarioDAO(conn);

        setTitle("Sistema de Biblioteca - Bienvenido, " + usuario.getNombre());
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Catálogo de Libros", crearPanelCatalogo());
        tabbedPane.addTab("Mis Préstamos", crearPanelMisPrestamos());
        tabbedPane.addTab("Historial de Préstamos", crearPanelHistorial());
        tabbedPane.addTab("Mi Perfil", crearPanelPerfil());

        add(tabbedPane);

        cargarDatosIniciales();
    }

    private void cargarDatosIniciales() {
        cargarDatosCatalogo();
        cargarDatosMisPrestamos();
        cargarDatosHistorial();
        cargarDatosPerfil();
    }

    private void cargarDatosPerfil() {
        perfilNombre.setText(usuarioLogueado.getNombre());
        perfilEmail.setText(usuarioLogueado.getCorreo());
        perfilTelefono.setText(usuarioLogueado.getTelefono());
        perfilContrasena.setText("");
    }

    private JPanel crearPanelCatalogo() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelSuperior = new JPanel(new BorderLayout(10, 0));
        campoBusqueda = new JTextField();
        JButton botonBuscar = new JButton("Buscar");
        JButton botonExportar = new JButton("Exportar a Excel");
        panelSuperior.add(campoBusqueda, BorderLayout.CENTER);
        panelSuperior.add(botonBuscar, BorderLayout.EAST);
        panelSuperior.add(botonExportar, BorderLayout.WEST);
        panel.add(panelSuperior, BorderLayout.NORTH);

        modeloCatalogo = new DefaultTableModel(new String[] { "ID", "Título", "Autor", "Año", "Disponible" }, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaCatalogo = new JTable(modeloCatalogo);
        panel.add(new JScrollPane(tablaCatalogo), BorderLayout.CENTER);

        botonSolicitar = new JButton("Solicitar Libro Seleccionado");
        botonSolicitar.setEnabled(false);
        panel.add(botonSolicitar, BorderLayout.SOUTH);

        botonBuscar.addActionListener(e -> buscarLibros());
        botonExportar.addActionListener(e -> exportarTablaAExcel(tablaCatalogo, "CatalogoLibros"));
        botonSolicitar.addActionListener(e -> solicitarLibro());

        tablaCatalogo.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                botonSolicitar.setEnabled(tablaCatalogo.getSelectedRow() != -1);
            }
        });

        return panel;
    }

    private JPanel crearPanelMisPrestamos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modeloMisPrestamos = new DefaultTableModel(new String[] { "ID Préstamo", "Título", "Fecha Préstamo",
                "Fecha Devolución", "Días Restantes", "Multa" }, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaMisPrestamos = new JTable(modeloMisPrestamos);
        panel.add(new JScrollPane(tablaMisPrestamos), BorderLayout.CENTER);

        botonDevolver = new JButton("Devolver Libro Seleccionado");
        botonDevolver.setEnabled(false);
        panel.add(botonDevolver, BorderLayout.SOUTH);

        botonDevolver.addActionListener(e -> devolverLibro());
        tablaMisPrestamos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                botonDevolver.setEnabled(tablaMisPrestamos.getSelectedRow() != -1);
            }
        });

        return panel;
    }

    private JPanel crearPanelHistorial() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modeloHistorial = new DefaultTableModel(
                new String[] { "ID Préstamo", "Título", "Fecha Préstamo", "Fecha Devolución", "Multa" }, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaHistorial = new JTable(modeloHistorial);
        panel.add(new JScrollPane(tablaHistorial), BorderLayout.CENTER);

        JButton botonExportar = new JButton("Exportar a Excel");
        panel.add(botonExportar, BorderLayout.SOUTH);
        botonExportar.addActionListener(e -> exportarTablaAExcel(tablaHistorial, "HistorialPrestamos"));

        return panel;
    }

    private JPanel crearPanelPerfil() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Nombre:"), gbc);
        perfilNombre = new JTextField(20);
        gbc.gridx = 1;
        panel.add(perfilNombre, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);
        perfilEmail = new JTextField(20);
        gbc.gridx = 1;
        panel.add(perfilEmail, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Teléfono:"), gbc);
        perfilTelefono = new JTextField(20);
        gbc.gridx = 1;
        panel.add(perfilTelefono, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Nueva Contraseña (opcional):"), gbc);
        perfilContrasena = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(perfilContrasena, gbc);

        JButton botonGuardar = new JButton("Guardar Cambios");
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(botonGuardar, gbc);

        botonGuardar.addActionListener(e -> guardarPerfil());

        return panel;
    }

    private void cargarDatosCatalogo() {
        try {
            modeloCatalogo.setRowCount(0);
            List<Libro> libros = libroDAO.obtenerTodosLosLibros();
            for (Libro libro : libros) {
                modeloCatalogo.addRow(new Object[] {
                        libro.getId(), libro.getTitulo(), libro.getAutor(),
                        libro.getAnioPublicacion(), libro.isDisponible() ? "Sí" : "No"
                });
            }
        } catch (SQLException e) {
            mostrarError("Error al cargar el catálogo: " + e.getMessage());
        }
    }

    private void cargarDatosMisPrestamos() {
        try {
            modeloMisPrestamos.setRowCount(0);
            // OJO: ahora usa tu método con parámetro String
            List<Prestamo> prestamos = prestamoDAO.obtenerPrestamosActivosPorUsuario(usuarioLogueado.getCodigo());
            for (Prestamo prestamo : prestamos) {
                long diasRestantes = calcularDiasRestantes(prestamo.getFechaDevolucion());
                modeloMisPrestamos.addRow(new Object[] {
                        prestamo.getId(),
                        prestamo.getLibro() != null ? prestamo.getLibro().getTitulo() : "",
                        prestamo.getFechaPrestamo(),
                        prestamo.getFechaDevolucion(),
                        diasRestantes < 0 ? "Vencido" : diasRestantes,
                        String.format("%.2f", prestamo.getMulta())
                });
            }
        } catch (Exception e) {
            mostrarError("Error al cargar los préstamos: " + e.getMessage());
        }
    }

    private void cargarDatosHistorial() {
        try {
            modeloHistorial.setRowCount(0);
            List<Prestamo> prestamos = prestamoDAO.obtenerHistorialPrestamosPorUsuario(usuarioLogueado.getCodigo());
            for (Prestamo prestamo : prestamos) {
                modeloHistorial.addRow(new Object[] {
                        prestamo.getId(),
                        prestamo.getLibro() != null ? prestamo.getLibro().getTitulo() : "",
                        prestamo.getFechaPrestamo(),
                        prestamo.getFechaDevolucion(),
                        String.format("%.2f", prestamo.getMulta())
                });
            }
        } catch (Exception e) {
            mostrarError("Error al cargar el historial: " + e.getMessage());
        }
    }

    private void buscarLibros() {
        try {
            String termino = campoBusqueda.getText().trim();
            modeloCatalogo.setRowCount(0);
            List<Libro> libros = libroDAO.buscarLibros(termino);
            for (Libro libro : libros) {
                modeloCatalogo.addRow(new Object[] {
                        libro.getId(), libro.getTitulo(), libro.getAutor(),
                        libro.getAnioPublicacion(), libro.isDisponible() ? "Sí" : "No"
                });
            }
        } catch (SQLException e) {
            mostrarError("Error al buscar libros: " + e.getMessage());
        }
    }

    private void solicitarLibro() {
        int filaSeleccionada = tablaCatalogo.getSelectedRow();
        if (filaSeleccionada == -1)
            return;

        int idLibro = (int) modeloCatalogo.getValueAt(filaSeleccionada, 0);
        boolean disponible = modeloCatalogo.getValueAt(filaSeleccionada, 4).equals("Sí");

        if (!disponible) {
            mostrarMensaje("El libro seleccionado no está disponible.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            Libro libro = libroDAO.obtenerPorId(idLibro);
            if (libro != null) {
                Prestamo prestamo = new Prestamo();
                prestamo.setUsuario(usuarioLogueado);
                prestamo.setLibro(libro);
                prestamo.setFechaPrestamo(new java.sql.Date(new Date().getTime()));

                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_MONTH, 15);
                prestamo.setFechaDevolucion(new java.sql.Date(cal.getTimeInMillis()));

                if (prestamoDAO.registrarPrestamo(prestamo)) {
                    libro.setDisponible(false);
                    libroDAO.actualizar(libro);
                    mostrarMensaje("Libro solicitado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarDatosCatalogo();
                    cargarDatosMisPrestamos();
                } else {
                    mostrarError("No se pudo registrar el préstamo.");
                }
            }
        } catch (Exception e) {
            mostrarError("Error al solicitar el libro: " + e.getMessage());
        }
    }

    private void devolverLibro() {
        int filaSeleccionada = tablaMisPrestamos.getSelectedRow();
        if (filaSeleccionada == -1)
            return;

        int idPrestamo = (int) modeloMisPrestamos.getValueAt(filaSeleccionada, 0);

        try {
            Prestamo prestamo = prestamoDAO.obtenerPrestamoPorId(idPrestamo);
            if (prestamo != null) {
                double multa = 0;
                long diasRestantes = calcularDiasRestantes(prestamo.getFechaDevolucion());
                if (diasRestantes < 0) {
                    multa = Math.abs(diasRestantes) * 1.5; // Ejemplo de multa
                }

                if (prestamoDAO.marcarComoDevuelto(idPrestamo, multa)) {
                    Libro libro = prestamo.getLibro();
                    libro.setDisponible(true);
                    libroDAO.actualizar(libro);
                    mostrarMensaje("Libro devuelto con éxito. Multa aplicada: S/ " + String.format("%.2f", multa),
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarDatosMisPrestamos();
                    cargarDatosHistorial();
                    cargarDatosCatalogo();
                } else {
                    mostrarError("No se pudo procesar la devolución.");
                }
            }
        } catch (SQLException e) {
            mostrarError("Error al devolver el libro: " + e.getMessage());
        }
    }

    private void guardarPerfil() {
        String nombre = perfilNombre.getText();
        String email = perfilEmail.getText();
        String telefono = perfilTelefono.getText();
        String contrasena = new String(perfilContrasena.getPassword());

        if (nombre.isEmpty() || email.isEmpty() || telefono.isEmpty()) {
            mostrarMensaje("Nombre, email y teléfono no pueden estar vacíos.", "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        usuarioLogueado.setNombre(nombre);
        usuarioLogueado.setCorreo(email);
        usuarioLogueado.setTelefono(telefono);
        if (!contrasena.isEmpty()) {
            usuarioLogueado.setContrasena(contrasena);
        }

        if (usuarioDAO.actualizarPerfil(usuarioLogueado)) {
            mostrarMensaje("Perfil actualizado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            setTitle("Sistema de Biblioteca - Bienvenido, " + usuarioLogueado.getNombre());
        } else {
            mostrarError("No se pudo actualizar el perfil.");
        }
    }

    private long calcularDiasRestantes(Date fechaDevolucion) {
        if (fechaDevolucion == null)
            return 0;
        long diff = fechaDevolucion.getTime() - new Date().getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    private void exportarTablaAExcel(JTable tabla, String nombreHoja) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar como archivo Excel");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos Excel", "xlsx"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".xlsx")) {
                    filePath += ".xlsx";
                }

                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet(nombreHoja);
                DefaultTableModel model = (DefaultTableModel) tabla.getModel();

                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < model.getColumnCount(); i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(model.getColumnName(i));
                }

                for (int i = 0; i < model.getRowCount(); i++) {
                    Row row = sheet.createRow(i + 1);
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        Cell cell = row.createCell(j);
                        Object value = model.getValueAt(i, j);
                        cell.setCellValue(value != null ? value.toString() : "");
                    }
                }

                try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                    workbook.write(fileOut);
                }
                workbook.close();

                mostrarMensaje("Datos exportados a " + filePath, "Exportación Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                mostrarError("Error al exportar a Excel: " + e.getMessage());
            }
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarMensaje(String mensaje, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipo);
    }
}
