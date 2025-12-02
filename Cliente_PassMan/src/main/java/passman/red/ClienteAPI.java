package passman.red;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import passman.ConfigCliente;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClienteAPI {

    private final HttpClient httpClient;
    private final Gson gson;

    public ClienteAPI() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5)) 
                .build();
        this.gson = new Gson();
    }
    // Constructor para pruebas unitarias (permite mocks)
    public ClienteAPI(HttpClient httpClient, Gson gson) {
        this.httpClient = httpClient;
        this.gson = gson;
    }

    // --- AUTENTICACIÓN ---
    public Map<String, String> login(String usuario, String password) {
        Map<String, String> data = Map.of("usuario", usuario, "password", password);
        return post("/auth/login", data);
    }

    public Map<String, String> registro(String usuario, String rut, String cumpleanos, String password) {
        Map<String, String> data = Map.of(
            "usuario", usuario, "password", password,
            "rut", rut, "cumpleanos", cumpleanos
        );
        return post("/auth/registro", data);
    }

    // --- NUEVO: RECUPERACIÓN DE CUENTA ---
    public Map<String, String> recuperar(String usuario, String codigo, String nuevaPass) {
        Map<String, String> data = Map.of(
            "usuario", usuario, 
            "codigo", codigo, 
            "nuevaPassword", nuevaPass
        );
        return post("/auth/recuperar", data);
    }

    // --- GESTIÓN DE CREDENCIALES ---
    public boolean guardarCredencial(String usuario, String servicio, String contrasena) {
        Map<String, String> data = Map.of(
            "usuario", usuario, "servicio", servicio, "contrasena", contrasena
        );
        Map<String, String> respuesta = post("/credenciales/guardar", data);
        return respuesta != null && "ok".equals(respuesta.get("status"));
    }

    public List<Map<String, String>> listarCredenciales(String usuario) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ConfigCliente.API_BASE_URL + "/credenciales/listar?usuario=" + usuario))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), new TypeToken<List<Map<String, String>>>(){}.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return List.of();
    }

    public String evaluarContrasena(String usuario, String contrasena) {
        Map<String, String> data = Map.of("usuario", usuario, "contrasena", contrasena);
        Map<String, String> respuesta = post("/credenciales/evaluar", data);
        
        if (respuesta != null && respuesta.containsKey("resultado")) {
            return respuesta.get("resultado");
        }
        return "ERROR|No se pudo conectar con el servidor.";
    }

    public boolean editarContrasena(String usuario, int indice, String nuevaContrasena) {
        Map<String, Object> data = Map.of(
            "usuario", usuario,
            "indice", indice,
            "nuevaContrasena", nuevaContrasena
        );
        Map<String, String> respuesta = post("/credenciales/editar", data);
        return respuesta != null && "ok".equals(respuesta.get("status"));
    }

    public boolean eliminarCredencial(String usuario, int indice) {
        Map<String, Object> data = Map.of(
            "usuario", usuario,
            "indice", indice
        );
        Map<String, String> respuesta = post("/credenciales/eliminar", data);
        return respuesta != null && "ok".equals(respuesta.get("status"));
    }

    // --- MÉTODO CENTRAL DE PETICIONES (Manejo de Excepciones) ---
    private Map<String, String> post(String endpoint, Object bodyData) {
        try {
            String jsonBody = gson.toJson(bodyData);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ConfigCliente.API_BASE_URL + endpoint))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), new TypeToken<Map<String, String>>(){}.getType());
            }
        } catch (Exception e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }

        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("status", "error");
        errorMap.put("mensaje", "No hay conexión con el servidor. Intente más tarde.");
        return errorMap;
    }
}
