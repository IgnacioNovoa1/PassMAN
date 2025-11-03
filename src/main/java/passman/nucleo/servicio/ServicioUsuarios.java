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

    public boolean crearUsuario(String nombreUsuario, String rut, String fechaNac, String passwordHash, String salt, int iteraciones) {
        String rutCifrado = cifrador.cifrar(rut);
        String fechaNacCifrado = cifrador.cifrar(fechaNac);

        if (rutCifrado == null || fechaNacCifrado == null) {
            return false;
        }

        Usuario usuario = new Usuario(nombreUsuario, passwordHash, salt, iteraciones);
        usuario.setRutCifrado(rutCifrado);
        usuario.setFechaNacCifrado(fechaNacCifrado);

        return persistencia.guardarUsuario(usuario);
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
        return  persistencia.buscarUsuarioPorNombre(nombreUsuario).isPresent();
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