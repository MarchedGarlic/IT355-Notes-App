
import java.io.Closeable;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * ERR53-J: Try to gracefully recover from system errors.
 *
 * <p>This example demonstrates a last-resort recovery pattern:
 * <ul>
 *   <li>Wrap a risky operation in a {@code try} block.</li>
 *   <li>Catch {@link Throwable} (or {@link Error}) only at a top-level boundary where you can
 *       log, fail fast, and attempt minimal cleanup.</li>
 *   <li>Always free critical resources in {@code finally}.</li>
 * </ul>
 *
 * <p>Important: Catching {@code Throwable} is not a general-purpose error handling strategy.
 * Do it only where you can safely handle failure (typically at thread boundaries or in main).
 */
public final class Recover {
	private Recover() {
		// no-op
	}

	public static void main(String[] args) {
		SimpleResource resource = new SimpleResource("demo-resource");
		List<byte[]> cache = new ArrayList<>();

		try {
			// Allocate a small cache to demonstrate cleanup.
			for (int i = 0; i < 4; i++) {
				cache.add(new byte[256 * 1024]);
			}

			// Trigger a system error for demonstration.
			throw new StackOverflowError("demo");
		} catch (Throwable t) {
			// Last-resort boundary: log and transition to a safe state.
			System.err.println("[" + Instant.now() + "] Fatal throwable captured: " + t);
			// In real applications, forward to a centralized handler / crash reporter.
		} finally {
			// Minimal cleanup to reduce damage: free memory caches, release key resources.
			cache.clear();
			try {
				resource.close();
			} catch (IOException e) {
				System.err.println("Failed to close resource: " + e.getMessage());
			}
		}

		System.out.println("Program reached a safe termination point.");
	}

	private static final class SimpleResource implements Closeable {
		private final String name;
		private boolean closed;

		SimpleResource(String name) {
			this.name = name;
		}

		@Override
		public void close() throws IOException {
			if (closed) {
				return;
			}
			closed = true;
			System.err.println("Closed resource: " + name);
		}
	}
}
