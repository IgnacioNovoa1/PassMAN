package passman.core;

import passman.model.Usuario;

public class IniciarSesion {

    public static boolean iniciarSesion(Usuario usuarioRegistrado, String nombreUsuario, String contraseña) {
        if (usuarioRegistrado == null) {
            System.out.println("No hay ningún usuario registrado. Por favor regístrate primero.");
            return false;
        }

        if (nombreUsuario.equals(usuarioRegistrado.getNombreUsuario()) && contraseña.equals(usuarioRegistrado.getContraseñaHash())) {
            System.out.println("Inicio de sesión exitoso.");
            return true;
        } else {
            System.out.println("Usuario o contraseña incorrectos.");
            return false;
        }
    }
}

