package com.universidad.biblioteca.model;

public class Usuario {
    private String codigo;
    private String password;
    private String nombre;
    private String email;
    private String telefono;
    private String rol;

    // --- Constructor completo (ya existía) ---
    public Usuario(String codigo, String password,
                   String nombre, String email,
                   String telefono, String rol) {
        this.codigo   = codigo;
        this.password = password;
        this.nombre   = nombre;
        this.email    = email;
        this.telefono = telefono;
        this.rol      = rol;
    }

    // --- Nuevo: Constructor para actualizar perfil ---
    public Usuario(String codigo,
                   String nombre,
                   String email,
                   String telefono) {
        this.codigo   = codigo;
        this.nombre   = nombre;
        this.email    = email;
        this.telefono = telefono;
        // password y rol quedan como null (no los toques)
    }

    // getters y setters...
    public String getCodigo()      { return codigo; }
    public String getPassword()    { return password; }
    public String getNombre()      { return nombre; }
    public String getEmail()       { return email; }
    public String getTelefono()    { return telefono; }
    public String getRol()         { return rol; }

    public void setPassword(String pw) { this.password = pw; }
    public void setRol(String r)       { this.rol = r; }
    // (no necesitas setters para codigo, nombre, email, teléfono
    //  si sólo usas el constructor para perfil)
}
