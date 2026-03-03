
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Demonstrates the compliant "Collection Lock Object" pattern.
 *
 * <p>When you have a synchronized collection view (for example, a {@link Set}
 * returned by {@link Map#keySet()}), you must synchronize on the same lock
 * object that guards the backing collection. In this example, the lock is the
 * synchronized {@code mapView} object returned by {@link Collections#synchronizedMap(Map)}.
 */
public final class CollectionView {
	/** Utility class; no instances. */
	private CollectionView() {
		// no-op
	}

	/**
	 * Program entry point.
	 *
	 * @param args unused
	 */
	public static void main(String[] args) {
		ViewHolder holder = new ViewHolder();
		holder.getMap().put(1, "one");
		holder.getMap().put(2, "two");
		holder.getMap().put(3, "three");

		holder.doSomething();
	}

	/**
	 * Holds a synchronized map and a set view of its keys.
	 */
	private static final class ViewHolder {
		private final Map<Integer, String> mapView =
				Collections.synchronizedMap(new HashMap<>());
		private final Set<Integer> setView = mapView.keySet();

		/**
		 * Returns the synchronized map.
		 *
		 * <p>Callers may still need external synchronization when performing
		 * composite operations (iteration, check-then-act, etc.).
		 *
		 * @return the synchronized map
		 */
		public Map<Integer, String> getMap() {
			return mapView;
		}

		/**
		 * Performs an iteration over the key set view while synchronizing on the
		 * backing map's lock object.
		 */
		public void doSomething() {
			synchronized (mapView) { // Synchronize on map, rather than set
				for (Integer k : setView) {
					String value = mapView.get(k);
					System.out.println(k + " -> " + value);
				}
			}
		}
	}
}

