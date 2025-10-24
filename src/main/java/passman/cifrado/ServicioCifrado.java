package passman.cifrado;

import java.util.Base64;
import java.util.UUID;

public class ServicioCifrado {

    /**
     * Genera un Vector de Inicialización (IV) único, simulando la generación segura
     * necesaria para el cifrado AES.
     */
    public String generarIV() {
        String iv = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        return Base64.getEncoder().encodeToString(iv.getBytes());
    }

    /**
     * Cifra un texto de forma simétrica (AES/KMS).
     * Nota: Actualmente solo envuelve el texto con un prefijo para simular el cifrado.
     */
    public String cifrarDatos(String texto) {
        return "CIFRADO_KMS(" + Base64.getEncoder().encodeToString(texto.getBytes()) + ")";
    }

    /**
     * Descifra un texto cifrado.
     * Nota: Actualmente solo elimina el prefijo para simular el descifrado.
     */
    public String descifrarDatos(String textoCifrado) {
        if (textoCifrado != null && textoCifrado.startsWith("CIFRADO_KMS(")) {
            try {
                String base64Cifrada = textoCifrado.substring(12, textoCifrado.length() - 1);
                return new String(Base64.getDecoder().decode(base64Cifrada));
            } catch (IllegalArgumentException e) {
                return "ERROR_DESCIFRADO_STUB";
            }
        }
        return textoCifrado;
    }
}