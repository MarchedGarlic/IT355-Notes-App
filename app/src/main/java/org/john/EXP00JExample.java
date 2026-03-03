package org.john;

public class EXP00JExample {
//Rule EXP00-J Do not ignore any values returned by methods

    /**
     * Demonstrates correct handling of String.replace() return value
     * 
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("This program shows off that we should not ignore values returned after methods");
        
        String original = "What is going on chat";

        System.out.println("Original string: " + original);
        String modified = original.replace('o', '0');
        System.out.println("After replacement: " + modified);


        System.out.println("return value was properly used.");
    }
}
