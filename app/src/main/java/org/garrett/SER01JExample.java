package org.garrett;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/// This shows SER01-J by maintaining the correct signature for writeObject and readObject
class Line implements Serializable {
    // Variables
    private double length;

    /**
     * Constructor for Line class. Initializes the length of the line.
     * @param length
     */
    public Line(double length) {
        this.length = length;
    }

    /**
     * Returns the length of the line.
     * @return length
     */
    public double getLength() {
        return length;
    }

    /**
     * Sets the length of the line to the new length.
     * @param length
     */
    public void setLength(double length) {
        this.length = length;
    }

    /**
     * Writes the object to the output stream.
     * @param stream
     * @throws IOException
     */
    private void writeObject(final ObjectOutputStream stream)
            throws IOException {
        stream.defaultWriteObject();
    }

    /**
     * Reads the object from the input stream.
     * @param stream
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(final ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
    }
}

public class SER01JExample {
    /**
     * Main method to demonstrate the functionality of the Line class.
     * @param args
     */
    public static void main(String[] args) {
        Line line = new Line(5.0);
        System.out.println("Length of the line: " + line.getLength());
        line.setLength(10.0);
        System.out.println("Updated length of the line: " + line.getLength());
    }
}