package org.Nick;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * SecureStorageExample demonstrates compliance with FIO52-J.
 *
 * This program encrypts sensitive information before storing it
 * on the client side (local file). Sensitive data is never written
 * in plaintext form.
 */
class FIO52codeEx {

    /**
     * Main method encrypts a password before saving it.
     *
     * @param args Command-line arguments (not used)
     * @throws Exception if encryption fails
     */
    public static void main(String[] args) throws Exception {
        String sensitiveData = "MySecretPassword123";
        /**
         * Generate an AES encryption key.
         */
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        SecretKey secretKey = keyGen.generateKey();
        /**
         * Initialize cipher for encryption.
         */
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        /**
         * Encrypt the sensitive data.
         */
        byte[] encryptedBytes = cipher.doFinal(sensitiveData.getBytes());
        String encodedEncryptedData =
                Base64.getEncoder().encodeToString(encryptedBytes);
        /**
         * Store encrypted data to file instead of plaintext.
         */
        Files.writeString(Path.of("secure.txt"), encodedEncryptedData);

        System.out.println("Sensitive data stored securely (encrypted).");
    }
}