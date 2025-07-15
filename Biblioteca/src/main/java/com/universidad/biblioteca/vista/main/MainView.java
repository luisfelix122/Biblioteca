package com.universidad.biblioteca.vista.main;
import com.universidad.biblioteca.controlador.LibroDAO;
import com.universidad.biblioteca.controlador.PrestamoDAO;

import com.universidad.biblioteca.modelo.Usuario;
import com.universidad.biblioteca.vista.panels.CatalogoPanel;
import com.universidad.biblioteca.vista.panels.GestionLibrosPanel;
import com.universidad.biblioteca.vista.panels.MisPrestamosPanel;
import com.universidad.biblioteca.vista.panels.MiPerfilPanel;
import com.universidad.biblioteca.vista.panels.HistorialPanel;
import com.universidad.biblioteca.config.ConexionBD;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;

public class MainView extends JFrame {

    private static final Color BACKGROUND_COLOR = new Color(243, 244, 246);

    private Usuario usuarioLogueado;
    private CatalogoPanel catalogoPanel;
    private MisPrestamosPanel misPrestamosPanel;
    private GestionLibrosPanel gestionLibrosPanel;
    private MiPerfilPanel miPerfilPanel;
    private HistorialPanel historialPanel;
    private Connection connection;

    public MainView(Usuario usuario) {
        this.usuarioLogueado = usuario;
        setTitle("Sistema de Gestión de Biblioteca - " + usuario.getNombre());
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);

        try {
            connection = ConexionBD.obtenerConexion();
            // Inicializar DAOs
            LibroDAO libroDAO = new LibroDAO(connection);
            PrestamoDAO prestamoDAO = new PrestamoDAO(connection);


        // Crear paneles
        catalogoPanel = new CatalogoPanel(this, libroDAO, prestamoDAO, usuarioLogueado);
        misPrestamosPanel = new MisPrestamosPanel(this, prestamoDAO, libroDAO, usuarioLogueado);
        miPerfilPanel = new MiPerfilPanel(usuarioLogueado);
        historialPanel = new HistorialPanel(this, prestamoDAO, usuarioLogueado);

        // Configurar pestañas
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setUI(new ModernTabbedPaneUI());
        tabbedPane.addTab("Catálogo de Libros", catalogoPanel);
        if ("ESTUDIANTE".equals(usuarioLogueado.getRol())) {
            tabbedPane.addTab("Mis Préstamos", misPrestamosPanel);
            tabbedPane.addTab("Historial", historialPanel);
        }

        // Si es bibliotecario, añadir panel de gestión
        if ("BIBLIOTECARIO".equals(usuarioLogueado.getRol())) {
            gestionLibrosPanel = new GestionLibrosPanel(this, libroDAO);
            tabbedPane.addTab("Gestión de Libros", gestionLibrosPanel);
        }

        tabbedPane.addTab("Mi Perfil", miPerfilPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Cargar datos iniciales
        cargarMisPrestamos();

        } catch (Exception e) {
            mostrarError("Ocurrió un error inesperado al iniciar la aplicación: " + e.getMessage());
            e.printStackTrace();
            // Forzamos el cierre para evitar que la aplicación quede en un estado inconsistente
            System.exit(1);
        }
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarMensaje(String mensaje, String titulo, int tipoMensaje) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipoMensaje);
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void cargarMisPrestamos() {
        if (misPrestamosPanel != null) {
            misPrestamosPanel.cargarDatosMisPrestamos();
        }
    }

    public MisPrestamosPanel getMisPrestamosPanel() {
        return misPrestamosPanel;
    }

    public CatalogoPanel getCatalogoPanel() {
        return catalogoPanel;
    }

    public Usuario getUsuario() {
        return usuarioLogueado;
    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public void dispose() {
        super.dispose();
        try {
            ConexionBD.cerrarConexion();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarError("Error al cerrar la conexión con la base de datos.");
        }
    }
}