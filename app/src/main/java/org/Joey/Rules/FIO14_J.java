import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * FIO14-J: Perform proper cleanup at program termination
 * 
 * This class shows how to properly clean up resources when programs terminate.
 */
public class FIO14_J {
    
    private static PrintStream logFile = null;
    
    /**
     * Creates a log file with proper cleanup on program termination.
     * 
     * @param filename Name of the log file to create
     * @throws IOException if file creation fails
     */
    public static void createSecureLog(String filename) throws IOException {
        logFile = new PrintStream(new FileOutputStream(filename));
        
        // Add shutdown hook to ensure file is closed
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (logFile != null) {
                System.out.println("Shutdown hook: Closing log file");
                logFile.close();
            }
        }));
        
        System.out.println("Log file created with shutdown hook");
    }
    
    /**
     * Safely writes to log file and ensures proper cleanup.
     * 
     * @param message Message to write to log
     */
    public static void writeToLog(String message) {
        if (logFile != null) {
            logFile.println(message);
            logFile.flush(); // Ensure data is written immediately
        }
    }
    
    /**
     * Manually closes resources before program termination.
     */
    public static void manualCleanup() {
        if (logFile != null) {
            System.out.println("Manual cleanup: Closing log file");
            logFile.close();
            logFile = null;
        }
    }
    
    /**
     * Demonstrates proper termination with cleanup.
     * 
     * @param exitCode Exit code for the program
     */
    public static void safeExit(int exitCode) {
        try {
            // Perform manual cleanup first
            manualCleanup();
            
            // Use exit() instead of halt() to allow shutdown hooks to run
            System.out.println("Exiting safely...");
            Runtime.getRuntime().exit(exitCode);
            
        } catch (Exception e) {
            System.err.println("Error during cleanup: " + e.getMessage());
            // Force exit if cleanup fails
            Runtime.getRuntime().halt(1);
        }
    }
    
    /**
     * Example of unsafe termination (for comparison).
     * This method shows what NOT to do.
     */
    public static void unsafeExit() {
        // DON'T DO THIS - halt() skips cleanup
        System.out.println("Unsafe exit - skipping cleanup");
        Runtime.getRuntime().halt(1);
    }
    
    /**
     * Main method demonstrating proper cleanup techniques.
     */
    public static void main(String[] args) {
        try {
            // Create log file with cleanup hook
            createSecureLog("application.log");
            
            // Write some data
            writeToLog("Application started");
            writeToLog("Processing data...");
            
            // Simulate some work
            Thread.sleep(1000);
            
            writeToLog("Application completed");
            
            // Terminate safely
            safeExit(0);
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            safeExit(1);
        }
    }
}
