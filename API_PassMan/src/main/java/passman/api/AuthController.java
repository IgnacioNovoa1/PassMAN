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
        
        if (exito) return Map.of("status", "ok", "mensaje", "Login exitoso");
        return Map.of("status", "error", "mensaje", "Usuario o contraseña incorrectos");
    }

    @PostMapping("/registro")
    public Map<String, String> registro(@RequestBody Map<String, String> body) {
        return servicioPassman.registrarUsuarioDetallado(
            body.get("usuario"),
            body.get("rut"),
            body.get("cumpleanos"),
            body.get("password")
        );
    }

    @PostMapping("/recuperar")
    public Map<String, String> recuperar(@RequestBody Map<String, String> body) {
        return servicioPassman.recuperarCuenta(
            body.get("usuario"),
            body.get("codigo"),
            body.get("nuevaPassword")
        );
    }
}