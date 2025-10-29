package passman.servicio;

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

        return servicioUsuarios.crearUsuario(nombreUsuario, salt, passwordHash, rut, fechaNac, iteraciones);
    }

    public boolean iniciarSesion(String nombreUsuario, String passwordPlana) {
        try {
            Usuario usuario = servicioUsuarios.obtenerUsuario(nombreUsuario);
            if (usuario == null) return false;

            return hasher.verificarPassword(
                    passwordPlana,
                    usuario.getPasswordHash(),
                    usuario.getSalt(),
                    usuario.getIteraciones()
            );
        } catch  (Exception e) {
            throw new RuntimeException("Error en autenticación", e);
        }
    }
}
