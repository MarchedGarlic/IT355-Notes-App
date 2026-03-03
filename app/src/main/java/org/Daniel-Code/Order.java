import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * Serializable value object that stores an order date.
 *
 * <p>This class demonstrates defensive copying for a mutable field ({@link Date})
 * and a custom {@code readObject} implementation that validates and safely
 * reconstructs state during deserialization (e.g., to align with secure
 * deserialization guidance such as CERT's SER06-J).</p>
 *
 * <p><strong>Immutability note:</strong> The internal {@code Date} reference is
 * never exposed directly; callers receive copies via the constructor and
 * accessor.</p>
 */
public class Order implements Serializable {

    /**
     * Serialization identifier to ensure compatible deserialization across
     * versions of this class.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Fallback value used when reading the {@code date} field from a stream.
     * This avoids returning {@code null} from {@link ObjectInputStream.GetField#get(String, Object)}.
     */
    private static final Date EPOCH = new Date(0L);

    /**
     * Order date. This field is mutable, so it must be defensively copied on
     * assignment and access.
     */
    private Date date;

    /**
     * Creates a new {@code Order} with the given date.
     *
     * @param date the order date; must not be {@code null}
     * @throws IllegalArgumentException if {@code date} is {@code null}
     */
    public Order(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("date must not be null");
        }
        this.date = new Date(date.getTime());
    }

    /**
     * Returns the order date.
     *
     * <p>A defensive copy is returned to prevent callers from mutating the
     * internal state of this object.</p>
     *
     * @return a copy of the order date
     */
    public Date getDate() {
        return new Date(date.getTime());
    }

    /**
     * Custom deserialization hook used to validate and defensively copy mutable
     * state.
     *
     * <p>Reads the serialized fields, ensures {@code date} is non-null, and
     * performs a defensive copy of the provided {@link Date}.</p>
     *
     * @param ois the object input stream
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if a class of a serialized object cannot be found
     */
    @Serial
    private void readObject(ObjectInputStream ois)
            throws IOException, ClassNotFoundException {

        ObjectInputStream.GetField fields = ois.readFields();
        Date inDate = (Date) fields.get("date", EPOCH);

        // Basic validation
        if (inDate == null) {
            throw new InvalidObjectException("date must not be null");
        }

        // Defensive copy of mutable Date
        date = new Date(inDate.getTime());
    }

    /**
     * Simple test driver that serializes and deserializes an {@code Order} in
     * memory and prints the before/after dates.
     *
     * @param args ignored
     * @throws Exception if serialization or deserialization fails
     */
    public static void main(String[] args) throws Exception {

        Order original = new Order(new Date());

        // Serialize
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(original);
        oos.close();

        // Deserialize
        ByteArrayInputStream bais =
                new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        Order restored = (Order) ois.readObject();
        ois.close();

        System.out.println("Original date : " + original.getDate());
        System.out.println("Restored date : " + restored.getDate());
    }
}