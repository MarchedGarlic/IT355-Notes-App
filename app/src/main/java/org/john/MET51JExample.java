package org.john;

// Recommendation Code: MET51-J Do not use overloaded methods to differentiate between runtime types

public class MET51JExample {

    /**
     * Shows the proper way to handle MET51-J, using method overriding to ensure behavior follows runtime type.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        System.out.println("MET51-J MET51-J Do not use overloaded methods to differentiate between runtime types");
        runExample();
    }

    /**
     * use overriding so behavior follows runtime type.
     */
    private static void runExample() {
        Person pet = new John();
        pet.describe();
    }

    /**
     * Base type
     */
    private static class Person {
        /**
         * Prints
         */
        void describe() {
            System.out.println("I am a Person");
        }
    }

    /**
     * Subtype used.
     */
    private static class John extends Person {
        /**
         * Prints a specific description.
         */
        @Override
        void describe() {
            System.out.println("My name is John and I am a Person");
        }
    }
}
