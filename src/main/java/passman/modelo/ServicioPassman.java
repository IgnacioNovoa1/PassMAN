package passman.modelo;

import passman.cifrado.ServicioHashing;
import passman.persistencia.ServicioPersistencia;
import passman.cifrado.ServicioCifrado;

public class ServicioPassman {
    private final ServicioPersistencia servicioPersistencia;
    private final ServicioHashing servicioHashing;
    private final ServicioCifrado servicioCifrado;

    public ServicioPassman(ServicioPersistencia persistencia, ServicioHashing hashing, ServicioCifrado cifrado) {
        this.servicioPersistencia = persistencia;
        this.servicioHashing = hashing;
        this.servicioCifrado = cifrado;
    }

    public boolean registrarUsuario(String nombreUsuario, String password, String rut, String fechaNac) {
        // Verifica existencia del usuario.
        if (servicioPersistencia.cargarUsuario(nombreUsuario) != null) {
            return false;
        }

        // Hashing de la contraseña Maestra.
        try {
            String[] hashData = servicioHashing.hashPassword(password);
            String hash = hashData[0];
            String salt = hashData[1];
            int iteraciones = Integer.parseInt(hashData[2]);

            // Crea objeto Usuario con datos de inicio de sesión seguros.
            Usuario nuevoUsuario = new Usuario(nombreUsuario, salt, fechaNac, iteraciones);

            //Cifra datos personales.
            String iv = servicioCifrado.generarIV();
            nuevoUsuario.setRutCifrado(servicioCifrado.cifrarDatos(rut));
            nuevoUsuario.setFechaNacCifrado(servicioCifrado.cifrarDatos(fechaNac));
            nuevoUsuario.setIvPersonales(iv);

            servicioPersistencia.guardarUsuario(nuevoUsuario);
            return true;
        } catch (Exception e) {
            //Manejo de errores
            return false;
        }
    }

    public Usuario iniciarSesion(String nombreUsuario, String password) {
        // Cargar el usuario
        Usuario user = servicioPersistencia.cargarUsuario(nombreUsuario);

        if (user == null) {
            return null; //Usuario no existe
        }

        //Verificar la contraseña
        try {
            boolean esValida = servicioHashing.verificarPassword(
                    password,
                    user.getPasswordHash(),
                    user.getSalt(),
                    user.iteraciones()
            );

            return esValida ? user : null;
        } catch (Exception e) {
            return null;
        }


    }
}
