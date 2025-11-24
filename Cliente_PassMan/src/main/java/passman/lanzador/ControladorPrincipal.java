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
        // Ahora usamos el cliente de red en lugar del servicio local
        this.clienteAPI = new ClienteAPI();
        iniciarAplicacion();
    }

    public void iniciarAplicacion() {
        SwingUtilities.invokeLater(() -> {
            loginVentana = new LoginVentana(this);
            loginVentana.setVisible(true);
        });
    }

    // --- Métodos que llaman a la API ---

    public boolean autenticarUsuario(String usuario, String password) {
        return clienteAPI.login(usuario, password);
    }

    public boolean registrarUsuario(String usuario, String rut, String cumpleanos, String password) {
        return clienteAPI.registro(usuario, rut, cumpleanos, password);
    }

    public boolean guardarContrasena(String usuario, String servicio, String contrasena) {
        return clienteAPI.guardarCredencial(usuario, servicio, contrasena);
    }

    public String obtenerBovedaFormateada(String usuario) {
        // Obtenemos la lista desde la API
        List<Map<String, String>> boveda = clienteAPI.listarCredenciales(usuario);
        
        // Formateamos para mostrar en el JTextArea
        if (boveda == null || boveda.isEmpty()) {
            return "No hay contraseñas guardadas o error de conexión.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("-----------------------------------------------------------------------\n");
        sb.append(String.format("| %-4s | %-25s | %-30s |\n", "No.", "Servicio", "Contraseña"));
        sb.append("-----------------------------------------------------------------------\n");

        for (int i = 0; i < boveda.size(); i++) {
            Map<String, String> entrada = boveda.get(i);
            String servicio = entrada.get("servicio");
            String contrasena = entrada.get("contraseña"); // La API ya la devuelve descifrada

            sb.append(String.format("| %-4d | %-25s | %-30s |\n",
                    i + 1, servicio, contrasena));
        }
        sb.append("-----------------------------------------------------------------------\n");
        return sb.toString();
    }

    public String evaluarContrasena(String contrasena, String usuario) {
        return clienteAPI.evaluarContrasena(usuario, contrasena);
    }

    public boolean editarContrasena(String usuario, int indice, String nuevaContrasena) {
        // (Implementación pendiente en API para editar, por ahora retorna falso)
        return false; 
    }

    // --- Navegación (Igual que antes) ---

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
}