/**
 * ERR50-J. Use exceptions only for exceptional conditions  
 * 
 */

package Deep;
/**
 * The StringArt class demonstrates the use of loops to create simple ASCII art patterns in the console.
 * It includes a pyramid pattern made of asterisks and a box with a text border. 
 * The main method contains the logic for generating these patterns, 
 * showcasing how nested loops can be used to control the layout of characters in the output.
 */
public class StringArt {
	/**
	 * Main method to generate and display ASCII art patterns, including a pyramid and a box with a text border.
	 * @param args command-line arguments (not used)
	 */
    public static void main(String[] args) {
        int height = 6;

        // Pyramid made with loops
        for (int i = 1; i <= height; i++) {
            for (int s = 0; s < height - i; s++) {
                System.out.print(" ");
            }
            for (int j = 0; j < 2 * i - 1; j++) {
                System.out.print("*");
            }
            System.out.println();
        }

        // Box with text border
        String text = " LOOP ART ";
        int width = text.length() + 4;

		// Print the top border of the box
        for (int i = 0; i < width; i++) System.out.print("#");
        System.out.println();

        System.out.println("#" + text + "#");
		
		// Print the bottom border of the box
        for (int i = 0; i < width; i++) System.out.print("#");
        System.out.println();
    }
}