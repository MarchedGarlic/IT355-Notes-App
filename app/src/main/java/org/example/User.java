package org.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class User {

    
    private final String id;
    private String username;
    private String password;
    private final List<Note> notes;

    /* LCK01-J: Do not synchronize on objects that may be reused.
       This is a private final lock object that is never exposed or reused,
       ensuring safe synchronization for batch note operations. */
    private final Object notesLock = new Object();

    /* LCK03-J: Do not synchronize on the intrinsic locks of high-level concurrency objects.
       We use ReentrantLock and only call its lock()/unlock() API methods,
       never synchronized(noteOpLock). */
    private final Lock noteOpLock = new ReentrantLock();

    public User(String id, String username, String password){
        /* OBJ11-J constructors must be fully constructed before returning 
        to the caller. In this code we check if the username or password is valid
        before constructing as to ensure that a incomplete object is NOT returned
        */
        if(id == null || username == null || password == null){
            throw new IllegalArgumentException("ID, Username, and Password cannot be null");
        }

        this.id = id;
        this.username = username;
        this.password = password;
        this.notes = new ArrayList<>();
    }

    public User(String username, String password) {
        this(UUID.randomUUID().toString(), username, password);
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password; 
    }

    public void setPassword(String password) {
        this.password = password;
    }
/*  OBJ05-J instead of returning the reference to
    the arraylist we return a copy of the list to prevent 
    external modification of the internal state of the User class
*/
    public List<Note> getNotes() {
        List<Note> notesCopy = new ArrayList<>(notes);
        return notesCopy;
    }

    /* LCK03-J: Use the ReentrantLock API (lock/unlock) to add a note safely.
       LCK08-J: Ensure actively held locks are released on exceptional conditions
       by always unlocking in a finally block. */
    public void addNote(Note note) {
        noteOpLock.lock();
        try {
            notes.add(note);
        } finally {
            noteOpLock.unlock();
        }
    }

    /* LCK03-J: Use the ReentrantLock API (lock/unlock) to remove notes safely.
       LCK08-J: Ensure actively held locks are released on exceptional conditions
       by always unlocking in a finally block. */
    public boolean removeNote(String noteId) {
        noteOpLock.lock();
        try {
            return notes.removeIf(n -> n.getId().equals(noteId));
        } finally {
            noteOpLock.unlock();
        }
    }

    /* LCK01-J: Do not synchronize on objects that may be reused.
       Batch-updates note titles using a private final lock object (notesLock)
       that is never shared or reused by the JVM. */
    public void batchUpdateNoteTitles(List<String> ids, String newTitle) {
        synchronized (notesLock) {
            for (Note note : notes) {
                if (ids.contains(note.getId())) {
                    note.setTitle(newTitle);
                }
            }
        }
    }

    /* LCK03-J + LCK08-J: Safely remove multiple notes using ReentrantLock.
       The lock is always released in the finally block even if an exception occurs. */
    public void safeRemoveNotes(List<String> noteIds) {
        noteOpLock.lock();
        try {
            notes.removeIf(n -> noteIds.contains(n.getId()));
        } finally {
            noteOpLock.unlock();
        }
    }

    /* THI00-J: Do not invoke Thread.run().
       Exports each note as a separate file in the given directory.
       Each thread writes one file named <title>.txt with the content.
       LCK01-J: Synchronizes on the private notesLock to safely
       read note data from multiple threads. */
    public void exportAllNotesParallel(String exportDirPath) {
        Path baseDir = Paths.get(exportDirPath);
        // Create a uniquely numbered subfolder under the given directory (e.g. exports/export-1)
        try {
            Files.createDirectories(baseDir);
        } catch (IOException e) {
            System.err.println("Failed to create base directory: " + e.getMessage());
            return;
        }
        int copyNum = 1;
        Path exportDir = baseDir.resolve("export-" + copyNum);
        while (Files.exists(exportDir)) {
            copyNum++;
            exportDir = baseDir.resolve("export-" + copyNum);
        }
        final Path finalExportDir = exportDir;
        try {
            Files.createDirectories(finalExportDir);
        } catch (IOException e) {
            System.err.println("Failed to create export directory: " + e.getMessage());
            return;
        }

        List<Thread> threads = new ArrayList<>();
        for (Note note : notes) {
            Thread t = new Thread(() -> {
                String safeTitle = note.getTitle().replaceAll("[^a-zA-Z0-9_\\-]", "_");
                Path noteFile = finalExportDir.resolve(safeTitle + ".txt");
                /* LCK01-J: Synchronize on private notesLock (not a reused object)
                   to safely access note data from multiple threads. */
                synchronized (notesLock) {
                    try (BufferedWriter bw = new BufferedWriter(new FileWriter(noteFile.toString()))) {
                        bw.write(note.getTitle());
                        bw.newLine();
                        bw.write(note.getContent());
                        bw.newLine();
                    } catch (IOException e) {
                        System.err.println("Failed to export note: " + e.getMessage());
                    }
                }
                System.out.println("Exported note: " + note.getTitle() + " -> " + noteFile);
            });
            threads.add(t);
            t.start(); // THI00-J: Always use start(), never run()
        }
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Parallel export complete. " + threads.size() + " note(s) exported to " + finalExportDir);
    }

    /* THI00-J: Do not invoke Thread.run().
       Imports notes in parallel from all .txt files in the given directory.
       Each file has the format: first line = title, second line = content.
       A new thread is started (not run()) for each file to create and add the note.
       LCK03-J + LCK08-J: addNoteWithUniqueTitle() uses the ReentrantLock with try/finally. */
    public void importNotesParallel(String importDirPath) {
        Path importDir = Paths.get(importDirPath);
        if (!Files.isDirectory(importDir)) {
            System.err.println("Import path is not a directory: " + importDirPath);
            return;
        }

        List<Path> files;
        try (var stream = Files.list(importDir)) {
            files = stream.filter(f -> f.toString().endsWith(".txt")).toList();
        } catch (IOException e) {
            System.err.println("Failed to list import directory: " + e.getMessage());
            return;
        }

        if (files.isEmpty()) {
            System.out.println("No .txt files found in " + importDirPath + ". Nothing to import.");
            return;
        }

        List<Thread> threads = new ArrayList<>();
        for (Path file : files) {
            Thread t = new Thread(() -> {
                try {
                    List<String> lines = Files.readAllLines(file);
                    if (lines.size() < 2) {
                        System.err.println("Skipping malformed file (need title + content): " + file);
                        return;
                    }
                    String title = lines.get(0);
                    String content = String.join("\n", lines.subList(1, lines.size()));
                    // If a note with this title already exists, append "-copy" until unique
                    // then add it — both done atomically inside addNoteWithUniqueTitle
                    addNoteWithUniqueTitle(title, content);
                    System.out.println("Imported note: " + title + " from " + file.getFileName());
                } catch (IOException e) {
                    System.err.println("Failed to read file: " + file + " - " + e.getMessage());
                }
            });
            threads.add(t);
            t.start(); // THI00-J: Always use start(), never run()
        }
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Parallel import complete. " + threads.size() + " note(s) imported from " + importDirPath);
    }

    /* Checks for a unique title and adds the note atomically under one lock,
       preventing race conditions where two threads could claim the same title.
       LCK03-J: Uses the ReentrantLock API (lock/unlock), not synchronized on the lock object.
       LCK08-J: The lock is always released in the finally block even if an exception occurs. */
    private void addNoteWithUniqueTitle(String title, String content) {
        noteOpLock.lock();
        try {
            String candidate = title;
            boolean exists = true;
            while (exists) {
                final String check = candidate;
                exists = notes.stream().anyMatch(n -> n.getTitle().equalsIgnoreCase(check));
                if (exists) {
                    candidate = candidate + "-copy";
                }
            }
            Note note = new Note(candidate, content);
            notes.add(note);
        } finally {
            noteOpLock.unlock();
        }
    }

    @Override
    public String toString() {
        return "User{id='" + id + "', username='" + username + "', notes=" + notes.size() + "}";
    }
}
