package org.john;

import java.io.File;
import java.io.IOException;

public class FIO02JExample {
//rule FIO02-J Detect and handle file-related errors

    /**
     * Main method demonstrating secure file creation, writing, and deletion
     * 
     *
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("Demonstrating FIO02-J Detect and handle file-related errors");

        File tempFile = new File("temp.txt");

        try {
            if (tempFile.createNewFile()) {
                System.out.println("Temporary file created successfully:" + tempFile.getName());
            } else {
                System.out.println("File already exists: " + tempFile.getName() + " - proceeding to use it.");
            }
        } catch (IOException e) {
            System.err.println("Failed to create temp backup file: " + e.getMessage());
            System.out.println("Aborting process due to creation error.");
            return;
        }

        try (java.io.PrintWriter writer = new java.io.PrintWriter(tempFile)) {
            writer.println("User Hello");
            writer.println("What is up User");
            System.out.println("Data written to file.");
        } catch (IOException e) {
            System.err.println("Failed to write to file: " + e.getMessage());
        }

        if (tempFile.delete()) {
            System.out.println("Temporary file deleted successfully.");
        } else {
            if (tempFile.exists()) {
                System.err.println("Failed to delete temp backup file: " + tempFile.getName() + "still exists!");
            } else {
                System.out.println("File already gone.");
            }
        }

        System.out.println("All file operations were checked for errors.");
    }
}
