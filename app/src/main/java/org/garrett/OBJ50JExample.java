package org.garrett;

/// This class shows how you should not mix reference types, recommendation OBJ50-J
public class OBJ50JExample {
    static class Line {
        // Variables
        private final float length;

        /**
         * Constructor for Line that initializes the length of the line. The length is a final
         * variable, meaning it cannot be changed after it is set in the constructor.
         * @param length
         */
        public Line(float length) {
            this.length = length;
        }

        /**
         * Getter for the length of the line. This method allows access to the length
         * of the line, which is a final variable and cannot be changed after initialization.
         * @return the length of the line
         */
        public float getLength() {
            return length;
        }
    }


    /**
     * Main method to demonstrate the usage of the Line class. It creates a Line object with a
     * specified length and prints it out.
     * @param args
     */
    public static void main(String[] args) {
        final Line line = new Line(5.0f);
        System.out.println("Length of the line: " + line.getLength());
    }
}
