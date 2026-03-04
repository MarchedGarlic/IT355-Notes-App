import java.security.Key;
import java.util.Arrays;

/**
 * MET56-J: Do not use Object.equals() to compare cryptographic keys
 *
 * This class shows a short example of comparing key material safely.
 */
public class MET56_J {

    /**
     *      *
     * @param key1 First key
     * @param key2 Second key
     * @return true if the keys represent the same value
     */
    public static boolean keysEqualCompliant(Key key1, Key key2) {
        if (key1 == null || key2 == null) {
            return false;
        }

        if (key1.equals(key2)) {
            return true;
        }

        byte[] encoded1 = key1.getEncoded();
        byte[] encoded2 = key2.getEncoded();
        if (encoded1 == null || encoded2 == null) {
            return false;
        }

        return Arrays.equals(encoded1, encoded2);
    }

    /**
     * Simple demo showing the two approaches.
     *
     * @param args Command-line arguments (unused)
     */
    public static void main(String[] args) {
        Key key1 = new DummyKey(new byte[] { 1, 2, 3 });
        Key key2 = new DummyKey(new byte[] { 1, 2, 3 });

        System.out.println("Compliant equals: " + keysEqualCompliant(key1, key2));
    }

    private static class DummyKey implements Key {
        private final byte[] encoded;

        private DummyKey(byte[] encoded) {
            this.encoded = encoded.clone();
        }

        @Override
        public String getAlgorithm() {
            return "Dummy";
        }

        @Override
        public String getFormat() {
            return "RAW";
        }

        @Override
        public byte[] getEncoded() {
            return encoded.clone();
        }
    }
}
