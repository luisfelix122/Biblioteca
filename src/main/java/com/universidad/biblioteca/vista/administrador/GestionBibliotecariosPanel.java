package com.universidad.biblioteca.vista.administrador;

import com.universidad.biblioteca.controlador.RolDAO;
import com.universidad.biblioteca.controlador.UsuarioDAO;
import com.universidad.biblioteca.modelo.Role;
import com.universidad.biblioteca.modelo.Usuario;
import com.universidad.biblioteca.vista.utils.RoundedBorder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class GestionBibliotecariosPanel extends JPanel {

    private static final Color BACKGROUND_COLOR = new Color(243, 244, 246);
    private static final Color FOREGROUND_COLOR = new Color(55, 65, 81);
    private static final Color BORDER_COLOR = new Color(209, 213, 219);
    private static final Font TEXT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Color PRIMARY_BUTTON_COLOR = new Color(34, 197, 94); // Verde
    private static final Color SECONDARY_BUTTON_COLOR = new Color(59, 130, 246); // Azul
    private static final Color DANGER_BUTTON_COLOR = new Color(239, 68, 68); // Rojo

    private final UsuarioDAO usuarioDAO;
    private final RolDAO rolDAO;
    private JTable tablaBibliotecarios;
    private DefaultTableModel modeloTabla;
    private JTextField campoBusqueda;

    public GestionBibliotecariosPanel(Usuario usuario) {
        this.usuarioDAO = new UsuarioDAO();
        this.rolDAO = new RolDAO();
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(BACKGROUND_COLOR);

        if (usuario == null || (!"Administrador".equalsIgnoreCase(usuario.getRol().getNombre().trim()) && !"Admin".equalsIgnoreCase(usuario.getRol().getNombre().trim()))) {
            mostrarAccesoDenegado();
        } else {
            initUI();
            cargarBibliotecarios();
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
        filterPanel.setBorder(BorderFactory.createTitledBorder("Buscar Bibliotecarios"));

        campoBusqueda = new JTextField(30);
        styleTextField(campoBusqueda);
        campoBusqueda.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrarBibliotecarios(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrarBibliotecarios(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrarBibliotecarios(); }
        });

        filterPanel.add(new JLabel("Buscar:"));
        filterPanel.add(campoBusqueda);
        return filterPanel;
    }

    private JScrollPane createTablePanel() {
        modeloTabla = new DefaultTableModel(new String[]{"ID", "Username", "Nombre", "Email"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaBibliotecarios = new JTable(modeloTabla);
        styleTable(tablaBibliotecarios);

        JScrollPane scrollPane = new JScrollPane(tablaBibliotecarios);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        return scrollPane;
    }

    private JPanel createActionsPanel() {
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionsPanel.setBackground(BACKGROUND_COLOR);

        JButton btnAnadir = createStyledButton("Añadir Bibliotecario", PRIMARY_BUTTON_COLOR, Color.WHITE);
        JButton btnEditar = createStyledButton("Editar", SECONDARY_BUTTON_COLOR, Color.WHITE);
        JButton btnEliminar = createStyledButton("Eliminar", DANGER_BUTTON_COLOR, Color.WHITE);

        btnAnadir.addActionListener(_ -> anadirBibliotecario());
        btnEditar.addActionListener(_ -> editarBibliotecarioSeleccionado());
        btnEliminar.addActionListener(_ -> eliminarBibliotecarioSeleccionado());

        actionsPanel.add(btnAnadir);
        actionsPanel.add(btnEditar);
        actionsPanel.add(btnEliminar);
        return actionsPanel;
    }

    private void cargarBibliotecarios() {
        try {
            List<Usuario> bibliotecarios = usuarioDAO.obtenerUsuariosPorRol("Bibliotecario");
            poblarTabla(bibliotecarios);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los bibliotecarios: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filtrarBibliotecarios() {
        String textoBusqueda = campoBusqueda.getText().toLowerCase();
        try {
            List<Usuario> todosLosBibliotecarios = usuarioDAO.obtenerUsuariosPorRol("Bibliotecario");
            if (textoBusqueda.isEmpty()) {
                poblarTabla(todosLosBibliotecarios);
            } else {
                List<Usuario> bibliotecariosFiltrados = todosLosBibliotecarios.stream()
                        .filter(u -> u.getNombre().toLowerCase().contains(textoBusqueda) ||
                                     u.getUsername().toLowerCase().contains(textoBusqueda) ||
                                     u.getEmail().toLowerCase().contains(textoBusqueda))
                        .toList();
                poblarTabla(bibliotecariosFiltrados);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al filtrar bibliotecarios: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void poblarTabla(List<Usuario> bibliotecarios) {
        modeloTabla.setRowCount(0);
        for (Usuario u : bibliotecarios) {
            modeloTabla.addRow(new Object[]{u.getId(), u.getUsername(), u.getNombre(), u.getEmail()});
        }
    }

    private void anadirBibliotecario() {
        abrirDialogoEdicion(null);
    }

    private void editarBibliotecarioSeleccionado() {
        int selectedRow = tablaBibliotecarios.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un bibliotecario para editar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int usuarioId = (int) tablaBibliotecarios.getValueAt(selectedRow, 0);
        try {
            Usuario usuario = usuarioDAO.obtenerUsuarioPorId(usuarioId);
            if (usuario != null) {
                abrirDialogoEdicion(usuario);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al obtener los datos del bibliotecario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarBibliotecarioSeleccionado() {
        int selectedRow = tablaBibliotecarios.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un bibliotecario para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea eliminar a este bibliotecario?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int usuarioId = (int) tablaBibliotecarios.getValueAt(selectedRow, 0);
            try {
                usuarioDAO.eliminar(usuarioId);
                JOptionPane.showMessageDialog(this, "Bibliotecario eliminado correctamente.");
                cargarBibliotecarios();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar el bibliotecario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void abrirDialogoEdicion(Usuario usuario) {
        boolean esNuevo = (usuario == null);
        String tituloDialogo = esNuevo ? "Añadir Nuevo Bibliotecario" : "Editar Bibliotecario";

        JTextField usernameField = new JTextField(esNuevo ? "" : usuario.getUsername(), 20);
        JPasswordField passwordField = new JPasswordField(20);
        JTextField nombreField = new JTextField(esNuevo ? "" : usuario.getNombre(), 20);
        JTextField emailField = new JTextField(esNuevo ? "" : usuario.getEmail(), 20);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; panel.add(usernameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; panel.add(passwordField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; panel.add(nombreField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; panel.add(emailField, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, tituloDialogo, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String nombre = nombreField.getText();
                String email = emailField.getText();

                if (username.isEmpty() || nombre.isEmpty() || email.isEmpty() || (esNuevo && password.isEmpty())) {
                    JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Role rolBibliotecario = rolDAO.obtenerRolPorNombre("Bibliotecario");
                if (rolBibliotecario == null) {
                    JOptionPane.showMessageDialog(this, "El rol 'Bibliotecario' no se encuentra en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (esNuevo) {
                    Usuario nuevoUsuario = new Usuario();
                    nuevoUsuario.setCodigo(username);
                    nuevoUsuario.setContrasena(password);
                    nuevoUsuario.setNombre(nombre);
                    nuevoUsuario.setEmail(email);
                    nuevoUsuario.setRol(rolBibliotecario);
                    usuarioDAO.insertar(nuevoUsuario);
                    JOptionPane.showMessageDialog(this, "Bibliotecario añadido correctamente.");
                } else {
                    usuario.setCodigo(username);
                    if (!password.isEmpty()) {
                        usuario.setContrasena(password); // Asumiendo que el DAO se encarga del hash
                    }
                    usuario.setNombre(nombre);
                    usuario.setEmail(email);
                    usuarioDAO.actualizarUsuario(usuario);
                    JOptionPane.showMessageDialog(this, "Bibliotecario actualizado correctamente.");
                }
                cargarBibliotecarios();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al guardar el bibliotecario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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