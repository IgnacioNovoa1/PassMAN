package passman.nucleo.seguridad;

import passman.modelo.Usuario;
import passman.cifrado.ServicioCifrado;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Evalúa la fortaleza de una contraseña.
 */
public class PasswordEvaluator {

    private static final int MIN_LENGTH = 8;
    private static final int RECOMMENDED_LENGTH = 12;

    private final HibpClient hibpClient;
    private final ServicioCifrado cifradoServicio;

    public PasswordEvaluator(HibpClient hibpClient, ServicioCifrado cifradoServicio) {
        this.hibpClient = hibpClient;
        this.cifradoServicio = cifradoServicio;
    }

    public PasswordCheckResult evaluate(String password, Usuario usuario) {
        List<String> messages = new ArrayList<>();
        if (password == null) password = "";

        // 1) HIBP
        int pwnedCount;
        try {
            pwnedCount = hibpClient.getPwnedCount(password);
        } catch (Exception e) {
            pwnedCount = -1;
        }
        if (pwnedCount > 0) {
            messages.add("CONTRASEÑA FILTRADA: encontrada " + pwnedCount + " veces.");
            return new PasswordCheckResult(PasswordStrength.FILTRADA, "#FF0000", messages, pwnedCount);
        }

        // 2) requisitos básicos
        boolean hasLower = Pattern.compile("[a-z]").matcher(password).find();
        boolean hasUpper = Pattern.compile("[A-Z]").matcher(password).find();
        boolean hasDigit = Pattern.compile("\\d").matcher(password).find();
        boolean hasSpecial = Pattern.compile("[^A-Za-z0-9]").matcher(password).find();
        int length = password.length();

        // 3) patrones
        List<String> weakPatterns = new ArrayList<>();
        if (length < MIN_LENGTH) weakPatterns.add("La contraseña es muy corta.");

        if (isSequence(password, 3)) weakPatterns.add("Contiene secuencia ascendente.");
        if (isReverseSequence(password, 3)) weakPatterns.add("Contiene secuencia descendente.");
        if (isRepeatedChar(password, 4)) weakPatterns.add("Contiene repeticiones.");
        if (hasArithmeticPattern(password, 3)) weakPatterns.add("Contiene patrón aritmético.");

        // 4) datos personales
        List<String> personalMatches = checkPersonalDataSubstrings(password, usuario);
        if (!personalMatches.isEmpty()) weakPatterns.addAll(personalMatches);

        // 5) puntaje global
        int score = 0;
        if (length >= MIN_LENGTH) score++;
        if (length >= RECOMMENDED_LENGTH) score++;
        if (hasLower) score++;
        if (hasUpper) score++;
        if (hasDigit) score++;
        if (hasSpecial) score++;
        if (!weakPatterns.isEmpty()) score -= 2 * weakPatterns.size();

        messages.addAll(weakPatterns);

        if (!hasUpper) messages.add("Añade una mayúscula.");
        if (!hasLower) messages.add("Añade una minúscula.");
        if (!hasDigit) messages.add("Añade un número.");
        if (!hasSpecial) messages.add("Añade un caracter especial.");
        if (length < RECOMMENDED_LENGTH) messages.add("Recomendación: usa 12+ caracteres.");

        PasswordStrength finalStrength;
        String color;
        if (score <= 0) {
            finalStrength = PasswordStrength.DEBIL;
            color = "#FF0000";
        } else if (score <= 3) {
            finalStrength = PasswordStrength.SEMIFUERTE;
            color = "#FFA500";
        } else {
            finalStrength = PasswordStrength.FUERTE;
            color = "#008000";
            messages.add("Contraseña fuerte.");
        }

        return new PasswordCheckResult(finalStrength, color, messages, 0);
    }

    // ---------- helpers ----------

    private boolean isSequence(String s, int minLen) {
        if (s.length() < minLen) return false;
        String lower = s.toLowerCase();
        for (int i = 0; i <= lower.length() - minLen; i++) {
            for (int len = minLen; i + len <= lower.length(); len++) {
                if (isConsecutiveIncreasing(lower.substring(i, i + len))) return true;
            }
        }
        return false;
    }

