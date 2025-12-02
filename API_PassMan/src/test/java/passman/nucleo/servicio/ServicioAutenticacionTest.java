package passman.nucleo.servicio;

import passman.modelo.Usuario;
import passman.cifrado.ServicioHashing;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServicioAutenticacionTest {

    @Test
    void testRegistrarUsuario() {
        ServicioUsuarios mockUsers = mock(ServicioUsuarios.class);
        ServicioHashing mockHash = mock(ServicioHashing.class);
        ServicioAutenticacion auth = new ServicioAutenticacion(mockUsers, mockHash);
        try {
            when(mockHash.hashPassword("clave123"))
                .thenReturn(new String[]{"HASH", "SALT", "5000"});
        } catch (Exception ignored) {}
        when(mockUsers.crearUsuario(any(), any(), any(), any(), any(), anyInt(), any()))
            .thenReturn(true);
        boolean ok = auth.registrarUsuario("ramon", "12345678-9", "1010", "clave123", "ABCD1234");
        assertTrue(ok);
    }

    @Test
    void testIniciarSesionCorrecto() throws Exception {
        ServicioUsuarios mockUsers = mock(ServicioUsuarios.class);
        ServicioHashing mockHash = mock(ServicioHashing.class);
        Usuario user = new Usuario("ramon", "HASH", "SALT", 6000000);
        when(mockUsers.obtenerUsuario("ramon")).thenReturn(user);
        when(mockHash.verificarPassword("pass", "HASH", "SALT", 6000000)).thenReturn(true);
        ServicioAutenticacion auth = new ServicioAutenticacion(mockUsers, mockHash);
        assertTrue(auth.iniciarSesion("ramon", "pass"));
    }

    @Test
    void testIniciarSesionIncorrecto() throws Exception {
        ServicioUsuarios mockUsers = mock(ServicioUsuarios.class);
        ServicioHashing mockHash = mock(ServicioHashing.class);
        Usuario user = new Usuario("ramon", "HASH", "SALT", 6000000);
        when(mockUsers.obtenerUsuario("ramon")).thenReturn(user);
        when(mockHash.verificarPassword(any(), any(), any(), anyInt())).thenReturn(false);
        ServicioAutenticacion auth = new ServicioAutenticacion(mockUsers, mockHash);
        assertFalse(auth.iniciarSesion("ramon", "malapass"));
    }

    @Test
    void testRestablecerPassword() throws Exception {
        ServicioUsuarios mockUsers = mock(ServicioUsuarios.class);
        ServicioHashing mockHash = mock(ServicioHashing.class);
        when(mockHash.hashPassword("nuevaPass")).thenReturn(new String[]{"H", "S", "100"});
        when(mockUsers.recuperarUsuario("ramon", "CODIGO", "H", "S", 100))
                .thenReturn(true);
        ServicioAutenticacion auth = new ServicioAutenticacion(mockUsers, mockHash);
        assertTrue(auth.restablecerPassword("ramon", "CODIGO", "nuevaPass"));
    }
}
