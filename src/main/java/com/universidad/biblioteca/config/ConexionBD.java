package com.universidad.biblioteca.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class ConexionBD {

    private static final Properties properties = new Properties();


    static {
        try (InputStream input = ConexionBD.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new RuntimeException("Error: No se pudo encontrar el archivo 'database.properties'. Asegúrese de que esté en la carpeta 'src/main/resources'.");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo cargar el archivo de propiedades de la base de datos", e);
        }
    }

    private ConexionBD() {
        // Clase de utilidad, no se debe instanciar
    }

    public static Connection obtenerConexion() throws SQLException {
        try {
            System.out.println("Attempting to connect to the database...");
            System.out.println("URL: " + properties.getProperty("db.url"));
            Connection newConnection = DriverManager.getConnection(
                properties.getProperty("db.url"),
                properties.getProperty("db.usuario"),
                properties.getProperty("db.contrasena")
            );
            System.out.println("Database connection successful.");
            return newConnection;
        } catch (SQLException e) {
            System.err.println("SQLException while connecting to the database: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


}