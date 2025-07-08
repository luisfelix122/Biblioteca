package com.universidad.biblioteca.controller;

import com.universidad.biblioteca.model.Libro;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad Libro, con métodos de listado y búsqueda.
 */
public class LibroDAO {

    private static final String URL =
        "jdbc:sqlserver://localhost:1433;"
      + "databaseName=BibliotecaDB;"
      + "encrypt=false;"
      + "trustServerCertificate=true;";
    private static final String USER = "sa";            // Ajusta usuario
    private static final String PASS = "Informatica1!";    // Ajusta contraseña

    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    /** Lista todos los libros de la tabla. */
    public List<Libro> listarTodos() throws SQLException {
        String sql = "SELECT isbn, titulo, autor, categoria, editorial, "
                   + "anioPublicacion, totalEjemplares, disponibles "
                   + "FROM dbo.Libro";
        List<Libro> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new Libro(
                    rs.getString("isbn"),
                    rs.getString("titulo"),
                    rs.getString("autor"),
                    rs.getString("categoria"),
                    rs.getString("editorial"),
                    rs.getInt("anioPublicacion"),
                    rs.getInt("totalEjemplares"),
                    rs.getInt("disponibles")
                ));
            }
        }
        return lista;
    }

    /** Busca libros cuyo título contenga el texto dado (insensible a mayúsculas). */
    public List<Libro> buscarPorTitulo(String texto) throws SQLException {
        String sql = "SELECT isbn, titulo, autor, categoria, editorial, "
                   + "anioPublicacion, totalEjemplares, disponibles "
                   + "FROM dbo.Libro "
                   + "WHERE LOWER(titulo) LIKE ?";
        List<Libro> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + texto.toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Libro(
                        rs.getString("isbn"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("categoria"),
                        rs.getString("editorial"),
                        rs.getInt("anioPublicacion"),
                        rs.getInt("totalEjemplares"),
                        rs.getInt("disponibles")
                    ));
                }
            }
        }
        return lista;
    }
}
