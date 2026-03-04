package org.Nick;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

/**
 * FIO51JcodeExdemonstrates compliance with FIO51-J.
 *
 * This implementation safely verifies multiple file attributes
 * and handles the possibility that fileKey() may return null.
 */
public class FIO51JcodeEx {
    /**
     * Main method that verifies file identity using multiple attributes.
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        Path path = Path.of("secureData.txt");
        try {
            /**
             * Read initial file attributes.
             */
            BasicFileAttributes originalAttributes =
                    Files.readAttributes(path, BasicFileAttributes.class);

            Object originalFileKey = originalAttributes.fileKey();
            long originalCreationTime = originalAttributes.creationTime().toMillis();
            long originalModifiedTime = originalAttributes.lastModifiedTime().toMillis();
            /**
             * Simulate reopening the file.
             */
            BasicFileAttributes newAttributes =
                    Files.readAttributes(path, BasicFileAttributes.class);
            Object newFileKey = newAttributes.fileKey();
            /**
             * Null-safe comparison of file attributes.
             */
            boolean sameFile =
                    Objects.equals(originalFileKey, newFileKey) &&
                    originalCreationTime == newAttributes.creationTime().toMillis() &&
                    originalModifiedTime == newAttributes.lastModifiedTime().toMillis();

            if (sameFile) {
                System.out.println("File verified as the same file.");
            } else {
                System.out.println("File may have been replaced or modified.");
            }
        } catch (IOException e) {
            /**
             * Safe failure if file cannot be accessed.
             */
            System.out.println("File could not be accessed.");
        }
    }
}