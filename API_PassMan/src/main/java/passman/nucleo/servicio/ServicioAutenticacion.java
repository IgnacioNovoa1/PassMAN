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

    public boolean registrarUsuario(String nombreUsuario, String rut, String fechaNac, String passwordPlana) {
        if (servicioUsuarios.existeUsuario(nombreUsuario)) {
            return false;
        }

        String[] hashResult;
        try {
            hashResult = hasher.hashPassword(passwordPlana);
        } catch (Exception e) {
            throw new RuntimeException("Error al hashear password", e);
        }

        String passwordHash = hashResult[0];
        String salt = hashResult[1];
        int iteraciones = Integer.parseInt(hashResult[2]);

        return servicioUsuarios.crearUsuario(nombreUsuario, rut, fechaNac, passwordHash, salt, iteraciones);
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

    public boolean cambiarPassword(String nombreUsuario, String nuevaPassword) {
        Usuario usuario = servicioUsuarios.obtenerUsuario(nombreUsuario);
        if (usuario == null) {
            return false;
        }

        try {
            String[] hashResult = hasher.hashPassword(nuevaPassword);
            if (hashResult == null || hashResult.length != 3) {
                return false;
            }


            usuario.setPasswordHash(hashResult[0]);
            usuario.setSalt(hashResult[1]);
            usuario.setIteraciones(Integer.parseInt(hashResult[2]));

            return servicioUsuarios.actualizarUsuario(usuario);
        } catch (Exception e) {
            return false;
        }
    }
}
