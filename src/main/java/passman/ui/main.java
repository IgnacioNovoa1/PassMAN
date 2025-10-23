package passman.ui;

import javax.swing.SwingUtilities;

public class main {

    // Dependencias
    private static final GestorArchivos Gestor = new GestorArchivos();
    private static final PassManService Service = new PassManService(Gestor);
    //Ya no es necesario pero lo dejamos por si a caso.
    //private static final MenuManager Menu = new MenuManager(Service); 

    public static void main(String[] args) {
        // Lógica delegada al MenuManager
        //String usuarioAutenticado = Menu.inicio(); //Tampoco es necesario

        SwingUtilities.invokeLater(() -> new LoginVentana(Service));
        
        //Tampoco es necesaria pero la dejamos por si las moscas.
        /*if (usuarioAutenticado != null) {
            Menu.menuPrincipal(usuarioAutenticado);
        }*/
    }
}