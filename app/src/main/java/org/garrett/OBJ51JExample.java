package org.garrett;

/// This class shows how only needed fields should be exposed, assume minimal accessibility possible
public class OBJ51JExample {
    // This class is only needed within this main class, so we can make it private to minimize accessibility.
    private static final class A {
        private int x = 10;

        /**
         * This method avoids an accessibility flag
         */
        void display() {
            System.out.println("Value of x: " + x);
        }

        /**
         * This method allows us to modify x while keeping it encapsulated within the class.
         * @param x
         */
        void setX(int x) {
            this.x = x;
        }
    }

    /**
     * The main method to test the functionality of class A.
     * @param args
     */
    public static void main(String[] args) {
        A a = new A();
        a.display(); // This will work because we are within the same class.
        a.setX(20);
        a.display(); // This will show the updated value of x.
    }
}
