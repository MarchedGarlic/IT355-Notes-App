package org.garrett;

/// This class shows how you should always work with the garbage collector and java quirks
public class OBJ52JExample {
    // Short lived immutable object
    public static class ShortLivedObject {
        private final String data;

        public ShortLivedObject(String data) {
            this.data = data;
        }

        public String getData() {
            return data;
        }
    }


    /**
     * Main method to demonstrate garbage collection friendly code.
     * @param args
     */
    public static void main(String[] args) {
        // Short lived objects that are created and discarded quickly
        for (int i = 0; i < 10; i++) {
            final ShortLivedObject obj = new ShortLivedObject("Data " + i);
            System.out.println(obj.getData());
        }

        // Avoid creating large objects
        // int[] largeArray = new int[1000000]; // This is a large object

        // Do not explicitly call the garbage collector
        // System.gc(); // This is not recommended as it can lead to performance issues.
    }
}
