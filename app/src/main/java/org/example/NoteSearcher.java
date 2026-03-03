package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
    /* TPS04-J: ThreadLocal value is reinitialized per task and removed in finally. */
    private static final ThreadLocal<String> searchKeywordLocal = new ThreadLocal<>();

     /* LCK04-J: Do not synchronize on a collection view if the backing collection
         is accessible. We keep a synchronized backing map and a key-set view.
         Any iteration over the key-set view is synchronized on the backing map. */
     private static final Map<String, Integer> searchCountMap = Collections.synchronizedMap(new HashMap<>());
     private static final Set<String> searchCountKeyView = searchCountMap.keySet();

    // Load persisted history from disk when the class is first used
    static {
        synchronized (historyLock) {
            try {
                if (Files.exists(HISTORY_FILE)) {
                    List<String> lines = Files.readAllLines(HISTORY_FILE);
                    searchHistory.addAll(lines);
                    for (String keyword : lines) {
                        searchCountMap.merge(keyword, 1, Integer::sum);
                    }
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

        if (notes.isEmpty()) {
            return results;
        }

        int poolSize = Math.min(notes.size(), Runtime.getRuntime().availableProcessors());
        ExecutorService executor = Executors.newFixedThreadPool(Math.max(1, poolSize));
        List<Future<?>> tasks = new ArrayList<>();

        for (Note note : notes) {
            Future<?> task = executor.submit(() -> {
                /* TPS04-J: Ensure ThreadLocal variables are reinitialized and cleaned
                   when running tasks in a thread pool. */
                try {
                    searchKeywordLocal.set(lowerKeyword);
                    String localKeyword = searchKeywordLocal.get();

                    boolean titleMatch = note.getTitle().toLowerCase().contains(localKeyword);
                    boolean contentMatch = note.getContent().toLowerCase().contains(localKeyword);

                    if (titleMatch || contentMatch) {
                        /* LCK02-J: Synchronize on the class literal NoteSearcher.class
                           to safely collect results from all search threads.
                           Using getClass() here would be wrong because a subclass
                           would get a different lock object. */
                        synchronized (NoteSearcher.class) {
                            results.add(note);
                        }
                    }
                } finally {
                    searchKeywordLocal.remove();
                }
            });
            tasks.add(task);
        }

        for (Future<?> task : tasks) {
            try {
                task.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (ExecutionException e) {
                System.err.println("Search worker failed: " + e.getMessage());
            }
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            executor.shutdownNow();
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
            searchCountMap.merge(keyword, 1, Integer::sum);
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
     * Returns formatted keyword frequency lines.
     * LCK04-J: Synchronize on the backing map (searchCountMap), not on the
     * key-set collection view (searchCountKeyView), when iterating keys.
     */
    public static List<String> getSearchKeywordFrequencies() {
        synchronized (searchCountMap) {
            List<String> frequencies = new ArrayList<>();
            for (String keyword : searchCountKeyView) {
                frequencies.add(keyword + " => " + searchCountMap.get(keyword));
            }
            return frequencies;
        }
    }

    /**
     * Clears all search history from memory and disk.
     * LCK06-J: Uses the static historyLock to protect the static searchHistory list.
     * FIO02-J & EXP00-J: Detect file-related errors and handle the boolean return value from deleteIfExists
     */
    public static void clearHistory() {
        synchronized (historyLock) {
            searchHistory.clear();
            synchronized (searchCountMap) {
                searchCountMap.clear();
            }
            try {
                /* FIO02-J: Check the return value to determine if file was actually deleted */
                boolean deleted = Files.deleteIfExists(HISTORY_FILE);
                if (deleted) {
                    System.out.println("Search history file successfully deleted.");
                } else {
                    System.out.println("FIO02-J: Search history file did not exist, nothing to delete.");
                }
            } catch (IOException e) {
                System.err.println("FIO02-J: IO error while deleting search history file: " + e.getMessage());
            }
        }
    }
}
