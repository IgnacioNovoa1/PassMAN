package passman.api;

import org.springframework.web.bind.annotation.*;
import passman.nucleo.servicio.ServicioPassman;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final ServicioPassman servicioPassman = new ServicioPassman();

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        String usuario = body.get("usuario");
        String password = body.get("password");

        boolean exito = servicioPassman.iniciarSesion(usuario, password);

        Map<String, Object> response = new HashMap<>();
        if (exito) {
            response.put("status", "ok");
            response.put("mensaje", "Login exitoso");
        } else {
            response.put("status", "error");
            response.put("mensaje", "Credenciales incorrectas");
        }
        return response;
    }

    @PostMapping("/registro")
    public Map<String, Object> registro(@RequestBody Map<String, String> body) {
        String usuario = body.get("usuario");
        String password = body.get("password");
        String rut = body.get("rut");
        String cumple = body.get("cumpleanos");

        boolean exito = servicioPassman.registrarUsuario(usuario, rut, cumple, password);

        Map<String, Object> response = new HashMap<>();
        if (exito) {
            response.put("status", "ok");
            response.put("mensaje", "Usuario creado");
        } else {
            response.put("status", "error");
            response.put("mensaje", "Error al registrar");
        }
        return response;
    }
}