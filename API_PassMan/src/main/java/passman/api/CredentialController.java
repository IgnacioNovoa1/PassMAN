package passman.api;

import org.springframework.web.bind.annotation.*;
import passman.nucleo.servicio.ServicioPassman;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/credenciales")
public class CredentialController {

    // Instanciamos el servicio principal
    private final ServicioPassman servicioPassman = new ServicioPassman();

    @PostMapping("/guardar")
    public Map<String, Object> guardar(@RequestBody Map<String, String> body) {
        String usuario = body.get("usuario");
        String servicio = body.get("servicio");
        String contrasena = body.get("contrasena");

        boolean exito = servicioPassman.guardarContrasena(usuario, servicio, contrasena);

        Map<String, Object> respuesta = new HashMap<>();
        if (exito) {
            respuesta.put("status", "ok");
        } else {
            respuesta.put("status", "error");
        }
        return respuesta;
    }

    @GetMapping("/listar")
    public List<Map<String, String>> listar(@RequestParam String usuario) {
        return servicioPassman.getContrasenasBoveda(usuario);
    }

    @PostMapping("/evaluar")
    public Map<String, String> evaluar(@RequestBody Map<String, String> body) {
        String usuario = body.get("usuario");
        String contrasena = body.get("contrasena");

        String resultado = servicioPassman.evaluarContrasena(contrasena, usuario);
        
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("resultado", resultado);
        return respuesta;
    }

    // --- NUEVO ENDPOINT PARA EDITAR ---
    @PostMapping("/editar")
    public Map<String, Object> editar(@RequestBody Map<String, Object> body) {
        String usuario = (String) body.get("usuario");
        int indice = (Integer) body.get("indice"); 
        String nuevaContrasena = (String) body.get("nuevaContrasena");

        boolean exito = servicioPassman.editarContrasena(usuario, indice, nuevaContrasena);

        Map<String, Object> respuesta = new HashMap<>();
        if (exito) {
            respuesta.put("status", "ok");
        } else {
            respuesta.put("status", "error");
        }
        return respuesta;
    }
}
