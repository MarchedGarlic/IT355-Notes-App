import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Demonstrates input validation to reduce XML injection risk.
 *
 * <p>This example accepts a user-supplied quantity and validates it as an
 * unsigned integer using {@link Integer#parseUnsignedInt(String)} before
 * inserting it into an XML string.
 *
 * <h2>Usage</h2>
 * <ul>
 *   <li>{@code java XMLInjection 10}</li>
 *   <li>Invalid examples: {@code -1}, {@code 1</quantity><evil/>}, {@code abc}</li>
 * </ul>
 */
public final class XMLInjection {

    /** Utility class; no instances. */
    private XMLInjection() {
        // no-op
    }

    /**
     * Writes an XML fragment to {@code outStream} only if {@code quantity} is a valid unsigned integer.
     *
     * @param outStream destination stream
     * @param quantity untrusted user input representing quantity
     * @throws IOException if writing fails
     * @throws NumberFormatException if {@code quantity} is not an unsigned integer
     */
    private static void createXMLStream(final BufferedOutputStream outStream, final String quantity)
            throws IOException, NumberFormatException {
        int count = Integer.parseUnsignedInt(quantity);

        String xmlString = """
                           <item>
                             <description>Widget</description>
                             <price>500</price>
                             <quantity>""" + count + "</quantity>\n"
                + "</item>\n";

        outStream.write(xmlString.getBytes(StandardCharsets.UTF_8));
        outStream.flush();
    }

    /**
     * Program entry point.
     *
     * @param args expects one argument: quantity
     */
    public static void main(String[] args) throws IOException {
        String quantity = (args != null && args.length > 0) ? args[0] : "";

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (BufferedOutputStream out = new BufferedOutputStream(buffer)) {
            createXMLStream(out, quantity);
        } catch (NumberFormatException ex) {
            System.out.println("Rejected quantity (must be an unsigned integer): " + quantity);
            return;
        }

        System.out.print(buffer.toString(StandardCharsets.UTF_8));
    }
}