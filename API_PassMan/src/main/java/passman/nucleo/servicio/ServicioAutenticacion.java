package passman.nucleo.servicio;

import passman.modelo.Usuario;
import passman.cifrado.ServicioHashing;

public class ServicioAutenticacion {
    private final ServicioUsuarios servicioUsuarios;
    private final ServicioHashing hasher;

    public ServicioAutenticacion(ServicioUsuarios servicioUsuarios, ServicioHashing hasher) {
        this.servicioUsuarios = servicioUsuarios;
        this.hasher = hasher;
    }

    public boolean registrarUsuario(String nombreUsuario, String rut, String fechaNac, String passwordPlana, String codigoRecuperacion) {
        
        String[] hashResult;
        try {
            hashResult = hasher.hashPassword(passwordPlana);
        } catch (Exception e) {
            throw new RuntimeException("Error al hashear password", e);
        }

        String passwordHash = hashResult[0];
        String salt = hashResult[1];
        int iteraciones = Integer.parseInt(hashResult[2]);

        return servicioUsuarios.crearUsuario(nombreUsuario, rut, fechaNac, passwordHash, salt, iteraciones, codigoRecuperacion);
    }

    public boolean iniciarSesion(String nombreUsuario, String passwordPlana) {
        Usuario usuario = servicioUsuarios.obtenerUsuario(nombreUsuario);
        if (usuario == null) {
            return false;
        }

        try {
            return hasher.verificarPassword(
                    passwordPlana,
                    usuario.getPasswordHash(),
                    usuario.getSalt(),
                    usuario.getIteraciones()
            );
        } catch (Exception e) {
            return false;
        }
    }

    // Nuevo método para recuperación
    public boolean restablecerPassword(String nombreUsuario, String codigo, String nuevaPassword) {
        try {
            String[] hashResult = hasher.hashPassword(nuevaPassword);
            return servicioUsuarios.recuperarUsuario(nombreUsuario, codigo, hashResult[0], hashResult[1], Integer.parseInt(hashResult[2]));
        } catch (Exception e) {
            return false;
        }
    }
}