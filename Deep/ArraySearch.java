/**
 * ERR52-J. Avoid in-band error indicators  
 */

package Deep;
// did not know this exception existed :O
import java.util.NoSuchElementException;

/**
 * The ArraySearch class provides a method to safely search for a target value in an array of integers.
 * 
 */
public class ArraySearch {

	/**
	 * Searches for the target value in the given array and returns its index if found.
	 * @param arr the array of integers to search through
	 * @param target the integer value to search for in the array
	 * @return the index of the target value in the array if found
	 * @throws NoSuchElementException if the target value is not found in the array
	 */  
    public static int indexOfSafe(int[] arr, int target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) return i;
        }
        throw new NoSuchElementException("Target not found: " + target);
    }

	/**
	 * Main method to test the indexOfSafe function by searching for a specific target value in a predefined array of integers.
	 * @param args command-line arguments (not used)
	 */
    public static void main(String[] args) {
        int[] numbers = {3, 7, 9, 12, 15};
		System.out.print("Array: [");

		for (int num : numbers) {
			System.out.print(num + " ");
		}
		System.out.println("]");

        try {
			System.out.println("Searching for target 9...");
            int idx = indexOfSafe(numbers, 9);
            System.out.println("found target at index: " + idx);
        } catch (NoSuchElementException e) {
            System.out.println("Not found: " + e.getMessage());
        }
    }
}