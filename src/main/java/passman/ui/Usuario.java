package passman.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Usuario {

    private String rut;
    private String cumpleanos;
    private String contrasena; // Hash de la contrasena maestra

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
    }

    public String getRut() { return rut; }
    public String getCumpleanos() { return cumpleanos; }
    public String getContrasena() { return contrasena; } // Hash del master
    public List<Map<String, String>> getBovedaDeContrasenas() { return bovedaDeContrasenas; }

    // --- Setters (Solo para uso en actualización o registro) ---
    public void setRut(String rut) { this.rut = rut; }
    public void setCumpleanos(String cumpleanos) { this.cumpleanos = cumpleanos; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; } // Hash del master
    public void setBovedaDeContrasenas(List<Map<String, String>> bovedaDeContrasenas) {
        this.bovedaDeContrasenas = bovedaDeContrasenas;
    }
}