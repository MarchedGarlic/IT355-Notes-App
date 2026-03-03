package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Provides parallel keyword search across notes and maintains
 * a persistent search history stored on disk.
 */
public class NoteSearcher {

    private static final Path HISTORY_FILE = Paths.get("data", "search_history.txt");

    /* LCK06-J: Do not use an instance lock to protect shared static data.
       searchHistory is static (shared across all callers), so it MUST be
       guarded by a static lock. An instance lock would allow unsynchronized
       concurrent access from different NoteSearcher references. */
    private static final Object historyLock = new Object();
    private static final List<String> searchHistory = new ArrayList<>();

    // Load persisted history from disk when the class is first used
    static {
        synchronized (historyLock) {
            try {
                if (Files.exists(HISTORY_FILE)) {
                    List<String> lines = Files.readAllLines(HISTORY_FILE);
                    searchHistory.addAll(lines);
                }
            } catch (IOException e) {
                System.err.println("Warning: Could not load search history: " + e.getMessage());
            }
        }
    }

    /**
     * Searches all notes in parallel for a keyword match in title or content.
     * Records the search term in the shared history and persists it to disk.
     *
     * LCK02-J: We synchronize on the class literal NoteSearcher.class,
     * NOT on getClass(), because a subclass's getClass() would return a
     * different Class object and break the synchronization contract.
     *
     * @param notes   The list of notes to search
     * @param keyword The keyword to search for (case-insensitive)
     * @return List of notes whose title or content contains the keyword
     */
    public static List<Note> search(List<Note> notes, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            System.out.println("Search keyword cannot be empty.");
            return Collections.emptyList();
        }

        // LCK06-J: Record the search term using the static lock
        recordSearch(keyword);

        String lowerKeyword = keyword.toLowerCase();
        List<Note> results = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();

        for (Note note : notes) {
            Thread t = new Thread(() -> {
                boolean titleMatch = note.getTitle().toLowerCase().contains(lowerKeyword);
                boolean contentMatch = note.getContent().toLowerCase().contains(lowerKeyword);

                if (titleMatch || contentMatch) {
                    /* LCK02-J: Synchronize on the class literal NoteSearcher.class
                       to safely collect results from all search threads.
                       Using getClass() here would be wrong because a subclass
                       would get a different lock object. */
                    synchronized (NoteSearcher.class) {
                        results.add(note);
                    }
                }
            });
            threads.add(t);
            t.start(); // THI00-J: Use Thread.start(), never Thread.run()
        }

        // Wait for all search threads to finish
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return results;
    }

    /**
     * Records a search term in the shared static history and appends it to disk.
     * LCK06-J: Uses the static historyLock to protect the static searchHistory list.
     */
    public static void recordSearch(String keyword) {
        synchronized (historyLock) {
            searchHistory.add(keyword);
            try {
                Files.createDirectories(HISTORY_FILE.getParent());
                Files.writeString(HISTORY_FILE, keyword + System.lineSeparator(),
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException e) {
                System.err.println("Warning: Could not save search history: " + e.getMessage());
            }
        }
    }

    /**
     * Returns a copy of the search history.
     * LCK06-J: Uses the static historyLock to protect the static searchHistory list.
     */
    public static List<String> getSearchHistory() {
        synchronized (historyLock) {
            return new ArrayList<>(searchHistory);
        }
    }

    /**
     * Clears all search history from memory and disk.
     * LCK06-J: Uses the static historyLock to protect the static searchHistory list.
     */
    public static void clearHistory() {
        synchronized (historyLock) {
            searchHistory.clear();
            try {
                Files.deleteIfExists(HISTORY_FILE);
            } catch (IOException e) {
                System.err.println("Warning: Could not delete search history file: " + e.getMessage());
            }
        }
    }
}
