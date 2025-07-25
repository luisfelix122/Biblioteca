package com.universidad.biblioteca.vista.administrador;

import com.universidad.biblioteca.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AuditoriaPanel extends JPanel {

    private static final Color BACKGROUND_COLOR = new Color(243, 244, 246);
    private static final Color FOREGROUND_COLOR = new Color(55, 65, 81);
    private static final Color BORDER_COLOR = new Color(209, 213, 219);
    private static final Font TEXT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Color PRIMARY_BUTTON_COLOR = new Color(59, 130, 246);

    private JTable tablaAuditoria;
    private DefaultTableModel modeloTabla;

    public AuditoriaPanel(Usuario usuario) {
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(BACKGROUND_COLOR);

        if (usuario == null || (!"Administrador".equalsIgnoreCase(usuario.getRol().getNombre().trim()) && !"Admin".equalsIgnoreCase(usuario.getRol().getNombre().trim()))) {
            mostrarAccesoDenegado();
        } else {
            initUI();
            cargarAuditoriaSimulada();
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
        add(createTablePanel(), BorderLayout.CENTER);
        add(createActionsPanel(), BorderLayout.SOUTH);
    }

    private JScrollPane createTablePanel() {
        modeloTabla = new DefaultTableModel(new String[]{"Fecha y Hora", "Usuario", "Acción", "Descripción"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaAuditoria = new JTable(modeloTabla);
        styleTable(tablaAuditoria);

        JScrollPane scrollPane = new JScrollPane(tablaAuditoria);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        return scrollPane;
    }

    private JPanel createActionsPanel() {
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionsPanel.setBackground(BACKGROUND_COLOR);

        JButton btnRefrescar = createStyledButton("Refrescar", PRIMARY_BUTTON_COLOR, Color.WHITE);
        btnRefrescar.addActionListener(_ -> cargarAuditoriaSimulada());

        actionsPanel.add(btnRefrescar);
        return actionsPanel;
    }

    private void cargarAuditoriaSimulada() {
        modeloTabla.setRowCount(0); // Limpiar tabla
        List<Object[]> registros = generarDatosSimulados();
        for (Object[] registro : registros) {
            modeloTabla.addRow(registro);
        }
    }

    private List<Object[]> generarDatosSimulados() {
        List<Object[]> datos = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        datos.add(new Object[]{
                LocalDateTime.now().minusHours(1).format(formatter),
                "admin",
                "INICIO_SESION",
                "El administrador inició sesión desde la IP 192.168.1.10"
        });
        datos.add(new Object[]{
                LocalDateTime.now().minusMinutes(45).format(formatter),
                "biblio01",
                "CREAR_LIBRO",
                "Se añadió el libro 'El código Da Vinci' (ID: 101)"
        });
        datos.add(new Object[]{
                LocalDateTime.now().minusMinutes(30).format(formatter),
                "admin",
                "MODIFICAR_USUARIO",
                "Se actualizaron los datos del usuario 'juanperez' (ID: 25)"
        });
        datos.add(new Object[]{
                LocalDateTime.now().minusMinutes(15).format(formatter),
                "biblio02",
                "REALIZAR_PRESTAMO",
                "Préstamo del libro ID 78 al usuario ID 42"
        });
        datos.add(new Object[]{
                LocalDateTime.now().minusMinutes(5).format(formatter),
                "admin",
                "ELIMINAR_USUARIO",
                "Se eliminó al bibliotecario 'anagarcia' (ID: 12)"
        });

        return datos;
    }

    // --- Métodos de estilo ---

    private void styleTable(JTable table) {
        table.setBackground(Color.WHITE);
        table.setForeground(FOREGROUND_COLOR);
        table.setSelectionBackground(new Color(187, 247, 208));
        table.setSelectionForeground(FOREGROUND_COLOR);
        table.setFont(TEXT_FONT);
        table.setRowHeight(30);
        table.setGridColor(BORDER_COLOR);
        table.setShowGrid(true);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(229, 231, 235));
        header.setForeground(FOREGROUND_COLOR);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}