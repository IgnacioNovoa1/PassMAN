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
}