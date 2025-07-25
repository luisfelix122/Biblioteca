package com.universidad.biblioteca.vista.main;
import com.universidad.biblioteca.controlador.LibroDAO;
import com.universidad.biblioteca.controlador.PrestamoDAO;

import com.universidad.biblioteca.modelo.Usuario;
import com.universidad.biblioteca.vista.estudiante.CatalogoPanel;
import com.universidad.biblioteca.vista.bibliotecario.GestionLibrosPanel;
import com.universidad.biblioteca.vista.estudiante.MisPrestamosPanel;
import com.universidad.biblioteca.vista.estudiante.MiPerfilPanel;
import com.universidad.biblioteca.vista.estudiante.HistorialLecturaPanel;
import com.universidad.biblioteca.vista.estudiante.SugerenciasPanel;
import com.universidad.biblioteca.vista.bibliotecario.HistorialPanel;



import javax.swing.*;
import java.awt.*;
import com.universidad.biblioteca.vista.auth.LoginView;

public class MainView extends JFrame {

    private static final Color BACKGROUND_COLOR = new Color(243, 244, 246);
    private static final Color PRIMARY_COLOR = new Color(67, 56, 202);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final int WINDOW_WIDTH = 1280;
    private static final int WINDOW_HEIGHT = 800;

    private Usuario usuarioLogueado;
    private CatalogoPanel catalogoPanel;
    private MisPrestamosPanel misPrestamosPanel;
    private GestionLibrosPanel gestionLibrosPanel;
    private MiPerfilPanel miPerfilPanel;
    private HistorialLecturaPanel historialLecturaPanel;
    private SugerenciasPanel sugerenciasPanel;
    private HistorialPanel historialPanel;
    @SuppressWarnings("unused")
    private com.universidad.biblioteca.vista.bibliotecario.GestionUsuariosPanel gestionUsuariosPanel;
    private com.universidad.biblioteca.vista.bibliotecario.ReportesPanel reportesPanel;
    private JPanel headerPanel;

    public MainView(Usuario usuario) {
        System.out.println("MainView constructor called. Timestamp: " + System.currentTimeMillis());
        this.usuarioLogueado = usuario;
        init();
    }

    

