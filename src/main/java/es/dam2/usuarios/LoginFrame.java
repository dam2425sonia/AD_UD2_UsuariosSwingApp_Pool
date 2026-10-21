package es.dam2.usuarios;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private final JTextField txtUsuario = new JTextField(18);
    private final JPasswordField txtPassword = new JPasswordField(18);
    private final UsuarioDAO dao = new UsuarioDAO();

    public LoginFrame() {
        setTitle("Acceso - Gestión de usuarios (pool)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(370, 210);
        setLocationRelativeTo(null);

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(BorderFactory.createEmptyBorder(18, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formulario.add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1;
        formulario.add(txtUsuario, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formulario.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1;
        formulario.add(txtPassword, gbc);

        JButton btnEntrar = new JButton("Iniciar sesión");
        btnEntrar.addActionListener(e -> iniciarSesion());
        getRootPane().setDefaultButton(btnEntrar);

        JPanel inferior = new JPanel();
        inferior.add(btnEntrar);
        add(formulario, BorderLayout.CENTER);
        add(inferior, BorderLayout.SOUTH);
    }

    private void iniciarSesion() {
        String usuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Introduce usuario y contraseña.",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (dao.validarCredenciales(usuario, password)) {
                dispose();
                new UsuariosFrame().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.",
                        "Acceso denegado", JOptionPane.ERROR_MESSAGE);
                txtPassword.setText("");
                txtPassword.requestFocusInWindow();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo conectar o consultar la base de datos.\n" +
                    "Comprueba MySQL, db.properties y que exista la tabla usuarios.\n\nDetalle: " + ex.getMessage(),
                    "Error de conexión", JOptionPane.ERROR_MESSAGE);
        }
    }
}
