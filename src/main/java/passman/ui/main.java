package passman.ui;

public class main {

    // Dependencias
    private static final GestorArchivos GESTOR_ARCHIVOS = new GestorArchivos();
    private static final PassManService SERVICE = new PassManService(GESTOR_ARCHIVOS);
    private static final MenuManager MENU_MANAGER = new MenuManager(SERVICE); 

    public static void main(String[] args) {
        // Lógica delegada al MenuManager
        String usuarioAutenticado = MENU_MANAGER.inicio();
        
        if (usuarioAutenticado != null) {
            MENU_MANAGER.menuPrincipal(usuarioAutenticado);
        }
    }
}