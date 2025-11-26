package passman.nucleo.seguridad;

import passman.modelo.Usuario;
import passman.cifrado.ServicioCifrado;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Evalúa la fortaleza de una contraseña teniendo en cuenta:
 * - presencia de mayúsculas, minúsculas, dígitos, caracteres especiales
 * - longitud mínima
 * - patrones: secuencias ascendentes/descendentes (abc, 123, cba, 321), repeticiones (aaaa, 1111)
 * - patrones aritméticos (pasos constantes p.ej. +2: 2,4,6,8)
 * - que no contenga datos personales (nombre, rut, fecha nacimiento en formatos)
 * - integracion con HIBP por medio de HibpClient
 */
public class PasswordEvaluator {

    private static final int MIN_LENGTH = 8;
    private static final int RECOMMENDED_LENGTH = 12;

    private final HibpClient hibpClient;
    private final ServicioCifrado  cifradoServicio;

    public PasswordEvaluator(HibpClient hibpClient,  ServicioCifrado cifradoServicio) {
        this.hibpClient = hibpClient;
        this.cifradoServicio = cifradoServicio;
    }

    public PasswordCheckResult evaluate(String password, Usuario usuario) {
        List<String> messages = new ArrayList<>();

        if (password == null) password = "";

        // 1) HIBP check (prioritario)
        int pwnedCount = -1;
        try {
            pwnedCount = hibpClient.getPwnedCount(password);
        } catch (Exception e) {
            pwnedCount = -1;
        }
        if (pwnedCount > 0) {
            messages.add(String.format("CONTRASEÑA FILTRADA: encontrada %d veces en bases de datos públicas. ¡Usa otra!", pwnedCount));
            return new PasswordCheckResult(PasswordStrength.FILTRADA, "#FF0000", messages, pwnedCount);
        }

        // 2) requisitos básicos
        boolean hasLower = Pattern.compile("[a-z]").matcher(password).find();
        boolean hasUpper = Pattern.compile("[A-Z]").matcher(password).find();
        boolean hasDigit = Pattern.compile("\\d").matcher(password).find();
        boolean hasSpecial = Pattern.compile("[^A-Za-z0-9]").matcher(password).find();
        int length = password.length();

        // 3) patrones débiles
        List<String> weakPatterns = new ArrayList<>();
        if (length < MIN_LENGTH) weakPatterns.add("La contraseña es muy corta (mínimo " + MIN_LENGTH + " caracteres).");

        if (isSequence(password, 3)) weakPatterns.add("Contiene secuencia (p.ej. 'abcd' o '1234').");
        if (isReverseSequence(password, 3)) weakPatterns.add("Contiene secuencia inversa (p.ej. 'dcba' o '4321').");
        if (isRepeatedChar(password, 4)) weakPatterns.add("Contiene repetición de un mismo carácter (p.ej. 'aaaa' o '1111').");
        if (hasArithmeticPattern(password, 3)) weakPatterns.add("Contiene patrón aritmético (p.ej. '2468' o 'aceg').");

        // 4) contiene datos personales
        List<String> personalMatches = checkPersonalDataSubstrings(password, usuario);
        if (!personalMatches.isEmpty()) {
            weakPatterns.addAll(personalMatches);
        }

        // 5) puntuación final heurística
        int score = 0;
        if (length >= MIN_LENGTH) score += 1;
        if (length >= RECOMMENDED_LENGTH) score += 1;
        if (hasLower) score += 1;
        if (hasUpper) score += 1;
        if (hasDigit) score += 1;
        if (hasSpecial) score += 1;
        if (weakPatterns.size() > 0) score -= 2 * weakPatterns.size();

        // Construir mensajes y nivel
        if (!weakPatterns.isEmpty()) {
            messages.addAll(weakPatterns);
        }

        // Recomendaciones de mejora
        if (!hasUpper) messages.add("Añade al menos una letra mayúscula.");
        if (!hasLower) messages.add("Añade al menos una letra minúscula.");
        if (!hasDigit) messages.add("Añade al menos un dígito.");
        if (!hasSpecial) messages.add("Añade al menos un carácter especial (p.ej. !@#$%).");
        if (length < RECOMMENDED_LENGTH) messages.add("Considera aumentar la longitud a 12+ caracteres.");

        PasswordStrength finalStrength;
        String color;
        if (score <= 0) {
            finalStrength = PasswordStrength.DEBIL;
            color = "#FF0000"; // rojo
            if (messages.isEmpty()) messages.add("Contraseña débil.");
        } else if (score <= 3) {
            finalStrength = PasswordStrength.SEMIFUERTE;
            color = "#FFA500"; // naranja
            if (messages.isEmpty()) messages.add("Contraseña semifuerte; mejora con más variedad y longitud.");
        } else {
            finalStrength = PasswordStrength.FUERTE;
            color = "#008000"; // verde
            messages.add("Contraseña fuerte.");
        }

        return new PasswordCheckResult(finalStrength, color, messages, 0);
    }

