package com.universidad.biblioteca.vista.bibliotecario;

import com.universidad.biblioteca.modelo.Usuario;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;



public class BibliotecarioView extends JFrame {

    public BibliotecarioView(Usuario usuario) {

        setTitle("Panel de Bibliotecario");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();



        // Paneles para Bibliotecario
        tabbedPane.addTab("Gestión de Libros", new GestionLibrosPanel(usuario));
        tabbedPane.addTab("Catálogo", new CatalogoPanelBibliotecario(usuario));
        tabbedPane.addTab("Historial", new HistorialPanel(usuario));
        tabbedPane.addTab("Reportes", new ReportesPanel(usuario));

        add(tabbedPane);
    }
}