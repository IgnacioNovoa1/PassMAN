package passman.nucleo.servicio;

import passman.cifrado.ServicioCifrado;
import passman.cifrado.ServicioHashing;
import passman.modelo.Usuario;
import passman.nucleo.seguridad.HibpClient;
import passman.nucleo.seguridad.PasswordEvaluator;
import passman.nucleo.seguridad.PasswordCheckResult;
import passman.nucleo.seguridad.PasswordStrength;
import passman.persistencia.ServicioPersistencia;

import java.util.List;
import java.util.Map;

public class ServicioPassman {
    private final ServicioAutenticacion servicioAuth;
    private final ServicioCredenciales servicioCred;
    private final ServicioUsuarios servicioUsers;
    private final PasswordEvaluator passwordEvaluator;

    public ServicioPassman() {
        ServicioPersistencia persistencia = new ServicioPersistencia();
        ServicioCifrado cifrador = new ServicioCifrado();
        ServicioHashing hasher = new ServicioHashing();
        
        this.servicioUsers = new ServicioUsuarios(persistencia, cifrador);
        this.servicioAuth = new ServicioAutenticacion(servicioUsers, hasher);
        this.servicioCred = new ServicioCredenciales(persistencia, cifrador, servicioUsers);
        
        HibpClient hibpClient = new HibpClient();
        this.passwordEvaluator = new PasswordEvaluator(hibpClient);
    }

    // --- AUTENTICACIÓN ---
    public boolean registrarUsuario(String usuario, String rut, String cumpleanos, String password) {
        if (!validarDatosRegistro(usuario,rut,cumpleanos,password)) return false;
        return servicioAuth.registrarUsuario(usuario, rut, cumpleanos, password);
    }

    public boolean iniciarSesion(String usuario, String password) {
        if (!validarDatosLogin(usuario, password)) return false;
        return servicioAuth.iniciarSesion(usuario, password);
    }

    // --- GESTIÓN CREDENCIALES ---
    public boolean guardarContrasena(String usuario, String servicio, String contrasena) {
        return servicioCred.guardarCredencial(usuario, servicio, usuario, contrasena);
    }

    public List<Map<String, String>> getContrasenasBoveda(String usuario) {
        if (usuario == null || usuario.trim().isEmpty()) return List.of();
        return servicioCred.obtenerCredencialesParaUI(usuario);
    }

    // --- EVALUACIÓN ---
    public String evaluarContrasena(String contrasena, String nombreUsuario) {
        if (contrasena == null || contrasena.trim().isEmpty()) {
            return "DÉBIL|La contraseña no puede estar vacía.";
        }

        Usuario usuarioObj = servicioUsers.obtenerUsuario(nombreUsuario);
        PasswordCheckResult resultado = passwordEvaluator.evaluate(contrasena, usuarioObj);
        PasswordStrength strength = resultado.getStrength();
        
        if (strength == PasswordStrength.FILTRADA || strength == PasswordStrength.DEBIL) {
            String sugerencia = servicioCred.generarContrasenaSegura(); 
            return String.format("DÉBIL|%s Sugerencia: %s", String.join(" ", resultado.getMessages()), sugerencia);
        }
        return "FUERTE|¡Contraseña segura!";
    }

    // --- VALIDACIONES PRIVADAS ---
    private boolean validarDatosRegistro(String usuario, String rut, String cumpleanos, String password) {
        return usuario != null && !usuario.trim().isEmpty() &&
                rut != null && rut.matches("\\d{8,9}") &&
                cumpleanos != null && cumpleanos.matches("\\d{4}") &&
                password != null && password.matches("\\d{4}");
    }

    private boolean validarDatosLogin(String usuario, String password) {
        return usuario != null && !usuario.trim().isEmpty() &&
                password != null && !password.trim().isEmpty();
    }
}