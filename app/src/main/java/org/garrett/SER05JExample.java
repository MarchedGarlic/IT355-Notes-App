package org.garrett;

import java.io.Serializable;

/// This Class shows how an inner class should not be serializable
public class SER05JExample implements Serializable {
    // Inner classes
    public class InnerClass {
        private int innerField;

        public InnerClass(int innerField) {
            this.innerField = innerField;
        }

        public int getInnerField() {
            return innerField;
        }
    }

    // Variables
    private int outerField;

    /**
     * Constructor for DontSerializeInnerClasses
     * @param outerField
     */
    public SER05JExample(int outerField) {
        this.outerField = outerField;
    }

    /**
     * Getter for outerField
     * @return
     */
    public int getOuterField() {
        return outerField;
    }

    public static void main(String[] args) {
        SER05JExample outer = new SER05JExample(10);
        InnerClass inner = outer.new InnerClass(20);

        System.out.println("Outer Field: " + outer.getOuterField());
        System.out.println("Inner Field: " + inner.getInnerField());

        // Serialization and deserialization example
        try {
            // Serialize
            java.io.FileOutputStream fileOut = new java.io.FileOutputStream("dontSerializeInnerClasses.ser");
            java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(fileOut);
            out.writeObject(outer);
            out.close();
            fileOut.close();

            // Deserialize
            java.io.FileInputStream fileIn = new java.io.FileInputStream("dontSerializeInnerClasses.ser");
            java.io.ObjectInputStream in = new java.io.ObjectInputStream(fileIn);
            SER05JExample deserializedOuter = (SER05JExample) in.readObject();
            in.close();
            fileIn.close();

            System.out.println("Deserialized Outer Field: " + deserializedOuter.getOuterField());
        } catch (java.io.IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
