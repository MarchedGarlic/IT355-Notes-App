package org.john;

import java.util.Scanner;
import java.util.regex.Pattern;

//Rule IDS11-J Perform any string modifications before validation

public class IDS11JExample {
    
    private static final Pattern VALID_STRING = Pattern.compile("^[a-zA-Z0-9_]+$");

    /**
     * Runs a loop that accepts user input which will be cleaned according to IDS11-J, and displays the result.
     *
     * 
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("This program will showcase string modifications before validation.");
        System.out.println("Enter a string (type 'quit' to exit):");

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("> ");
            String userInput = scanner.nextLine();

            if (userInput.equalsIgnoreCase("quit")) {
                System.out.println("Exiting...");
                break;
            }

            try {
                String cleanString = cleanAndValidateString(userInput);
                System.out.println("String accepted and cleaned: \"" + cleanString + "\"");
            } catch (IllegalArgumentException e) {
                System.out.println("Username rejected: " + e.getMessage());
            }

            System.out.println();
        }
        scanner.close();
    }

    /**
     * Cleans the raw username input by performing all modifications first, then validates the final cleaned string
     * Modifications happen before validation checks to ensure that no bypass is possible
     * 
     * @param rawString the raw, untrusted string
     * @return the cleaned and validated username
     * @throws IllegalArgumentException if the cleaned username is invalid 
     */
    private static String cleanAndValidateString(String rawString) {
        if (rawString == null) {
            throw new IllegalArgumentException("String cannot be null");
        }

        String cleaned = rawString.trim();

        cleaned = cleaned.replaceAll("[\\p{Cc}\\p{Cn}]", "");

        if (cleaned.length() < 3 || cleaned.length() > 20) {
            throw new IllegalArgumentException("String must be 3-20 characters long after cleaning");
        }

        if (!VALID_STRING.matcher(cleaned).matches()) {
            throw new IllegalArgumentException("String must contain only letters, digits, or underscores after cleaning");
        }

        return cleaned;
    }
}
