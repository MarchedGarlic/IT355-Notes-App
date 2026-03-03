/**
 * LCK02-J. Do not synchronize on the class object returned by getClass()  
 * 
 * Please run with Base.java
 */

package Deep;
/**
 * The Derived class extends the Base class and provides a thread-safe method to decrement the sharedValue variable.
 * It also contains a main method to test the incrementing and decrementing of sharedValue using multiple threads.
 */
public class Derived extends Base {
	/**
	 * Atomically decrements the sharedValue by 1 in a thread-safe manner using synchronization on the Base class object
	 */
    public static void decrementSharedValue() {
        synchronized (Base.class) {
            sharedValue--;
            System.out.println(Thread.currentThread().getName() + " decremented value to: " + sharedValue);
        }
    }

	/**
	 * Main method to test the incrementing and decrementing of sharedValue using multiple threads
	 * It creates threads that increment and decrement the sharedValue and waits for them to finish before printing the final value
	 * @param args command-line arguments (not used)
	 * @throws InterruptedException if any thread is interrupted while waiting for others to finish
	 */
    public static void main(String[] args) throws InterruptedException {
        int numThreads = 5;
        int incrementsPerThread = 1000;
        int decrementsPerThread = 1000;
        Thread[] incThreads = new Thread[numThreads];
        Thread[] decThreads = new Thread[numThreads];
		// Create and start incrementing and decrementing threads
        for (int i = 0; i < numThreads; i++) {
            incThreads[i] = new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    Base.incrementSharedValue();
                }
            }, "Inc-Thread-" + i);
            incThreads[i].start();

            decThreads[i] = new Thread(() -> {
                for (int j = 0; j < decrementsPerThread; j++) {
                    Derived.decrementSharedValue();
                }
            }, "Dec-Thread-" + i);
            decThreads[i].start();
        }

		// Wait for all threads to complete
        for (Thread thread : incThreads) {
            thread.join();
        }
        for (Thread thread : decThreads) {
            thread.join();
        }
		//Should be 0 since increments and decrements are equal 
        System.out.println("Final shared value: " + Base.getSharedValue());
    }
}