
/**
 * Entry point for the ThreadLocal + thread-pool example.
 */
public final class Threadpool {
	private Threadpool() {
		// no-op
	}

	public static void main(String[] args) {
		try (DiaryPool pool = new DiaryPool(2)) {
			pool.doSomething1();
			pool.doSomething1();
		}
	}
}
