package org.Nick;


public class OBJ09codeEx {
    public class Obj09Example {

        /**
         * Represents a normal user.
         */
    static class User { }

    
        /**
         * Represents an admin user.
         */
    static class Admin { }

 /**
         * Checks whether the given object is exactly of type User.
         *
         * @param obj the object to check
         */
    public static void checkIfUser(Object obj) {
        if (obj.getClass() == User.class) {
            System.out.println("checkIfUser: Object is exactly a User");
        } else {
            System.out.println("checkIfUser: Object is NOT exactly a User");
        }
    }
        /**
         * Main method to test class comparison.
         *
         * @param args command-line arguments
         */

    public static void main(String[] args) {

        User normalUser = new User();
        Admin adminUser = new Admin();

        System.out.println("Testing User:");
        checkIfUser(normalUser);

        System.out.println("\nTesting Admin:");
        checkIfUser(adminUser);
    }
}

}
