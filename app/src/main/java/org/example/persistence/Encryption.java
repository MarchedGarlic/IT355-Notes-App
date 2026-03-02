package org.example.persistence;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Encryption {
    private static final byte[] SALT = {0x21, 0x24, 0x2F};

    /**
     * Generates a secure key from a password
     * @param password
     * @return
     */
    public static byte[] generateKeyBytes(String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), SALT, 100000, 512);
        return f.generateSecret(spec).getEncoded();
    }
}
