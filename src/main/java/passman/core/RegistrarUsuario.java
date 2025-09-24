package passman.core;

import passman.model.Usuario;
import java.util.Scanner;

public class RegistrarUsuario {

    private static Usuario usuarioRegistrado;

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

    public static Usuario getUsuarioRegistrado() {
        return usuarioRegistrado;
    }
}
