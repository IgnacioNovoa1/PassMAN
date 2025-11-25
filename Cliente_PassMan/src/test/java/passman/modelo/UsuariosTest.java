package passman.modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.UUID;

public class UsuariosTest {
    private final UUID idUsuarioExistente = UUID.randomUUID();
    private final String nombreUsuario = "adminTest";
    private final String contrasenaHash = "hashed_pass_value";
    private final String salt = "random_salt";
    private final int iteraciones = 10000;
    
    @Test
    void constructorNuevoUsuarioTest() {
        Usuario usuario = new Usuario(nombreUsuario, contrasenaHash, salt, iteraciones);

        assertNotNull(usuario.getIdUsuario(), "El ID de usuario debe generarse automáticamente.");
        assertEquals(nombreUsuario, usuario.getNombreUsuario(), "El nombre de usuario debe coincidir.");
        assertEquals(iteraciones, usuario.getIteraciones(), "Las iteraciones deben coincidir.");
        assertNull(usuario.getRutCifrado(), "Los campos personales deben ser nulos inicialmente.");
    }

    @Test
    void constructorUsuarioExistenteTest() {
        Usuario usuario = new Usuario(idUsuarioExistente, nombreUsuario, contrasenaHash, salt, iteraciones);

        assertEquals(idUsuarioExistente, usuario.getIdUsuario(), "El ID de usuario debe ser el provisto.");
        assertEquals(contrasenaHash, usuario.getPasswordHash(), "El hash debe coincidir.");
    }
    
    @Test
    void gettersYSettersTest() {
        Usuario usuario = new Usuario(nombreUsuario, contrasenaHash, salt, iteraciones);
        
        String nombre = "NombreCifrado";
        String rut = "RutCifrado";
        
        usuario.setNombreCifrado(nombre);
        usuario.setRutCifrado(rut);
        usuario.setIvPersonales("iv_data");
        
        assertEquals(nombre, usuario.getNombreCifrado(), "El nombre cifrado debe actualizarse.");
        assertEquals(rut, usuario.getRutCifrado(), "El rut cifrado debe actualizarse.");
        assertEquals("iv_data", usuario.getIvPersonales(), "El IV personal debe actualizarse.");
    }
    
    @Test
    void settersDatosHasheadosTest() {
        Usuario usuario = new Usuario(nombreUsuario, contrasenaHash, salt, iteraciones);
        
        String nuevoHash = "new_hash";
        int nuevasiteraciones = 15000;

        usuario.setPasswordHash(nuevoHash);
        usuario.setIteraciones(nuevasiteraciones);

        assertEquals(nuevoHash, usuario.getPasswordHash(), "El hash debe actualizarse.");
        assertEquals(nuevasiteraciones, usuario.getIteraciones(), "Las iteraciones deben actualizarse.");
    }
}
