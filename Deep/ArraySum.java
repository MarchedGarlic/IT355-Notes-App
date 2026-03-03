/**
 * LCK08-J. Ensure actively held locks are released on exceptional conditions  
 * 
 */


package Deep;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The ArraySum class demonstrates a thread-safe implementation of a method that sums elements of an array up to a specified index.
 * It uses a ReentrantLock to ensure that the critical section of code that accesses the array is executed by only one thread at a time, 
 * preventing concurrent modification issues. 
 * The main method tests the sumUpTo method by intentionally providing an out-of-bounds index, 
 * which will throw an exception and verify that the lock is properly released even when an error occurs. 
 * This ensures that the lock is not left in a locked state, 
 * allowing other threads to continue functioning without being blocked indefinitely.
 */
public final class ArraySum {
    private final int[] numbers = {1, 2, 3, 4, 5};
    private final Lock lock = new ReentrantLock();

    // Sums elements up to the given index, throws exception if index is out of bounds
    public int sumUpTo(int index) {
        lock.lock();
        System.out.println("Lock acquired.");
        try {
            int sum = 0;
            for (int i = 0; i <= index; i++) {
                sum += numbers[i];
            }
            return sum;
        } finally {
            lock.unlock();
            System.out.println("Lock released.");
        }
    }

    public static void main(String[] args) {
        ArraySum arraySum = new ArraySum();
        try {
            // This will throw ArrayIndexOutOfBoundsException and test lock release
            int result = arraySum.sumUpTo(10);
            System.out.println("Sum: " + result);
        } catch (Exception e) {
            System.out.println("Caught exception: " + e);
        }
    }
}