package passman.cifrado;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class ServicioHashing {
    private static final int ITERACIONES = 6000000;
    private static final int LONGITUD_KEY = 256;
    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";

    public byte[] generarSalt() {
        SecureRandom random= new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }
    private byte[] hash(char[] password, byte[] salt, int iteraciones) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iteraciones, LONGITUD_KEY);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITMO);
        return skf.generateSecret(spec).getEncoded();
    }
    public String[] hashPassword(String password) throws Exception {
        byte[] salt = generarSalt();
        byte[] hash = hash(password.toCharArray(), salt, ITERACIONES);

        String saltBase64 = Base64.getEncoder().encodeToString(salt);
        String hashBase64 = Base64.getEncoder().encodeToString(hash);
        return new String[]{hashBase64, saltBase64, String.valueOf(ITERACIONES)};
    }
    public boolean verificarPassword(String pass, String hashAlmacenado64, String saltAlmacenado64, int iterac) throws Exception {
        byte[] salt = Base64.getDecoder().decode(saltAlmacenado64);
        byte[] hash = Base64.getDecoder().decode(hashAlmacenado64);
        byte[] hashInterno = hash(pass.toCharArray(), salt, iterac);
        return Arrays.equals(hash, hashInterno);
    }
}