package passman.nucleo.servicio;

import passman.modelo.EntradaCredencial;
import passman.cifrado.ServicioCifrado;
import passman.persistencia.ServicioPersistencia;

import org.junit.jupiter.api.Test;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ServicioCredencialesTest {

    @Test
    void testGuardarCredencial() {
        ServicioPersistencia mockDB = mock(ServicioPersistencia.class);
        ServicioCifrado mockCif = mock(ServicioCifrado.class);
        ServicioUsuarios mockUsers = mock(ServicioUsuarios.class);
        when(mockUsers.obtenerIdUsuario("ramon")).thenReturn(UUID.randomUUID());
        when(mockCif.cifrar("clave123")).thenReturn("CIFRADO");
        when(mockDB.guardarCredencial(any())).thenReturn(true);
        ServicioCredenciales serv = new ServicioCredenciales(mockDB, mockCif, mockUsers);
        boolean ok = serv.guardarCredencial("ramon", "gmail", "ramon", "clave123");
        assertTrue(ok);
    }

    @Test
    void testObtenerCredencialesParaUI() throws Exception {
        ServicioPersistencia mockDB = mock(ServicioPersistencia.class);
        ServicioCifrado mockCif = mock(ServicioCifrado.class);
        ServicioUsuarios mockUsers = mock(ServicioUsuarios.class);
        UUID id = UUID.randomUUID();
        when(mockUsers.obtenerIdUsuario("ramon")).thenReturn(id);
        EntradaCredencial cred = new EntradaCredencial(
                id, "gmail", "ramon", "CIFRADA", "");
        when(mockDB.cargarCredenciales(id)).thenReturn(List.of(cred));
        when(mockCif.descifrar("CIFRADA")).thenReturn("decifrada");
        ServicioCredenciales serv = new ServicioCredenciales(mockDB, mockCif, mockUsers);
        List<Map<String, String>> lista = serv.obtenerCredencialesParaUI("ramon");
        assertEquals(1, lista.size());
        assertEquals("decifrada", lista.get(0).get("contraseña"));
    }

    @Test
    void testEditarCredencial() {
        ServicioPersistencia mockDB = mock(ServicioPersistencia.class);
        ServicioCifrado mockCif = mock(ServicioCifrado.class);
        ServicioUsuarios mockUsers = mock(ServicioUsuarios.class);
        UUID id = UUID.randomUUID();
        EntradaCredencial cred = new EntradaCredencial(id, id, "gmail", "ramon", "C1", "");
        when(mockUsers.obtenerIdUsuario("ramon")).thenReturn(id);
        when(mockDB.cargarCredenciales(id)).thenReturn(List.of(cred));
        when(mockCif.cifrar("NUEVA")).thenReturn("C2");
        when(mockDB.actualizarCredencial(any())).thenReturn(true);
        ServicioCredenciales serv = new ServicioCredenciales(mockDB, mockCif, mockUsers);
        assertTrue(serv.editarCredencial("ramon", 0, "NUEVA"));
    }

    @Test
    void testVerificarReutilizacion() throws Exception {
        ServicioPersistencia mockDB = mock(ServicioPersistencia.class);
        ServicioCifrado mockCif = mock(ServicioCifrado.class);
        ServicioUsuarios mockUsers = mock(ServicioUsuarios.class);
        UUID id = UUID.randomUUID();
        EntradaCredencial cred = new EntradaCredencial(id, id, "gmail", "ramon", "CIFRADA", "");
        when(mockUsers.obtenerIdUsuario("ramon")).thenReturn(id);
        when(mockDB.cargarCredenciales(id)).thenReturn(List.of(cred));
        when(mockCif.descifrar("CIFRADA")).thenReturn("pass123");
        ServicioCredenciales serv = new ServicioCredenciales(mockDB, mockCif, mockUsers);
        assertTrue(serv.verificarReutilizacion("ramon", "pass123"));
        assertFalse(serv.verificarReutilizacion("ramon", "otraPass"));
    }
}
