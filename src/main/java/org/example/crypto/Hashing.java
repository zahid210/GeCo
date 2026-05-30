package org.example.crypto;

import org.mindrot.jbcrypt.BCrypt;

public class Hashing {
    public static String generarHash(String passwordPlano) {
        String sal = BCrypt.gensalt(12);
        return BCrypt.hashpw(passwordPlano, sal);
    }

    public static boolean verificarPassword(String passwordPlano, String hashAlmacenado) {
        try {
            return BCrypt.checkpw(passwordPlano, hashAlmacenado);
        } catch (Exception e) {
            return false;
        }
    }
}