package passman.api;

import org.springframework.web.bind.annotation.*;
import passman.nucleo.servicio.ServicioAutenticacion;
import passman.nucleo.servicio.ServicioPassman;


import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final ServicioPassman servicioPassman = new ServicioPassman();

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        String usuario = body.get("usuario");
        String password = body.get("password");

        boolean exito = servicioPassman.iniciarSesion(usuario, password);

        if (exito) {
            return Map.of("status", "ok", "mensaje", "Login exitoso");
        } else {
            return Map.of("status", "error", "mensaje", "Credenciales incorrectas");
        }
    }

    @PostMapping("/registro")
    public Map<String, Object> registro(@RequestBody Map<String, String> body) {
        String usuario = body.get("usuario");
        String password = body.get("password");
        String rut = body.get("rut");
        String cumple = body.get("cumpleanos");

        boolean exito = servicioPassman.registrarUsuario(usuario, rut, cumple, password);

        if (exito) {
            return Map.of("status", "ok", "mensaje", "Usuario creado");
        } else {
            return Map.of("status", "error", "mensaje", "Error al registrar (¿Usuario duplicado?)");
        }
    }
}