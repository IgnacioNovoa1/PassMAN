package passman.lanzador;

import passman.InterfazGrafica.*;
import passman.red.ClienteAPI;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public class ControladorPrincipal {
    
    private final ClienteAPI clienteAPI;
    private LoginVentana loginVentana;
    private MenuVentana menuVentana;

    public ControladorPrincipal() {
        this.clienteAPI = new ClienteAPI();
        iniciarAplicacion();
    }

    public void iniciarAplicacion() {
        SwingUtilities.invokeLater(() -> {
            loginVentana = new LoginVentana(this);
            loginVentana.setVisible(true);
        });
    }

    // --- Métodos API (Login y Registro ahora retornan MAP) ---

    public Map<String, String> autenticarUsuarioMap(String usuario, String password) {
        return clienteAPI.login(usuario, password);
    }

    public Map<String, String> registrarUsuarioMap(String usuario, String rut, String cumpleanos, String password) {
        return clienteAPI.registro(usuario, rut, cumpleanos, password);
    }

    public Map<String, String> recuperarUsuarioMap(String usuario, String codigo, String nuevaPass) {
        return clienteAPI.recuperar(usuario, codigo, nuevaPass);
    }

    public boolean guardarContrasena(String usuario, String servicio, String contrasena) {
        return clienteAPI.guardarCredencial(usuario, servicio, contrasena);
    }
    
    public boolean eliminarContrasena(String usuario, int indice) {
        return clienteAPI.eliminarCredencial(usuario, indice);
    }
    
    public boolean editarContrasena(String usuario, int indice, String nuevaContrasena) {
        return clienteAPI.editarContrasena(usuario, indice, nuevaContrasena);
    }

    public String obtenerBovedaFormateada(String usuario) {
        List<Map<String, String>> boveda = clienteAPI.listarCredenciales(usuario);
        
        if (boveda == null || boveda.isEmpty()) {
            return "No hay contraseñas guardadas o no hay conexión.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("-----------------------------------------------------------------------\n");
        sb.append(String.format("| %-4s | %-25s | %-30s |\n", "No.", "Servicio", "Contraseña"));
        sb.append("-----------------------------------------------------------------------\n");

        for (int i = 0; i < boveda.size(); i++) {
            Map<String, String> entrada = boveda.get(i);
            String servicio = entrada.get("servicio");
            String contrasena = entrada.get("contraseña");

            sb.append(String.format("| %-4d | %-25s | %-30s |\n",
                    i + 1, servicio, contrasena));
        }
        sb.append("-----------------------------------------------------------------------\n");
        return sb.toString();
    }

    public String evaluarContrasena(String contrasena, String usuario) {
        return clienteAPI.evaluarContrasena(usuario, contrasena);
    }

    public void abrirMenuPrincipal(String usuario) {
        if (loginVentana != null) loginVentana.dispose();
        SwingUtilities.invokeLater(() -> {
            menuVentana = new MenuVentana(usuario, this);
            menuVentana.setVisible(true);
        });
    }

    public void abrirRegistro(LoginVentana parent) {
        new RegistroVentana(parent, this).setVisible(true);
    }

    public void abrirEdicion(MenuVentana parent) {
        new EditarContrasenaVentana(parent, this).setVisible(true);
    }

    public void abrirEvaluacion(MenuVentana parent) {
        new EvaluarContrasenaVentana(parent, this).setVisible(true);
    }
    
    public void abrirRecuperacion(LoginVentana parent) {
        new RecuperarVentana(parent, this).setVisible(true);
    }
}
