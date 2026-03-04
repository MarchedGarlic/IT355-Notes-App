import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * MET55-J: Return an empty array or collection instead of a null value
 *
 * This class shows a short compliant example for returning collections.
 */
public class MET55_J {

    /**
     * Returns an empty list when no items are found.
     *
     * @param items Source list (may be empty)
     * @return A list, possibly empty, never {@code null}
     */
    public static List<String> getItems(List<String> items) {
        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> copy = new ArrayList<String>(items);
        return copy;
    }

    /**
     * Simple demo showing safe handling of empty returns.
     *
     * @param args Command-line arguments (unused)
     */
    public static void main(String[] args) {
        List<String> items = getItems(Collections.emptyList());
        System.out.println("Item count: " + items.size());
    }
}
