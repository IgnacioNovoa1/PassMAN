package passman.persistence;

import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GestorArchivos {

    private static final String RUTA_USUARIOS = "usuarios.json";

    public static JSONObject cargarUsuarios() {
        File archivo = new File(RUTA_USUARIOS);
        if (!archivo.exists()) {
            return new JSONObject();
        }
        try {
            String contenido = new String(Files.readAllBytes(Paths.get(RUTA_USUARIOS)));
            return new JSONObject(contenido);
        } catch (IOException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    public static void guardarUsuarios(JSONObject datos) {
        try {
            Files.write(Paths.get(RUTA_USUARIOS), datos.toString(2).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}