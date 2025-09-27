package passman.ui;
import java.util.Scanner;
public class MenuManager {

    private final PassManService service;
    private static final Scanner SC = new Scanner(System.in);

    public MenuManager(PassManService service) {
        this.service = service;
    }

    public String inicio() {
        System.out.println("--- PasSecurity ---");
        String usuario = null;
        while (usuario == null) {
            System.out.println("\n1. Iniciar sesión");
            System.out.println("2. Registrarse");
            System.out.print("Seleccione una opción: ");
            String opcion = SC.nextLine().trim();

            if (opcion.equals("1")) {
                usuario = iniciarSesion();
            } else if (opcion.equals("2")) {
                usuario = registrar();
            } else {
                System.out.println("Opción inválida.");
            }
        }
        return usuario;
    }

    private String registrar() {
        System.out.print("Ingresa tu nombre de usuario: ");
        String nombreUsuario = SC.nextLine().trim();

        String rut = validarEntrada("Ingresa tu RUT (8 o 9 dígitos, sin guión): ", 8, 9, true, "Rut inválido. Ingresa un rut válido.");
        if (rut == null) return null;

        String cumpleanos = validarEntrada("Ingresa tu cumpleaños (DDMM): ", 4, 4, true, "Formato de cumpleaños inválido. Ingresa uno correcto.");
        if (cumpleanos == null) return null;

        String contrasena = validarEntrada("Ingresa tu contraseña de acceso (4 dígitos): ", 4, 4, true, "Contraseña inválida. Ingresa una contraseña de 4 dígitos.");
        if (contrasena == null) return null;

        if (service.registrarUsuario(nombreUsuario, rut, cumpleanos, contrasena)) {
            System.out.println("Registro exitoso.");
            return nombreUsuario;
        } else {
            System.out.println("El usuario ya existe.");
            return null;
        }
    }

    private String iniciarSesion() {
        System.out.print("Nombre de usuario: ");
        String nombreUsuario = SC.nextLine().trim();
        System.out.print("Contraseña: ");
        String contrasena = SC.nextLine().trim();

        if (service.iniciarSesion(nombreUsuario, contrasena)) {
            System.out.println("Inicio de sesión exitoso.");
            return nombreUsuario;
        } else {
            System.out.println("Usuario o contraseña incorrecta.");
            return null;
        }
    }


    public void menuPrincipal(String usuario) {
        while (true) {
            System.out.println("\n--- Menú de Opciones ---");
            System.out.println("1. Ver contraseñas guardadas");
            System.out.println("2. Guardar una nueva contraseña");
            System.out.println("3. Editar contraseñas");
            System.out.println("4. Evaluar una contraseña");
            System.out.println("5. Salir");
            System.out.print("Seleccione una opción: ");
            String opcion = SC.nextLine().trim();

            switch (opcion) {
                case "1":
                    service.verContrasenas(usuario);
                    break;
                case "2":
                    guardarContrasena(usuario);
                    break;
                case "3":
                    editarContrasena(usuario);
                    break;
                case "4":
                    evaluarContrasena(usuario);
                    break;
                case "5":
                    System.out.println("¡Hasta luego!");
                    return;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        }
    }


    private void guardarContrasena(String usuario) {
        System.out.print("Nombre del servicio: ");
        String servicio = SC.nextLine();
        System.out.print("Contraseña a guardar: ");
        String contrasena = SC.nextLine();
        service.guardarContrasena(usuario, servicio, contrasena);
    }

    private void editarContrasena(String usuario) {
        service.verContrasenas(usuario);

        System.out.print("Seleccione el número de contraseña a editar: ");
        try {
            int opcion = Integer.parseInt(SC.nextLine()) - 1;

            System.out.print("Ingresa tu nueva contraseña: ");
            String nuevaContrasena = SC.nextLine();

            if (service.editarContrasena(usuario, opcion, nuevaContrasena)) {
                System.out.println("Contraseña actualizada.");
            } else {
                System.out.println("Opción inválida.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Opción inválida.");
        }
    }

    private void evaluarContrasena(String usuario) {
        while (true) {
            System.out.print("Ingrese una contraseña de 4 dígitos para evaluar: ");
            String contrasena = SC.nextLine().trim();

            if (contrasena.length() == 4 && contrasena.matches("\\d+")) {
                if (service.esContrasenaDebil(contrasena, usuario)) {
                    System.out.println("Contraseña débil. Se recomienda cambiarla.");
                    String sugerencia = service.generarContrasenaFuerte(usuario);
                    System.out.printf("Sugerencia de contraseña más segura: %s\n", sugerencia);
                } else {
                    System.out.println("Contraseña segura.");
                }
                break;
            } else {
                System.out.println("Formato inválido inténtalo de nuevo");
            }
        }
    }

    private String validarEntrada(String prompt, int minLen, int maxLen, boolean mustBeDigit, String errorMsg) {
        while (true) {
            System.out.print(prompt);
            String entrada = SC.nextLine().trim();
            boolean isValidLength = entrada.length() >= minLen && entrada.length() <= maxLen;
            boolean isValidDigit = !mustBeDigit || entrada.matches("\\d+");

            if (isValidLength && isValidDigit) {
                return entrada;
            } else {
                System.out.println(errorMsg);
            }
        }
    }
}