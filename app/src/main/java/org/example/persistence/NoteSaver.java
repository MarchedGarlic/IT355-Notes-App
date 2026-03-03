package org.example.persistence;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Set;

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

    /* FIO00-J: Do not operate on files in shared directories
    Reserved Windows device names that must be rejected to prevent
    crashes and denial-of-service attacks when interpreted as file resources
    */
    private static final String[] RESERVED_DEVICE_NAMES = {
        "CON", "PRN", "AUX", "NUL",
        "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9",
        "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"
    };

    /**
     * FIO00-J: Checks if the directory containing the file is secure.
     * A secure directory exists, is readable, and is a real directory
     * (not a symbolic link).
     *
     * @param filePath Path to check
     * @return true if the parent directory is secure
     */
    static boolean isSecureDirectory(String filePath) {
        try {
            Path path = Paths.get(filePath).getParent();
            if (path == null) {
                return false;
            }
            // Check if directory exists and is readable
            if (!Files.exists(path) || !Files.isReadable(path)) {
                return false;
            }
            // Check if it's a regular directory (not a link)
            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class,
                                                             LinkOption.NOFOLLOW_LINKS);
            return attrs.isDirectory() && !attrs.isSymbolicLink();
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * FIO00-J: Validates that a file path is safe to use.
     * Rejects device files, symbolic links, and files not in secure directories.
     *
     * @param filename File to validate
     * @return true if the file is safe to operate on
     */
    static boolean isFileSafe(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return false;
        }

        // Reject reserved device file names (Windows)
        String baseName = Paths.get(filename).getFileName().toString().toUpperCase();
        // Strip extension for comparison (e.g. "CON.txt" is still a device)
        String nameNoExt = baseName.contains(".") ? baseName.substring(0, baseName.indexOf('.')) : baseName;
        for (String device : RESERVED_DEVICE_NAMES) {
            if (nameNoExt.equals(device)) {
                System.out.println("FIO00-J: Device file detected: " + filename);
                return false;
            }
        }

        // Check if the file resides in a secure directory
        if (!isSecureDirectory(filename)) {
            return false;
        }

        // If the file already exists, ensure it is a regular file (not a link or device)
        Path path = Paths.get(filename);
        if (Files.exists(path)) {
            try {
                BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class,
                                                                 LinkOption.NOFOLLOW_LINKS);
                if (!attrs.isRegularFile()) {
                    System.out.println("FIO00-J: Not a regular file: " + filename);
                    return false;
                }
            } catch (IOException e) {
                return false;
            }
        }

        return true;
    }

    /**
     * FIO01-J: Sets secure permissions on a file so only the owner can read/write.
     * Attempts POSIX permissions first (rw-------); falls back to java.io.File
     * methods on platforms where POSIX is not supported (e.g. Windows).
     *
     * @param filename Path to the file to secure
     * @throws IOException if unable to change permissions
     */
    static void makeFileSecure(String filename) throws IOException {
        Path path = Paths.get(filename);
        try {
            // Try POSIX permissions first — owner read/write only
            Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rw-------");
            Files.setPosixFilePermissions(path, perms);
            System.out.println("FIO01-J: File secured with POSIX permissions: " + filename);
        } catch (UnsupportedOperationException e) {
            // POSIX not supported (e.g. Windows) — fall back to java.io.File
            File f = path.toFile();
            f.setReadable(false, false);   // Remove read for everyone
            f.setReadable(true, true);     // Add read for owner only
            f.setWritable(false, false);   // Remove write for everyone
            f.setWritable(true, true);     // Add write for owner only
            f.setExecutable(false, false); // Remove execute for everyone
            System.out.println("FIO01-J: File secured with fallback permissions: " + filename);
        }
    }

    /**
     * FIO01-J: Sets secure permissions on a directory so only the owner can access it.
     * Attempts POSIX permissions first (rwx------); falls back to java.io.File
     * methods on platforms where POSIX is not supported (e.g. Windows).
     *
     * @param dirPath Path to the directory to secure
     * @throws IOException if unable to change permissions
     */
    static void makeDirectorySecure(String dirPath) throws IOException {
        Path path = Paths.get(dirPath);
        try {
            // Try POSIX permissions first — owner read/write/execute only
            Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwx------");
            Files.setPosixFilePermissions(path, perms);
            System.out.println("FIO01-J: Directory secured with POSIX permissions: " + dirPath);
        } catch (UnsupportedOperationException e) {
            // POSIX not supported (e.g. Windows) — fall back to java.io.File
            File d = path.toFile();
            d.setReadable(false, false);
            d.setReadable(true, true);
            d.setWritable(false, false);
            d.setWritable(true, true);
            d.setExecutable(false, false);
            d.setExecutable(true, true);
            System.out.println("FIO01-J: Directory secured with fallback permissions: " + dirPath);
        }
    }

    /**
     * FIO01-J: Checks whether a file has secure permissions (owner-only read/write).
     * On POSIX systems this checks the actual permission set; on other systems
     * it verifies the file is readable and writable (best-effort).
     *
     * @param filename Path to the file to check
     * @return true if the file has owner-only permissions
     */
    static boolean hasSecurePermissions(String filename) {
        Path path = Paths.get(filename);
        if (!Files.exists(path)) {
            return false;
        }
        try {
            Set<PosixFilePermission> perms = Files.getPosixFilePermissions(path);
            boolean ownerRead  = perms.contains(PosixFilePermission.OWNER_READ);
            boolean ownerWrite = perms.contains(PosixFilePermission.OWNER_WRITE);
            boolean groupRead  = perms.contains(PosixFilePermission.GROUP_READ);
            boolean groupWrite = perms.contains(PosixFilePermission.GROUP_WRITE);
            boolean othersRead  = perms.contains(PosixFilePermission.OTHERS_READ);
            boolean othersWrite = perms.contains(PosixFilePermission.OTHERS_WRITE);
            return ownerRead && ownerWrite && !groupRead && !groupWrite && !othersRead && !othersWrite;
        } catch (UnsupportedOperationException e) {
            // POSIX not supported — best-effort check
            File f = path.toFile();
            return f.canRead() && f.canWrite();
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * FIO08-J: Safely reads all bytes from a FileInputStream.
     * Uses an int variable to capture each read() return value and checks
     * for -1 (end of stream) BEFORE narrowing to byte, so that a 0xFF byte
     * is never confused with the end-of-stream indicator.
     *
     * @param fileIn The FileInputStream to read from
     * @return A byte array containing all bytes read from the stream
     * @throws IOException if a read error occurs
     */
    static byte[] readStreamSafely(FileInputStream fileIn) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int inbuff;
        byte data;
        // Check for -1 BEFORE casting to byte
        while ((inbuff = fileIn.read()) != -1) {
            data = (byte) inbuff; // Safe to cast after checking
            buffer.write(data);
        }
        return buffer.toByteArray();
    }

    /**
     * FIO08-J: Safely reads all characters from a FileReader.
     * Uses an int variable to capture each read() return value and checks
     * for -1 (end of stream) BEFORE narrowing to char, so that a 0xFFFF
     * character is never confused with the end-of-stream indicator.
     *
     * @param reader The FileReader to read from
     * @return A String containing all characters read from the stream
     * @throws IOException if a read error occurs
     */
    static String readCharactersSafely(FileReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        int inbuff;
        char data;
        // Check for -1 BEFORE casting to char
        while ((inbuff = reader.read()) != -1) {
            data = (char) inbuff; // Safe to cast after checking
            sb.append(data);
        }
        return sb.toString();
    }

    /**
     * FIO12-J: Reads a little-endian integer from an input stream.
     * Uses ByteBuffer with explicit LITTLE_ENDIAN byte order so that data
     * exchanged between big-endian and little-endian systems is interpreted
     * correctly.
     *
     * @param input The input stream to read from
     * @return Integer value in correct byte order
     * @throws IOException if read fails or stream ends prematurely
     */
    static int readLittleEndianInt(InputStream input) throws IOException {
        byte[] buffer = new byte[4];
        int bytesRead = input.read(buffer);
        if (bytesRead != 4) {
            throw new IOException("FIO12-J: Unexpected end of stream reading int");
        }
        return ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    /**
     * FIO12-J: Writes a little-endian integer to an output stream.
     * Uses ByteBuffer with explicit LITTLE_ENDIAN byte order to ensure
     * portable data representation across different platforms.
     *
     * @param value Integer value to write
     * @param output The output stream to write to
     * @throws IOException if write fails
     */
    static void writeLittleEndianInt(int value, OutputStream output) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(value);
        output.write(buffer.array());
    }

    /**
     * FIO12-J: Reads a little-endian short from an input stream.
     *
     * @param input The input stream to read from
     * @return Short value in correct byte order
     * @throws IOException if read fails or stream ends prematurely
     */
    static short readLittleEndianShort(InputStream input) throws IOException {
        byte[] buffer = new byte[2];
        int bytesRead = input.read(buffer);
        if (bytesRead != 2) {
            throw new IOException("FIO12-J: Unexpected end of stream reading short");
        }
        return ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    /**
     * FIO12-J: Writes a little-endian short to an output stream.
     *
     * @param value Short value to write
     * @param output The output stream to write to
     * @throws IOException if write fails
     */
    static void writeLittleEndianShort(short value, OutputStream output) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort(value);
        output.write(buffer.array());
    }

    /**
     * FIO12-J: Converts an integer from big-endian to little-endian
     * using Integer.reverseBytes().
     *
     * @param bigEndianValue Integer in big-endian format
     * @return Integer in little-endian format
     */
    static int convertToLittleEndian(int bigEndianValue) {
        return Integer.reverseBytes(bigEndianValue);
    }

    /**
     * FIO12-J: Checks the current system's native byte order.
     *
     * @return true if the system is little-endian, false if big-endian
     */
    static boolean isLittleEndian() {
        return ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN;
    }

    /**
     * Saves the note to a file
     * @param note
     * @param file
     * @throws IOException
     */
    static void saveNote(User user, Note note, String file) throws IOException {
        /* FIO00-J: Validate that the file path is safe before writing */
        if (!isFileSafe(file)) {
            throw new IOException("FIO00-J: Refusing to save note to unsafe file path: " + file);
        }

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

            // Save to file as HMAC | IV | CipherLength (little-endian int) | Cipher
            /* FIO12-J: Provide methods to read and write little-endian data
            Write the ciphertext length as a little-endian integer so the file format
            is portable across systems with different native byte orders
            */
            FileOutputStream fileOut = new FileOutputStream(file);
            fileOut.write(macBytes);
            fileOut.write(iv);
            writeLittleEndianInt(notesCipher.length, fileOut);
            fileOut.write(notesCipher);
            fileOut.close();

            /* FIO01-J: Create files with appropriate access permissions
            Restrict the note file to owner-only read/write immediately after creation
            */
            makeFileSecure(file);
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
        /* FIO00-J: Validate that the file path is safe before reading */
        if (!isFileSafe(file)) {
            throw new IOException("FIO00-J: Refusing to load note from unsafe file path: " + file);
        }

        // This function involves SER04-J: Do not allow serialization and deserialization to bypass the security manager 
        // The note class has no field to actually validate, so the content is validated by confirming it was not tampered with
        // and this being the only function which can actually deserialize, thus making it unable to be bypassed;
        try {
            // Generate keys from the user
            byte[] keyBytes = Encryption.generateKeyBytes(user.getPassword());
            SecretKey encKey = new SecretKeySpec(Arrays.copyOfRange(keyBytes, 0, 32), "AES");
            SecretKey macKey = new SecretKeySpec(Arrays.copyOfRange(keyBytes, 32, 64), "HmacSHA256");

            // Reading file from disk
            /* FIO08-J: Distinguish between characters or bytes read from a stream and -1
            Use readStreamSafely() which checks for end-of-stream (-1) as an int
            BEFORE narrowing to byte, preventing 0xFF from being misinterpreted
            */
            FileInputStream fileIn = new FileInputStream(file);
            byte[] data = readStreamSafely(fileIn);
            fileIn.close();

            // Extract byte ranges
            // File format: HMAC (32 bytes) | IV (16 bytes) | CipherLength (4 bytes, little-endian) | Cipher
            byte[] macBytes = Arrays.copyOfRange(data, 0, 32);
            byte[] iv = Arrays.copyOfRange(data, 32, 48);

            /* FIO12-J: Read the ciphertext length as a little-endian integer
            so the file format is interpreted correctly regardless of native byte order
            */
            int cipherLen = ByteBuffer.wrap(data, 48, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
            byte[] notesCipher = Arrays.copyOfRange(data, 52, 52 + cipherLen);

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
