package passman.ui;

import javax.swing.SwingUtilities;

import passman.ui.InterfazGrafica.LoginVentana;

public class main {

    // Dependencias
    private static final GestorArchivos Gestor = new GestorArchivos();
    private static final PassManService Service = new PassManService(Gestor);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginVentana(Service));
    }
}