package org.john;

import java.util.logging.Level;
import java.util.logging.Logger;

//Rule ERR02-J Prevent Exceptions while logging data

public class ERR02Example {


    private static final Logger logger = Logger.getLogger(ERR02Example.class.getName());

    /**
     * Main method that simulates user login attempts and logs failures securely.
     *
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("Simulating user login attempts...");

        attemptLogin("John", "correctPass");

        attemptLogin("Nick", "wrongPass");

        System.out.println("All attempts processed. Check logs for security events.");
    }

    /**
     * Tries to authenticate a user and logs failures safely
     * 
     * @param username the username attempting login
     * @param password the provided password
     */
    private static void attemptLogin(String username, String password) {
        boolean success = "correctPass".equals(password);

        if (success) {
            System.out.println("Login successful for " + username);
        } else {
            String logMessage = "Failed login attempt for user: " + username;
            logger.log(Level.WARNING, logMessage, new SecurityException("Invalid credentials"));

            System.out.println("Login failed for " + username + "- event safely logged.");
        }
    }
}

