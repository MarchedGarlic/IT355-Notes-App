package org.Nick;

package org.Nick;

public class OBJ10codeEx {
   
    static final class GlobalSettings {
        /**
     * Stores global application settings.
     */
        public final static String currentUser = "Admin";
    }
 /**
     * Main method of the program.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {

        System.out.println("Before attack: " + GlobalSettings.currentUser);

        GlobalSettings.currentUser = "Hacker";

        System.out.println("After attack: " + GlobalSettings.currentUser);
    }
}


