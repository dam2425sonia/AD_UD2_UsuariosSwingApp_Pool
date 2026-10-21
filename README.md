# UsuariosSwingAppPool

Aplicación Java Swing con MySQL y pool de conexiones mediante HikariCP.

## Requisitos
- JDK 17 o superior.
- Maven.
- MySQL activo, con base de datos `dam2` y tabla `usuarios`.

## Configuración
Edita `src/main/resources/db.properties` si tu conexión es distinta. El fichero contiene tanto las propiedades de conexión como los parámetros del pool.

## Ejecutar
Desde la carpeta del proyecto:

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="es.dam2.usuarios.App"
```

Maven descargará las dependencias necesarias, incluido MySQL Connector/J y HikariCP.

## ¿Cómo funciona el pool?
- `Database` crea un único `HikariDataSource` al inicializar la clase.
- `Database.getConnection()` solicita una conexión disponible del pool.
- Cada método de `UsuarioDAO` usa `try-with-resources`.
- Al salir del `try`, `Connection.close()` devuelve la conexión al pool para que pueda reutilizarse; normalmente no cierra la conexión física con MySQL.
- HikariCP gestiona las conexiones físicas y puede crear o retirar conexiones según su configuración.
- El pool se mantiene durante la vida de la aplicación. En este ejemplo sencillo se cierra al terminar el proceso.

## Parámetros incluidos
- `pool.maximumPoolSize=10`: máximo de conexiones del pool.
- `pool.minimumIdle=2`: objetivo de conexiones inactivas que mantiene el pool.
- `pool.connectionTimeout=30000`: máximo de espera para conseguir una conexión, en milisegundos.
- `pool.idleTimeout=600000`: tiempo de inactividad antes de retirar conexiones inactivas excedentes.
- `pool.maxLifetime=1800000`: vida máxima de una conexión física, en milisegundos.

## Nota de seguridad
Se muestra la contraseña de la tabla porque así lo pide el ejercicio. No es recomendable para aplicaciones reales: las contraseñas deben guardarse como hashes seguros y nunca mostrarse en pantalla. No subas credenciales reales a repositorios.
