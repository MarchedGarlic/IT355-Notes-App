package org.Nick;

public class OBJ13codeEx {
          /**
     * Represents an inventory that stores items.
     */
    static class Inventory {
        private String[] items = {"item1", "item2", "item3"};


        /**
         * Array storing inventory items.
         */
        public String[] getItems() {
             /**
         * Returns a copy of the items array to protect internal state.
         *
         *  @return a cloned copy of the items array
         */
            return items.clone();
        }
    }

    public static void main(String[] args) {

        Inventory inventory = new Inventory();

        // Get copy of array
        String[] copy = inventory.getItems();

        // Modify the copy
        copy[0] = "BADITEM";

        // Internal state remains safe
        System.out.println("After modification attempt:");
        for (String item : inventory.getItems()) {
            System.out.println(item);
        }
    }
}

