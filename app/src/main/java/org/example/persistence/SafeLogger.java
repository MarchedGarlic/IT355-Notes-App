package org.example.persistence;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ERR02-J: Prevent Exceptions while logging
 * 
 * This utility class provides safe logging methods that prevent exceptions
 * from being thrown during logging operations. When an exception occurs during
 * logging, it is caught and handled promptly.
 */
public class SafeLogger {
    private static final Logger logger = Logger.getLogger(SafeLogger.class.getName());

    /**
     * Safely logs an informational message without throwing exceptions.
     * If an exception occurs during logging, it is caught and printed to stderr.
     * ERR02-J: Ensures that logging exceptions do not propagate to the caller.
     *
     * @param message The message to log
     */
    public static void safeLogInfo(String message) {
        try {
            if (message != null) {
                logger.log(Level.INFO, message);
            }
        } catch (Exception e) {
            System.err.println("Exception occurred while logging info: " + e.getMessage());
        }
    }

    /**
     * Safely logs a warning message without throwing exceptions.
     * If an exception occurs during logging, it is caught and printed to stderr.
     * ERR02-J: Ensures that logging exceptions do not propagate to the caller.
     *
     * @param message The warning message to log
     */
    public static void safeLogWarning(String message) {
        try {
            if (message != null) {
                logger.log(Level.WARNING, message);
            }
        } catch (Exception e) {
            System.err.println("Exception occurred while logging warning: " + e.getMessage());
        }
    }

    /**
     * Safely logs an error message with associated exception without throwing exceptions.
     * If an exception occurs during logging, it is caught and printed to stderr.
     * ERR02-J: Ensures that logging exceptions do not prevent error reporting.
     *
     * @param message The error message to log
     * @param throwable The exception that caused the error (may be null)
     */
    public static void safeLogError(String message, Throwable throwable) {
        try {
            if (message != null) {
                if (throwable != null) {
                    logger.log(Level.SEVERE, message, throwable);
                } else {
                    logger.log(Level.SEVERE, message);
                }
            }
        } catch (Exception e) {
            System.err.println("Exception occurred while logging error: " + e.getMessage());
            if (throwable != null) {
                System.err.println("Original error: " + throwable.getMessage());
            }
        }
    }

    /**
     * Safely logs a message with a severity level, using null-safe parameter handling.
     * If an exception occurs during logging, it is caught and printed to stderr.
     * This method is especially useful for complex logging scenarios.
     * ERR02-J: Ensures that null parameters or logging framework issues don't crash.
     *
     * @param level The logging level
     * @param message The message to log (can be null)
     */
    public static void safeLog(Level level, String message) {
        try {
            if (level != null && message != null) {
                logger.log(level, message);
            } else if (level != null) {
                logger.log(level, "[No message provided]");
            }
        } catch (Exception e) {
            System.err.println("Exception occurred while logging at level " + 
                             (level != null ? level.getName() : "UNKNOWN") + 
                             ": " + e.getMessage());
        }
    }
}
