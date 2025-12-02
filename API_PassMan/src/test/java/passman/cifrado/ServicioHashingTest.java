package passman.cifrado;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ServicioHashingTest {
    @Test
    void testGenerarSalt() {
        ServicioHashing hashing = new ServicioHashing();
        byte[] salt1 = hashing.generarSalt();
        byte[] salt2 = hashing.generarSalt();
        assertNotNull(salt1);
        assertNotNull(salt2);
        assertEquals(16, salt1.length);
        assertEquals(16, salt2.length);
        assertFalse(java.util.Arrays.equals(salt1, salt2));
    }

    @Test
    void testHashPasswordFormato() throws Exception {
        ServicioHashing hashing = new ServicioHashing();
        String[] resultado = hashing.hashPassword("MiPassword123!");
        assertEquals(3, resultado.length);
        assertNotNull(resultado[0]);
        assertNotNull(resultado[1]);
        assertTrue(Integer.parseInt(resultado[2]) > 10000);
    }

    @Test
    void testVerificarPasswordCorrecta() throws Exception {
        ServicioHashing hashing = new ServicioHashing();
        String password = "ClaveSegura123!";
        String[] resultado = hashing.hashPassword(password);
        assertTrue(hashing.verificarPassword(
                password,
                resultado[0],
                resultado[1],
                Integer.parseInt(resultado[2])
        ));
    }

    @Test
    void testVerificarPasswordIncorrecta() throws Exception {
        ServicioHashing hashing = new ServicioHashing();
        String[] resultado = hashing.hashPassword("Correcta123!");
        assertFalse(hashing.verificarPassword(
                "IncorrectaXD!",
                resultado[0],
                resultado[1],
                Integer.parseInt(resultado[2])
        ));
    }
}
