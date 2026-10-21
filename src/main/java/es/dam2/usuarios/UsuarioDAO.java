package es.dam2.usuarios;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
    public boolean validarCredenciales(String username, String password) throws SQLException {
        String sql = "SELECT 1 FROM usuarios WHERE username = ? AND password = ? LIMIT 1";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<Usuario> listar() throws SQLException {
        String sql = "SELECT id, username, password, fecha_creacion FROM usuarios ORDER BY id";
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                usuarios.add(new Usuario(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getTimestamp("fecha_creacion")
                ));
            }
        }
        return usuarios;
    }

    public void actualizar(int id, String username, String password) throws SQLException {
        String sql = "UPDATE usuarios SET username = ?, password = ? WHERE id = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setInt(3, id);
            ps.executeUpdate();
        }
    }
}
