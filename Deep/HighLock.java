/**
 * LCK03-J. Do not synchronize on the intrinsic locks of high-level concurrency objects  
 * 
 * THI00-J. Do not invoke Thread.run()  (Please see HighLockTest.java for an example of the exception to this rule)
 */
package Deep;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The HighLock class demonstrates a thread-safe counter implementation using 
 * ReentrantLock to ensure that only one thread can access the critical section 
 * of code that modifies the count variable at a time. It includes methods to 
 * increment the count and retrieve the current count value, as 
 * well as a main method to test the functionality with multiple threads.
 */
public class HighLock {
    private int count = 0;
    private final Lock lock = new ReentrantLock();
	/**
	 * Increments the count value in a thread-safe manner by acquiring the lock 
	 * before modifying the count and releasing it afterward, ensuring that only one thread can modify the count at a time
	 */
    public void increment() {
        lock.lock();
        try {
            count++;
            System.out.println(Thread.currentThread().getName() + " incremented to: " + count);
        } finally {
            lock.unlock();
        }
    }

	/**
	 * Retrieves the current count value in a thread-safe manner by acquiring the 
	 * lock before accessing the count and releasing it afterward, ensuring that the 
	 * value returned is consistent and not modified by another thread during the retrieval process
	 * @return the current count value
	 */
    public int getCount() {
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }
	/**
	 * Main method to test the HighLock class with multiple threads incrementing the count
	 * and retrieving the final count value. It creates several threads that perform increments on the counter
	 * and waits for all threads to complete before printing the final count, 
	 * which should reflect the total number of increments performed by all threads.
	 * @param args command-line arguments (not used)
	 * @throws InterruptedException if any thread is interrupted while waiting for others to finish
	 */
    public static void main(String[] args) throws InterruptedException {
        HighLock counter = new HighLock();
        int numThreads = 5;
        int incrementsPerThread = 1000;
        Thread[] threads = new Thread[numThreads];

		// Create and start worker threads that increment the counter
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    counter.increment();
                }
            }, "Thread-" + i);
            threads[i].start();
        }
		// Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }
		// Print final count (should be numThreads * incrementsPerThread)
        System.out.println("Final count: " + counter.getCount());
    }
}