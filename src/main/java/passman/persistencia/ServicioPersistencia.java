package passman.persistencia;

import passman.modelo.Usuario;
import passman.modelo.EntradaCredencial;
import java.util.List;
import java.util.UUID;

public class ServicioPersistencia {

    // --- Métodos de Usuario  ---

    /** Carga un Usuario de la DB por su nombre de usuario. */
    public Usuario cargarUsuario(String nombreUsuario) {
        System.out.println("[STUB PERSISTENCIA]: Intentando cargar usuario: " + nombreUsuario);
        return null;
    }

    /** Guarda un nuevo Usuario o actualiza uno existente en la DB. */
    public boolean guardarUsuario(Usuario usuario) {
        System.out.println("[STUB PERSISTENCIA]: Guardando/actualizando usuario con ID: " + usuario.getIdUsuario());
        return true;
    }

    // --- Métodos de Credenciales ---

    /** Carga todas las credenciales asociadas a un ID de Usuario. */
    public List<EntradaCredencial> cargarCredenciales(UUID idUsuario) {
        System.out.println("[STUB PERSISTENCIA]: Cargando credenciales para ID: " + idUsuario);
        return java.util.Collections.emptyList();
    }

    /** Guarda una nueva credencial en la DB. */
    public boolean guardarCredencial(EntradaCredencial credencial) {
        System.out.println("[STUB PERSISTENCIA]: Guardando credencial de servicio: " + credencial.getServicio());
        return true;
    }

    /** Actualiza una credencial existente. */
    public boolean actualizarCredencial(EntradaCredencial credencial) {
        System.out.println("[STUB PERSISTENCIA]: Actualizando credencial con ID: " + credencial.getIdCredencial());
        return true;
    }

    /** Elimina una credencial por su ID. */
    public boolean eliminarCredencial(UUID idCredencial) {
        System.out.println("[STUB PERSISTENCIA]: Eliminando credencial con ID: " + idCredencial);
        return true;
    }
}