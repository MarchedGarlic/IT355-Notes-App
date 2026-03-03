/**
 * THI00-J. Do not invoke Thread.run()  
 * 
 */

/**
 * I provided the Junit console standalone to use for this test
 * It is located in the Deep/lib directory
 * Compile and run instructions:
 * Compile: javac -cp Deep/lib/junit-platform-console-standalone.jar Deep/HighLock.java Deep/HighLockTest.java
 * 
 * Run Junit Test: java -jar Deep/lib/junit-platform-console-standalone.jar --class-path . --select-class Deep.HighLockTest
 * 
 */


package Deep;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Unit test for the HighLock class, 
 * which demonstrates the intentional direct invocation of 
 * the run() method of a Runnable for testing purposes.
 */
public class HighLockTest {
	/**
	 * Test method that directly invokes the run() method of a Runnable to test the increment functionality of the HighLock class.
	 */
    @Test
    public void testRunMethodDirectInvocation() {
        HighLock counter = new HighLock();
        Runnable task = () -> {
            for (int i = 0; i < 10; i++) {
                counter.increment();
            }
        };
        Thread thread = new Thread(task);

        // THI00-J-EX0: Intentional direct invocation of run() for unit testing, since we want to test the behavior of the run() method itself without starting a new thread
        ((Runnable) thread).run();

        assertEquals(10, counter.getCount(), "Counter should be incremented 10 times.");
		System.out.println("Test ran successfully.");
    }
}