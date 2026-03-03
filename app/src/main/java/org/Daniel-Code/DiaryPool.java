import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Demonstrates the compliant try/finally cleanup pattern for ThreadLocal values
 * when using a thread pool.
 */
public final class DiaryPool implements AutoCloseable {
	private final ExecutorService exec;

	public DiaryPool(int threads) {
		if (threads <= 0) {
			throw new IllegalArgumentException("threads must be > 0");
		}
		this.exec = Executors.newFixedThreadPool(threads);
	}

	public void doSomething1() {
		exec.execute(() -> {
                    try {
                        Diary.setDay(Day.FRIDAY);
                        threadSpecificTask();
                    } finally {
                        Diary.removeDay();
                        // Diary.setDay(Day.MONDAY) can also be used
                    }
                });
	}

	private void threadSpecificTask() {
		System.out.println(Thread.currentThread().getName() + " day=" + Diary.getDay());
	}

	@Override
	public void close() {
		exec.shutdown();
		try {
			if (!exec.awaitTermination(2, TimeUnit.SECONDS)) {
				exec.shutdownNow();
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			exec.shutdownNow();
		}
	}
}
