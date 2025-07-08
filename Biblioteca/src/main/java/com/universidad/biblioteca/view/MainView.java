package com.universidad.biblioteca.view;

import com.universidad.biblioteca.controller.LibroDAO;
import com.universidad.biblioteca.controller.PrestamoDAO;
import com.universidad.biblioteca.controller.UsuarioDAO;
import com.universidad.biblioteca.model.Libro;
import com.universidad.biblioteca.model.Prestamo;
import com.universidad.biblioteca.model.Usuario;

// iText PDF imports
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Paragraph;

// Swing imports
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

// Apache POI imports
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// Java utilities
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.util.List;

public class MainView extends JFrame {

    private final String codigoUser;
    private final LibroDAO libroDAO;
    private final PrestamoDAO prestamoDAO;

    // Colores del tema
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);
    private static final Color WARNING_COLOR = new Color(241, 196, 15);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(44, 62, 80);
    private static final Color TEXT_SECONDARY = new Color(127, 140, 141);

    // Componentes UI
    private JTabbedPane tabbedPane;
    private JPanel panelCatalogo, panelMisPrestamos, panelHistorial, panelPerfil;
    private JTextField txtBuscar;
    private JTextField txtNombre, txtEmail, txtTelefono;
    private JButton btnBuscar, btnSolicitarPrestamo, btnDevolver;
    private JButton btnExportCatalogo, btnExportHistorial;
    private JButton btnExportCatalogoPDF, btnExportHistorialPDF;
    private JButton btnGuardarPerfil;
    private JTable tblCatalogo, tblMisPrestamos, tblHistorial;

    // Iconos (usando caracteres Unicode)
    private static final String ICON_BOOK = "📚";
    private static final String ICON_SEARCH = "🔍";
    private static final String ICON_ADD = "➕";
    private static final String ICON_RETURN = "↩️";
    private static final String ICON_EXPORT = "📤";
    private static final String ICON_PDF = "📄";
    private static final String ICON_EXCEL = "📊";
    private static final String ICON_SAVE = "💾";
    private static final String ICON_USER = "👤";
    private static final String ICON_HISTORY = "📋";
    private static final String ICON_LOAN = "📖";
    private static final String ICON_CATALOG = "📚";

    public MainView(String codigoUser) {
        this.codigoUser = codigoUser;
        
        // Configurar Look & Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        initComponents();
        
        libroDAO = new LibroDAO();
        prestamoDAO = new PrestamoDAO();
        
        setupEventHandlers();
        loadInitialData();
    }

    private void initComponents() {
        // Configuración principal de la ventana
        setTitle("📚 Sistema de Biblioteca Universitaria - Panel Principal");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Panel principal con gradiente mejorado
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradiente diagonal más suave
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(240, 248, 255),
                    getWidth(), getHeight(), new Color(230, 240, 250)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Patrón de puntos sutil
                g2d.setColor(new Color(255, 255, 255, 30));
                for (int x = 0; x < getWidth(); x += 40) {
                    for (int y = 0; y < getHeight(); y += 40) {
                        g2d.fillOval(x, y, 2, 2);
                    }
                }
            }
        };
        mainPanel.setLayout(new BorderLayout());
        
        // Header mejorado
        JPanel headerPanel = createEnhancedHeaderPanel();
        
        // TabbedPane estilizado
        tabbedPane = new JTabbedPane();
        styleEnhancedTabbedPane(tabbedPane);
        
        // Crear paneles mejorados
        createEnhancedCatalogoPanel();
        createEnhancedMisPrestamosPanel();
        createEnhancedHistorialPanel();
        createEnhancedPerfilPanel();
        
        // Agregar tabs con iconos
        tabbedPane.addTab(ICON_CATALOG + " Catálogo", panelCatalogo);
        tabbedPane.addTab(ICON_LOAN + " Mis Préstamos", panelMisPrestamos);
        tabbedPane.addTab(ICON_HISTORY + " Historial", panelHistorial);
        tabbedPane.addTab(ICON_USER + " Perfil", panelPerfil);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Footer con información adicional
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private JPanel createEnhancedHeaderPanel() {
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradiente para el header
                GradientPaint gradient = new GradientPaint(
                    0, 0, PRIMARY_COLOR,
                    0, getHeight(), SECONDARY_COLOR
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Sombra sutil
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.fillRect(0, getHeight() - 2, getWidth(), 2);
            }
        };
        
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(25, 40, 25, 40));
        headerPanel.setPreferredSize(new Dimension(0, 90));
        
        // Panel izquierdo con logo y título
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        
        JLabel logoLabel = new JLabel("📚");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.setBorder(new EmptyBorder(0, 15, 0, 0));
        
        JLabel titleLabel = new JLabel("Sistema de Biblioteca Universitaria");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Gestión integral de recursos bibliográficos");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(255, 255, 255, 180));
        
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        
        leftPanel.add(logoLabel);
        leftPanel.add(titlePanel);
        
        // Panel derecho con información del usuario
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);
        
        JLabel userIconLabel = new JLabel(ICON_USER);
        userIconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        userIconLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        JLabel userLabel = new JLabel("Usuario: " + codigoUser);
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        userLabel.setForeground(Color.WHITE);
        userLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        JLabel timeLabel = new JLabel("Conectado: " + java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timeLabel.setForeground(new Color(255, 255, 255, 180));
        timeLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        rightPanel.add(userIconLabel);
        rightPanel.add(userLabel);
        rightPanel.add(timeLabel);
        
        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JLabel footerLabel = new JLabel("© 2024 Sistema de Biblioteca Universitaria - Versión 2.0");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerLabel.setForeground(TEXT_SECONDARY);
        
        JLabel statusLabel = new JLabel("🟢 Sistema operativo");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(SUCCESS_COLOR);
        
        footerPanel.add(footerLabel, BorderLayout.WEST);
        footerPanel.add(statusLabel, BorderLayout.EAST);
        
        return footerPanel;
    }
    
    private void styleEnhancedTabbedPane(JTabbedPane tabbedPane) {
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tabbedPane.setBackground(BACKGROUND_COLOR);
        tabbedPane.setForeground(TEXT_PRIMARY);
        tabbedPane.setBorder(new EmptyBorder(15, 25, 25, 25));
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        
        // Personalizar apariencia de las tabs
        UIManager.put("TabbedPane.selected", CARD_COLOR);
        UIManager.put("TabbedPane.background", BACKGROUND_COLOR);
        UIManager.put("TabbedPane.foreground", TEXT_PRIMARY);
        UIManager.put("TabbedPane.selectedForeground", PRIMARY_COLOR);
    }
        private void createEnhancedCatalogoPanel() {
        panelCatalogo = new JPanel(new BorderLayout());
        panelCatalogo.setBackground(BACKGROUND_COLOR);
        panelCatalogo.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        // Card container
        JPanel cardPanel = createCardPanel();
        cardPanel.setLayout(new BorderLayout());
        
        // Panel de búsqueda mejorado
        JPanel searchPanel = createSearchPanel();
        
        // Tabla de catálogo mejorada
        tblCatalogo = new JTable(new DefaultTableModel(
                new Object[][]{}, new String[]{"📖 Título", "👨‍💼 Autor", "📅 Año", "✅ Disponible"}
        ));
        styleEnhancedTable(tblCatalogo);
        
        JScrollPane scrollCatalogo = new JScrollPane(tblCatalogo);
        styleEnhancedScrollPane(scrollCatalogo);
        
        // Panel de botones mejorado
        JPanel buttonPanel = createCatalogoButtonPanel();
        
        cardPanel.add(searchPanel, BorderLayout.NORTH);
        cardPanel.add(scrollCatalogo, BorderLayout.CENTER);
        cardPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panelCatalogo.add(cardPanel, BorderLayout.CENTER);
    }
    
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Título de sección
        JLabel titleLabel = new JLabel(ICON_SEARCH + " Búsqueda de Libros");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Panel de búsqueda
        JPanel searchInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
        searchInputPanel.setOpaque(false);
        searchInputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel searchLabel = new JLabel("Buscar libro:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchLabel.setForeground(TEXT_SECONDARY);
        
        txtBuscar = new JTextField(30);
        styleEnhancedTextField(txtBuscar, "Ingrese título, autor o ISBN...");
        
        btnBuscar = new JButton(ICON_SEARCH + " Buscar");
        styleEnhancedButton(btnBuscar, PRIMARY_COLOR, Color.WHITE, true);
        
        JButton btnLimpiar = new JButton("🔄 Limpiar");
        styleEnhancedButton(btnLimpiar, TEXT_SECONDARY, Color.WHITE, false);
        
        searchInputPanel.add(searchLabel);
        searchInputPanel.add(Box.createHorizontalStrut(10));
        searchInputPanel.add(txtBuscar);
        searchInputPanel.add(Box.createHorizontalStrut(10));
        searchInputPanel.add(btnBuscar);
        searchInputPanel.add(Box.createHorizontalStrut(5));
        searchInputPanel.add(btnLimpiar);
        
        searchPanel.add(titleLabel);
        searchPanel.add(Box.createVerticalStrut(15));
        searchPanel.add(searchInputPanel);
        
        return searchPanel;
    }
    
    private JPanel createCatalogoButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setOpaque(false);
        
        btnSolicitarPrestamo = new JButton(ICON_ADD + " Solicitar Préstamo");
        styleEnhancedButton(btnSolicitarPrestamo, SUCCESS_COLOR, Color.WHITE, true);
        
        btnExportCatalogo = new JButton(ICON_EXCEL + " Exportar Excel");
        styleEnhancedButton(btnExportCatalogo, WARNING_COLOR, Color.WHITE, false);
        
        btnExportCatalogoPDF = new JButton(ICON_PDF + " Exportar PDF");
        styleEnhancedButton(btnExportCatalogoPDF, DANGER_COLOR, Color.WHITE, false);
        
        buttonPanel.add(btnSolicitarPrestamo);
        buttonPanel.add(btnExportCatalogo);
        buttonPanel.add(btnExportCatalogoPDF);
        
        return buttonPanel;
    }
    
    private void createEnhancedMisPrestamosPanel() {
        panelMisPrestamos = new JPanel(new BorderLayout());
        panelMisPrestamos.setBackground(BACKGROUND_COLOR);
        panelMisPrestamos.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        // Card container
        JPanel cardPanel = createCardPanel();
        cardPanel.setLayout(new BorderLayout());
        
        // Header del panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel(ICON_LOAN + " Mis Préstamos Activos");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_PRIMARY);
        
        JLabel countLabel = new JLabel("Total: 0 préstamos");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        countLabel.setForeground(TEXT_SECONDARY);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(countLabel, BorderLayout.EAST);
        
        // Tabla mejorada
        tblMisPrestamos = new JTable(new DefaultTableModel(
                new Object[][]{}, new String[]{"🆔 ID", "📖 Título", "📅 Fecha Préstamo", "⏰ Días Restantes"}
        ));
        styleEnhancedTable(tblMisPrestamos);
        
        JScrollPane scrollPrestamos = new JScrollPane(tblMisPrestamos);
        styleEnhancedScrollPane(scrollPrestamos);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setOpaque(false);
        
        btnDevolver = new JButton(ICON_RETURN + " Devolver Libro");
        styleEnhancedButton(btnDevolver, PRIMARY_COLOR, Color.WHITE, true);
        
        JButton btnRenovar = new JButton("🔄 Renovar Préstamo");
        styleEnhancedButton(btnRenovar, ACCENT_COLOR, Color.WHITE, false);
        
        buttonPanel.add(btnDevolver);
        buttonPanel.add(btnRenovar);
        
        cardPanel.add(headerPanel, BorderLayout.NORTH);
        cardPanel.add(formPanel, BorderLayout.CENTER);
        
        panelPerfil.add(cardPanel, BorderLayout.CENTER);
    }
    
    // Métodos de utilidad para estilizar componentes
    private JPanel createCardPanel() {
        JPanel cardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Sombra del card
                g2d.setColor(new Color(0, 0, 0, 10));
                g2d.fillRoundRect(3, 3, getWidth() - 3, getHeight() - 3, 15, 15);
                
                // Card principal
                g2d.setColor(CARD_COLOR);
                g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 15, 15);
                
                // Borde sutil
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.drawRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 15, 15);
            }
        };
        cardPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        return cardPanel;
    }
    
    private void styleEnhancedTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(45);
        table.setGridColor(new Color(240, 240, 240));
        table.setSelectionBackground(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 30));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        
        // Header personalizado
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 50));
        header.setBorder(BorderFactory.createEmptyBorder());
        
        // Renderer personalizado para celdas
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                }
                
                setBorder(new EmptyBorder(10, 15, 10, 15));
                return c;
            }
        };
        
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
    }
    
    private void styleEnhancedScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBackground(Color.WHITE);
        
        // Personalizar scrollbars
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(200, 200, 200);
                this.trackColor = new Color(245, 245, 245);
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });
    }
    
    private void styleEnhancedButton(JButton button, Color bgColor, Color textColor, boolean isPrimary) {
        button.setFont(new Font("Segoe UI", isPrimary ? Font.BOLD : Font.PLAIN, 13));
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efectos hover
        button.addMouseListener(new MouseAdapter() {
            Color originalColor = bgColor;
            
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(originalColor.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalColor);
            }
        });
        
        // Bordes redondeados
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (c.getModel().isPressed()) {
                    g2d.setColor(bgColor.darker().darker());
                } else {
                    g2d.setColor(c.getBackground());
                }
                
                g2d.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 8, 8);
                
                FontMetrics fm = g2d.getFontMetrics();
                Rectangle stringBounds = fm.getStringBounds(button.getText(), g2d).getBounds();
                int textX = (c.getWidth() - stringBounds.width) / 2;
                int textY = (c.getHeight() - stringBounds.height) / 2 + fm.getAscent();
                
                g2d.setColor(textColor);
                g2d.drawString(button.getText(), textX, textY);
            }
        });
    }
    
    private void styleEnhancedTextField(JTextField textField, String placeholder) {
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1, true),
            new EmptyBorder(12, 15, 12, 15)
        ));
        textField.setBackground(Color.WHITE);
        textField.setForeground(TEXT_PRIMARY);
        
        // Placeholder text
        textField.setText(placeholder);
        textField.setForeground(TEXT_SECONDARY);
        
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(TEXT_PRIMARY);
                }
                textField.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(PRIMARY_COLOR, 2, true),
                    new EmptyBorder(11, 14, 11, 14)
                ));
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(TEXT_SECONDARY);
                }
                textField.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(220, 220, 220), 1, true),
                    new EmptyBorder(12, 15, 12, 15)
                ));
            }
        });
    }
    
    private void styleEnhancedLabel(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
    }
    
    // Métodos de manejo de eventos
    private void setupEventHandlers() {
        // Configurar eventos de botones y otros componentes
        btnBuscar.addActionListener(e -> buscarLibros());
        btnSolicitarPrestamo.addActionListener(e -> solicitarPrestamo());
        btnDevolver.addActionListener(e -> devolverLibro());
        btnExportCatalogo.addActionListener(e -> exportarCatalogoExcel());
        btnExportCatalogoPDF.addActionListener(e -> exportarCatalogoPDF());
        btnExportHistorial.addActionListener(e -> exportarHistorialExcel());
        btnExportHistorialPDF.addActionListener(e -> exportarHistorialPDF());
        btnGuardarPerfil.addActionListener(e -> guardarPerfil());
        
        // Enter key para búsqueda
        txtBuscar.addActionListener(e -> buscarLibros());
    }
    
    private void loadInitialData() {
        cargarCatalogo();
        cargarMisPrestamos();
        cargarHistorial();
        cargarDatosUsuario();
    }
    
    // Métodos de funcionalidad
    private void buscarLibros() {
        try {
            String termino = txtBuscar.getText().trim();
            if (termino.isEmpty() || termino.equals("Ingrese título, autor o ISBN...")) {
                cargarCatalogo();
                return;
            }
            
            List<Libro> libros = libroDAO.buscarLibros(termino);
            actualizarTablaCatalogo(libros);
        } catch (SQLException e) {
            mostrarError("Error al buscar libros: " + e.getMessage());
        }
    }
    
    private void cargarCatalogo() {
        try {
            List<Libro> libros = libroDAO.obtenerTodosLosLibros();
            actualizarTablaCatalogo(libros);
        } catch (SQLException e) {
            mostrarError("Error al cargar catálogo: " + e.getMessage());
        }
    }
    
    private void actualizarTablaCatalogo(List<Libro> libros) {
        DefaultTableModel model = (DefaultTableModel) tblCatalogo.getModel();
        model.setRowCount(0);
        
        for (Libro libro : libros) {
            model.addRow(new Object[]{
                libro.getTitulo(),
                libro.getAutor(),
                libro.getAnioPublicacion(),
                libro.isDisponible() ? "✅ Disponible" : "❌ No disponible"
            });
        }
    }
    
    private void cargarMisPrestamos() {
        try {
            List<Prestamo> prestamos = prestamoDAO.obtenerPrestamosPorUsuario(codigoUser);
            actualizarTablaPrestamos(prestamos);
        } catch (SQLException e) {
            mostrarError("Error al cargar préstamos: " + e.getMessage());
        }
    }
    
    private void actualizarTablaPrestamos(List<Prestamo> prestamos) {
        DefaultTableModel model = (DefaultTableModel) tblMisPrestamos.getModel();
        model.setRowCount(0);
        
        for (Prestamo prestamo : prestamos) {
            model.addRow(new Object[]{
                prestamo.getId(),
                prestamo.getLibro().getTitulo(),
                prestamo.getFechaPrestamo(),
                calcularDiasRestantes(prestamo.getFechaPrestamo())
            });
        }
    }
    
    private void cargarHistorial() {
        try {
            List<Prestamo> historial = prestamoDAO.obtenerHistorialPorUsuario(codigoUser);
            actualizarTablaHistorial(historial);
        } catch (SQLException e) {
            mostrarError("Error al cargar historial: " + e.getMessage());
        }
    }
    
    private void actualizarTablaHistorial(List<Prestamo> historial) {
        DefaultTableModel model = (DefaultTableModel) tblHistorial.getModel();
        model.setRowCount(0);
        
        for (Prestamo prestamo : historial) {
            model.addRow(new Object[]{
                prestamo.getId(),
                prestamo.getLibro().getTitulo(),
                prestamo.getFechaPrestamo(),
                prestamo.getFechaDevolucion(),
                prestamo.getMulta() > 0 ? "$" + prestamo.getMulta() : "$0.00",
                prestamo.getEstado()
            });
        }
    }
    
    private void cargarDatosUsuario() {
        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            Usuario usuario = usuarioDAO.obtenerUsuarioPorCodigo(codigoUser);
            
            if (usuario != null) {
                txtNombre.setText(usuario.getNombre());
                txtNombre.setForeground(TEXT_PRIMARY);
                txtEmail.setText(usuario.getEmail());
                txtEmail.setForeground(TEXT_PRIMARY);
                txtTelefono.setText(usuario.getTelefono());
                txtTelefono.setForeground(TEXT_PRIMARY);
            }
        } catch (SQLException e) {
            mostrarError("Error al cargar datos del usuario: " + e.getMessage());
        }
    }
    
    private void solicitarPrestamo() {
        int selectedRow = tblCatalogo.getSelectedRow();
        if (selectedRow == -1) {
            mostrarAdvertencia("Por favor, seleccione un libro del catálogo.");
            return;
        }
        
        try {
            String titulo = (String) tblCatalogo.getValueAt(selectedRow, 0);
            Libro libro = libroDAO.obtenerLibroPorTitulo(titulo);
            
            if (libro != null && libro.isDisponible()) {
                boolean exito = prestamoDAO.crearPrestamo(codigoUser, libro.getId());
                if (exito) {
                    mostrarExito("Préstamo solicitado exitosamente.");
                    cargarCatalogo();
                    cargarMisPrestamos();
                } else {
                    mostrarError("No se pudo procesar el préstamo.");
                }
            } else {
                mostrarAdvertencia("El libro seleccionado no está disponible.");
            }
        } catch (SQLException e) {
            mostrarError("Error al solicitar préstamo: " + e.getMessage());
        }
    }
    
    private void devolverLibro() {
        int selectedRow = tblMisPrestamos.getSelectedRow();
        if (selectedRow == -1) {
            mostrarAdvertencia("Por favor, seleccione un préstamo para devolver.");
            return;
        }
        
        try {
            int prestamoId = (Integer) tblMisPrestamos.getValueAt(selectedRow, 0);
            boolean exito = prestamoDAO.devolverLibro(prestamoId);
            
            if (exito) {
                mostrarExito("Libro devuelto exitosamente.");
                cargarCatalogo();
                cargarMisPrestamos();
                cargarHistorial();
            } else {
                mostrarError("No se pudo procesar la devolución.");
            }
        } catch (SQLException e) {
            mostrarError("Error al devolver libro: " + e.getMessage());
        }
    }
    
    private void guardarPerfil() {
        try {
            String nombre = txtNombre.getText().trim();
            String email = txtEmail.getText().trim();
            String telefono = txtTelefono.getText().trim();
            
            if (nombre.isEmpty() || email.isEmpty() || telefono.isEmpty()) {
                mostrarAdvertencia("Por favor, complete todos los campos.");
                return;
            }
            
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            Usuario usuario = new Usuario();
            usuario.setCodigo(codigoUser);
            usuario.setNombre(nombre);
            usuario.setEmail(email);
            usuario.setTelefono(telefono);
            
            boolean exito = usuarioDAO.actualizarUsuario(usuario);
            if (exito) {
                mostrarExito("Perfil actualizado exitosamente.");
            } else {
                mostrarError("No se pudo actualizar el perfil.");
            }
        } catch (SQLException e) {
            mostrarError("Error al guardar perfil: " + e.getMessage());
        }
    }
    
    // Métodos de exportación
    private void exportarCatalogoExcel() {
        try {
            List<Libro> libros = libroDAO.obtenerTodosLosLibros();
            exportarExcel(libros, "Catalogo.xlsx", "Catálogo de Libros");
            mostrarExito("Catálogo exportado exitosamente.");
        } catch (Exception e) {
            mostrarError("Error al exportar catálogo: " + e.getMessage());
        }
    }
    
    private void exportarCatalogoPDF() {
        try {
            List<Libro> libros = libroDAO.obtenerTodosLosLibros();
            exportarPDF(libros, "Catalogo.pdf", "Catálogo de Libros");
            mostrarExito("Catálogo PDF exportado exitosamente.");
        } catch (Exception e) {
            mostrarError("Error al exportar PDF: " + e.getMessage());
        }
    }
    
    private void exportarHistorialExcel() {
        try {
            List<Prestamo> historial = prestamoDAO.obtenerHistorialPorUsuario(codigoUser);
            exportarHistorialExcel(historial, "Historial.xlsx");
            mostrarExito("Historial exportado exitosamente.");
        } catch (Exception e) {
            mostrarError("Error al exportar historial: " + e.getMessage());
        }
    }
    
    private void exportarHistorialPDF() {
        try {
            List<Prestamo> historial = prestamoDAO.obtenerHistorialPorUsuario(codigoUser);
            exportarHistorialPDF(historial, "Historial.pdf");
            mostrarExito("Historial PDF exportado exitosamente.");
        } catch (Exception e) {
            mostrarError("Error al exportar PDF: " + e.getMessage());
        }
    }
    
    // Métodos auxiliares
    private void exportarExcel(List<Libro> libros, String filename, String sheetName) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);
        
        // Header
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Título");
        headerRow.createCell(1).setCellValue("Autor");
        headerRow.createCell(2).setCellValue("Año");
        headerRow.createCell(3).setCellValue("Disponible");
        
        // Data
        for (int i = 0; i < libros.size(); i++) {
            Row row = sheet.createRow(i + 1);
            Libro libro = libros.get(i);
            row.createCell(0).setCellValue(libro.getTitulo());
            row.createCell(1).setCellValue(libro.getAutor());
            row.createCell(2).setCellValue(libro.getAnioPublicacion());
            row.createCell(3).setCellValue(libro.isDisponible() ? "Sí" : "No");
        }
        
        try (FileOutputStream fileOut = new FileOutputStream(filename)) {
            workbook.write(fileOut);
        }
        workbook.close();
    }
    
    private void exportarPDF(List<Libro> libros, String filename, String title) throws Exception {
        PdfWriter writer = new PdfWriter(filename);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        
        document.add(new Paragraph(title).setFontSize(18).setBold());
        
        Table table = new Table(4);
        table.addHeaderCell(new Cell().add(new Paragraph("Título")));
        table.addHeaderCell(new Cell().add(new Paragraph("Autor")));
        table.addHeaderCell(new Cell().add(new Paragraph("Año")));
        table.addHeaderCell(new Cell().add(new Paragraph("Disponible")));
        
        for (Libro libro : libros) {
            table.addCell(new Cell().add(new Paragraph(libro.getTitulo())));
            table.addCell(new Cell().add(new Paragraph(libro.getAutor())));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(libro.getAnioPublicacion()))));
            table.addCell(new Cell().add(new Paragraph(libro.isDisponible() ? "Sí" : "No")));
        }
        
        document.add(table);
        document.close();
    }
    
    private void exportarHistorialExcel(List<Prestamo> historial, String filename) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Historial de Préstamos");
        
        // Header
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Título");
        headerRow.createCell(2).setCellValue("Fecha Préstamo");
        headerRow.createCell(3).setCellValue("Fecha Devolución");
        headerRow.createCell(4).setCellValue("Multa");
        headerRow.createCell(5).setCellValue("Estado");
        
        // Data
        for (int i = 0; i < historial.size(); i++) {
            Row row = sheet.createRow(i + 1);
            Prestamo prestamo = historial.get(i);
            row.createCell(0).setCellValue(prestamo.getId());
            row.createCell(1).setCellValue(prestamo.getLibro().getTitulo());
            row.createCell(2).setCellValue(prestamo.getFechaPrestamo().toString());
            row.createCell(3).setCellValue(prestamo.getFechaDevolucion() != null ? 
                prestamo.getFechaDevolucion().toString() : "Pendiente");
            row.createCell(4).setCellValue(prestamo.getMulta());
            row.createCell(5).setCellValue(prestamo.getEstado());
        }
        
        try (FileOutputStream fileOut = new FileOutputStream(filename)) {
            workbook.write(fileOut);
        }
        workbook.close();
    }
    
    private void exportarHistorialPDF(List<Prestamo> historial, String filename) throws Exception {
        PdfWriter writer = new PdfWriter(filename);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        
        document.add(new Paragraph("Historial de Préstamos").setFontSize(18).setBold());
        
        Table table = new Table(6);
        table.addHeaderCell(new Cell().add(new Paragraph("ID")));
        table.addHeaderCell(new Cell().add(new Paragraph("Título")));
        table.addHeaderCell(new Cell().add(new Paragraph("F. Préstamo")));
        table.addHeaderCell(new Cell().add(new Paragraph("F. Devolución")));
        table.addHeaderCell(new Cell().add(new Paragraph("Multa")));
        table.addHeaderCell(new Cell().add(new Paragraph("Estado")));
        
        for (Prestamo prestamo : historial) {
            table.addCell(new Cell().add(new Paragraph(String.valueOf(prestamo.getId()))));
            table.addCell(new Cell().add(new Paragraph(prestamo.getLibro().getTitulo())));
            table.addCell(new Cell().add(new Paragraph(prestamo.getFechaPrestamo().toString())));
            table.addCell(new Cell().add(new Paragraph(
                prestamo.getFechaDevolucion() != null ? 
                prestamo.getFechaDevolucion().toString() : "Pendiente")));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(prestamo.getMulta()))));
            table.addCell(new Cell().add(new Paragraph(prestamo.getEstado())));
        }
        
        document.add(table);
        document.close();
    }
    
    private String calcularDiasRestantes(java.sql.Date fechaPrestamo) {
        long diffInMillies = System.currentTimeMillis() - fechaPrestamo.getTime();
        long diffInDays = diffInMillies / (24 * 60 * 60 * 1000);
        long diasRestantes = 15 - diffInDays; // Asumiendo 15 días de préstamo
        
        if (diasRestantes > 0) {
            return diasRestantes + " días";
        } else {
            return "⚠️ Vencido (" + Math.abs(diasRestantes) + " días)";
        }
    }
    
    // Métodos de notificación
    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }
}d(scrollPrestamos, BorderLayout.CENTER);
        cardPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panelMisPrestamos.add(cardPanel, BorderLayout.CENTER);
    }
    
    private void createEnhancedHistorialPanel() {
        panelHistorial = new JPanel(new BorderLayout());
        panelHistorial.setBackground(BACKGROUND_COLOR);
        panelHistorial.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        // Card container
        JPanel cardPanel = createCardPanel();
        cardPanel.setLayout(new BorderLayout());
        
        // Header mejorado
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel(ICON_HISTORY + " Historial de Préstamos");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_PRIMARY);
        
        // Panel de estadísticas
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        statsPanel.setOpaque(false);
        
        JLabel totalLabel = new JLabel("📊 Total: 0");
        totalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        totalLabel.setForeground(TEXT_SECONDARY);
        
        JLabel multasLabel = new JLabel("💰 Multas: $0.00");
        multasLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        multasLabel.setForeground(DANGER_COLOR);
        
        statsPanel.add(totalLabel);
        statsPanel.add(multasLabel);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(statsPanel, BorderLayout.EAST);
        
        // Tabla mejorada
        tblHistorial = new JTable(new DefaultTableModel(
                new Object[][]{},
                new String[]{"🆔 ID", "📖 Título", "📅 F. Préstamo", "📅 F. Devolución", "💰 Multa", "📊 Estado"}
        ));
        styleEnhancedTable(tblHistorial);
        
        JScrollPane scrollHistorial = new JScrollPane(tblHistorial);
        styleEnhancedScrollPane(scrollHistorial);
        
        // Panel de botones mejorado
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setOpaque(false);
        
        btnExportHistorial = new JButton(ICON_EXCEL + " Exportar Excel");
        styleEnhancedButton(btnExportHistorial, WARNING_COLOR, Color.WHITE, false);
        
        btnExportHistorialPDF = new JButton(ICON_PDF + " Exportar PDF");
        styleEnhancedButton(btnExportHistorialPDF, DANGER_COLOR, Color.WHITE, false);
        
        JButton btnFiltrar = new JButton("🔍 Filtrar por Fecha");
        styleEnhancedButton(btnFiltrar, TEXT_SECONDARY, Color.WHITE, false);
        
        buttonPanel.add(btnExportHistorial);
        buttonPanel.add(btnExportHistorialPDF);
        buttonPanel.add(btnFiltrar);
        
        cardPanel.add(headerPanel, BorderLayout.NORTH);
        cardPanel.add(scrollHistorial, BorderLayout.CENTER);
        cardPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panelHistorial.add(cardPanel, BorderLayout.CENTER);
    }
    
    private void createEnhancedPerfilPanel() {
        panelPerfil = new JPanel(new BorderLayout());
        panelPerfil.setBackground(BACKGROUND_COLOR);
        panelPerfil.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        // Card container
        JPanel cardPanel = createCardPanel();
        cardPanel.setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 30, 0));
        
        JLabel titleLabel = new JLabel(ICON_USER + " Mi Perfil");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_PRIMARY);
        
        JLabel subtitleLabel = new JLabel("Gestiona tu información personal");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        
        // Formulario mejorado
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        
        // Avatar placeholder
        JPanel avatarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        avatarPanel.setOpaque(false);
        avatarPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel avatarLabel = new JLabel(ICON_USER);
        avatarLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        avatarLabel.setForeground(PRIMARY_COLOR);
        avatarLabel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(PRIMARY_COLOR, 3, true),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        avatarPanel.add(avatarLabel);
        
        // Campos del formulario
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setOpaque(false);
        fieldsPanel.setMaximumSize(new Dimension(600, Integer.MAX_VALUE));
        fieldsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Campo nombre
        JLabel lblNombre = new JLabel("👤 Nombre completo:");
        styleEnhancedLabel(lblNombre);
        txtNombre = new JTextField();
        styleEnhancedTextField(txtNombre, "Ingrese su nombre completo...");
        
        // Campo email
        JLabel lblEmail = new JLabel("📧 Correo electrónico:");
        styleEnhancedLabel(lblEmail);
        txtEmail = new JTextField();
        styleEnhancedTextField(txtEmail, "nombre@universidad.edu...");
        
        // Campo teléfono
        JLabel lblTelefono = new JLabel("📞 Teléfono:");
        styleEnhancedLabel(lblTelefono);
        txtTelefono = new JTextField();
        styleEnhancedTextField(txtTelefono, "Ingrese su número de teléfono...");
        
        // Botón guardar
        btnGuardarPerfil = new JButton(ICON_SAVE + " Guardar Cambios");
        styleEnhancedButton(btnGuardarPerfil, SUCCESS_COLOR, Color.WHITE, true);
        btnGuardarPerfil.setMaximumSize(new Dimension(250, 50));
        btnGuardarPerfil.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        fieldsPanel.add(lblNombre);
        fieldsPanel.add(Box.createVerticalStrut(8));
        fieldsPanel.add(txtNombre);
        fieldsPanel.add(Box.createVerticalStrut(25));
        fieldsPanel.add(lblEmail);
        fieldsPanel.add(Box.createVerticalStrut(8));
        fieldsPanel.add(txtEmail);
        fieldsPanel.add(Box.createVerticalStrut(25));
        fieldsPanel.add(lblTelefono);
        fieldsPanel.add(Box.createVerticalStrut(8));
        fieldsPanel.add(txtTelefono);
        fieldsPanel.add(Box.createVerticalStrut(40));
        fieldsPanel.add(btnGuardarPerfil);
        
        formPanel.add(avatarPanel);
        formPanel.add(fieldsPanel);
        formPanel.add(Box.createVerticalGlue());
        
        cardPanel.add(headerPanel, BorderLayout.NORTH);
        cardPanel.ad
    }