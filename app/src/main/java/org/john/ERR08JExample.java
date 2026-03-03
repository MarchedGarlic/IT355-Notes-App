package org.john;

import java.util.Objects;

//Rule ERR08-J Do not catch NullPointerExceptions or any of its ancestors

public class ERR08JExample {

    /**
     * Runs three cases showing compliant null-handling patterns and prints results
     * 
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("ERR08-J Rule Do not catch NullPointerExceptions or any of its ancestors");

        System.out.println("\nCase 1 - Safe with explicit null check:");
        printSafe(null);
        printSafe("Matthew");

        System.out.println("\nCase 2 - Required name:");
        try {
            printRequired("Nick");
            printRequired(null);
        } catch (NullPointerException e) {
            System.out.println("Missing required name: " + e.getMessage());
        }

        System.out.println("\nCase 3 - Fail-fast name!=null:");
        try {
            printStrict("John");
            printStrict(null);
        } catch (NullPointerException e) {
            System.out.println("Null name passed (bug!): " + e.getMessage());
        }

        System.out.println("\nNo NullPointerException was caught to hide bugs.");
    }

    /**
     * Prints a greeting if name is provided, or a default message if the name is null or empty
     * 
     * @param name The name
     */
    private static void printSafe(String name) {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Hello, guest! (No name provided)");
            return;
        }
        System.out.println("Hello, " + name + "!");
    }

    /**
     * Prints a welcome back message 
     * if null is passed it throws a NullPointerException with descriptive message
     * 
     * @param name the name
     * @throws NullPointerException if name is null
     */
    private static void printRequired(String name) {
        Objects.requireNonNull(name, "Name is required");
        System.out.println("Welcome back, " + name + "!");
    }

    /**
     * Prints an uppercase greeting using the provided name
     * this method has a precondition that the name must not be null
     * 
     * @param name the name
     * @throws NullPointerException if name is null
     */
    private static void printStrict(String name) {
        System.out.println("Hi there, " + name.toUpperCase() + "!");
    }
}