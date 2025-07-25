package com.universidad.biblioteca.vista.estudiante;

import com.universidad.biblioteca.controlador.LibroDAO;
import com.universidad.biblioteca.controlador.PrestamoDAO;
import com.universidad.biblioteca.modelo.Prestamo;
import com.universidad.biblioteca.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.universidad.biblioteca.vista.utils.RoundedBorder;

public class MisPrestamosPanel extends JPanel {

    private static final Color BACKGROUND_COLOR = new Color(243, 244, 246);
    private static final Color FOREGROUND_COLOR = new Color(55, 65, 81);
    private static final Color BORDER_COLOR = new Color(209, 213, 219);
    private static final Color PRIMARY_BUTTON_COLOR = new Color(67, 56, 202);
    private static final Font TEXT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    private static final String[] TABLE_HEADERS = {"ID Préstamo", "Libro", "Fecha Préstamo", "Fecha Devolución", "Multa", "Devuelto"};
    private final com.universidad.biblioteca.vista.main.MainView mainView;
    private final PrestamoDAO prestamoDAO;
    private final LibroDAO libroDAO;
    private final Usuario usuarioLogueado;
    private JTable tablaMisPrestamos;
    private DefaultTableModel modeloMisPrestamos;
    private JButton botonDevolver;
    private JLabel labelPrestamosActivos, labelProximoVencimiento;

    public MisPrestamosPanel(com.universidad.biblioteca.vista.main.MainView mainView, PrestamoDAO prestamoDAO, LibroDAO libroDAO, Usuario usuarioLogueado) {
        this.mainView = mainView;
        this.prestamoDAO = prestamoDAO;
        this.libroDAO = libroDAO;
        this.usuarioLogueado = usuarioLogueado;

        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(BACKGROUND_COLOR);

        if (usuarioLogueado != null && "Estudiante".equals(usuarioLogueado.getRol().getNombre())) {
            initUI();
            cargarDatosMisPrestamos();
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
        // 1. Panel de Estadísticas (Superior)
        add(createStatsPanel(), BorderLayout.NORTH);

        // 2. Tabla de Préstamos (Centro)
        add(createTablePanel(), BorderLayout.CENTER);

        // 3. Panel de Acciones (Inferior)
        add(createActionsPanel(), BorderLayout.SOUTH);
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridBagLayout());
        statsPanel.setBorder(new EmptyBorder(10, 0, 20, 0));
        statsPanel.setBorder(new RoundedBorder(BORDER_COLOR, 10));
        statsPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 10, 0, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;

        labelPrestamosActivos = createStatLabel("Préstamos Activos: 0");
        gbc.gridx = 0;
        statsPanel.add(labelPrestamosActivos, gbc);

        labelProximoVencimiento = createStatLabel("Próximo Vencimiento: N/A");
        gbc.gridx = 1;
        statsPanel.add(labelProximoVencimiento, gbc);

        return statsPanel;
    }

    private JScrollPane createTablePanel() {
        tablaMisPrestamos = createTable();
        return new JScrollPane(tablaMisPrestamos);
    }

    private JPanel createActionsPanel() {
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actionsPanel.setBackground(BACKGROUND_COLOR);
        actionsPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        botonDevolver = createStyledButton("Devolver Libro Seleccionado", PRIMARY_BUTTON_COLOR, Color.WHITE);
        botonDevolver.setEnabled(false);
        botonDevolver.addActionListener(_ -> devolverLibro());
        actionsPanel.add(botonDevolver);

        return actionsPanel;
    }

