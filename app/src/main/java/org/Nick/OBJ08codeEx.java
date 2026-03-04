package org.Nick;

class outerClass {

    /**
     * X coordinate value.
     */
    private int x;

    /**
     * Y coordinate value.
     */
    private int y;
    /**
     * Private inner class that cannot be accessed
     * outside of outerClass.
     */
  private class innerClass {
    
        /**
         * Prints the (x, y) values.
         */
    private void print() {
      System.out.println("(" + x + "," + y + ")");
    } 
  }
}
public class OBJ08codeEx {
public static void main(String[] args) {
    outerClass outer = new outerClass();
    //this will NOT work becasue the inner class is private and cannot be accessed outside of the outer class
    outerClass.innerClass inner = outer.new innerClass();
    inner.print();
}
}
