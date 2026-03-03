/**
 * LCK01-J: Do not synchronize on objects that may be reused 
 */
package Deep;

/**
 * A thread-safe counter implementation that allows multiple threads to increment and retrieve the count
 */
public class Counter {
	private int count = 0;
	// Lock object to synchronize access to the count variable
	private final Object lock = new Object();

	/**
	 * Increments the count value in a thread-safe manner
	 */
	public void increment() {
		synchronized (lock) {
			count++;
		}
	}

	/**
	 * Retrieves the current count value in a thread-safe manner
	 * @return the current count value
	 */
	public int getCount() {
		synchronized (lock) {
			return count;
		}
	}
	/**
	 * Main method to test the Counter class with multiple threads incrementing the count
	 * and retrieving the final count value
	 * @param args command-line arguments (not used)
	 * @throws InterruptedException if any thread is interrupted while waiting for others to finish
	 */
	public static void main(String[] args) throws InterruptedException {
		Counter counter = new Counter();
		int numThreads = 5;
		int incrementsPerThread = 1000;

		// Create and start worker threads
		Thread[] threads = new Thread[numThreads];
		for (int i = 0; i < numThreads; i++) {
			threads[i] = new Thread(() -> {
				// Each thread increments the counter 1000 times
				for (int j = 0; j < incrementsPerThread; j++) {
					counter.increment();
				}
			});
			threads[i].start();
		}

		// Wait for all threads to complete
		for (int i = 0; i < numThreads; i++) {
			threads[i].join();
		}

		// Print final count (should be 5000)
		System.out.println("Final count: " + counter.getCount());
	}
}