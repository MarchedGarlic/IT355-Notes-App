
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * ERR54-J: Use a try-with-resources statement to safely handle closeable resources.
 *
 * <p>Try-with-resources guarantees that each resource is closed, even when:
 * <ul>
 *   <li>processing throws an exception</li>
 *   <li>closing one resource throws an exception</li>
 * </ul>
 */
public final class CloseableResourcesDemo {
	private CloseableResourcesDemo() {
		// no-op
	}

	static void processFile(Path inPath, Path outPath) throws IOException {
		// Both resources are closed automatically in reverse order.
		try (BufferedReader br = Files.newBufferedReader(inPath, StandardCharsets.UTF_8); BufferedWriter bw = Files.newBufferedWriter(outPath, StandardCharsets.UTF_8)) {
			String line;
			while ((line = br.readLine()) != null) {
				bw.write(line);
				bw.newLine();
			}
		}
	}

	public static void main(String[] args) throws IOException {
		Path in = Path.of("input.txt");
		Path out = Path.of("output.txt");
		Files.writeString(in, "hello\nworld\n", StandardCharsets.UTF_8);

		processFile(in, out);
		System.out.println("Wrote: " + out.toAbsolutePath());
	}
}
