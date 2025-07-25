package com.universidad.biblioteca.vista.administrador;

import javax.swing.JFrame;

import com.universidad.biblioteca.modelo.Usuario;
import javax.swing.JTabbedPane;

public class AdminView extends JFrame {

    public AdminView(Usuario usuario) {
        setTitle("Panel de Administrador");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Paneles para Administrador
        tabbedPane.addTab("Gestión de Usuarios", new GestionUsuariosPanel(usuario));
        tabbedPane.addTab("Gestión de Bibliotecarios", new GestionBibliotecariosPanel(usuario));
        tabbedPane.addTab("Configuración del Sistema", new ConfiguracionSistemaPanel(usuario));
        tabbedPane.addTab("Auditoría", new AuditoriaPanel(usuario));

        add(tabbedPane);
    }
}