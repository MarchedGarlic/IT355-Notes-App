package org.Nick;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * FIO50JcodeEx demonstrates compliant file creation.
 *
 * This example follows FIO50-J by ensuring that file creation
 * is handled atomically using CREATE_NEW. If the file already exists,
 * the operation fails safely instead of overwriting existing data.
 */
public class FIO50JcodeEx {
    /**
     * Main method that attempts to securely create a file.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        Path path = Path.of("secureData.txt");
        try {
            /**
             * CREATE_NEW ensures:
             * - The file is created atomically.
             * - An exception is thrown if the file already exists.
             */
            Files.writeString(
                path,
                "Confidential Information",
                StandardOpenOption.CREATE_NEW
            );
            System.out.println("File created safely.");
        } catch (IOException e) {
            /**
             * If file creation fails, the exception is caught.
             */
            System.out.println("File already exists or could not be created.");
        }
    }
}