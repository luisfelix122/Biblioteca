package com.universidad.biblioteca;
import javax.swing.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import com.universidad.biblioteca.controlador.auth.LoginController;

import javax.swing.SwingUtilities;
public class App {
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            System.err.println("Uncaught exception in thread: " + thread.getName());
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            String stackTrace = sw.toString();
            System.err.println(stackTrace);
            try (java.io.PrintStream ps = new java.io.PrintStream("error.log")) {
                ps.print(stackTrace);
            } catch (java.io.FileNotFoundException e) {
                System.err.println("FileNotFoundException when creating error.log: " + e.getMessage());
                e.printStackTrace();
            }
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                    null,
                    "An unexpected error occurred:\n" + throwable.getMessage() + "\n\n" + stackTrace,
                    "Unhandled Exception",
                    JOptionPane.ERROR_MESSAGE
                );
            });
        });
        SwingUtilities.invokeLater(() -> {
            try {
                com.universidad.biblioteca.vista.auth.LoginView loginView = new com.universidad.biblioteca.vista.auth.LoginView();
                new LoginController(loginView);
                System.out.println("Setting LoginView visible.");
                SwingUtilities.invokeLater(() -> {
                    System.out.println("Inside SwingUtilities.invokeLater for setVisible.");
                    loginView.setVisible(true);
                });
// Removed Thread.sleep(60000); // Keep the application alive for 60 seconds for testing
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Ocurrió un error inesperado al iniciar la aplicación: " + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}