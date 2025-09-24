package passman.model;

import passman.model.Usuario;
import java.util.Scanner;

public class RegistrarUsuario {

    private static Usuario usuarioRegistrado;

    // Método para registrar el usuario
    public static Usuario registrar(Scanner scanner) {
        if (usuarioRegistrado != null) {
            System.out.println("Ya tienes un usuario registrado. Por favor, inicia sesión.");
            return null;
        }

        System.out.print("Introduce tu nombre de usuario: ");
        String nombreUsuario = scanner.nextLine();
        System.out.print("Introduce tu contraseña: ");
        String contraseñaHash = scanner.nextLine();

        usuarioRegistrado = new Usuario(nombreUsuario, contraseñaHash);
        System.out.println("Registro exitoso.");
        return usuarioRegistrado;
    }

    // Método para obtener el usuario registrado
    public static Usuario getUsuarioRegistrado() {
        return usuarioRegistrado;
    }
}
