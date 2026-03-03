/**
 * Demonstrates safe use of {@link ThreadLocal} by providing an explicit cleanup method.
 */
public final class Diary {
	private static final ThreadLocal<Day> days = ThreadLocal.withInitial(() -> Day.MONDAY);

	private Diary() {
		// utility class
	}

	public static void setDay(Day day) {
		if (day == null) {
			throw new IllegalArgumentException("day must not be null");
		}
		days.set(day);
	}

	public static Day getDay() {
		return days.get();
	}

	// Compliant solution helper: restores initial state for pooled threads.
	public static void removeDay() {
		days.remove();
	}
}
