import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * FIO08-J: Distinguish between characters or bytes read from a stream and -1
 * 
 * This class shows how to properly check for end-of-stream before casting to byte/char.
 */
public class FIO08 {
    
    /**
     * Safely reads bytes from a stream, checking for end-of-stream properly.
     * 
     * @param filename File to read from
     * @throws IOException if read operation fails
     */
    public static void readBytesCorrectly(String filename) throws IOException {
        try (FileInputStream in = new FileInputStream(filename)) {
            int inbuff;
            byte data;
            
            // Check for -1 BEFORE casting to byte
            while ((inbuff = in.read()) != -1) {
                data = (byte) inbuff;  // Safe to cast after checking
                System.out.printf("Read byte: 0x%02X%n", data & 0xFF);
            }
            
            System.out.println("End of stream reached");
        }
    }
    
    /**
     * Safely reads characters from a stream, checking for end-of-stream properly.
     * 
     * @param filename File to read from
     * @throws IOException if read operation fails
     */
    public static void readCharactersCorrectly(String filename) throws IOException {
        try (FileReader in = new FileReader(filename)) {
            int inbuff;
            char data;
            
            // Check for -1 BEFORE casting to char
            while ((inbuff = in.read()) != -1) {
                data = (char) inbuff;  // Safe to cast after checking
                System.out.printf("Read character: '%c' (0x%04X)%n", data, (int) data);
            }
            
            System.out.println("End of stream reached");
        }
    }
    
    /**
     * Demonstrates incorrect byte reading (what NOT to do).
     * This method will fail if it encounters a 0xFF byte.
     * 
     * @param filename File to read from
     * @throws IOException if read operation fails
     */
    public static void readBytesIncorrectly(String filename) throws IOException {
        try (FileInputStream in = new FileInputStream(filename)) {
            byte data;
            
            // WRONG: Casting before checking for -1
            // This will fail if 0xFF byte is encountered
            while ((data = (byte) in.read()) != -1) {
                System.out.printf("Read byte: 0x%02X%n", data & 0xFF);
            }
        }
    }
    
    /**
     * Safely processes a single byte, checking for end-of-stream.
     * 
     * @param in Input stream to read from
     * @return true if byte was read, false if end-of-stream
     * @throws IOException if read operation fails
     */
    public static boolean processSingleByte(FileInputStream in) throws IOException {
        int inbuff = in.read();
        
        if (inbuff == -1) {
            return false;  // End of stream
        }
        
        byte data = (byte) inbuff;
        System.out.printf("Processed byte: 0x%02X%n", data & 0xFF);
        return true;
    }
    
    /**
     * Safely processes a single character, checking for end-of-stream.
     * 
     * @param in Input stream to read from
     * @return true if character was read, false if end-of-stream
     * @throws IOException if read operation fails
     */
    public static boolean processSingleChar(FileReader in) throws IOException {
        int inbuff = in.read();
        
        if (inbuff == -1) {
            return false;  // End of stream
        }
        
        char data = (char) inbuff;
        System.out.printf("Processed character: '%c'%n", data);
        return true;
    }
}