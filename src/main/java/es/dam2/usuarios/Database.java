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
    private static final Properties PROPERTIES = new Properties();
    private static final HikariDataSource DATA_SOURCE;

    static {
        try (InputStream input = Database.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new IllegalStateException("No se encuentra db.properties en src/main/resources.");
            }
            PROPERTIES.load(input);

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(required("db.url"));
            config.setUsername(required("db.user"));
            config.setPassword(PROPERTIES.getProperty("db.password", ""));
            config.setMaximumPoolSize(integerProperty("pool.maximumPoolSize", 10));
            config.setMinimumIdle(integerProperty("pool.minimumIdle", 2));
            config.setConnectionTimeout(longProperty("pool.connectionTimeout", 30000));
            config.setIdleTimeout(longProperty("pool.idleTimeout", 600000));
            config.setMaxLifetime(longProperty("pool.maxLifetime", 1800000));
            config.setPoolName("UsuariosSwingPool");

            DATA_SOURCE = new HikariDataSource(config);
        } catch (IOException e) {
            throw new ExceptionInInitializerError("No se pudo leer db.properties: " + e.getMessage());
        }
    }

    private Database() {}

    public static Connection getConnection() throws SQLException {
        return DATA_SOURCE.getConnection();
    }

    private static String required(String key) {
        String value = PROPERTIES.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Falta la propiedad obligatoria: " + key);
        }
        return value.trim();
    }

    private static int integerProperty(String key, int defaultValue) {
        return Integer.parseInt(PROPERTIES.getProperty(key, String.valueOf(defaultValue)).trim());
    }

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
