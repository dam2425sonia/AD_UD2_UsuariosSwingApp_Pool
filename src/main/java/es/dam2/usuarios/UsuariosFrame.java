package es.dam2.usuarios;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class UsuariosFrame extends JFrame {
    private final UsuarioDAO dao = new UsuarioDAO();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Usuario", "Fecha de creación", "Password"}, 0) {
        @Override public boolean isCellEditable(int row, int column) {
            return column == 1 || column == 3;
        }
    };
    private final JTable tabla = new JTable(modelo);

    public UsuariosFrame() {
        setTitle("Gestión de usuarios (pool de conexiones)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(760, 430);
        setLocationRelativeTo(null);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(45);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(160);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(190);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(220);

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JButton btnGuardar = new JButton("Guardar cambios");
        JButton btnRecargar = new JButton("Recargar");
        JButton btnCerrar = new JButton("Cerrar sesión");

        btnGuardar.addActionListener(e -> guardarCambios());
        btnRecargar.addActionListener(e -> cargarUsuarios());
        btnCerrar.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnRecargar);
        botones.add(btnGuardar);
        botones.add(btnCerrar);
        add(botones, BorderLayout.SOUTH);

        cargarUsuarios();
    }

    private void cargarUsuarios() {
        try {
            List<Usuario> usuarios = dao.listar();
            modelo.setRowCount(0);
            for (Usuario u : usuarios) {
                modelo.addRow(new Object[]{
                    u.getId(),
                    u.getUsername(),
                    u.getFechaCreacion() == null ? "" : u.getFechaCreacion().toString(),
                    u.getPassword()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudieron cargar los usuarios:\n" + ex.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarCambios() {
        if (tabla.isEditing()) {
            tabla.getCellEditor().stopCellEditing();
        }

        int filaVista = tabla.getSelectedRow();
        if (filaVista < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona la fila del usuario que quieres guardar.",
                    "Ninguna fila seleccionada", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int fila = tabla.convertRowIndexToModel(filaVista);
        int id = Integer.parseInt(modelo.getValueAt(fila, 0).toString());
        String username = String.valueOf(modelo.getValueAt(fila, 1)).trim();
        String password = String.valueOf(modelo.getValueAt(fila, 3));

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El usuario y la contraseña no pueden estar vacíos.",
                    "Datos no válidos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            dao.actualizar(id, username, password);
            JOptionPane.showMessageDialog(this, "Cambios guardados correctamente.",
                    "Guardado", JOptionPane.INFORMATION_MESSAGE);
            cargarUsuarios();
        } catch (SQLException ex) {
            String mensaje = ex.getMessage();
            if (ex.getErrorCode() == 1062) {
                mensaje = "Ese nombre de usuario ya existe. Elige otro.";
            }
            JOptionPane.showMessageDialog(this, "No se pudieron guardar los cambios:\n" + mensaje,
                    "Error al guardar", JOptionPane.ERROR_MESSAGE);
        }
    }
}
