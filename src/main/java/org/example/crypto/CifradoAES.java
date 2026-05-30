package org.example.crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CifradoAES {
    private static final String ALGORITMO = "AES";

    private static SecretKeySpec prepararClave(String llaveMaestra) {
        byte[] claveBytes = new byte[16];
        byte[] passwordBytes = llaveMaestra.getBytes(StandardCharsets.UTF_8);

        // Copia los bytes de la contraseña en el contenedor de 16 bytes
        System.arraycopy(passwordBytes, 0, claveBytes, 0, Math.min(passwordBytes.length, 16));

        return new SecretKeySpec(claveBytes, ALGORITMO);
    }

    public static String encriptar(String textoPlano, String llaveMaestra) throws Exception {
        SecretKeySpec claveSecreta = prepararClave(llaveMaestra);

        Cipher cipher = Cipher.getInstance(ALGORITMO);
        cipher.init(Cipher.ENCRYPT_MODE, claveSecreta);

        byte[] textoCifradoBytes = cipher.doFinal(textoPlano.getBytes(StandardCharsets.UTF_8));

        // Convertimos los bytes cifrados a texto legible (Base64) para guardarlo fácilmente en el JSON
        return Base64.getEncoder().encodeToString(textoCifradoBytes);
    }

    public static String desencriptar(String textoCifradoBase64, String llaveMaestra) throws Exception {
        SecretKeySpec claveSecreta = prepararClave(llaveMaestra);

        Cipher cipher = Cipher.getInstance(ALGORITMO);
        cipher.init(Cipher.DECRYPT_MODE, claveSecreta);

        byte[] textoCifradoBytes = Base64.getDecoder().decode(textoCifradoBase64);
        byte[] textoPlanoBytes = cipher.doFinal(textoCifradoBytes);

        return new String(textoPlanoBytes, StandardCharsets.UTF_8);
    }

}
