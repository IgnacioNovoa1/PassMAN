package passman.model;

import passman.model.Usuario;
import java.util.Scanner;

public class IniciarSesion {

    public static void iniciar(Scanner scanner) {
        // Asegúrate de que la clase RegistrarUsuario tenga el método getUsuarioRegistrado()
        Usuario usuarioRegistrado = RegistrarUsuario.getUsuarioRegistrado();

        if (usuarioRegistrado == null) {
            System.out.println("No hay ningún usuario registrado. Por favor regístrate primero.");
            return;
        }

        System.out.print("Introduce tu nombre de usuario: ");
        String nombreUsuario = scanner.nextLine();
        System.out.print("Introduce tu contraseña: ");
        String contrasena = scanner.nextLine();

        // Verificar que el nombre de usuario y contraseña coincidan con los datos del usuario registrado
        if (nombreUsuario.equals(usuarioRegistrado.getNombreUsuario()) && contrasena.equals(usuarioRegistrado.getContraseñaHash())) {
            System.out.println("Inicio de sesión exitoso.");
        } else {
            System.out.println("Usuario o contraseña incorrectos.");
        }
    }
}

