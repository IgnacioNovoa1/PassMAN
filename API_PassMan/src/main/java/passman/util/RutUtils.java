package passman.util;

import java.util.regex.Pattern;

public class RutUtils {

    public static boolean validar(String rut) {
        if (rut == null || rut.trim().isEmpty()) return false;
        
        // Limpiar puntos y guión
        String rutLimpio = rut.replace(".", "").replace("-", "").toUpperCase();
        
        // Validar formato base (números + K)
        if (!rutLimpio.matches("^[0-9]+[0-9K]$")) return false;

        try {
            String cuerpo = rutLimpio.substring(0, rutLimpio.length() - 1);
            char dv = rutLimpio.charAt(rutLimpio.length() - 1);
            
            return calcularDV(cuerpo) == dv;
        } catch (Exception e) {
            return false;
        }
    }

    private static char calcularDV(String cuerpo) {
        int rut = Integer.parseInt(cuerpo);
        int m = 0, s = 1;
        for (; rut != 0; rut /= 10) {
            s = (s + rut % 10 * (9 - m++ % 6)) % 11;
        }
        return (char) (s != 0 ? s + 47 : 75);
    }
}