package com.universidad.biblioteca.controlador;

import com.universidad.biblioteca.config.ConexionBD;
import com.universidad.biblioteca.modelo.Role;
import com.universidad.biblioteca.modelo.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public UsuarioDAO() {}


    public boolean actualizarUsuario(Usuario usuario) throws SQLException {
        String sql = "UPDATE Usuario SET nombre = ?, email = ? WHERE id = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getEmail());
            stmt.setInt(3, usuario.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public Usuario verificarCredenciales(String codigo, String contrasenaHash) throws SQLException {
        System.out.println("UsuarioDAO: Verifying credentials for code: " + codigo + " with provided hash: " + contrasenaHash);
        Usuario usuario = buscarPorUsername(codigo);
        if (usuario != null && usuario.getContrasena().equals(contrasenaHash)) {
            System.out.println("UsuarioDAO: Password match for user: " + codigo);
            // La contraseña ya está hasheada, solo necesitamos obtener el rol
            String sql = "SELECT r.nombre AS nombreRol, r.id AS idRol FROM Usuario u JOIN Rol r ON u.idRol = r.id WHERE u.codigo = ?";
            try (Connection conn = ConexionBD.obtenerConexion();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, codigo);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Role rol = new Role();
                        rol.setId(rs.getInt("idRol"));
                        rol.setNombre(rs.getString("nombreRol"));
                        usuario.setRol(rol);
                        return usuario;
                    }
                }
            }
        }
        return null;
    }

    public Usuario obtenerUsuarioPorId(int id) throws SQLException {
        String sql = "SELECT u.*, r.nombre AS nombreRol FROM Usuario u JOIN Rol r ON u.idRol = r.id WHERE u.id = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setCodigo(rs.getString("codigo"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setTelefono(rs.getString("telefono"));
                    Role rol = new Role();
                    rol.setId(rs.getInt("idRol"));
                    rol.setNombre(rs.getString("nombreRol"));
                    usuario.setRol(rol);
                    return usuario;
                }
            }
        }
        return null;
    }

    public Usuario obtenerPorCodigo(String codigo) throws SQLException {
        String sql = "SELECT * FROM Usuario WHERE codigo = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codigo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setCodigo(rs.getString("codigo"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setTelefono(rs.getString("telefono"));
                    usuario.setContrasena(rs.getString("contrasena"));
                    return usuario;
                }
            }
        }
        return null;
    }

    public Usuario buscarPorUsername(String username) throws SQLException {
        String sql = "SELECT * FROM Usuario WHERE codigo = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setCodigo(rs.getString("codigo"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setEmail(rs.getString("email"));
                usuario.setTelefono(rs.getString("telefono"));
                usuario.setContrasena(rs.getString("contrasena"));
                // usuario.setRol(rs.getString("rol")); // This column might not exist here
                usuario.setFechaRegistro(rs.getDate("fechaRegistro"));
                return usuario;
            }
        }
        return null;
    }

    public boolean insertar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO Usuario (codigo, nombre, email, telefono, contrasena, idRol, fechaRegistro) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getCodigo());
            stmt.setString(2, usuario.getNombre());
            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, usuario.getTelefono());
            stmt.setString(5, usuario.getContrasena());
            stmt.setInt(6, usuario.getRol().getId());
            stmt.setDate(7, new java.sql.Date(System.currentTimeMillis()));
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean actualizarPerfil(Usuario usuario) {
        String sql = "UPDATE Usuario SET nombre = ?, email = ?, telefono = ? WHERE id = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getTelefono());
            stmt.setInt(4, usuario.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarContrasena(String username, String hashedPassword) {
        String sql = "UPDATE Usuario SET contrasena = ? WHERE codigo = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hashedPassword);
            stmt.setString(2, username);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Usuario> obtenerUsuariosPorRol(String nombreRol) throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT u.*, r.nombre AS nombreRol FROM Usuario u JOIN Rol r ON u.idRol = r.id WHERE r.nombre = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombreRol);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setCodigo(rs.getString("codigo"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setTelefono(rs.getString("telefono"));
                    usuario.setContrasena(rs.getString("contrasena"));
                    
                    Role rol = new Role();
                    rol.setId(rs.getInt("idRol"));
                    rol.setNombre(rs.getString("nombreRol"));
                    usuario.setRol(rol);
                    
                    usuario.setFechaRegistro(rs.getDate("fechaRegistro"));
                    
                    usuarios.add(usuario);
                }
            }
        }
        return usuarios;
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM Usuario WHERE id = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Usuario> obtenerTodosLosUsuarios() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT u.*, r.nombre AS nombreRol FROM Usuario u JOIN Rol r ON u.idRol = r.id";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setCodigo(rs.getString("codigo"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setEmail(rs.getString("email"));
                usuario.setTelefono(rs.getString("telefono"));
                usuario.setContrasena(rs.getString("contrasena"));
                
                Role rol = new Role();
                rol.setId(rs.getInt("idRol"));
                rol.setNombre(rs.getString("nombreRol"));
                usuario.setRol(rol);
                
                usuario.setFechaRegistro(rs.getDate("fechaRegistro"));
                
                usuarios.add(usuario);
            }
        }
        return usuarios;
    }

}