    private boolean isReverseSequence(String s, int minLen) {
        if (s.length() < minLen) return false;
        String lower = s.toLowerCase();
        for (int i = 0; i <= lower.length() - minLen; i++) {
            for (int len = minLen; i + len <= lower.length(); len++) {
                if (isConsecutiveDecreasing(lower.substring(i, i + len))) return true;
            }
        }
        return false;
    }

    private boolean isConsecutiveIncreasing(String s) {
        for (int i = 1; i < s.length(); i++)
            if (s.charAt(i) - s.charAt(i - 1) != 1) return false;
        return true;
    }

    private boolean isConsecutiveDecreasing(String s) {
        for (int i = 1; i < s.length(); i++)
            if (s.charAt(i - 1) - s.charAt(i) != 1) return false;
        return true;
    }

    private boolean isRepeatedChar(String s, int threshold) {
        int count = 1;
        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) == s.charAt(i - 1)) {
                if (++count >= threshold) return true;
            } else count = 1;
        }
        return false;
    }

    private boolean hasArithmeticPattern(String s, int minLen) {
        for (int i = 0; i <= s.length() - minLen; i++) {
            for (int len = minLen; i + len <= s.length(); len++) {
                if (isArithmeticSequence(s.substring(i, i + len))) return true;
            }
        }
        return false;
    }

    private boolean isArithmeticSequence(String s) {
        int d = s.charAt(1) - s.charAt(0);
        if (d == 0) return false;
        if (Math.abs(d) > 5) return false;
        for (int i = 2; i < s.length(); i++)
            if (s.charAt(i) - s.charAt(i - 1) != d) return false;
        return true;
    }

    private List<String> checkPersonalDataSubstrings(String password, Usuario usuario) {
        List<String> matches = new ArrayList<>();
        if (usuario == null) return matches;
        String passLower = password.toLowerCase();
        // 1) Nombre usuario
        if (usuario.getNombreUsuario() != null) {
            String nom = usuario.getNombreUsuario().toLowerCase();
            if (!nom.isEmpty() && passLower.contains(nom))
                matches.add("Contiene el nombre del usuario.");
        }
        // 2) RUT
        String rutCifrado = usuario.getRutCifrado();
        if (rutCifrado != null && !rutCifrado.isEmpty()) {
            try {
                String rut = cifradoServicio.descifrar(rutCifrado).toLowerCase();
                String limpio = rut.replaceAll("[^0-9kK]", "");
                if (limpio.length() >= 4 && passLower.contains(limpio))
                    matches.add("Contiene (parte del) RUT sin formato.");
                if (rut.length() >= 4 && passLower.contains(rut))
                    matches.add("Contiene (parte del) RUT con formato.");
            } catch (Exception ignored) {}
        }
        // 3) Fecha nacimiento 
        String fechaCifrada = usuario.getFechaNacCifrada();
        if (fechaCifrada != null && !fechaCifrada.isEmpty()) {
            try {
                String fechaDescifrada = cifradoServicio.descifrar(fechaCifrada);

                LocalDate fecha = tryParseDate(fechaDescifrada);
                if (fecha != null) {
                    List<String> formatos = Arrays.asList(
                            fecha.format(DateTimeFormatter.ofPattern("ddMM")),
                            fecha.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                            fecha.format(DateTimeFormatter.ofPattern("ddMMyyyy")),
                            fecha.format(DateTimeFormatter.ofPattern("yyyy")),
                            fecha.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                            fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            fechaDescifrada.toLowerCase()
                    );
                    boolean found = formatos.stream()
                            .filter(s -> s.length() >= 4)
                            .anyMatch(s -> passLower.contains(s.toLowerCase()));
                    if (found)
                        matches.add("Contiene fecha de nacimiento en formato común.");
                }
            } catch (Exception ignored) {}
        }
        return matches;
    }
    private LocalDate tryParseDate(String fecha) {
        List<String> formatos = Arrays.asList(
                "yyyy-MM-dd",
                "dd-MM-yyyy",
                "dd/MM/yyyy",
                "yyyyMMdd",
                "ddMMyyyy"
        );
        for (String f : formatos) {
            try {
                return LocalDate.parse(fecha, DateTimeFormatter.ofPattern(f));
            } catch (Exception ignored) {}
        }
        return null;
    }
}