    private void init() {
        System.out.println("MainView init() called.");
        setTitle("Sistema de Gestión de Biblioteca");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setMinimumSize(new Dimension(1024, 768));

        // Configurar el header
        System.out.println("Creando header panel...");
        headerPanel = createHeaderPanel();
        System.out.println("Header panel creado.");



        try {
            System.out.println("Dentro del bloque try-catch de MainView.");
            //connection = ConexionBD.obtenerConexion();
            // Inicializar DAOs
            System.out.println("Inicializando DAOs...");
            LibroDAO libroDAO = new LibroDAO();
            PrestamoDAO prestamoDAO = new PrestamoDAO();
            System.out.println("DAOs inicializados.");


        // Configurar pestañas con estilo moderno
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setUI(new ModernTabbedPaneUI());
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder());

        if (usuarioLogueado.getRol() != null) {
            System.out.println("Configurando pestañas para el rol: " + usuarioLogueado.getRol().getNombre());

            if ("Estudiante".equalsIgnoreCase(usuarioLogueado.getRol().getNombre().trim())) {
                System.out.println("Configurando UI para Estudiante.");
                catalogoPanel = new CatalogoPanel(this, libroDAO, prestamoDAO, usuarioLogueado);
                misPrestamosPanel = new MisPrestamosPanel(this, prestamoDAO, libroDAO, usuarioLogueado);
                miPerfilPanel = new MiPerfilPanel(usuarioLogueado);
                historialLecturaPanel = new HistorialLecturaPanel(usuarioLogueado);
                sugerenciasPanel = new SugerenciasPanel(usuarioLogueado);

                tabbedPane.addTab("Catálogo", catalogoPanel);
                tabbedPane.addTab("Mis Préstamos", misPrestamosPanel);
                tabbedPane.addTab("Historial de Lectura", historialLecturaPanel);
                tabbedPane.addTab("Sugerencias", sugerenciasPanel);
                tabbedPane.addTab("Mi Perfil", miPerfilPanel);
                System.out.println("UI para Estudiante configurada.");
                catalogoPanel.cargarDatosCatalogo();
            } else if ("Bibliotecario".equalsIgnoreCase(usuarioLogueado.getRol().getNombre().trim())) {
                System.out.println("Configurando UI para Bibliotecario.");
                gestionLibrosPanel = new GestionLibrosPanel(usuarioLogueado);
                historialPanel = new HistorialPanel(usuarioLogueado);
                com.universidad.biblioteca.vista.bibliotecario.CatalogoPanelBibliotecario catalogoPanelBibliotecario = new com.universidad.biblioteca.vista.bibliotecario.CatalogoPanelBibliotecario(usuarioLogueado);
                reportesPanel = new com.universidad.biblioteca.vista.bibliotecario.ReportesPanel(usuarioLogueado);

                java.net.URL usersIconUrl = getClass().getResource("/icons/users.png");
                if (usersIconUrl == null) {
                    System.err.println("Error: users.png resource not found!");
                } else {
                    System.out.println("usersIconUrl: " + usersIconUrl);
                }
                @SuppressWarnings("unused")
                ImageIcon usersIcon = (usersIconUrl != null) ? new ImageIcon(usersIconUrl) : new ImageIcon();
                tabbedPane.addTab("Catálogo", catalogoPanelBibliotecario);
                java.net.URL reportsIconUrl = getClass().getResource("/icons/reports.png");
                if (reportsIconUrl == null) {
                    System.err.println("Error: reports.png resource not found!");
                }
                else {
                    System.out.println("reportsIconUrl: " + reportsIconUrl);
                }
                ImageIcon reportsIcon = (reportsIconUrl != null) ? new ImageIcon(reportsIconUrl) : new ImageIcon();
                tabbedPane.addTab("Gestión de Libros", gestionLibrosPanel);
                tabbedPane.addTab("Historial", historialPanel);
                tabbedPane.addTab("Reportes", reportsIcon, reportesPanel);
                catalogoPanelBibliotecario.cargarLibros(1);
                System.out.println("UI para Bibliotecario configurada.");
            } else if ("Administrador".equalsIgnoreCase(usuarioLogueado.getRol().getNombre().trim()) || "Admin".equalsIgnoreCase(usuarioLogueado.getRol().getNombre().trim())) {
                System.out.println("Configurando UI para Administrador.");
                tabbedPane.addTab("Gestión de Usuarios", new com.universidad.biblioteca.vista.administrador.GestionUsuariosPanel(usuarioLogueado));
                tabbedPane.addTab("Gestión de Bibliotecarios", new com.universidad.biblioteca.vista.administrador.GestionBibliotecariosPanel(usuarioLogueado));
                tabbedPane.addTab("Auditoría", new com.universidad.biblioteca.vista.administrador.AuditoriaPanel(usuarioLogueado));
                tabbedPane.addTab("Configuración", new com.universidad.biblioteca.vista.administrador.ConfiguracionSistemaPanel(usuarioLogueado));
                System.out.println("UI para Administrador configurada.");
            } else {
                System.out.println("Rol no reconocido: " + usuarioLogueado.getRol().getNombre());
                // Optionally, add a default tab to avoid the exception
                JPanel defaultPanel = new JPanel();
                defaultPanel.add(new JLabel("Bienvenido. No hay vistas configuradas para su rol."));
                tabbedPane.addTab("Inicio", defaultPanel);
            }
        } else {
            System.out.println("El usuario no tiene un rol asignado.");
            mostrarError("Error: El usuario no tiene un rol asignado.");
        }

        // Crear panel principal con header y contenido
        System.out.println("Creando panel principal...");
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        System.out.println("Panel principal creado.");

        add(mainPanel);

        // Cargar datos iniciales
        System.out.println("Cargando datos iniciales...");
        cargarMisPrestamos();
        System.out.println("Datos iniciales cargados.");

        } catch (Throwable t) {
            mostrarError("Ocurrió un error inesperado al iniciar la aplicación: " + t.getMessage());
            t.printStackTrace();
            // Forzamos el cierre para evitar que la aplicación quede en un estado inconsistente
            System.out.println("Error en MainView: " + t.getMessage());
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



    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        // Panel izquierdo para el título
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Biblioteca Universitaria");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        leftPanel.add(titleLabel);

        // Panel derecho para la información del usuario
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);

        // Add logout button
        JButton logoutButton = new JButton("Cerrar Sesión");
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutButton.setBackground(new Color(220, 53, 69)); // Red color
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        logoutButton.addActionListener(_ -> {
            // Dispose current frame
            dispose();
            // Open login view
            SwingUtilities.invokeLater(() -> {
                LoginView loginView = new LoginView();
                new com.universidad.biblioteca.controlador.auth.LoginController(loginView);
                loginView.setVisible(true);
            });
        });
        rightPanel.add(logoutButton);

        String roleName = (usuarioLogueado.getRol() != null) ? usuarioLogueado.getRol().getNombre() : "Rol no asignado";
        JLabel userLabel = new JLabel(usuarioLogueado.getNombre() + " | " + roleName);
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(Color.WHITE);
        rightPanel.add(userLabel);

        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }


}