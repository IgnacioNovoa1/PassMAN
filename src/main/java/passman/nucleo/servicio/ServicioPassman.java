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
import java.util.Random;

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

    public boolean guardarContrasena(String usuario, String servicio, String contrasena) {
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

    public String evaluarContrasena(String contrasena, String nombreUsuario) {
        if (contrasena == null || contrasena.trim().isEmpty()) {
            return "DÉBIL|La contraseña no puede estar vacía.";
        }

        Usuario usuarioObj = servicioUsers.obtenerUsuario(nombreUsuario);
        PasswordCheckResult resultado = passwordEvaluator.evaluate(contrasena, usuarioObj);

        PasswordStrength strength = resultado.getStrength();
        
        if (strength == PasswordStrength.FILTRADA) {
            String sugerencia = generarContrasenaSegura(); 
            return String.format("DÉBIL|FILTRADA: %s. Sugerencia: %s", resultado.getMessages().get(0), sugerencia);
        }
        
        if (strength == PasswordStrength.DEBIL) {
            String sugerencia = generarContrasenaSegura();
            return String.format("DÉBIL|%s Sugerencia: %s", String.join(" ", resultado.getMessages()), sugerencia);
        }
        
        if (strength == PasswordStrength.SEMIFUERTE) {
            return String.format("FUERTE|%s", String.join(" ", resultado.getMessages()));
        }
        return "FUERTE|¡Contraseña segura!";
    }
    private String generarContrasenaSegura() {
        String mayusculas = "ABDEFGHIJKLMNOPQRSTUVWXYZ";
        String minusculas = "abcdefghijklmnopqrstuvwxyz";
        String numeros = "0123456789";
        String simbolos = "!@#$%^&*()_+-=[]{}|;:,.<>?";

        Random random = new Random();
        StringBuilder password  = new StringBuilder();

        password.append(mayusculas.charAt(random.nextInt(mayusculas.length())));
        password.append(minusculas.charAt(random.nextInt(minusculas.length())));
        password.append(numeros.charAt(random.nextInt(numeros.length())));
        password.append(simbolos.charAt(random.nextInt(simbolos.length())));

        String todosCaracteres = mayusculas + minusculas + numeros + simbolos;
        for (int i = 4; i < 12; i++) {
            password.append(todosCaracteres.charAt(random.nextInt(todosCaracteres.length())));
        }
        
        char[] arrayPassword = password.toString().toCharArray();
        for (int i = arrayPassword.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = arrayPassword[i];
            arrayPassword[i] = arrayPassword[j];
            arrayPassword[j] = temp;
        }
        return new String(arrayPassword);
    }
    
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