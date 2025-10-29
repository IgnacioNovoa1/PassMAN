package passman.servicio;

import passman.modelo.EntradaCredencial;
import passman.cifrado.ServicioCifrado;
import passman.persistencia.ServicioPersistencia;

import java.util.*;

public class ServicioCredenciales {
    private final ServicioPersistencia persistencia;
    private final ServicioCifrado cifrador;

    public ServicioCredenciales(ServicioPersistencia persistencia, ServicioCifrado cifrador) {
        this.persistencia = persistencia;
        this.cifrador = cifrador;
    }

    public boolean guardarCredencial(UUID usuarioId, String servicio, String usuarioServicio, String password) {
        String passwordCifrada = cifrador.cifrar(password);

        EntradaCredencial credencial = new EntradaCredencial(
                usuarioId, servicio, usuarioServicio, passwordCifrada, ""
        );

        return persistencia.guardarCredencial(credencial);
    }

    public List<Map<String,String>> obtenerCredencialesParaUI(String usuario) {
        List<EntradaCredencial> credenciales = persistencia.cargarCredenciales(obtenerUsuarioId(usuario));
        List<Map<String,String>> resultado = new ArrayList<>();

        for (EntradaCredencial cred : credenciales) {
            Map<String,String> entrada = new HashMap<>();
            entrada.put("servicio", cred.getServicio());

            try {
                String passwordDescifrada = cifrador.descifrar(cred.getPasswordCifrada());
                entrada.put("contraseña", passwordDescifrada);
            } catch (Exception e) {
                entrada.put("contraseña","ERROR");
            }

            resultado.add(entrada);
        }

        return resultado;
    }

    public boolean editarCredencial(UUID usuarioId, int indice, String nuevaPassword) {
        List<EntradaCredencial> credenciales = persistencia.cargarCredenciales(usuarioId);

        if (indice < 0 || indice >= credenciales.size()) {
            return false;
        }

        EntradaCredencial credencial = credenciales.get(indice);
        String nuevaPasswordCifrada = cifrador.cifrar(nuevaPassword);
        credencial.setPasswordCifrada(nuevaPasswordCifrada);

        return persistencia.actualizarCredencial(credencial);
    }

    private UUID obtenerUsuarioId(String nombreUsuario) {
        return UUID.randomUUID();
    }
}
