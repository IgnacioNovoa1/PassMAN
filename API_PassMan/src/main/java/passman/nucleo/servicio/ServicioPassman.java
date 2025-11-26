package passman.nucleo.servicio;

import passman.cifrado.ServicioCifrado;
import passman.cifrado.ServicioHashing;
import passman.modelo.Usuario;
import passman.nucleo.seguridad.HibpClient;
import passman.nucleo.seguridad.PasswordEvaluator;
import passman.nucleo.seguridad.PasswordCheckResult;
import passman.nucleo.seguridad.PasswordStrength;
import passman.persistencia.ServicioPersistencia;
import passman.util.RutUtils;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

public class ServicioPassman {
    private final ServicioAutenticacion servicioAuth;
    private final ServicioCredenciales servicioCred;
    private final ServicioUsuarios servicioUsers;
    private final PasswordEvaluator passwordEvaluator;
    private final ServicioPersistencia persistencia;

    public ServicioPassman() {
        this.persistencia = new ServicioPersistencia();
        ServicioCifrado cifrador = new ServicioCifrado();
        ServicioHashing hasher = new ServicioHashing();
        
        this.servicioUsers = new ServicioUsuarios(persistencia, cifrador);
        this.servicioAuth = new ServicioAutenticacion(servicioUsers, hasher);
        this.servicioCred = new ServicioCredenciales(persistencia, cifrador, servicioUsers);
        
        HibpClient hibpClient = new HibpClient();
        this.passwordEvaluator = new PasswordEvaluator(hibpClient, cifrador);
    }

    public Map<String, String> registrarUsuarioDetallado(String usuario, String rut, String cumpleanos, String password) {
        Map<String, String> respuesta = new HashMap<>();

        if (usuario == null || usuario.trim().isEmpty()) return error("El nombre de usuario es obligatorio.");

        if (!RutUtils.validar(rut)) {
            return error("RUT inválido. Revise el formato y dígito verificador.");
        }

        if (cumpleanos == null || !cumpleanos.matches("^\\d{4}$")) {
            return error("El cumpleaños debe ser de 4 dígitos numéricos (Ej: 2510).");
        }

        if (password == null || password.trim().isEmpty()) {
            return error("La contraseña maestra no puede estar vacía.");
        }

        if (persistencia.existeUsuario(usuario)) {
            return error("El nombre de usuario '" + usuario + "' ya está en uso.");
        }

        String codigoRecuperacion = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        try {
            boolean exito = servicioAuth.registrarUsuario(usuario, rut, cumpleanos, password, codigoRecuperacion);
            if (exito) {
                respuesta.put("status", "ok");
                respuesta.put("mensaje", "Usuario creado exitosamente.");
                respuesta.put("codigoRecuperacion", codigoRecuperacion);
            } else {
                return error("Error interno al guardar usuario.");
            }
        } catch (Exception e) {
            return error("Error de conexión con la Base de Datos.");
        }
        return respuesta;
    }

    // --- RECUPERACIÓN ---
    public Map<String, String> recuperarCuenta(String usuario, String codigo, String nuevaPass) {
        if (nuevaPass == null || nuevaPass.isEmpty()) return error("La nueva contraseña es obligatoria.");
        
        boolean exito = servicioAuth.restablecerPassword(usuario, codigo, nuevaPass);
        
        if (exito) return Map.of("status", "ok", "mensaje", "Contraseña restablecida correctamente.");
        return error("Datos incorrectos. Usuario no existe o código inválido.");
    }

    private Map<String, String> error(String msg) {
        Map<String, String> r = new HashMap<>();
        r.put("status", "error");
        r.put("mensaje", msg);
        return r;
    }
    
    public boolean iniciarSesion(String usuario, String password) {
        if (usuario == null || usuario.trim().isEmpty() || password == null) return false;
        return servicioAuth.iniciarSesion(usuario, password);
    }

    public boolean guardarContrasena(String usuario, String servicio, String contrasena) {
        return servicioCred.guardarCredencial(usuario, servicio, usuario, contrasena);
    }

    public List<Map<String, String>> getContrasenasBoveda(String usuario) {
        if (usuario == null || usuario.trim().isEmpty()) return List.of();
        return servicioCred.obtenerCredencialesParaUI(usuario);
    }

    public boolean editarContrasena(String usuario, int indice, String nuevaContrasena) {
        if (indice < 0 || nuevaContrasena == null || nuevaContrasena.trim().isEmpty()) return false;
        return servicioCred.editarCredencial(usuario, indice, nuevaContrasena);
    }

    public boolean eliminarContrasena(String usuario, int indice) {
        if (indice < 0) return false;
        return servicioCred.eliminarCredencial(usuario, indice);
    }

    public String evaluarContrasena(String contrasena, String nombreUsuario) {
        if (contrasena == null || contrasena.trim().isEmpty()) return "DÉBIL|Vacía";
        Usuario usuarioObj = servicioUsers.obtenerUsuario(nombreUsuario);
        PasswordCheckResult resultado = passwordEvaluator.evaluate(contrasena, usuarioObj);

        // Genera String con todos los mensajes/sugerencias de mejora
        String detalleMensajes = String.join(". ", resultado.getMessages());

        if (resultado.getStrength() == PasswordStrength.FILTRADA || resultado.getStrength() == PasswordStrength.DEBIL) {
            String sugerencia = servicioCred.generarContrasenaSegura();
            detalleMensajes = detalleMensajes + ". Sugerencia: " + sugerencia;
        }
        return String.format("%s|%s", resultado.getStrength().name(), detalleMensajes);
    }
}