package sn.actualite.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecuriteUtil {

    /**
     * Hache le mot de passe en utilisant l'algorithme SHA-256.
     * @param motDePasse le mot de passe en clair
     * @return le mot de passe haché sous forme de chaîne hexadécimale
     */
    public static String hacherMotDePasse(String motDePasse) {
        if (motDePasse == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(motDePasse.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("L'algorithme SHA-256 n'est pas disponible", e);
        }
    }
}
