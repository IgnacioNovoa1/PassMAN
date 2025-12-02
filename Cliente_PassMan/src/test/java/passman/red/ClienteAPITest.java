package passman.red;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test unitarios para ClienteAPI usando JUnit 5 + Mockito.
 */
public class ClienteAPITest {

    private HttpClient mockHttp;
    private Gson gson;
    private ClienteAPI api;
    private HttpResponse<String> mockResponse;

    @BeforeEach
    void setup() {
        mockHttp = mock(HttpClient.class);
        gson = new Gson();
        api = new ClienteAPI(mockHttp, gson);

        mockResponse = (HttpResponse<String>) mock(HttpResponse.class);
    }
    @Test
    void testLogin_ok() throws Exception {
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"status\":\"ok\"}");

        when(mockHttp.send(any(), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        Map<String, String> resp = api.login("ramon", "123");

        assertEquals("ok", resp.get("status"));
    }

    @Test
    void testRegistro_ok() throws Exception {
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"status\":\"ok\"}");

        when(mockHttp.send(any(), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        Map<String, String> resp = api.registro("ramon", "11.111.111-1", "2112", "1234");

        assertEquals("ok", resp.get("status"));
    }

    @Test
    void testRecuperar_ok() throws Exception {
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"status\":\"ok\"}");

        when(mockHttp.send(any(), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        Map<String, String> resp = api.recuperar("ramon", "ABC123", "nuevaClave");

        assertEquals("ok", resp.get("status"));
    }

    @Test
    void testGuardarCredencial_ok() throws Exception {
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"status\":\"ok\"}");

        when(mockHttp.send(any(), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        boolean ok = api.guardarCredencial("ramon", "gmail", "123");

        assertTrue(ok);
    }

    @Test
    void testEditarContrasena_ok() throws Exception {
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"status\":\"ok\"}");

        when(mockHttp.send(any(), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        boolean ok = api.editarContrasena("ramon", 0, "nuevaPass");

        assertTrue(ok);
    }
    @Test
    void testEliminarCredencial_ok() throws Exception {
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"status\":\"ok\"}");

        when(mockHttp.send(any(), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        boolean ok = api.eliminarCredencial("ramon", 0);

        assertTrue(ok);
    }

    @Test
    void testListarCredenciales_ok() throws Exception {
        String jsonList = "[{\"servicio\":\"gmail\",\"usuario\":\"ramon\",\"contraseña\":\"123\"}]";

        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(jsonList);

        when(mockHttp.send(any(), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        List<Map<String, String>> lista = api.listarCredenciales("ramon");

        assertEquals(1, lista.size());
        assertEquals("gmail", lista.get(0).get("servicio"));
    }

    @Test
    void testEvaluarContrasena_ok() throws Exception {
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"resultado\":\"DEBIL\"}");

        when(mockHttp.send(any(), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        String res = api.evaluarContrasena("ramon", "ABC123!");

        assertEquals("DEBIL", res);
    }
}
