package com.universidad.biblioteca.view.main;

import com.universidad.biblioteca.controller.LibroDAO;
import com.universidad.biblioteca.controller.PrestamoDAO;
import com.universidad.biblioteca.controller.UsuarioDAO;
import com.universidad.biblioteca.model.Usuario;
import com.universidad.biblioteca.view.panels.CatalogoPanel;
import com.universidad.biblioteca.view.panels.HistorialPanel;
import com.universidad.biblioteca.view.panels.MisPrestamosPanel;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class MainView extends JFrame {

    private final Usuario usuarioLogueado;
    private final LibroDAO libroDAO;
    private final PrestamoDAO prestamoDAO;
    private final UsuarioDAO usuarioDAO;

    private JTabbedPane tabbedPane;
    private CatalogoPanel catalogoPanel;
    private MisPrestamosPanel misPrestamosPanel;
    private HistorialPanel historialPanel;

    // Componentes para "Mi Perfil"
    private JTextField campoCodigo, campoNombres, campoApellidos, campoDni;
    private JPasswordField campoContrasena;
    private JButton botonActualizarPerfil;

    public MainView(Usuario usuario) {
        this.usuarioLogueado = usuario;
        this.libroDAO = new LibroDAO();
        this.prestamoDAO = new PrestamoDAO();
        this.usuarioDAO = new UsuarioDAO();

        setTitle("Sistema de Biblioteca Universitaria - Bienvenido, " + usuarioLogueado.getNombres());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        cargarDatosPerfil();
    }

    private void initUI() {
        tabbedPane = new JTabbedPane();

        // Panel Catálogo
        catalogoPanel = new CatalogoPanel(this, libroDAO, prestamoDAO, usuarioLogueado);
        tabbedPane.addTab("Catálogo de Libros", catalogoPanel);

        // Panel Mis Préstamos
        misPrestamosPanel = new MisPrestamosPanel(this, prestamoDAO, libroDAO, usuarioLogueado);
        tabbedPane.addTab("Mis Préstamos", misPrestamosPanel);

        // Panel Historial de Préstamos
        historialPanel = new HistorialPanel(this, prestamoDAO, usuarioLogueado);
        tabbedPane.addTab("Historial de Préstamos", historialPanel);

        // Panel Mi Perfil
        JPanel panelPerfil = new JPanel(new GridBagLayout());
        panelPerfil.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; panelPerfil.add(new JLabel("Código:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; campoCodigo = new JTextField(20); campoCodigo.setEditable(false); panelPerfil.add(campoCodigo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panelPerfil.add(new JLabel("Nombres:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; campoNombres = new JTextField(20); panelPerfil.add(campoNombres, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panelPerfil.add(new JLabel("Apellidos:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; campoApellidos = new JTextField(20); panelPerfil.add(campoApellidos, gbc);

        gbc.gridx = 0; gbc.gridy = 3; panelPerfil.add(new JLabel("DNI:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; campoDni = new JTextField(20); panelPerfil.add(campoDni, gbc);

        gbc.gridx = 0; gbc.gridy = 4; panelPerfil.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; campoContrasena = new JPasswordField(20); panelPerfil.add(campoContrasena, gbc);

        botonActualizarPerfil = new JButton("Actualizar Perfil");
        gbc.gridx = 1; gbc.gridy = 5; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        panelPerfil.add(botonActualizarPerfil, gbc);
        botonActualizarPerfil.addActionListener(e -> actualizarPerfil());

        tabbedPane.addTab("Mi Perfil", panelPerfil);

        add(tabbedPane);
    }

    public void cargarDatosHistorial() {
        historialPanel.cargarDatosHistorial();
    }

    private void cargarDatosPerfil() {
        campoCodigo.setText(usuarioLogueado.getCodigo());
        campoNombres.setText(usuarioLogueado.getNombres());
        campoApellidos.setText(usuarioLogueado.getApellidos());
        campoDni.setText(usuarioLogueado.getDni());
    }

    private void actualizarPerfil() {
        String nombres = campoNombres.getText();
        String apellidos = campoApellidos.getText();
        String dni = campoDni.getText();
        String contrasena = new String(campoContrasena.getPassword());

        if (nombres.isEmpty() || apellidos.isEmpty() || dni.isEmpty()) {
            mostrarError("Todos los campos son obligatorios, excepto la contraseña.");
            return;
        }

        usuarioLogueado.setNombres(nombres);
        usuarioLogueado.setApellidos(apellidos);
        usuarioLogueado.setDni(dni);
        if (!contrasena.isEmpty()) {
            usuarioLogueado.setContrasena(contrasena);
        }

        try {
            if (usuarioDAO.actualizar(usuarioLogueado)) {
                mostrarMensaje("Perfil actualizado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                if (!contrasena.isEmpty()) {
                    campoContrasena.setText("");
                }
            } else {
                mostrarError("No se pudo actualizar el perfil.");
            }
        } catch (SQLException e) {
            mostrarError("Error al actualizar el perfil: " + e.getMessage());
        }
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void mostrarMensaje(String mensaje, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipo);
    }

    public CatalogoPanel getCatalogoPanel() {
        return catalogoPanel;
    }

    public MisPrestamosPanel getMisPrestamosPanel() {
        return misPrestamosPanel;
    }

    public HistorialPanel getHistorialPanel() {
        return historialPanel;
    }
}