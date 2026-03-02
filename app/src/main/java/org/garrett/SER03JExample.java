package org.garrett;

import java.io.*;

/// This class shows how sensitive data should always be encrypted, like rule SER03-J states
public class SER03JExample {
    static class SecurePoint implements Serializable {
        private double x; // sensitive
        private double y; // sensitive

        /**
         * Constructor for SecurePoint that initializes the x and y coordinates.
         * @param x
         * @param y
         */
        public SecurePoint(double x, double y) {
            this.x = x;
            this.y = y;
        }

        /**
         * Default constructor for SecurePoint. It is required for deserialization, but we will not use it to set any values.
         */
        public SecurePoint() {}

        /**
         * Custom serialization method that only saves non-sensitive data. In this case, we are not saving the coordinates at all.
         * @param out
         * @throws IOException
         */
        private void writeObject(ObjectOutputStream out) throws IOException {
            // Do NOT serialize coordinates
            out.defaultWriteObject(); 
            // nothing sensitive written
        }

        /**
         * Custom deserialization method that reads the non-sensitive data and resets the sensitive data to default values.
         * In this case, we are not reading any coordinates and just setting them to 0.
         * @param in
         * @throws IOException
         * @throws ClassNotFoundException
         */
        private void readObject(ObjectInputStream in)
                throws IOException, ClassNotFoundException {
            in.defaultReadObject();

            // Reset sensitive data after deserialization
            this.x = 0.0;
            this.y = 0.0;
        }

        /**
         * Override the toString method to provide a string representation of the SecurePoint.
         * This will show the coordinates, which will be (0.0, 0.0) after deserialization since we do not save them.
         */
        @Override
        public String toString() {
            return "SecurePoint(" + x + ", " + y + ")";
        }
    }

    /**
     * Main method for testing the serialization and deserialization of the SecurePoint class.
     * It demonstrates how to serialize an object of SecurePoint and then deserialize it, showing
     * that the sensitive data is not preserved.
     * @param args
     */
    public static void main(String[] args) {
        SecurePoint point = new SecurePoint(10, 20);

        try (ObjectOutputStream out =
                     new ObjectOutputStream(
                         new FileOutputStream("securePoint.ser"))) {

            out.writeObject(point);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
