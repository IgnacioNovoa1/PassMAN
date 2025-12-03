# PassMan - Cliente de Escritorio

Esta es la interfaz gráfica (Frontend) del gestor de contraseñas PassMan. Es una aplicación ligera ("Thin Client") desarrollada en **Java Swing** que permite a los usuarios gestionar sus credenciales de forma segura.

**Nota de Seguridad:** Este cliente **NO** almacena datos locales, no tiene conexión a base de datos ni contiene claves de cifrado. Toda la lógica sensible se delega a la API remota.

##  Configuración

Para que la aplicación funcione, debe saber dónde está el servidor.

1.  Abrir el archivo `src/main/java/passman/ConfigCliente.java`.
2.  Editar la variable `API_BASE_URL`:
    ```java
    // Reemplazar con la IP Elástica de tu servidor EC2
    public static final String API_BASE_URL = "http://XX.XX.XX.XX:8080/api";
    ```

##  Ejecución

### Requisitos
* Java JDK 11 o superior.
* Maven.
* Conexión a Internet.

### Cómo correr la App
Desde la raíz del proyecto:
```bash
# Compilar y ejecutar
mvn clean install
java -jar target/passman-cliente-1.0.0.jar
````
Correr el archivo `src/main/java/passman/lanzador/Principal.java`
