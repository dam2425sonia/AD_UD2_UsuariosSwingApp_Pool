package es.dam2.usuarios;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Gestiona un único pool de conexiones compartido por toda la aplicación.
 * getConnection() entrega una conexión disponible del pool.
 * connection.close() la devuelve al pool; no suele cerrar la conexión física.
 */
public final class Database {
    // Propiedades cargadas desde db.properties
    private static final Properties PROPERTIES = new Properties();
    // Pool de conexiones HikariCP
    private static final HikariDataSource DATA_SOURCE;

    // Inicializa el pool de conexiones al cargar la clase.
    static {
        // Carga de propiedades desde db.properties
        try (InputStream input = Database.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new IllegalStateException("No se encuentra db.properties en src/main/resources.");
            }
            PROPERTIES.load(input);
            
            // Configuración de HikariCP    
            HikariConfig config = new HikariConfig();
            
            // Configuración de la base de datos y del pool
            config.setJdbcUrl(required("db.url"));
            
            // Configuración de usuario y contraseña    
            config.setUsername(required("db.user"));
            
            // Configuración de la contraseña, si no está definida se deja vacía
            config.setPassword(PROPERTIES.getProperty("db.password", ""));
            
            // Configuración del pool de conexiones
            // Tamaño máximo del pool, mínimo de conexiones inactivas, tiempo de espera, etc.
            config.setMaximumPoolSize(integerProperty("pool.maximumPoolSize", 10));
            config.setMinimumIdle(integerProperty("pool.minimumIdle", 2));
            config.setConnectionTimeout(longProperty("pool.connectionTimeout", 30000));
            config.setIdleTimeout(longProperty("pool.idleTimeout", 600000));
            config.setMaxLifetime(longProperty("pool.maxLifetime", 1800000));
            config.setPoolName("UsuariosSwingPool");
            
            // Inicializa el pool de conexiones
            DATA_SOURCE = new HikariDataSource(config);
        } catch (IOException e) {
            throw new ExceptionInInitializerError("No se pudo leer db.properties: " + e.getMessage());
        }
    }

    // Constructor privado para evitar instanciación
    private Database() {}

    // Obtiene una conexión del pool de conexiones.
    public static Connection getConnection() throws SQLException {
        return DATA_SOURCE.getConnection();
    }

    // Obtiene una propiedad obligatoria del archivo de propiedades.
    private static String required(String key) {
        String value = PROPERTIES.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Falta la propiedad obligatoria: " + key);
        }
        return value.trim();
    }

    // Obtiene una propiedad entera del archivo de propiedades, con valor por defecto.
    private static int integerProperty(String key, int defaultValue) {
        return Integer.parseInt(PROPERTIES.getProperty(key, String.valueOf(defaultValue)).trim());
    }

    // Obtiene una propiedad long del archivo de propiedades, con valor por defecto.
    private static long longProperty(String key, long defaultValue) {
        return Long.parseLong(PROPERTIES.getProperty(key, String.valueOf(defaultValue)).trim());
    }

    /**
     * Cierra el pool al finalizar la aplicación.
     */
    public static void closePool() {
        if (DATA_SOURCE != null && !DATA_SOURCE.isClosed()) {
            DATA_SOURCE.close();
        }
    }
}
