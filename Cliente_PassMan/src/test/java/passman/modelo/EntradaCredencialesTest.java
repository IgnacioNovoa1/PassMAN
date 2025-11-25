package passman.modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

public class EntradaCredencialesTest {
    private final UUID idUsuario = UUID.randomUUID();
    private final String servicio = "Facebook";
    private final String usuarioServicio = "admin@email.com";
    private final String passCifrada = "holabb2233";
    private final String iv = "iv_vector_inicial";
    
    private final UUID idCredencialesExistentes = UUID.randomUUID();

    @Test
    void constructorNuevaCredencialTest() {
        EntradaCredencial credencial = new EntradaCredencial(idUsuario, servicio, usuarioServicio, passCifrada, iv);

        assertNotNull(credencial.getIdCredencial(), "El ID de la credencial debe generarse automáticamente.");
        assertEquals(idUsuario, credencial.getIdUsuario(), "El ID de usuario debe coincidir.");
        assertEquals(servicio, credencial.getServicio(), "El servicio debe coincidir.");
        assertEquals(passCifrada, credencial.getPasswordCifrada(), "La contraseña cifrada debe coincidir.");
    }
    
    @Test
    void constructorCredencialExistenteTest() {
        EntradaCredencial credencial = new EntradaCredencial(idCredencialesExistentes, idUsuario, servicio, usuarioServicio, passCifrada, iv);

        assertEquals(idCredencialesExistentes, credencial.getIdCredencial(), "El ID de la credencial debe ser el provisto.");
        assertEquals(idUsuario, credencial.getIdUsuario(), "El ID de usuario debe ser el provisto.");
    }

    @Test
    void settersTest() {
        EntradaCredencial credencial = new EntradaCredencial(idUsuario, servicio, usuarioServicio, passCifrada, iv);
        
        String nuevoservicio = "Amazon";
        String nuevaPassCifrada = "new_cifrado";

        credencial.setServicio(nuevoservicio);
        credencial.setPasswordCifrada(nuevaPassCifrada);
        credencial.setIv("nuevo_iv");

        assertEquals(nuevoservicio, credencial.getServicio(), "El servicio debe actualizarse correctamente.");
        assertEquals(nuevaPassCifrada, credencial.getPasswordCifrada(), "La contraseña cifrada debe actualizarse.");
        assertEquals("nuevo_iv", credencial.getIv(), "El iv debe actualizarse.");
    }
    
    @Test
    void toStringTest() {
        EntradaCredencial credencial = new EntradaCredencial(idUsuario, servicio, usuarioServicio, passCifrada, iv);
        String expected = "Servicio: Facebook (Usuario: admin@email.com)";
        
        String actual = credencial.toString();
        
        assertEquals(expected, actual, "El método toString debe retornar el formato esperado.");
    }
}
