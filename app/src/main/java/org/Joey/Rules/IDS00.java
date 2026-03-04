import java.util.regex.Pattern;

/**
 * IDS00-J: Prevent SQL injection vulnerabilities
 * 
 * This class validates and cleans user input to prevent SQL injection attacks.
 */
public class IDS00 {
    
    // SQL injection patterns to detect
    private static final Pattern[] SQL_INJECTION_PATTERNS = {
        Pattern.compile("('|(\\-\\-)|(;)|(\\|))", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\b(union|select|insert|update|delete|drop|create|alter|exec|execute)\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(\\bor\\b|\\band\\b)\\s*[=<>]", Pattern.CASE_INSENSITIVE)
    };
    
    
    /**
     * Checks whether the supplied input contains common SQL injection patterns.
     *
     * @param userInput The raw input to evaluate
     * @return {@code true} if no injection patterns are detected; {@code false} otherwise
     * @throws IllegalArgumentException if {@code userInput} is {@code null}
     */
    public boolean isSqlSafe(String userInput) {
        if (userInput == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        
        // Check against SQL injection patterns
        for (Pattern pattern : SQL_INJECTION_PATTERNS) {
            if (pattern.matcher(userInput).find()) {
                return false; // SQL injection pattern detected
            }
        }
        
        return true; // Input appears safe from SQL injection
    }
    
    
    /**
     * Removes characters and keywords commonly used in SQL injection attempts.
     *
     * @param userInput The raw input to clean
     * @return A sanitized version of the input with risky tokens removed
     * @throws IllegalArgumentException if {@code userInput} is {@code null}
     */
    public String sanitizeSqlInput(String userInput) {
        if (userInput == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        
        // Remove SQL injection characters
        String cleaned = userInput.replaceAll("[';\"\\-\\-]", "");
        
        // Remove SQL keywords (case insensitive)
        cleaned = cleaned.replaceAll("(?i)\\b(union|select|insert|update|delete|drop|create|alter|exec|execute)\\b", "");
        
        // Remove dangerous operators used in SQL injection
        cleaned = cleaned.replaceAll("(?i)(\\bor\\b|\\band\\b)\\s*[=<>]", "");
        
        return cleaned.trim();
    }
    
    /**
     * Validates input for SQL injection patterns and sanitizes it if safe.
     *
     * @param userInput The input to process
     * @return Sanitized input if safe, {@code null} if empty or malicious
     */
    public String processSqlInput(String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            return null;
        }
        
        // First check if input is safe
        if (!isSqlSafe(userInput)) {
            System.err.println("SQL injection attempt detected: " + userInput);
            return null; // Reject potentially malicious input
        }
        
        // Sanitize for extra protection
        String sanitized = sanitizeSqlInput(userInput);
        
        System.out.println("Original: " + userInput);
        System.out.println("Processed: " + sanitized);
        return sanitized;
    }
}