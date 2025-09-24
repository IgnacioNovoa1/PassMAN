package main.passman.model;

import java.util.Scanner;

public class PasswordManager {
    public static void main(String[] args) {
        menu();
    }

    public static void mostrarMenu(){
        System.out.println("""
                ---Bienvenido al menu de PassMan---
                
                1. Agregar contraseña
                2. Eliminar contraseña
                3. Actualizar contraseña
                4. Ver contraseñas
                5. Salir
                
                Seleccione una opcion:""");
    }

    public static void menu(){
        Scanner entrada = new Scanner(System.in);
        boolean salir = false;

        do {
            mostrarMenu();

            //Pide un dato por consola y lo guarda en una variable.
            String opcion = entrada.nextLine();

            //Permite eligir una opcion por consola en base a un dato.
            switch (opcion) {
                case "1" -> agregarContrasena();
                case "2" -> eliminarContrasena();
                case "3" -> actualizarContrasena();
                case "4" -> verContrasenas();
                case "5" -> salir = true;
                default -> System.out.println("Opcion invalida. Intente nuevamente");
            }
        }

        while (!salir);
        System.out.println("Gracias por usar la PassMan...");
    }

    public static void agregarContrasena(){
        System.out.println("Aquí se agregan las cotraseñas");
    }

    public static void eliminarContrasena(){
        System.out.println("Aquí se liminan las contraseñas");
    }

    public static void actualizarContrasena(){
        System.out.println("Aquí se actualizan las cotraseñas");
    }

    public static void verContrasenas(){
        System.out.println("Aquí se muestran las cotraseñas");
    }
}
