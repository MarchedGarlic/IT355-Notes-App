/**
 * ERR51-J. Prefer user-defined exceptions over more general exception types  
 * 
 */
package Deep;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * InvalidConfigException is a custom exception class that extends the Exception class.
 * It is used to indicate that a configuration file is invalid, such as when it is not a valid JSON object,
 */
class InvalidConfigException extends Exception {
    InvalidConfigException(String msg) { super(msg); }
}

/**
 * The JsonConfig class provides a method to load an API key from a JSON configuration file.
 * It includes error handling for various scenarios, such as missing files, access issues, and invalid
 * JSON formats. 
 */
public class JsonConfig {
	/**
	 * Loads the API key from a JSON configuration file. It checks for the existence and readability of the file,
	 * and validates the JSON structure to ensure it contains the required "apiKey" field.
	 * If any of these checks fail, it throws appropriate exceptions to indicate the specific issue with the configuration file.
	 * @param file the path to the JSON configuration file that contains the API key
	 * @return the API key extracted from the JSON configuration file
	 * @throws IOException if there is an I/O error while reading the file
	 * @throws InvalidConfigException if the JSON is invalid or does not contain the required "apiKey" field
	 */
    static String loadApiKey(String file) throws IOException, InvalidConfigException {
        Path p = Path.of(file);
        if (!Files.exists(p)) throw new FileNotFoundException(file);
        if (!Files.isReadable(p)) throw new AccessDeniedException(file);

        String json = Files.readString(p).trim();
        if (!json.startsWith("{") || !json.endsWith("}")) {
            throw new InvalidConfigException("Config is not a valid JSON object");
        }

        int key = json.indexOf("\"apiKey\"");
        if (key < 0) throw new InvalidConfigException("Missing 'apiKey' field");

        int colon = json.indexOf(':', key);
        int firstQuote = json.indexOf('"', colon + 1);
        int secondQuote = json.indexOf('"', firstQuote + 1);
        if (colon < 0 || firstQuote < 0 || secondQuote < 0) {
            throw new InvalidConfigException("'apiKey' must be a string");
        }

        String apiKey = json.substring(firstQuote + 1, secondQuote).trim();
        if (apiKey.isEmpty()) throw new InvalidConfigException("'apiKey' cannot be empty");

        return apiKey;
    }
	/**
	 * Main method to test the loadApiKey function by attempting to load an API key from a specified JSON configuration file.
	 * It handles various exceptions that may arise during the loading process, such as file not found, access denied, invalid JSON format, and I/O errors,
	 * and prints appropriate messages to indicate the nature of any issues encountered with the configuration file.
	 * If the API key is successfully loaded, it prints the loaded API key to the console
	 * @param args command-line arguments (not used)
	 */
    public static void main(String[] args) {
        try {
            String apiKey = loadApiKey("config.json");
            System.out.println("Loaded API key: " + apiKey);
        } catch (FileNotFoundException e) {
            System.out.println("Create config.json first.");
        } catch (AccessDeniedException e) {
            System.out.println("Fix file permissions for config.json.");
        } catch (InvalidConfigException e) {
            System.out.println("Fix JSON config: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("I/O error reading config.");
        }
    }
}