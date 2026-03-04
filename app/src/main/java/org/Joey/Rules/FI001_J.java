import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.HashSet;
import java.util.Set;

/**
 * FIO01-J: Create files with appropriate access permissions
 * 
 * This class demonstrates secure file creation with proper permission controls.
 */
public class FI001_J {
    
    /**
     * Creates a secure file with restricted permissions.
     * Only the owner can read and write to the file.
     * 
     * @param filename Name of the file to create
     * @throws IOException if file creation fails
     */
    public void createSecureFile(String filename) throws IOException {
        Path file = Paths.get(filename);
        
        // Set file creation options
        Set<StandardOpenOption> options = new HashSet<>();
        options.add(StandardOpenOption.CREATE_NEW);  // Fail if file exists
        options.add(StandardOpenOption.WRITE);
        
        // Set secure permissions - only owner can read/write
        Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rw-------");
        FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);
        
        // Create file with secure permissions
        try (SeekableByteChannel channel = Files.newByteChannel(file, options, attr)) {
            System.out.println("Secure file created: " + filename);
        }
    }
    
    /**
     * Creates a file that can be read by owner and group members.
     * Demonstrates different permission levels.
     * 
     * @param filename Name of the file to create
     * @throws IOException if file creation fails
     */
    public void createGroupReadableFile(String filename) throws IOException {
        Path file = Paths.get(filename);
        
        // File creation options
        Set<StandardOpenOption> options = new HashSet<>();
        options.add(StandardOpenOption.CREATE_NEW);
        options.add(StandardOpenOption.WRITE);
        
        // Owner read/write, group read-only, others no access
        Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rw-r-----");
        FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);
        
        // Create file with group-readable permissions
        try (SeekableByteChannel channel = Files.newByteChannel(file, options, attr)) {
            System.out.println("Group-readable file created: " + filename);
        }
    }
    
    /**
     * Validates that a file has secure permissions.
     * Checks if file is readable/writable only by owner.
     * 
     * @param filename Name of the file to check
     * @return true if file has secure permissions
     * @throws IOException if unable to read file permissions
     */
    public boolean hasSecurePermissions(String filename) throws IOException {
        Path file = Paths.get(filename);
        
        if (!Files.exists(file)) {
            return false;
        }
        
        // Get current file permissions
        Set<PosixFilePermission> perms = Files.getPosixFilePermissions(file);
        
        // Check that only owner has read/write access
        boolean ownerRead = perms.contains(PosixFilePermission.OWNER_READ);
        boolean ownerWrite = perms.contains(PosixFilePermission.OWNER_WRITE);
        boolean groupRead = perms.contains(PosixFilePermission.GROUP_READ);
        boolean groupWrite = perms.contains(PosixFilePermission.GROUP_WRITE);
        boolean othersRead = perms.contains(PosixFilePermission.OTHERS_READ);
        boolean othersWrite = perms.contains(PosixFilePermission.OTHERS_WRITE);
        
        // Secure = owner can read/write, but group and others cannot
        return ownerRead && ownerWrite && !groupRead && !groupWrite && !othersRead && !othersWrite;
    }
    
    /**
     * Sets secure permissions on an existing file.
     * Changes file to be readable/writable only by owner.
     * 
     * @param filename Name of the file to secure
     * @throws IOException if unable to change permissions
     */
    public void makeFileSecure(String filename) throws IOException {
        Path file = Paths.get(filename);
        
        // Set secure permissions on existing file
        Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rw-------");
        Files.setPosixFilePermissions(file, perms);
        
        System.out.println("File secured: " + filename);
    }
}
