package org.example.persistence;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.example.Note;
import org.example.User;

public class NoteSaver {
    /**
     * Saves the note to a file
     * @param note
     * @param file
     * @throws IOException
     */
    static void saveNote(User user, Note note, String file) throws IOException {
        // This whole function satisfies SER03-J: Do not serialize unencrypted sensitive data 
        // because the note being saved is considered sensitive, so it is being encrypted
        // Convert note to a byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(note);
        out.close();

        byte[] noteData = bos.toByteArray();

        try {
            // Generate keys to use
            byte[] keyBytes = Encryption.generateKeyBytes(user.getPassword());
            SecretKey encKey = new SecretKeySpec(Arrays.copyOfRange(keyBytes, 0, 32), "AES");
            SecretKey macKey = new SecretKeySpec(Arrays.copyOfRange(keyBytes, 32, 64), "HmacSHA256");

            // Encrypt the data from the note
            byte[] iv = SecureRandom.getInstanceStrong().generateSeed(16);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, encKey, new IvParameterSpec(iv));
            byte[] notesCipher = cipher.doFinal(noteData);

            // Generate MAC for integrity
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(macKey);
            byte[] macBytes = mac.doFinal(notesCipher);

            // Save to file as HMAC | IV | Cipher
            FileOutputStream fileOut = new FileOutputStream(file);
            fileOut.write(macBytes);
            fileOut.write(iv);
            fileOut.write(notesCipher);
            fileOut.close();
        } catch (Exception e) {
            // Nothing to do because these exceptions relate to a misconfigured environment
            e.printStackTrace();
            throw new IOException();
        }
    }

    /**
     * Loads the file to a Note object if the user ID matches
     * @param file
     * @param userID
     * @return
     * @throws IOException
     * @throws SecurityException
     * @throws ClassNotFoundException
     */
    static Note loadNote(User user, String file) throws IOException, SecurityException {
        // This function involves SER04-J: Do not allow serialization and deserialization to bypass the security manager 
        // The note class has no field to actually validate, so the content is validated by confirming it was not tampered with
        // and this being the only function which can actually deserialize, thus making it unable to be bypassed;
        try {
            // Generate keys from the user
            byte[] keyBytes = Encryption.generateKeyBytes(user.getPassword());
            SecretKey encKey = new SecretKeySpec(Arrays.copyOfRange(keyBytes, 0, 32), "AES");
            SecretKey macKey = new SecretKeySpec(Arrays.copyOfRange(keyBytes, 32, 64), "HmacSHA256");

            // Reading file from disk
            FileInputStream fileIn = new FileInputStream(file);
            byte[] data = fileIn.readAllBytes();
            fileIn.close();

            // Extract byte ranges
            byte[] macBytes = Arrays.copyOfRange(data, 0, 32);
            byte[] iv = Arrays.copyOfRange(data, 32, 48);
            byte[] notesCipher = Arrays.copyOfRange(data, 48, data.length);

            // Compare HMAC to read HMAC for integrity.
            // This satisfies SER12-J: Prevent deserialization of untrusted data 
            // because the data MUST be trustworthy
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(macKey);
            byte[] expectedMacBytes = mac.doFinal(notesCipher);

            if(!MessageDigest.isEqual(macBytes, expectedMacBytes)) {
                throw new SecurityException("Tampered file");
            }

            // Convert cipher note to real note byte data
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, encKey, new IvParameterSpec(iv));
            byte[] notesPlainData = cipher.doFinal(notesCipher);

            // Create the actual object now that we've confirmed its safe
            ObjectInputStream objectIn = new ObjectInputStream(new ByteArrayInputStream(notesPlainData));
            Note note = (Note)objectIn.readObject();

            // OBJ14-J: Do not use an object that has been freed
            // This one here is avoids this, as in is no longer used once its been freed
            objectIn.close();

            return note;
        } catch(InvalidKeySpecException | NoSuchAlgorithmException e){
            e.printStackTrace();
        } catch(InvalidKeyException | ClassNotFoundException e){
            e.printStackTrace();
        } catch(NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e){
            e.printStackTrace();
        }

        throw new SecurityException("Unable to decrypt");
    }

    public static void main(String[] args) throws IOException {
        Files.createDirectory(Paths.get("./newDirectory"));

        User user1 = new User("Garrett", "password123");
        User user2 = new User("Garrett", "abcdefgh");
        Note test = new Note("Hello world!", "This is a test note");

        // Save to a file
        try {
            saveNote(user1, test, "./data/test.ser");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Load with wrong user
        try {
            Note loaded = loadNote(user2, "./data/test.ser");
            System.out.println(loaded);
        } catch(IOException e){
            e.printStackTrace();
        } catch(SecurityException e){
            System.out.println(e.getMessage());
        }

        // Load with correct user
        try {
            Note loaded = loadNote(user1, "./data/test.ser");
            System.out.println(loaded);
        } catch(IOException e){
            e.printStackTrace();
        } catch(SecurityException e){
            System.out.println(e.getMessage());
        }
    }
}
