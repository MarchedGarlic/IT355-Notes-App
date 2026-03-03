package org.john;

// Recommendation Code: OBJ54-J Do not attempt to help the garbage collector by setting local reference variables to null

public class OBJ54JExample {

    /**
     * Shows the proper way to handle this recommendation.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        System.out.println("OBJ54-J do not attempt to help the garbage collector by setting local reference variables to null");
        example();
    }

    /**
     * 
     */
    private static void example() {
        String message = buildMessage("John");
        System.out.println(message);
    }

    /**
     * Displays a message.
     *
     * @param name user name
     * @return greeting text
     */
    private static String buildMessage(String name) {
        return "Hello, " + name + "!";
    }
}




