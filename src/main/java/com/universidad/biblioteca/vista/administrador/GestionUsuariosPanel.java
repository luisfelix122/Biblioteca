package com.universidad.biblioteca.vista.administrador;

import com.universidad.biblioteca.controlador.UsuarioDAO;
import com.universidad.biblioteca.modelo.Usuario;
import com.universidad.biblioteca.vista.utils.RoundedBorder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class GestionUsuariosPanel extends JPanel {

    private static final Color BACKGROUND_COLOR = new Color(243, 244, 246);
    private static final Color FOREGROUND_COLOR = new Color(55, 65, 81);
    private static final Color BORDER_COLOR = new Color(209, 213, 219);
    private static final Font TEXT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Color PRIMARY_BUTTON_COLOR = new Color(59, 130, 246);

    private final UsuarioDAO usuarioDAO;
    private JTable tablaUsuarios;
    private DefaultTableModel modeloTabla;
    private JTextField campoBusqueda;

    public GestionUsuariosPanel(Usuario usuario) {
        this.usuarioDAO = new UsuarioDAO();
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(BACKGROUND_COLOR);

        if (usuario == null || (!"Administrador".equalsIgnoreCase(usuario.getRol().getNombre().trim()) && !"Admin".equalsIgnoreCase(usuario.getRol().getNombre().trim()))) {
            mostrarAccesoDenegado();
        } else {
            initUI();
            cargarUsuarios();
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
        add(createFilterPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createActionsPanel(), BorderLayout.SOUTH);
    }

    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(BACKGROUND_COLOR);
        filterPanel.setBorder(BorderFactory.createTitledBorder("Buscar Usuarios"));

        campoBusqueda = new JTextField(30);
        styleTextField(campoBusqueda);
        campoBusqueda.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrarUsuarios(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrarUsuarios(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrarUsuarios(); }
        });

        filterPanel.add(new JLabel("Buscar:"));
        filterPanel.add(campoBusqueda);
        return filterPanel;
    }

    private JScrollPane createTablePanel() {
        modeloTabla = new DefaultTableModel(new String[]{"ID", "Username", "Nombre", "Email", "Rol"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaUsuarios = new JTable(modeloTabla);
        styleTable(tablaUsuarios);

        JScrollPane scrollPane = new JScrollPane(tablaUsuarios);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        return scrollPane;
    }

    private JPanel createActionsPanel() {
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionsPanel.setBackground(BACKGROUND_COLOR);

        JButton btnEditar = createStyledButton("Modificar Datos", PRIMARY_BUTTON_COLOR, Color.WHITE);
        btnEditar.addActionListener(_ -> modificarUsuarioSeleccionado());

        actionsPanel.add(btnEditar);
        return actionsPanel;
    }

    private void cargarUsuarios() {
        try {
            List<Usuario> usuarios = usuarioDAO.obtenerTodosLosUsuarios(); // Cambiado para obtener todos los usuarios
            poblarTabla(usuarios);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los usuarios: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filtrarUsuarios() {
        String textoBusqueda = campoBusqueda.getText().toLowerCase();
        try {
            List<Usuario> todosLosUsuarios = usuarioDAO.obtenerTodosLosUsuarios(); // Cambiado para obtener todos los usuarios
            if (textoBusqueda.isEmpty()) {
                poblarTabla(todosLosUsuarios);
            } else {
                List<Usuario> usuariosFiltrados = todosLosUsuarios.stream()
                        .filter(u -> u.getNombre().toLowerCase().contains(textoBusqueda) ||
                                     u.getUsername().toLowerCase().contains(textoBusqueda) ||
                                     u.getEmail().toLowerCase().contains(textoBusqueda) ||
                                     u.getRol().getNombre().toLowerCase().contains(textoBusqueda)) // Añadido filtro por rol
                        .toList();
                poblarTabla(usuariosFiltrados);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al filtrar usuarios: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void poblarTabla(List<Usuario> usuarios) {
        modeloTabla.setRowCount(0);
        for (Usuario u : usuarios) {
            modeloTabla.addRow(new Object[]{u.getId(), u.getUsername(), u.getNombre(), u.getEmail(), u.getRol().getNombre()});
        }
    }

    private void modificarUsuarioSeleccionado() {
        int selectedRow = tablaUsuarios.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un usuario para modificar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int usuarioId = (int) tablaUsuarios.getValueAt(selectedRow, 0);
        try {
            Usuario usuario = usuarioDAO.obtenerUsuarioPorId(usuarioId);
            if (usuario != null) {
                abrirDialogoModificacion(usuario);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al obtener los datos del usuario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirDialogoModificacion(Usuario usuario) {
        JTextField nombreField = new JTextField(usuario.getNombre(), 20);
        JTextField emailField = new JTextField(usuario.getEmail(), 20);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Nombre:"));
        panel.add(nombreField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Modificar Usuario",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            usuario.setNombre(nombreField.getText());
            usuario.setEmail(emailField.getText());

            try {
                usuarioDAO.actualizarUsuario(usuario);
                JOptionPane.showMessageDialog(this, "Usuario actualizado correctamente.");
                cargarUsuarios(); // Recargar la tabla
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al actualizar el usuario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
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

    private void styleTextField(JTextField textField) {
        textField.setFont(TEXT_FONT);
        textField.setForeground(FOREGROUND_COLOR);
        textField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDER_COLOR, 10),
                new EmptyBorder(5, 10, 5, 10)
        ));
    }
}