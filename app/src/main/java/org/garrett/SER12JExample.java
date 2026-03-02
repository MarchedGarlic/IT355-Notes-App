package org.garrett;

import java.io.*;

/// This class shows rule SER12-J by showing how untrusted data should never be allowed to be unserialized
class SER12JExample {
    // This class implements a custom ObjectInputStream that only allows deserialization of a specific class.
    static class WhitelistedObjectInputStream extends ObjectInputStream {
        public String allowedClass;

        /**
         * Constructor for WhitelistedObjectInputStream that takes an InputStream and the name of the allowed class.
         * @param inputStream
         * @param allowedClass
         * @throws IOException
         */
        public WhitelistedObjectInputStream(InputStream inputStream, String allowedClass) throws IOException {
            super(inputStream);
            this.allowedClass = allowedClass;
        }

        /**
         * Override the resolveClass method to check if the class being deserialized is the allowed class. If it is not, throw an InvalidClassException.
         * @param cls
         * @return
         * @throws IOException
         * @throws ClassNotFoundException
         */
        @Override
        protected Class<?> resolveClass(ObjectStreamClass cls) throws IOException, ClassNotFoundException {
            if (!cls.getName().equals(allowedClass)) {
                throw new InvalidClassException("Unexpected serialized class", cls.getName());
            }
            
            return super.resolveClass(cls);
        }
    }

    // This is a simple class that we will allow to be deserialized. It must implement Serializable.
    static class GoodClass1 implements Serializable {
        private static final long serialVersionUID = 1L;
        public String data;

        /**
         * Constructor for GoodClass1 that initializes the data field.
         * @param data
         */
        public GoodClass1(String data) {
            this.data = data;
        }
    }

    /**
     * This method takes a byte array as input and attempts to deserialize it using the WhitelistedObjectInputStream.
     * @param buffer
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private static Object deserialize(byte[] buffer) throws IOException, ClassNotFoundException {
        Object ret = null;
        String allowedClass = "TrustedDataOnly$GoodClass1"; // This should be set to the expected class name
        
        try (ByteArrayInputStream bais = new ByteArrayInputStream(buffer)) {
            try (WhitelistedObjectInputStream ois = new WhitelistedObjectInputStream(bais, allowedClass)) {
                ret = ois.readObject();
            }
        }

        return ret;
    }


    /**
     * Main method for testing the deserialization of a trusted class.
     * It demonstrates how to serialize an object of GoodClass1 and then deserialize it using
     * the custom ObjectInputStream that only allows GoodClass1 to be deserialized.
     * @param args
     */
    public static void main(String[] args) {
        // Example usage from serializing and deserializing an object
        try {
            // Serialize an object
            GoodClass1 obj = new GoodClass1("Hello, World!");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.close();

            byte[] serializedData = baos.toByteArray();

            // Deserialize the object
            Object deserializedObj = deserialize(serializedData);

            if (deserializedObj instanceof GoodClass1) {
                System.out.println("Deserialized data: " + ((GoodClass1) deserializedObj).data);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}