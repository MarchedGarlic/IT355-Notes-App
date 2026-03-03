import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates compliant serialization using writeObject()/readObject()
 * to preserve object identity within the object graph.
 *
 * <p>The example builds an object graph where multiple {@code Student} instances
 * share the same {@code Professor} reference. After serializing and
 * deserializing, {@link Professor#checkTutees()} verifies that the shared
 * reference relationship is preserved (i.e., each student's tutor reference
 * points to the deserialized professor instance).</p>
 */
public final class SerializationDemo {

    /**
     * Utility class; not intended to be instantiated.
     */
    private SerializationDemo() {
        // no-op
    }

    /**
     * Builds a sample object graph, writes it to disk, reads it back, and checks
     * whether object identity is preserved after deserialization.
     *
     * @param args not used
     */
    public static void main(String[] args) {

        String filename = "serial";

        Professor jane = new Professor("Jane");
        Student s1 = new Student("Alice", jane);
        Student s2 = new Student("Bob", jane);
        Student s3 = new Student("Chloe", jane);

        jane.addTutee(s1);
        jane.addTutee(s2);
        jane.addTutee(s3);

        // Serialize
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {

            oos.writeObject(jane);

        } catch (IOException e) {
            System.out.println("Serialization failed: " + e.getMessage());
            return;
        }

        // Deserialize
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {

            Professor jane2 = (Professor) ois.readObject();
            System.out.println("checkTutees returns: " +
                    jane2.checkTutees());

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Deserialization failed: " + e.getMessage());
        }
    }

    /**
     * Professor entity that owns a list of tutees.
     *
     * <p>Used here to demonstrate that serialized object graphs preserve shared
     * references when deserialized, so long as the graph is serialized as a
     * whole.</p>
     */
    private static final class Professor implements Serializable {

        /** Serialization identifier for this inner class. */
        @Serial
        private static final long serialVersionUID = 1L;

        /** Display name for the professor (not used for identity checks). */
        private final String name;

        /**
         * List of tutees associated with this professor.
         *
         * <p>This list is part of the serialized object graph.</p>
         */
        private final List<Student> tutees = new ArrayList<>();

        /**
         * Creates a professor with the given name.
         *
         * @param name professor name
         */
        Professor(String name) {
            this.name = name;
        }

        /**
         * Adds a student to this professor's tutee list.
         *
         * @param student the student to add; must not be {@code null}
         * @throws IllegalArgumentException if {@code student} is {@code null}
         */
        void addTutee(Student student) {
            if (student == null) {
                throw new IllegalArgumentException("student must not be null");
            }
            tutees.add(student);
        }

        /**
         * Validates that every stored tutee is non-null and that each tutee's
         * tutor reference points back to this professor instance.
         *
         * @return {@code true} if all tutees reference this instance; otherwise {@code false}
         */
        boolean checkTutees() {
            for (Student tutee : tutees) {
                if (tutee == null || tutee.getTutor() != this) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Student entity that holds a reference to a tutor.
     *
     * <p>In this demo, multiple students share the same {@link Professor}
     * instance to test whether the reference relationship is preserved after
     * serialization.</p>
     */
    private static final class Student implements Serializable {

        /** Serialization identifier for this inner class. */
        @Serial
        private static final long serialVersionUID = 1L;

        /** Display name for the student (not used for identity checks). */
        private final String name;

        /** Tutor reference that should point to the owning professor. */
        private final Professor tutor;

        /**
         * Creates a student with a tutor.
         *
         * @param name student name
         * @param tutor the student's tutor; must not be {@code null}
         * @throws IllegalArgumentException if {@code tutor} is {@code null}
         */
        Student(String name, Professor tutor) {
            if (tutor == null) {
                throw new IllegalArgumentException("tutor must not be null");
            }
            this.name = name;
            this.tutor = tutor;
        }

        /**
         * Returns the student's tutor.
         *
         * @return the tutor reference
         */
        Professor getTutor() {
            return tutor;
        }
    }
}