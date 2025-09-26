package passman.ui;

import org.mindrot.jbcrypt.BCrypt;
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
        Map<String, Usuario> usuarios = gestorArchivos.cargarUsuarios();

        if (usuarios.containsKey(nombreUsuario)) {
            return false;
        }

        // Hashing de la contraseña maestra
        String hashContrasena = BCrypt.hashpw(contrasena, BCrypt.gensalt()).replace("\n", "");

        // Cifrado César de datos sensibles
        String rutCifrado = CifradoCesar.cifrar(rut, DESPLAZAMIENTO);
        String cumpleanosCifrado = CifradoCesar.cifrar(cumpleanos, DESPLAZAMIENTO);

        Usuario nuevoUsuario = new Usuario(rutCifrado, cumpleanosCifrado, hashContrasena);
        usuarios.put(nombreUsuario, nuevoUsuario);
        gestorArchivos.guardarUsuarios(usuarios);
        return true;
    }

    public boolean iniciarSesion(String nombreUsuario, String contrasena) {
        Map<String, Usuario> usuarios = gestorArchivos.cargarUsuarios();

        if (!usuarios.containsKey(nombreUsuario)) {
            return false;
        }

        Usuario user = usuarios.get(nombreUsuario);
        String contrasenaHash = user.getContrasena();

        return BCrypt.checkpw(contrasena, contrasenaHash);
    }
}