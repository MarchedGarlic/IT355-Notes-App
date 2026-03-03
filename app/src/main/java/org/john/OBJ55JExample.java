package org.john;

import java.util.ArrayList;
import java.util.List;

// Recommendation Code: OBJ55-J Remove short-lived objects from long-lived container objects

public class OBJ55JExample {

    private static final List<String> longLivedContainer = new ArrayList<>();

    /**
     * Runs the OBJ55-J example
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        System.out.println("OBJ55-J Remove short-lived objects from long-lived container objects");
        runExample();
        System.out.println("Container size after call: " + longLivedContainer.size());
    }

    /**
     * Remove the short-lived object when done.
     */
    private static void runExample() {
        String temporaryToken = "token-456";
        longLivedContainer.add(temporaryToken);

        try {
            System.out.println("Using temporary token: " + temporaryToken);
        } finally {
            longLivedContainer.remove(temporaryToken);
            System.out.println("Temporary token removed.");
        }
    }
}
