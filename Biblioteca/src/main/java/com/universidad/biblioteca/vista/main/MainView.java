package com.universidad.biblioteca.vista.main;

import com.universidad.biblioteca.config.ConexionBD;
import com.universidad.biblioteca.controlador.LibroDAO;
import com.universidad.biblioteca.controlador.PrestamoDAO;
import com.universidad.biblioteca.controlador.UsuarioDAO;
import com.universidad.biblioteca.modelo.Usuario;
import com.universidad.biblioteca.vista.panels.CatalogoPanel;
import com.universidad.biblioteca.vista.panels.HistorialPanel;
import com.universidad.biblioteca.vista.panels.MisPrestamosPanel;
import com.universidad.biblioteca.vista.panels.PerfilPanel;

import javax.swing.*;
import java.sql.Connection;
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
    private PerfilPanel perfilPanel;

    public MainView(Usuario usuario) throws SQLException {
        this.usuarioLogueado = usuario;
        Connection conexion = ConexionBD.obtenerConexion();
        this.libroDAO = new LibroDAO(conexion);
        this.prestamoDAO = new PrestamoDAO(conexion);
        this.usuarioDAO = new UsuarioDAO(conexion);

        setTitle("Sistema de Biblioteca Universitaria - Bienvenido, " + usuarioLogueado.getNombre());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
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
        perfilPanel = new PerfilPanel(this, usuarioDAO, usuarioLogueado);
        tabbedPane.addTab("Mi Perfil", perfilPanel);

        add(tabbedPane);
    }

    public void cargarDatosHistorial() {
        historialPanel.cargarDatosHistorial();
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