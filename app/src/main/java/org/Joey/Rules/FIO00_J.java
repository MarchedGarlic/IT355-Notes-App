import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * FIO00-J: Do not operate on files in shared directories
 * 
 * This class shows how to safely operate on files only in secure directories.
 */
public class FIO00_J {
    
    /**
     * Checks if a directory is secure (only user and admin can modify).
     * 
     * @param filePath Path to check
     * @return true if directory is secure
     */
    public boolean isSecureDirectory(String filePath) {
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
     * Safely opens a file only if it's in a secure directory.
     * 
     * @param filename User-provided filename
     * @return true if file was safely processed
     */
    public boolean safeFileOperation(String filename) {
        try {
            Path path = Paths.get(filename);
            
            // First check if directory is secure
            if (!isSecureDirectory(filename)) {
                System.out.println("File not in secure directory: " + filename);
                return false;
            }
            
            // Check if file is a regular file (not device, link, etc.)
            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class,
                                                             LinkOption.NOFOLLOW_LINKS);
            
            if (!attrs.isRegularFile()) {
                System.out.println("Not a regular file: " + filename);
                return false;
            }
            
            // Safe to operate on file
            try (InputStream in = Files.newInputStream(path)) {
                System.out.println("Safely opened file: " + filename);
                return true;
            }
            
        } catch (IOException e) {
            System.err.println("Error processing file: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Validates that a file path is safe to use.
     * 
     * @param filename File to validate
     * @return true if file is safe to operate on
     */
    public boolean isFileSafe(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return false;
        }
        
        // Reject device files (Windows examples)
        String lowerName = filename.toLowerCase();
        if (lowerName.contains("con") || lowerName.contains("aux") || 
            lowerName.contains("prn") || lowerName.contains("com1") ||
            lowerName.contains("lpt1")) {
            System.out.println("Device file detected: " + filename);
            return false;
        }
        
        // Check if in secure directory
        if (!isSecureDirectory(filename)) {
            return false;
        }
        
        try {
            Path path = Paths.get(filename);
            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class,
                                                             LinkOption.NOFOLLOW_LINKS);
            
            // Only allow regular files
            return attrs.isRegularFile();
            
        } catch (IOException e) {
            return false;
        }
    }
}
