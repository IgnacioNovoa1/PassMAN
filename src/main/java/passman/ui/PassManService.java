package passman.ui;

import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PassManService {

    private final GestorArchivos gestorArchivos;
    private static final int DESPLAZAMIENTO = 4;
    private final Random random = new Random();

    public PassManService(GestorArchivos gestorArchivos) {
        this.gestorArchivos = gestorArchivos;
    }

    public List<Map<String, String>> getContrasenasBoveda(String usuario) {
    Map<String, passman.ui.Usuario> datos = gestorArchivos.cargarUsuarios();
    passman.ui.Usuario user = datos.get(usuario);

    if (user != null) {
        return user.getBovedaDeContrasenas();
    }
    return null; }

    // Registro e inicio de sesión
    public boolean registrarUsuario(String nombreUsuario, String rut, String cumpleanos, String contrasena) {
        Map<String, passman.ui.Usuario> usuarios = gestorArchivos.cargarUsuarios();

        if (usuarios.containsKey(nombreUsuario)) {
            return false;
        }

        // Hashing de la contraseña maestra
        String hashContrasena = BCrypt.hashpw(contrasena, BCrypt.gensalt()).replace("\n", "");

        // Cifrado César de datos sensibles
        String rutCifrado = passman.ui.CifradoCesar.cifrar(rut, DESPLAZAMIENTO);
        String cumpleanosCifrado = passman.ui.CifradoCesar.cifrar(cumpleanos, DESPLAZAMIENTO);

        passman.ui.Usuario nuevoUsuario = new passman.ui.Usuario(rutCifrado, cumpleanosCifrado, hashContrasena);
        usuarios.put(nombreUsuario, nuevoUsuario);
        gestorArchivos.guardarUsuarios(usuarios);
        return true;
    }

    public boolean iniciarSesion(String nombreUsuario, String contrasena) {
        Map<String, passman.ui.Usuario> usuarios = gestorArchivos.cargarUsuarios();

        if (!usuarios.containsKey(nombreUsuario)) {
            return false;
        }

        passman.ui.Usuario user = usuarios.get(nombreUsuario);
        String contrasenaHash = user.getContrasena();

        return BCrypt.checkpw(contrasena, contrasenaHash);
    }

    //Muestras las contraseñas ingresadas por el usuario
    public void verContrasenas(String usuario) {
        Map<String, passman.ui.Usuario> datos = gestorArchivos.cargarUsuarios();
        passman.ui.Usuario user = datos.get(usuario);
        List<Map<String, String>> boveda = user.getBovedaDeContrasenas();

        if (boveda.isEmpty()) {
            System.out.println("No hay contraseñas guardadas.");
            return;
        }

        System.out.println("\n--- Boveda de Contraseñas ---");
        for (int i = 0; i < boveda.size(); i++) {
            Map<String, String> entrada = boveda.get(i);
            String servicio = entrada.get("servicio");
            String contrasenaCifrada = entrada.get("contraseña");

            String contrasenaDescifrada = passman.ui.CifradoCesar.descifrar(contrasenaCifrada, DESPLAZAMIENTO);

            System.out.printf("%d. Servicio: %s | Contraseña: %s\n", i + 1, servicio, contrasenaDescifrada);
        }
    }

    //Permite guardar una contraseña cifrandola
    public void guardarContrasena(String usuario, String servicio, String contrasena) {
        Map<String, passman.ui.Usuario> datos = gestorArchivos.cargarUsuarios();
        passman.ui.Usuario user = datos.get(usuario);

        String cifrada = passman.ui.CifradoCesar.cifrar(contrasena, DESPLAZAMIENTO);

        Map<String, String> nuevaEntrada = new HashMap<>();
        nuevaEntrada.put("servicio", servicio);
        nuevaEntrada.put("contraseña", cifrada);

        user.getBovedaDeContrasenas().add(nuevaEntrada);
        gestorArchivos.guardarUsuarios(datos);
        System.out.println("Contraseña guardada correctamente.");
    }

    //Permite editar una contraseña
    public boolean editarContrasena(String usuario, int indice, String nuevaContrasena) {
        Map<String, passman.ui.Usuario> datos = gestorArchivos.cargarUsuarios();
        passman.ui.Usuario user = datos.get(usuario);
        List<Map<String, String>> boveda = user.getBovedaDeContrasenas();

        if (indice < 0 || indice >= boveda.size()) {
            return false;
        }

        String cifrada = passman.ui.CifradoCesar.cifrar(nuevaContrasena, DESPLAZAMIENTO);

        boveda.get(indice).put("contraseña", cifrada);
        gestorArchivos.guardarUsuarios(datos);
        return true;
    }
    //Verifica la fortelaza de la contraseña
    public boolean esContrasenaDebil(String contrasena, String usuario) {
        Map<String, passman.ui.Usuario> datos = gestorArchivos.cargarUsuarios();
        passman.ui.Usuario user = datos.get(usuario);

        String rutDescifrado = passman.ui.CifradoCesar.descifrar(user.getRut(), DESPLAZAMIENTO);
        String cumpleDescifrado = passman.ui.CifradoCesar.descifrar(user.getCumpleanos(), DESPLAZAMIENTO);

        // 1. Contiene datos personales
        if (contrasena.contains(rutDescifrado) || contrasena.contains(cumpleDescifrado)) {
            return true;
        }

        // 2. Todos los dígitos iguales (Regex: cualquier dígito seguido de sí mismo 3 veces)
        if (contrasena.matches("(\\d)\\1{3}")) {
            return true;
        }

        // 3. Progresión aritmética simple (ej: 1234, 4321, 2468)
        try {
            int d0 = Character.getNumericValue(contrasena.charAt(0));
            int d1 = Character.getNumericValue(contrasena.charAt(1));
            int d2 = Character.getNumericValue(contrasena.charAt(2));
            int d3 = Character.getNumericValue(contrasena.charAt(3));

            int diff = d1 - d0;
            if (d2 - d1 == diff && d3 - d2 == diff) {
                return true;
            }
        } catch (Exception ignored) {
            // Ignorar errores, la validación de formato se hace en Main
        }

        return false;
    }

    //Genera una contraseña fuerte para el usuario
    public String generarContrasenaFuerte(String usuario) {
        while (true) {
            StringBuilder sugerencia = new StringBuilder();
            for (int i = 0; i < 4; i++) {
                sugerencia.append(random.nextInt(10)); // Dígito aleatorio
            }
            if (!esContrasenaDebil(sugerencia.toString(), usuario)) {
                return sugerencia.toString();
            }
        }
    }
}
