package com.universidad.biblioteca.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class ConexionBD {

    private static final Properties properties = new Properties();
    private static Connection conexion = null;

    static {
        try (InputStream input = ConexionBD.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new IllegalStateException("No se encontró el archivo database.properties");
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
        if (conexion == null || conexion.isClosed()) {
            conexion = DriverManager.getConnection(
                properties.getProperty("db.url"),
                properties.getProperty("db.usuario"),
                properties.getProperty("db.contrasena")
            );
        }
        return conexion;
    }

    public static void cerrarConexion() throws SQLException {
        if (conexion != null && !conexion.isClosed()) {
            conexion.close();
            conexion = null;
        }
    }
}