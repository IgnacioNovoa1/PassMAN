package passman.nucleo.servicio;

import passman.modelo.Usuario;
import passman.cifrado.ServicioCifrado;
import passman.persistencia.ServicioPersistencia;

import java.util.Optional;
import java.util.UUID;

public class ServicioUsuarios {
    private final ServicioPersistencia persistencia;
    private final ServicioCifrado cifrador;

    public ServicioUsuarios(ServicioPersistencia persistencia, ServicioCifrado cifrador) {
        this.persistencia = persistencia;
        this.cifrador = cifrador;
    }

    // Método actualizado con codigoRecuperacion
    public boolean crearUsuario(String nombreUsuario, String rut, String fechaNac, String passwordHash, String salt, int iteraciones, String codigoRecuperacion) {
        String rutCifrado = cifrador.cifrar(rut);
        String fechaNacCifrado = cifrador.cifrar(fechaNac);
        String nombreCifrado = cifrador.cifrar(nombreUsuario);

        if (rutCifrado == null || fechaNacCifrado == null  || nombreCifrado == null) {
            return false;
        }

        Usuario usuario = new Usuario(nombreUsuario, passwordHash, salt, iteraciones);
        usuario.setRutCifrado(rutCifrado);
        usuario.setFechaNacCifrada(fechaNacCifrado);
        usuario.setNombreCifrado(nombreCifrado);
        usuario.setCodigoRecuperacion(codigoRecuperacion); 
        return persistencia.guardarUsuario(usuario);
    }

    // Método nuevo para recuperación
    public boolean recuperarUsuario(String usuario, String codigo, String hash, String salt, int iteraciones) {
        return persistencia.recuperarPassword(usuario, codigo, hash, salt, iteraciones);
    }

    public Usuario obtenerUsuario(String nombreUsuario) {
        Optional<Usuario> usuarioOpt = persistencia.buscarUsuarioPorNombre(nombreUsuario);
        return usuarioOpt.orElse(null);
    }

    public UUID obtenerIdUsuario(String nombreUsuario) {
        Usuario usuario = obtenerUsuario(nombreUsuario);
        return usuario != null ? usuario.getIdUsuario() : null;
    }

    public boolean existeUsuario(String nombreUsuario) {
        return persistencia.existeUsuario(nombreUsuario);
    }

    public boolean actualizarUsuario(Usuario usuario) {
        return persistencia.actualizarUsuario(usuario);
    }

    public String obtenerRutDescifrado(String nombreUsuario) {
        Usuario usuario = obtenerUsuario(nombreUsuario);
        if (usuario == null || usuario.getRutCifrado() == null) {
            return null;
        }
        return cifrador.descifrar(usuario.getRutCifrado());
    }
}