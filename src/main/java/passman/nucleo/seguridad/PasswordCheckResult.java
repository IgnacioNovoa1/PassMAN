package passman.nucleo.seguridad;

import java.util.ArrayList;
import java.util.List;

public class PasswordCheckResult {
    private final PasswordStrength strength;
    private final String colorHex;
    private final List<String> messages;
    private final int pwnedCount; // 0 si no está en HIBP

    public PasswordCheckResult(PasswordStrength strength, String colorHex, List<String> messages, int pwnedCount) {
        this.strength = strength;
        this.colorHex = colorHex;
        this.messages = new ArrayList<>(messages);
        this.pwnedCount = pwnedCount;
    }

    public PasswordStrength getStrength() { return strength; }
    public String getColorHex() { return colorHex; }
    public List<String> getMessages() { return new ArrayList<>(messages); }
    public int getPwnedCount() { return pwnedCount; }
}
