/**
 * LCK06-J. Do not use an instance lock to protect shared static data  
 * 
 */

package Deep;
/**
 * The SharedLogger class demonstrates a thread-safe logging mechanism where multiple threads can log messages concurrently without
 * interfering with each other's log counts. It uses a static lock to synchronize access to the total log count and instance log count, 
 * ensuring that the logging operations are thread-safe. 
 * The main method creates multiple threads that log messages and prints the total number of log messages at the end.
 */
public final class SharedLogger implements Runnable {
    private static int totalLogCount = 0;
    private static final Object staticLock = new Object();

    private int instanceLogCount = 0;
    private final String loggerName;

	/**
	 * Constructor to initialize the SharedLogger with a specific logger name, which is used to identify the logger in the log messages.
	 * @param loggerName the name of the logger, used for identifying log messages from different instances of SharedLogger
	 */
    public SharedLogger(String loggerName) {
        this.loggerName = loggerName;
    }

	/**
	 * The run method is the entry point for the thread execution, where it logs a series of messages in a loop.
	 * It calls the logMessage method to log each message, which is synchronized to ensure thread safety when updating the log counts.
	 * After logging the messages, it prints the total number of messages logged by this instance of SharedLogger.
	 * The total log count across all instances is also updated in a thread-safe manner
	 * and printed at the end of the main method to show the total number of log messages from all threads.
	 */
    @Override
    public void run() {
        for (int i = 0; i < 500; i++) {
            logMessage("Message #" + i);
        }
        System.out.println(loggerName + " logged " + instanceLogCount + " messages.");
    }

    /**
	 * Logs a message in a thread-safe manner by synchronizing on the static lock object.
	 * It increments both the total log count and the instance log count, ensuring that the counts
	 * are updated correctly even when multiple threads are logging messages concurrently. 
	 * The log message includes the logger name and the current total log count for better 
	 * visibility of the logging activity across threads.
	 * @param msg the message to be logged, which is included in the output along with the logger name and total log count
	 */
    public void logMessage(String msg) {
        synchronized (staticLock) {
            totalLogCount++;
            instanceLogCount++;
            System.out.println(loggerName + ": " + msg + " (totalLogCount=" + totalLogCount + ")");
        }
    }

	/**
	 * Main method to test the SharedLogger class by creating multiple threads that log messages concurrently.
	 * It creates two threads, each with its own SharedLogger instance, and starts them to
	 * log messages. After both threads have completed their logging, 
	 * it prints the total number of log messages logged by all threads, which should be 1000 (500 messages from each thread).
	 * @param args command-line arguments (not used)
	 * @throws InterruptedException if any thread is interrupted while waiting for others to finish
	 */
    public static void main(String[] args) throws InterruptedException {
		// Create and start threads that log messages using SharedLogger instances
        Thread t1 = new Thread(new SharedLogger("Logger-1"));
        Thread t2 = new Thread(new SharedLogger("Logger-2"));
		// Start the threads and wait for them to finish
        t1.start();
        t2.start();
		// Wait for both threads to complete their logging
        t1.join();
        t2.join();
		// Print the total number of log messages logged by all threads (should be 1000)
        System.out.println("Total log messages: " + totalLogCount); // Should be 1000
    }
}