    private JTable createTable() {
        modeloMisPrestamos = new DefaultTableModel(TABLE_HEADERS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(modeloMisPrestamos);
        styleTable(table);
        table.setDefaultRenderer(Object.class, new PrestamoCellRenderer());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateDevolverButtonState();
            }
        });

        return table;
    }

    private void updateDevolverButtonState() {
        int selectedRow = tablaMisPrestamos.getSelectedRow();
        if (selectedRow != -1) {
            boolean devuelto = (boolean) modeloMisPrestamos.getValueAt(selectedRow, 5);
            botonDevolver.setEnabled(!devuelto);
        } else {
            botonDevolver.setEnabled(false);
        }
    }

    public void cargarDatosMisPrestamos() {
        java.sql.Connection conn = null;
        try {
            conn = com.universidad.biblioteca.config.ConexionBD.obtenerConexion();
            conn.setAutoCommit(false);

            modeloMisPrestamos.setRowCount(0);
            List<Prestamo> prestamos = prestamoDAO.obtenerPrestamosPorUsuario(conn, usuarioLogueado.getCodigo());
            int prestamosActivos = 0;
            Date proximoVencimiento = null;

            for (Prestamo prestamo : prestamos) {
                modeloMisPrestamos.addRow(new Object[]{
                        prestamo.getId(),
                        prestamo.getLibro() != null ? prestamo.getLibro().getTitulo() : "(Libro no disponible)",
                        prestamo.getFechaPrestamo(),
                        prestamo.getFechaDevolucion(),
                        prestamo.getMulta(),
                        prestamo.isDevuelto()
                });
                if (!prestamo.isDevuelto()) {
                    prestamosActivos++;
                    if (proximoVencimiento == null || prestamo.getFechaDevolucion().before(proximoVencimiento)) {
                        proximoVencimiento = prestamo.getFechaDevolucion();
                    }
                }
            }

            labelPrestamosActivos.setText("Préstamos Activos: " + prestamosActivos);
            if (proximoVencimiento != null) {
                labelProximoVencimiento.setText("Próximo Vencimiento: " + new java.text.SimpleDateFormat("dd/MM/yyyy").format(proximoVencimiento));
            } else {
                labelProximoVencimiento.setText("Próximo Vencimiento: N/A");
            }
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                mainView.mostrarError("Error al intentar revertir la transacción: " + ex.getMessage());
            }
            mainView.mostrarError("Error al cargar mis préstamos: " + e.getMessage());
        }
    }

    private void devolverLibro() {
        int filaSeleccionada = tablaMisPrestamos.getSelectedRow();
        if (filaSeleccionada == -1) return;

        int idPrestamo = (int) modeloMisPrestamos.getValueAt(filaSeleccionada, 0);
        boolean devuelto = (boolean) modeloMisPrestamos.getValueAt(filaSeleccionada, 5);

        if (devuelto) {
            mainView.mostrarMensaje("Este libro ya ha sido devuelto.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        java.sql.Connection conn = null;
        try {
            conn = com.universidad.biblioteca.config.ConexionBD.obtenerConexion();
            conn.setAutoCommit(false);

            Prestamo prestamo = prestamoDAO.obtenerPrestamoPorId(conn, idPrestamo);
            if (prestamo != null) {
                // Calcular multa si aplica
                double multa = 0.0;
                Date hoy = new Date();
                if (hoy.after(prestamo.getFechaDevolucion())) {
                    long diff = hoy.getTime() - prestamo.getFechaDevolucion().getTime();
                    long diffDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                    multa = diffDays * 1.0; // 1 unidad de multa por día de retraso
                }

                if (prestamoDAO.marcarComoDevuelto(conn, idPrestamo, multa)) {
                    // Actualizar disponibilidad del libro
                    prestamo.getLibro().setDisponible(true);
                    libroDAO.actualizar(conn, prestamo.getLibro());

                    conn.commit();
                    mainView.mostrarMensaje("Libro devuelto con éxito. Multa: " + String.format("%.2f", multa), "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarDatosMisPrestamos();
                    mainView.getCatalogoPanel().cargarDatosCatalogo();
                } else {
                    conn.rollback();
                    mainView.mostrarError("No se pudo marcar el préstamo como devuelto.");
                }
            } else {
                conn.rollback();
            }
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                mainView.mostrarError("Error al intentar revertir la transacción: " + ex.getMessage());
            }
            mainView.mostrarError("Error al devolver el libro: " + e.getMessage());
        }
    }

    // Clase interna para renderizar las celdas con colores
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

    private JLabel createStatLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(BOLD_FONT);
        label.setForeground(FOREGROUND_COLOR);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDER_COLOR, 10),
                new EmptyBorder(15, 20, 15, 20)
        ));
        label.setOpaque(true);
        label.setBackground(Color.WHITE);
        return label;
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

    // Clase interna para renderizar las celdas con colores
    private static class PrestamoCellRenderer extends DefaultTableCellRenderer {
        private static final Color COLOR_VENCIDO = new Color(254, 226, 226); // Rojo claro
        private static final Color COLOR_PROXIMO_VENCER = new Color(254, 249, 195); // Amarillo claro
        private static final Color COLOR_DEVUELTO = new Color(220, 252, 231); // Verde claro
        private static final Color COLOR_VENCIDO_FG = new Color(159, 28, 28);
        private static final Color COLOR_PROXIMO_VENCER_FG = new Color(133, 77, 14);
        private static final Color COLOR_DEVUELTO_FG = new Color(21, 101, 52);

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBorder(new EmptyBorder(5, 10, 5, 10));

            if (isSelected) {
                c.setBackground(table.getSelectionBackground());
                c.setForeground(table.getSelectionForeground());
                return c;
            }

            boolean devuelto = (boolean) table.getModel().getValueAt(row, 5);
            Date fechaDevolucion = (Date) table.getModel().getValueAt(row, 3);

            if (!devuelto) {
                Date hoy = new Date();
                if (fechaDevolucion != null && hoy.after(fechaDevolucion)) {
                    c.setBackground(COLOR_VENCIDO);
                    c.setForeground(COLOR_VENCIDO_FG);
                } else if (fechaDevolucion != null) {
                    long diff = fechaDevolucion.getTime() - hoy.getTime();
                    long diffDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                    if (diffDays <= 3) { // Próximo a vencer (ej. 3 días o menos)
                        c.setBackground(COLOR_PROXIMO_VENCER);
                        c.setForeground(COLOR_PROXIMO_VENCER_FG);
                    } else {
                        c.setBackground(Color.WHITE);
                        c.setForeground(table.getForeground());
                    }
                } else {
                     c.setBackground(Color.WHITE);
                     c.setForeground(table.getForeground());
                }
            } else {
                c.setBackground(COLOR_DEVUELTO);
                c.setForeground(COLOR_DEVUELTO_FG);
            }

            return c;
        }
    }
}