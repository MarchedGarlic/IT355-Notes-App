import java.io.File;

/**
 * Lists the contents of a directory without executing system commands.
 *
 * <p>This program intentionally avoids {@code Runtime.exec()} and similar APIs.
 * Instead, it uses {@link File#list()} to obtain a directory listing, which
 * eliminates the risk of command or argument injection.
 *
 * <h2>Usage</h2>
 * <ul>
 *   <li>Pass a directory path as the first argument: {@code java Sanitize C:\\Temp}</li>
 *   <li>Or pass it via a system property: {@code java -Ddir=C:\\Temp Sanitize}</li>
 *   <li>If neither is provided, the current working directory is used.</li>
 * </ul>
 */
public final class Sanitize {

	/** Utility class; no instances. */
	private Sanitize() {
		// no-op
	}

	/**
	 * Program entry point.
	 *
	 * @param args if present, {@code args[0]} is treated as the directory path
	 */
	public static void main(String[] args) {
		String dirPath = (args != null && args.length > 0) ? args[0] : System.getProperty("dir");
		if (dirPath == null || dirPath.trim().isEmpty()) {
			dirPath = ".";
		}

		File dir = new File(dirPath);
		if (!dir.isDirectory()) {
			System.out.println("Not a directory: " + dir.getPath());
			return;
		}

		String[] entries = dir.list();
		if (entries == null) {
			System.out.println("Unable to list directory contents: " + dir.getPath());
			return;
		}

		for (String entry : entries) {
			System.out.println(entry);
		}
	}
}
