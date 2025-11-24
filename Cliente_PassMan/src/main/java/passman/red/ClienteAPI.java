package passman.red;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import passman.ConfigCliente;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class ClienteAPI {

    private final HttpClient httpClient;
    private final Gson gson;

    public ClienteAPI() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.gson = new Gson();
    }

    public boolean login(String usuario, String password) {
        Map<String, String> data = Map.of("usuario", usuario, "password", password);
        Map<String, Object> respuesta = post("/auth/login", data);
        return respuesta != null && "ok".equals(respuesta.get("status"));
    }

    public boolean registro(String usuario, String rut, String cumpleanos, String password) {
        Map<String, String> data = Map.of(
            "usuario", usuario, "password", password,
            "rut", rut, "cumpleanos", cumpleanos
        );
        Map<String, Object> respuesta = post("/auth/registro", data);
        return respuesta != null && "ok".equals(respuesta.get("status"));
    }

    public boolean guardarCredencial(String usuario, String servicio, String contrasena) {
        Map<String, String> data = Map.of(
            "usuario", usuario, "servicio", servicio, "contrasena", contrasena
        );
        Map<String, Object> respuesta = post("/credenciales/guardar", data);
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
        Map<String, Object> respuesta = post("/credenciales/evaluar", data);
        
        if (respuesta != null && respuesta.containsKey("resultado")) {
            return (String) respuesta.get("resultado");
        }
        return "ERROR|No se pudo conectar con el servidor.";
    }

    public boolean editarContrasena(String usuario, int indice, String nuevaContrasena) {
        Map<String, Object> data = Map.of(
            "usuario", usuario,
            "indice", indice,
            "nuevaContrasena", nuevaContrasena
        );
        

        Map<String, Object> respuesta = post("/credenciales/editar", data);
        
        return respuesta != null && "ok".equals(respuesta.get("status"));
    }

    private Map<String, Object> post(String endpoint, Object bodyData) {
        try {
            String jsonBody = gson.toJson(bodyData);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ConfigCliente.API_BASE_URL + endpoint))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), new TypeToken<Map<String, Object>>(){}.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean eliminarCredencial(String usuario, int indice) {
        Map<String, Object> data = Map.of(
            "usuario", usuario,
            "indice", indice
        );
        
        Map<String, Object> respuesta = post("/credenciales/eliminar", data);
        
        return respuesta != null && "ok".equals(respuesta.get("status"));
    }
}