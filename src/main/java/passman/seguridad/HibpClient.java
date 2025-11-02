package passman.seguridad;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

/**
 * Cliente simple para la API Pwned Passwords (k-anonymity).
 * Método principal: isPwned(password) -> devuelve número de veces que aparece en la DB (0 si no).
 */
public class HibpClient {

    private static final String API_RANGE_URL = "https://api.pwnedpasswords.com/range/";

    /**
     * Devuelve el recuento de apariciones de la contraseña en la BD de HIBP.
     * Si hubo un error de red devuelve -1.
     */
    public int getPwnedCount(String password) {
        try {
            String sha1 = sha1Hex(password).toUpperCase();
            String prefix = sha1.substring(0, 5);
            String suffix = sha1.substring(5);

            URL url = new URL(API_RANGE_URL + prefix);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "passman/1.0");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int code = conn.getResponseCode();
            if (code != 200) {
                // error (podrías lanzar excepción o registrar)
                return -1;
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    // cada línea: SUFFIX:COUNT
                    String[] parts = line.split(":");
                    if (parts.length != 2) continue;
                    if (parts[0].equalsIgnoreCase(suffix)) {
                        return Integer.parseInt(parts[1].trim());
                    }
                }
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private String sha1Hex(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] digest = md.digest(input.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }
}
