package passman.lanzador;

import passman.nucleo.servicio.ServicioPassman;
import passman.InterfazGrafica.*;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public class ControladorPrincipal {
    private final ServicioPassman servicioPassman;
    private LoginVentana loginVentana;
    private MenuVentana menuVentana;

    public ControladorPrincipal() {
        this.servicioPassman = new ServicioPassman();
        iniciarAplicacion();
    }

    public void iniciarAplicacion() {
        SwingUtilities.invokeLater(() -> {
            loginVentana = new LoginVentana(this);
            loginVentana.setVisible(true);
        });
    }

    // Métodos de Autenticación.
    public boolean autenticarUsuario(String usuario, String password) {
        return servicioPassman.iniciarSesion(usuario, password);
    }

    public boolean registrarUsuario(String usuario, String rut, String cumpleanos, String password) {
        return servicioPassman.registrarUsuario(usuario, rut, cumpleanos, password);
    }

    // Métodos Gestión.

    public boolean guardarContrasena(String usuario, String servicio, String contrasena) {
        return servicioPassman.guardarContrasena(usuario, servicio, contrasena);
    }

    public String obtenerBovedaFormateada(String usuario) {
        var boveda = servicioPassman.getContrasenasBoveda(usuario);
        return formatearBovedaParaUI(boveda);
    }

    public boolean editarContrasena(String usuario, int indice, String nuevaContrasena) {
        return servicioPassman.editarContrasena(usuario, indice, nuevaContrasena);
    }

    public String evaluarContrasena(String contrasena, String usuario) {
        return servicioPassman.evaluarContrasena(contrasena, usuario);
    }

    // Navegación.

    public void abrirMenuPrincipal(String usuario) {
        if (loginVentana != null) {
            loginVentana.dispose();
        }

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

    // Privados.

    private String formatearBovedaParaUI(List<Map<String, String>> boveda) {
        if (boveda == null || boveda.isEmpty()) {
            return "No hay contraseñas guardadas en tu bóveda.";
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
                    i + 1, servicio, contrasena != null ? contrasena : "ERROR"));
        }
        sb.append("-----------------------------------------------------------------------\n");

        return sb.toString();
    }
}
