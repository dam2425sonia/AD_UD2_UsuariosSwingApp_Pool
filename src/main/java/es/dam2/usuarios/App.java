package es.dam2.usuarios;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class App {
    public static void main(String[] args) {
        // Configuración de la apariencia del sistema operativo
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Se mantiene el aspecto predeterminado si no está disponible el del sistema.
            }
            new LoginFrame().setVisible(true);
        });
    }
}