    // ---------- helpers ----------

    private boolean isSequence(String s, int minLen) {
        if (s.length() < minLen) return false;
        String lower = s.toLowerCase();
        // check for alphabetical or numeric sequences of length >= minLen
        for (int i = 0; i <= lower.length() - minLen; i++) {
            for (int len = minLen; i + len <= lower.length(); len++) {
                String sub = lower.substring(i, i + len);
                if (isConsecutiveIncreasing(sub)) return true;
            }
        }
        return false;
    }

    private boolean isReverseSequence(String s, int minLen) {
        if (s.length() < minLen) return false;
        String lower = s.toLowerCase();
        for (int i = 0; i <= lower.length() - minLen; i++) {
            for (int len = minLen; i + len <= lower.length(); len++) {
                String sub = lower.substring(i, i + len);
                if (isConsecutiveDecreasing(sub)) return true;
            }
        }
        return false;
    }

    private boolean isConsecutiveIncreasing(String s) {
        if (s.length() < 2) return false;
        for (int i = 1; i < s.length(); i++) {
            int prev = s.charAt(i - 1);
            int cur = s.charAt(i);
            if (cur - prev != 1) return false;
        }
        return true;
    }

    private boolean isConsecutiveDecreasing(String s) {
        if (s.length() < 2) return false;
        for (int i = 1; i < s.length(); i++) {
            int prev = s.charAt(i - 1);
            int cur = s.charAt(i);
            if (prev - cur != 1) return false;
        }
        return true;
    }

