package passman.ui;

public class main {

    // Dependencias
    private static final GestorArchivos Gestor = new GestorArchivos();
    private static final PassManService Service = new PassManService(Gestor);
    private static final MenuManager Menu = new MenuManager(Service); 

    public static void main(String[] args) {
        // Lógica delegada al MenuManager
        String usuarioAutenticado = Menu.inicio();
        
        if (usuarioAutenticado != null) {
            Menu.menuPrincipal(usuarioAutenticado);
        }
    }
}