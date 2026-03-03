package org.john;

import java.text.Normalizer;
import java.util.Scanner;
import java.util.regex.Pattern;

//Rule IDS01-J normalize strings before validating

public class IDS01JExample {
    
    //Pattern to block angle brackets
    private static final Pattern DANGEROUS_PATTERN = Pattern.compile("[<>]");

    /**
     * Demonstrates safe normalization and validation; catches exceptions
     * 
     * 
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("Program demonstrating IDS01-J Normalize Strings Before Validating Them.");
        System.out.println("Enter your comment (type 'quit' to exit):");

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("> ");
            String userInput = scanner.nextLine().trim();
            
            if (userInput.equalsIgnoreCase("quit")) {
                System.out.println("Exiting...");
                break;
            }

            try {
                String safeComment = validatingAndNormalizeComment(userInput);
                System.out.println("Processed result: Comment accepted: \"" + safeComment + "\"");
            } catch (IllegalStateException e) {
                System.out.println("Processed result: Comment rejected: " + e.getMessage());
            }

            System.out.println();
        }
        scanner.close();
    }

    /**
     * Normalizes the user input and validates it for dangerous content.
     * Normalization happens first using NFKC to collapse compatibility equivalents
     * and ensure canonical form. Validation is performed only on the normalized string to prevent
     * bypass attacks.
     * 
     * @param rawComment untrusted user input
     * @return the normalized, validated comment if safe
     * @throws IllegalStateException if the normalized comment contains dangerous patterns
     */
    private static String validatingAndNormalizeComment(String rawComment) {
        if (rawComment == null || rawComment.isEmpty()) {
            throw new IllegalStateException("Comment cannot be empty");
        }

        String normalized = Normalizer.normalize(rawComment, Normalizer.Form.NFKC);

        if (DANGEROUS_PATTERN.matcher(normalized).find()) {
            throw new IllegalStateException("Contains potentially dangerous content (e.g., tags or brackets)");
        }

        return normalized;
    }
}



