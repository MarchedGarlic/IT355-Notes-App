import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * FIO12-J: Provide methods to read and write little-endian data
 * 
 * This class shows how to properly handle byte order when reading/writing data.
 */
public class FIO12 {
    
    /**
     * Reads a little-endian integer from an input stream.
     * 
     * @param input The input stream to read from
     * @return Integer value in correct byte order
     * @throws IOException if read operation fails
     */
    public static int readLittleEndianInt(InputStream input) throws IOException {
        byte[] buffer = new byte[4];
        int bytesRead = input.read(buffer);
        
        if (bytesRead != 4) {
            throw new IOException("Unexpected end of stream");
        }
        
        // Use ByteBuffer to handle byte order conversion
        return ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }
    
    /**
     * Writes a little-endian integer to an output stream.
     * 
     * @param value Integer value to write
     * @param output The output stream to write to
     * @throws IOException if write operation fails
     */
    public static void writeLittleEndianInt(int value, OutputStream output) throws IOException {
        // Create buffer and set byte order
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(value);
        
        // Write the bytes to output stream
        output.write(buffer.array());
    }
    
    /**
     * Converts an integer from big-endian to little-endian using reverseBytes.
     * 
     * @param bigEndianValue Integer in big-endian format
     * @return Integer in little-endian format
     */
    public static int convertToLittleEndian(int bigEndianValue) {
        return Integer.reverseBytes(bigEndianValue);
    }
    
    /**
     * Reads a little-endian short from an input stream.
     * 
     * @param input The input stream to read from
     * @return Short value in correct byte order
     * @throws IOException if read operation fails
     */
    public static short readLittleEndianShort(InputStream input) throws IOException {
        byte[] buffer = new byte[2];
        int bytesRead = input.read(buffer);
        
        if (bytesRead != 2) {
            throw new IOException("Unexpected end of stream");
        }
        
        return ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }
    
    /**
     * Writes a little-endian short to an output stream.
     * 
     * @param value Short value to write
     * @param output The output stream to write to
     * @throws IOException if write operation fails
     */
    public static void writeLittleEndianShort(short value, OutputStream output) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort(value);
        output.write(buffer.array());
    }
    
    /**
     * Checks the current system's byte order.
     * 
     * @return true if system is little-endian, false if big-endian
     */
    public static boolean isLittleEndian() {
        return ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN;
    }
}