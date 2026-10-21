package es.dam2.usuarios;

import java.sql.Timestamp;

public class Usuario {
    private final int id;
    private String username;
    private String password;
    private final Timestamp fechaCreacion;

    public Usuario(int id, String username, String password, Timestamp fechaCreacion) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fechaCreacion = fechaCreacion;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Timestamp getFechaCreacion() { return fechaCreacion; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
}