    private boolean isRepeatedChar(String s, int threshold) {
        if (s.length() < threshold) return false;
        int count = 1;
        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) == s.charAt(i - 1)) {
                count++;
                if (count >= threshold) return true;
            } else {
                count = 1;
            }
        }
        return false;
    }

    /**
     * Detecta patrones aritméticos en la representación de caracteres.
     * Por ejemplo "2468" (paso 2), "aceg" (paso 2 entre letras).
     */
    private boolean hasArithmeticPattern(String s, int minLen) {
        if (s.length() < minLen) return false;
        for (int i = 0; i <= s.length() - minLen; i++) {
            for (int len = minLen; i + len <= s.length(); len++) {
                String sub = s.substring(i, i + len);
                if (isArithmeticSequence(sub)) return true;
            }
        }
        return false;
    }

    private boolean isArithmeticSequence(String s) {
        if (s.length() < 3) return false;
        // compute difference between first two chars
        int d = s.charAt(1) - s.charAt(0);
        if (d == 0) return false; // same char handled elsewhere
        for (int i = 2; i < s.length(); i++) {
            if (s.charAt(i) - s.charAt(i - 1) != d) return false;
        }
        // allow only small steps (e.g. -5..5) to avoid accidental matches across unicode
        return Math.abs(d) <= 5;
    }

    /**
     * Revisa si la contraseña contiene trozos obvios de datos personales del Usuario.
     * Busca:
     *  - nombre de usuario / nombre (case-insensitive)
     *  - rut (con o sin puntos, con o sin guión)
     *  - fecha nac en formatos yyyyMMdd, ddMMyyyy, dd-MM-yyyy, dd/MM/yyyy, yyyy-MM-dd
     */
    private List<String> checkPersonalDataSubstrings(String password, Usuario usuario) {
        List<String> matches = new ArrayList<>();
        if (usuario == null) return matches;
        String passLower = password.toLowerCase();

        // Nombre usuario y nombreCifrado no siempre están en claro; si guardas versión clara, cambia aquí.
        if (usuario.getNombreUsuario() != null) {
            String nom = usuario.getNombreUsuario().toLowerCase();
            if (!nom.isEmpty() && passLower.contains(nom)) matches.add("Contiene el nombre de usuario o parte de él.");
        }

        // Intentamos extraer rut (si lo guardas en claro; en tu modelo está cifrado, así que depende si tienes dato)
        String rutCifrado = usuario.getRutCifrado(); // en tu modelo está cifrado; si tienes rut en claro, pásalo aquí
        if (rutCifrado != null && !rutCifrado.isEmpty()) {
            try {
                // Se llama al servicio para descifrar el RUT.
                String rutDescifrado = cifradoServicio.descifrar(rutCifrado);

                // Limpiar puntos y guiones para comparar.
                String rawRut = rutDescifrado.replaceAll("[^0-9kK]", "").toLowerCase();

                if (rawRut.length() >= 4 && passLower.contains(rawRut)) {
                    matches.add("Contiene (parte del) RUT sin formato.");
                }

                // si contraseña contiene rut con guion/puntos
                String withSep = rutDescifrado.toLowerCase();
                if (withSep.length() >= 4 && passLower.contains(withSep)) {
                    matches.add("Contiene (parte del) RUT con formato.");
                }
            } catch (Exception e) {
                // Manejar errores de descifrado. (Texto corrupto o llave incorrecta)
                System.err.println("Error al descifrar RUT: " + e.getMessage());
            }
        }

        // Fecha de nacimiento (si la guardas en claro o en formatos predecibles)
        String fechaCifrada = usuario.getFechaNacCifrada();
        if (fechaCifrada != null && !fechaCifrada.isEmpty()) {
            try {
                // Descifrar el dato
                String fechaDescifrada = cifradoServicio.descifrar(fechaCifrada);

                LocalDate dt = LocalDate.parse(fechaDescifrada, DateTimeFormatter.ISO_LOCAL_DATE);

                // Formatos débiles para buscar
                String ymd = dt.format(DateTimeFormatter.ofPattern("yyyyMMdd"));       // 19901231
                String dmy = dt.format(DateTimeFormatter.ofPattern("ddMMyyyy"));       // 31121990
                String year = dt.format(DateTimeFormatter.ofPattern("yyyy"));          // 1990
                String dash = dt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));    // 31-12-1990
                String slash = dt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));   // 31/12/1990

                // Lista de los substrings a buscar
                List<String> substrings = Arrays.asList(
                        ymd.toLowerCase(), dmy.toLowerCase(), year.toLowerCase(),
                        dash.toLowerCase(), slash.toLowerCase(), fechaDescifrada.toLowerCase()
                );

                // Buscar coincidencias en la contraseña
                boolean found = false;
                for (String sub : substrings) {
                    if (passLower.contains(sub)) {
                        found = true;
                        break;
                    }
                }

                if (found) {
                    matches.add("Contiene fecha de nacimiento (o el año) en algún formato común.");
                }

            } catch (Exception ignored) {}
        }

        // Si en tu modelo guardas nombre/apellido descifrado, agrégalos aquí
        String nombreC = usuario.getNombreCifrado();
        if (nombreC != null && !nombreC.isEmpty()) {
            try {
                String nombreDesc = cifradoServicio.descifrar(nombreC);
                if (passLower.contains(nombreDesc.toLowerCase())) matches.add("Contiene el nombre personal.");
            } catch (Exception ignored) {}
        }
        return matches;
    }
}
