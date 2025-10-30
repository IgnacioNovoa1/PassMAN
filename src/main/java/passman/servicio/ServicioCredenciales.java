package passman.servicio;

import passman.modelo.EntradaCredencial;
import passman.cifrado.ServicioCifrado;
import passman.persistencia.ServicioPersistencia;

import java.util.*;

public class ServicioCredenciales {
    private final ServicioPersistencia persistencia;
    private final ServicioCifrado cifrador;
    private final ServicioUsuarios servicioUsers;

    public ServicioCredenciales(ServicioPersistencia persistencia, ServicioCifrado cifrador, ServicioUsuarios servicioUsers) {
        this.persistencia = persistencia;
        this.cifrador = cifrador;
        this.servicioUsers = servicioUsers;
    }

    public boolean guardarCredencial(String nombreUsuario, String servicio, String usuarioServicio, String password) {
        UUID usuarioId = servicioUsers.obtenerIdUsuario(nombreUsuario);
        if (usuarioId == null) {
            return false;
        }

        String passwordCifrada = cifrador.cifrar(password);
        if  (passwordCifrada == null) {
            return false;
        }

        EntradaCredencial credencial = new EntradaCredencial(
                usuarioId, servicio, usuarioServicio, passwordCifrada,""
        );

        return persistencia.guardarCredencial(credencial);
    }

    public List<Map<String,String>> obtenerCredencialesParaUI(String nombreUsuario) {
        UUID usuarioId = servicioUsers.obtenerIdUsuario(nombreUsuario);
        if (usuarioId == null) {
            return List.of();
        }

        List<EntradaCredencial> credenciales = persistencia.cargarCredenciales(usuarioId);
        List<Map<String,String>> resultado = new ArrayList<>();

        for (EntradaCredencial cred : credenciales) {
            Map<String,String> entrada = new HashMap<>();
            entrada.put("servicio", cred.getServicio());
            entrada.put("usuario", cred.getUsuarioServicio());

            String passwordDescifrada = cifrador.descifrar(cred.getPasswordCifrada());
            entrada.put("contraseña", passwordDescifrada != null ? passwordDescifrada : "ERROR");

            resultado.add(entrada);
        }
        return resultado;
    }

    public boolean editarCredencial(String nombreUsuario, int indice, String nuevaPassword) {
        UUID usuarioId = servicioUsers.obtenerIdUsuario(nombreUsuario);
        if (usuarioId == null) {
            return false;
        }

        List<EntradaCredencial> credenciales = persistencia.cargarCredenciales(usuarioId);
        if (indice < 0 || indice >= credenciales.size()) {
            return false;
        }

        EntradaCredencial credencial = credenciales.get(indice);
        String nuevaPasswordCifrada = cifrador.cifrar(nuevaPassword);
        if (nuevaPasswordCifrada == null) {
            return false;
        }

        credencial.setPasswordCifrada(nuevaPasswordCifrada);
        return persistencia.actualizarCredencial(credencial);
    }

    public boolean eliminarCredencial(String nombreUsuario, int indice) {
        UUID usuarioId = servicioUsers.obtenerIdUsuario(nombreUsuario);
        if (usuarioId == null) {
            return false;
        }

        List<EntradaCredencial> credenciales = persistencia.cargarCredenciales(usuarioId);
        if (indice < 0 || indice >= credenciales.size()) {
            return false;
        }

        EntradaCredencial credencial = credenciales.get(indice);
        return persistencia.eliminarCredencial(credencial.getIdCredencial());
    }

    public boolean verificarReutilizacion(String nombreUsuario, String nuevaPssword) {
        UUID usuarioId = servicioUsers.obtenerIdUsuario(nombreUsuario);
        if (usuarioId == null) {
            return false;
        }

        List<EntradaCredencial> credenciales = persistencia.cargarCredenciales(usuarioId);

        for (EntradaCredencial cred : credenciales) {
            String passwordAlmacenada = cifrador.descifrar(cred.getPasswordCifrada());
            if (passwordAlmacenada == null && passwordAlmacenada.equals(nuevaPssword)) {
                return true;
            }
        }
        return false;
    }

    public String generarContrasenaSegura() {
        String mayusculas = "ABDEFGHIJKLMNOPQRSTUVWXYZ";
        String minusculas = "abcdefghijklmnopqrstuvwxyz";
        String numeros = "0123456789";
        String simbolos = "!@#$%^&*()_+-=[]{}|;:,.<>?";

        Random random = new Random();
        StringBuilder password  = new StringBuilder();

        // Asegurar al menos un caracter de cada tipo.
        password.append(mayusculas.charAt(random.nextInt(mayusculas.length())));
        password.append(minusculas.charAt(random.nextInt(minusculas.length())));
        password.append(numeros.charAt(random.nextInt(numeros.length())));
        password.append(simbolos.charAt(random.nextInt(simbolos.length())));

        // Completar hasta 12 caracteres
        String todosCaracteres = mayusculas + minusculas + numeros + simbolos;
        for (int i = 4; i < 12; i++) {
            password.append(todosCaracteres.charAt(random.nextInt(todosCaracteres.length())));
        }

        // Mezclar los caracteres
        char[] arrayPassword = password.toString().toCharArray();
        for (int i = arrayPassword.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = arrayPassword[i];
            arrayPassword[i] = arrayPassword[j];
            arrayPassword[j] = temp;
        }
        return new String(arrayPassword);
    }
}
