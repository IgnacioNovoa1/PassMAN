package passman.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Usuario {

    private String rut;
    private String cumpleanos;
    private String contrasena; // Hash de la contrasena maestra
    private int desplazamiento;

    private List<Map<String, String>> bovedaDeContrasenas;

    public Usuario() {
        this.bovedaDeContrasenas = new ArrayList<>();
    }

    // Constructor con datos iniciales (para registro)
    public Usuario(String rut, String cumpleanos, String contrasena) {
        this.rut = rut;
        this.cumpleanos = cumpleanos;
        this.contrasena = contrasena;
        this.bovedaDeContrasenas = new ArrayList<>();
        this.desplazamiento = 3;
    }

    public String getRut() { return rut; }
    public String getCumpleanos() { return cumpleanos; }
    public String getContrasena() { return contrasena; } // Hash del master
    public List<Map<String, String>> getBovedaDeContrasenas() { return bovedaDeContrasenas; }

    //Setters (Solo para uso en actualización o registro)
    public void setRut(String rut) { this.rut = rut; }
    public void setCumpleanos(String cumpleanos) { this.cumpleanos = cumpleanos; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; } // Hash del master
    public void setBovedaDeContrasenas(List<Map<String, String>> bovedaDeContrasenas) {
        this.bovedaDeContrasenas = bovedaDeContrasenas;
    }

    public void verContrasenas(String usuario) {
        GestorArchivos gestor = new GestorArchivos();
        Map<String, Usuario> datos = gestor.cargarUsuarios();
        Usuario user = datos.get(usuario);
        List<Map<String, String>> boveda = user.getBovedaDeContrasenas();

        if (boveda.isEmpty()) {
            System.out.println("No hay contraseñas guardadas.");
            return;
        }

        System.out.println("\n--- Boveda de Contraseñas ---");
        for (int i = 0; i < boveda.size(); i++) {
            Map<String, String> entrada = boveda.get(i);
            String servicio = entrada.get("servicio");
            String contrasenaCifrada = entrada.get("contraseña");

            String contrasenaDescifrada = CifradoCesar.descifrar(contrasenaCifrada, this.desplazamiento);

            System.out.printf("%d. Servicio: %s | Contraseña: %s\n", i + 1, servicio, contrasenaDescifrada);
        }
    }

    public void guardarContrasena(String usuario, String servicio, String contrasena) {
        GestorArchivos gestor = new GestorArchivos();
        Map<String, Usuario> datos = gestor.cargarUsuarios();
        Usuario user = datos.get(usuario);

        String cifrada = CifradoCesar.cifrar(contrasena, this.desplazamiento);

        Map<String, String> nuevaEntrada = new HashMap<>();
        nuevaEntrada.put("servicio", servicio);
        nuevaEntrada.put("contraseña", cifrada);

        user.getBovedaDeContrasenas().add(nuevaEntrada);
        gestor.guardarUsuarios(datos);
        System.out.println("Contraseña guardada correctamente.");
    }
}