package passman.nucleo.servicio;

import passman.cifrado.ServicioCifrado;
import passman.cifrado.ServicioHashing;
import passman.persistencia.ServicioPersistencia;

import java.util.List;
import java.util.Map;

public class ServicioPassman {
    private final ServicioAutenticacion servicioAuth;
    private final ServicioCredenciales servicioCred;

    public ServicioPassman() {
        ServicioPersistencia persistencia = new ServicioPersistencia();
        ServicioCifrado cifrador = new ServicioCifrado();
        ServicioHashing hasher = new ServicioHashing();
        ServicioUsuarios servicioUsers = new ServicioUsuarios(persistencia, cifrador);

        this.servicioAuth = new ServicioAutenticacion(servicioUsers, hasher);
        this.servicioCred = new ServicioCredenciales(persistencia, cifrador, servicioUsers);
    }

    // AUTENTICACION
    public boolean registrarUsuario(String usuario, String rut, String cumpleanos, String password) {
        if (!validarDatosRegistro(usuario,rut,cumpleanos,password)) {
            return false;
        }
        return servicioAuth.registrarUsuario(usuario, rut, cumpleanos, password);
    }

    public boolean iniciarSesion(String usuario, String password) {
        if (!validarDatosLogin(usuario, password)) {
            return false;
        }
        return servicioAuth.iniciarSesion(usuario, password);
    }

    // GESTION CREDENCIALES
    public boolean guardarContrasena(String usuario, String servicio, String contrasena) {
        if (!validarCredencial(servicio, contrasena)) {
            return false;
        }
        return servicioCred.guardarCredencial(usuario, servicio, usuario, contrasena);
    }

    public List<Map<String, String>> getContrasenasBoveda(String usuario) {
        if (usuario == null || usuario.trim().isEmpty()) {
            return List.of();
        }
        return servicioCred.obtenerCredencialesParaUI(usuario);
    }

    public boolean editarContrasena(String usuario, int indice, String nuevaContrasena) {
        if (indice < 0 || nuevaContrasena == null || nuevaContrasena.trim().isEmpty()) {
            return false;
        }
        return servicioCred.editarCredencial(usuario, indice, nuevaContrasena);
    }

    public boolean eliminarContrasena(String usuario, int indice) {
        if (indice < 0) {
            return false;
        }
        return servicioCred.eliminarCredencial(usuario, indice);
    }

    // EVALUACION CONTRASEÑAS
    public boolean esContrasenaDebil(String contrasena, String usuario) {
        if (contrasena == null || contrasena.trim().isEmpty()) {
            return true;
        }
        return servicioCred.verificarReutilizacion(usuario, contrasena) ||
                !esContrasenaFuerte(contrasena);
    }

    public String generarContrasenaFuerte(String usuario) {
        return servicioCred.generarContrasenaSegura();
    }

    // VALIDACIONES

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

    private boolean validarCredencial(String servicio, String password) {
        return servicio != null && !servicio.trim().isEmpty() &&
                password != null && !password.trim().isEmpty();
    }

    private boolean esContrasenaFuerte(String contrasena) {
        if (contrasena == null || contrasena.length() < 8) return false;

        boolean tieneMayuscula = contrasena.matches(".*[A-Z].*");
        boolean tieneMinuscula = contrasena.matches(".*[a-z].*");
        boolean tieneNumero = contrasena.matches(".*[0-9].*");
        boolean tieneSimbolo = contrasena.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>?].*");

        return tieneMayuscula && tieneMinuscula && tieneNumero && tieneSimbolo;
    }
}