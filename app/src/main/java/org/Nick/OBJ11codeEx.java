package org.Nick;

public class OBJ11codeEx {
    
    //OBJ11-J:Be wary of letting constructors throw exceptions
    /**
 * Example demonstrating safe constructor behavior.
 * Shows how making a class final helps prevent access
 * to partially initialized objects if a constructor fails.
 */
    public class Obj11Compliant {


        /**
         * A class that is safely initialized.
         */
        static final class SafeClass {
             /**
             * Message stored after successful initialization.
             */
        private final String message;
            /**
             * Creates a SafeClass object.
             *
             * @param fail if true, simulates constructor failure
             * @throws RuntimeException if initialization fails
             */
            public SafeClass(boolean fail) {
            if (fail) {
                throw new RuntimeException("Constructor failed!");
            }
            message = "Securely Initialized";
        }
             /**
             * Prints the stored message.
             */
        public void printMessage() {
            System.out.println("Message: " + message);
        
        }
    }

        /**
         * Main method demonstrating constructor safety.
         *
         * @param args command-line arguments
         */
    public static void main(String[] args) {

        try {
            // true => simulating a failure in the constructor false => simulating a successful constructor
            new SafeClass(true);
        } catch (Exception e) {
            System.out.println("Constructor failed safely.");
        }

        System.out.println("No partially initialized object can be accessed.");
    }
}
}
