package passman.persistencia;

import passman.modelo.Usuario;
import passman.modelo.EntradaCredencial;
import java.util.List;
import java.util.UUID;

public class ServicioPersistencia {

    // --- Métodos de Usuario (CRUD) ---

    /** Carga un Usuario de la DB por su nombre de usuario. */
    public Usuario cargarUsuario(String nombreUsuario) {
        // STUB: Simula que no se encontró ningún usuario, devolviendo null.
        System.out.println("[STUB PERSISTENCIA]: Intentando cargar usuario: " + nombreUsuario);
        return null;
    }

    /** Guarda un nuevo Usuario o actualiza uno existente en la DB. */
    public boolean guardarUsuario(Usuario usuario) {
        // STUB: Simula la escritura exitosa en la DB.
        System.out.println("[STUB PERSISTENCIA]: Guardando/actualizando usuario con ID: " + usuario.getIdUsuario());
        return true;
    }

    // --- Métodos de Credenciales (Bóveda) ---

    /** Carga todas las credenciales asociadas a un ID de Usuario. */
    public List<EntradaCredencial> cargarCredenciales(UUID idUsuario) {
        // STUB: Simula la carga de datos de la bóveda, devolviendo una lista vacía.
        System.out.println("[STUB PERSISTENCIA]: Cargando credenciales para ID: " + idUsuario);
        return java.util.Collections.emptyList();
    }

    /** Guarda una nueva credencial en la DB. */
    public boolean guardarCredencial(EntradaCredencial credencial) {
        // STUB: Simula la inserción exitosa de una credencial.
        System.out.println("[STUB PERSISTENCIA]: Guardando credencial de servicio: " + credencial.getServicio());
        return true;
    }

    /** Actualiza una credencial existente. */
    public boolean actualizarCredencial(EntradaCredencial credencial) {
        // STUB: Simula la actualización exitosa.
        System.out.println("[STUB PERSISTENCIA]: Actualizando credencial con ID: " + credencial.getIdCredencial());
        return true;
    }

    /** Elimina una credencial por su ID. */
    public boolean eliminarCredencial(UUID idCredencial) {
        // STUB: Simula la eliminación exitosa.
        System.out.println("[STUB PERSISTENCIA]: Eliminando credencial con ID: " + idCredencial);
        return true;
    }
}