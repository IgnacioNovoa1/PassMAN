package passman.ui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class GestorArchivos {

    private static final String ARCHIVO_USUARIOS = "usuarios.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // El tipo de objeto principal que manejaremos: Map<String, Usuario>
    private static final Type TIPO_MAP_USUARIO = new TypeToken<Map<String, Usuario>>() {}.getType();

    /**
     * Carga el Map de usuarios desde el archivo JSON.
     */
    public Map<String, Usuario> cargarUsuarios() {
        try (Reader reader = new FileReader(ARCHIVO_USUARIOS)) {
            Map<String, Usuario> usuarios = GSON.fromJson(reader, TIPO_MAP_USUARIO);
            return usuarios != null ? usuarios : new HashMap<>();
        } catch (FileNotFoundException e) {
            return new HashMap<>();
        } catch (IOException e) {
            System.err.println("Error de I/O al leer el archivo de usuarios: " + e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * Guarda el Map de usuarios en el archivo JSON.
     */
    public void guardarUsuarios(Map<String, Usuario> datos) {
        try (Writer writer = new FileWriter(ARCHIVO_USUARIOS)) {
            GSON.toJson(datos, writer);
        } catch (IOException e) {
            System.err.println("Error de I/O al escribir en el archivo de usuarios: " + e.getMessage());
        }
    }
}