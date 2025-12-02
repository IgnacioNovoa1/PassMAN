package passman.nucleo.seguridad;

import passman.modelo.Usuario;
import passman.cifrado.ServicioCifrado;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PasswordEvaluatorTest {

    @Test
    void testPasswordFiltradaHIBP() throws Exception {
        HibpClient hibp = mock(HibpClient.class);
        ServicioCifrado cif = mock(ServicioCifrado.class);
        when(hibp.getPwnedCount("1234")).thenReturn(500);
        PasswordEvaluator eval = new PasswordEvaluator(hibp, cif);
        Usuario usr = new Usuario("ramon", "H", "S", 100);
        PasswordCheckResult r = eval.evaluate("1234", usr);
        assertEquals(PasswordStrength.FILTRADA, r.getStrength());
    }

    @Test
    void testSecuenciaDebil() {
        HibpClient hibp = mock(HibpClient.class);
        ServicioCifrado cif = mock(ServicioCifrado.class);
        when(hibp.getPwnedCount(any())).thenReturn(0);
        PasswordEvaluator eval = new PasswordEvaluator(hibp, cif);
        Usuario usr = new Usuario("ramon", "H", "S", 100);
        PasswordCheckResult r = eval.evaluate("abcd1234", usr);
        assertEquals(PasswordStrength.DEBIL, r.getStrength());
    }

    @Test
    void testDatosPersonales() throws Exception {
        HibpClient hibp = mock(HibpClient.class);
        ServicioCifrado cif = mock(ServicioCifrado.class);
        Usuario usr = new Usuario("ramon", "H", "S", 100);
        usr.setRutCifrado("RUTCIFRADO");
        usr.setFechaNacCifrada("FECHACIF");
        when(hibp.getPwnedCount(any())).thenReturn(0);
        when(cif.descifrar("RUTCIFRADO")).thenReturn("12345678-9");
        when(cif.descifrar("FECHACIF")).thenReturn("1990-01-01");
        PasswordEvaluator eval = new PasswordEvaluator(hibp, cif);
        PasswordCheckResult r = eval.evaluate("clave1990ramon", usr);
        assertEquals(PasswordStrength.DEBIL, r.getStrength());
    }

    @Test
    void testPasswordFuerte() {
        HibpClient hibp = mock(HibpClient.class);
        ServicioCifrado cif = mock(ServicioCifrado.class);
        when(hibp.getPwnedCount(any())).thenReturn(0);
        PasswordEvaluator eval = new PasswordEvaluator(hibp, cif);
        Usuario usr = new Usuario("ramon", "H", "S", 100);
        PasswordCheckResult r = eval.evaluate("Ab!7xK93%qLp", usr);
        assertEquals(PasswordStrength.FUERTE, r.getStrength());
    }
}
