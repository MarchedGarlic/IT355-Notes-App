import java.util.HashMap;
import java.util.Map;

/**
 * MET50-J: Avoid ambiguous or confusing uses of overloading
 *
 * This class demonstrates distinct method names instead of overloads.
 */
public class MET50_J {

    private final Map<Integer, Integer> records = new HashMap<Integer, Integer>();

    public MET50_J() {
        records.put(1, 111990000);
        records.put(2, 222990000);
        records.put(3, 333990000);
    }

    /**
     * Gets a record value by its index key. Showing that instead of overloading getRecord(), there are two different methods with a clear difference in their name.
     *
     * @param index Record index
     * @return Record value, or {@code null} if absent
     */
    public Integer getRecordByIndex(int index) {
        return records.get(index);
    }

    /**
     * Gets a record value by searching for the provided value.
     *
     * @param value Record value to find
     * @return The matching record value, or {@code null} if not found
     */
    public Integer getRecordByValue(Integer value) {
        for (Integer record : records.values()) {
            if (record.equals(value)) {
                return record;
            }
        }
        return null;
    }

    /**
     * Simple demo showing clear method intent without overloading.
     *
     * @param args Command-line arguments (unused)
     */
    public static void main(String[] args) {
        MET50_J loader = new MET50_J();
        System.out.println("Record at index 3: " + loader.getRecordByIndex(3));
        System.out.println("Record by value 111990000: " + loader.getRecordByValue(111990000));
    }
}
