package main.java.passman.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Usuario {

    private String rut;
    private String cumpleaños;
    private String contraseña; // Hash de la contraseña maestra

    private List<Map<String, String>> bovedaDeContraseñas;

    public Usuario() {
        this.bovedaDeContraseñas = new ArrayList<>();
    }

    // Constructor con datos iniciales (para registro)
    public Usuario(String rut, String cumpleaños, String contraseña) {
        this.rut = rut;
        this.cumpleaños = cumpleaños;
        this.contraseña = contraseña;
        this.bovedaDeContraseñas = new ArrayList<>();
    }

    public String getRut() { return rut; }
    public String getCumpleaños() { return cumpleaños; }
    public String getContraseña() { return contraseña; } // Hash del master
    public List<Map<String, String>> getBovedaDeContraseñas() { return bovedaDeContraseñas; }

    // --- Setters (Solo para uso en actualización o registro) ---
    public void setRut(String rut) { this.rut = rut; }
    public void setCumpleaños(String cumpleaños) { this.cumpleaños = cumpleaños; }
    public void setContraseña(String contraseña) { this.contraseña = contraseña; } // Hash del master
    public void setBovedaDeContraseñas(List<Map<String, String>> bovedaDeContraseñas) {
        this.bovedaDeContraseñas = bovedaDeContraseñas;
    }
}