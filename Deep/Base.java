/**
 * LCK02-J. Do not synchronize on the class object returned by getClass()  
 * 
 * Please run with Derived.java
 */

package Deep;
/**
 * The Base class contains a shared static variable and provides thread-safe methods to increment and retrieve its value
 */
public class Base {
    protected static int sharedValue = 0;

    /**
	 * Atomically increments the sharedValue by 1 in a thread-safe manner using synchronization on the Base class object
	 */
    public static void incrementSharedValue() {
        synchronized (Base.class) {
            sharedValue++;
            System.out.println(Thread.currentThread().getName() + " incremented value to: " + sharedValue);
        }
    }
	/**
	 * Atomically retrieves the current value of sharedValue in a thread-safe manner using synchronization on the Base class object
	 * @return the current value of sharedValue
	 */
    public static int getSharedValue() {
        synchronized (Base.class) {
            return sharedValue;
        }
    }
}