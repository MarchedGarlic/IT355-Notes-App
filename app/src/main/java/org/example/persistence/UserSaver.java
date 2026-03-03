package org.example.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.example.Note;
import org.example.User;

public class UserSaver {
    private static final String DB_ADAPTER = "jdbc:sqlite:data/sample.db";

    /* IDS00-J: Prevent SQL injection vulnerabilities
    SQL injection patterns used to validate input at the database layer
    as an additional defense-in-depth measure
    */
    private static final Pattern[] SQL_INJECTION_PATTERNS = {
        Pattern.compile("('|(\\-\\-)|(;)|(\\|))", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\b(union|select|insert|update|delete|drop|create|alter|exec|execute)\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(\\bor\\b|\\band\\b)\\s*[=<>]", Pattern.CASE_INSENSITIVE)
    };

    /**
     * IDS00-J: Checks whether the supplied input contains common SQL injection patterns.
     * Used as defense-in-depth before parameterized queries.
     *
     * @param userInput The raw input to evaluate
     * @return {@code true} if no injection patterns are detected; {@code false} otherwise
     */
    private static boolean isSqlSafe(String userInput) {
        if (userInput == null) {
            return false;
        }
        for (Pattern pattern : SQL_INJECTION_PATTERNS) {
            if (pattern.matcher(userInput).find()) {
                return false;
            }
        }
        return true;
    }

    /**
     * This class is the exception if a user cant be saved
     */
    public static final class UserException extends IOException {
    }

    /**
     * This will create the initial table to hold users
     * @return
     * @throws SQLException
     */
    private static void initTables() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_ADAPTER); Statement stat = conn.createStatement()){
            stat.executeUpdate("CREATE TABLE IF NOT EXISTS users (id STRING PRIMARY KEY, username STRING NOT NULL UNIQUE, passwordHash STRING NOT NULL)");
        }
    }

    /**
     * This will have a single user's data to the table and all their notes to files
     * @param user
     */
    public static void saveUser(User user) throws UserException {
        /* IDS00-J: Validate username before it reaches any SQL operation */
        if (!isSqlSafe(user.getUsername())) {
            System.err.println("IDS00-J: SQL injection attempt detected in username during save");
            throw new UserException();
        }

        // Tries to save user to the disk
        try {
            // Make sure the tables actually exist
            initTables();

            // Convert user password to hash
            String passwordHash = new String(Encryption.generateKeyBytes(user.getPassword()));

            // Insert the user into the table if they arent already inserted
            String sql = """
                INSERT INTO users(id, username, passwordHash)
                VALUES(?, ?, ?)
                ON CONFLICT(id)
                DO UPDATE SET username=excluded.username, passwordHash=excluded.passwordHash;
            """;
            try (Connection conn = DriverManager.getConnection(DB_ADAPTER); PreparedStatement pstmt = conn.prepareStatement(sql)){
                pstmt.setString(1, user.getId());
                pstmt.setString(2, user.getUsername());
                pstmt.setString(3, passwordHash);
                pstmt.executeUpdate();
            }

            // Prep the note vault
            Path vault = Paths.get("data", user.getId());
            Files.createDirectories(vault);

            /* FIO00-J: Do not operate on files in shared directories
            Verify the vault directory is a real directory and not a symbolic link
            before writing any note files into it
            */
            BasicFileAttributes vaultAttrs = Files.readAttributes(vault, BasicFileAttributes.class,
                                                                   LinkOption.NOFOLLOW_LINKS);
            if (!vaultAttrs.isDirectory() || vaultAttrs.isSymbolicLink()) {
                throw new IOException("FIO00-J: Note vault is not a secure directory: " + vault);
            }

            /* FIO01-J: Create files with appropriate access permissions
            Restrict the vault directory to owner-only access after creation
            */
            NoteSaver.makeDirectorySecure(vault.toString());

            // Remove all old notes
            Files.list(vault).forEach(file -> {
                try {
                    Files.delete(file);
                } catch (IOException e) {
                    System.err.println("Failed to delete old note: " + file);
                }
            });

            // Save all the users notes to files
            for(Note note : user.getNotes()){
                Path notePath = vault.resolve(note.getId() + ".ser");
                NoteSaver.saveNote(user, note, notePath.toString());
            }
        } catch (SQLException | IOException | InvalidKeySpecException | NoSuchAlgorithmException e) {
            // If there's a SQL error, throw a user exception saying the save couldn't be completed
            System.err.println(e.getMessage());
            throw new UserException();
        }
    }

    /**
     * This function will take a username and password and use it to load the data of the user
     * @param username
     * @param password
     * @return
     * @throws UserException
     */
    public static User loadUser(String username, String password) throws UserException, SecurityException {
        /* IDS00-J: Validate username before it reaches any SQL operation */
        if (!isSqlSafe(username)) {
            System.err.println("IDS00-J: SQL injection attempt detected in username during login");
            throw new SecurityException("Invalid username input");
        }

        try {
            // Make sure tables exist, even if just to query something that doesn't exist
            initTables();

            // Retrieve the user fields
            String sql = "SELECT * FROM users WHERE username=?;";

            try (Connection conn = DriverManager.getConnection(DB_ADAPTER); PreparedStatement pstmt = conn.prepareStatement(sql)){
                pstmt.setString(1, username);

                ResultSet rs = pstmt.executeQuery();

                // Get results and instantiate new user object from it
                if(!rs.next())
                    throw new UserException();

                String userID = rs.getString("id");
                String passwordHashFromDB = rs.getString("passwordHash");

                // Compare the password hash to their supplied password
                String passwordHashFromArgs = new String(Encryption.generateKeyBytes(password));

                if(!passwordHashFromArgs.equals(passwordHashFromDB))
                    throw new SecurityException("Invalid password");

                // Create the actual user
                User user = new User(userID, username, password);
                
                // Load the notes for this specific user
                Path vault = Paths.get("data", user.getId());
                Files.createDirectories(vault);

                /* FIO00-J: Do not operate on files in shared directories
                Verify the vault directory is a real directory and not a symbolic link
                before reading any note files from it
                */
                BasicFileAttributes vaultAttrs = Files.readAttributes(vault, BasicFileAttributes.class,
                                                                       LinkOption.NOFOLLOW_LINKS);
                if (!vaultAttrs.isDirectory() || vaultAttrs.isSymbolicLink()) {
                    throw new IOException("FIO00-J: Note vault is not a secure directory: " + vault);
                }

                /* FIO01-J: Create files with appropriate access permissions
                Ensure the vault directory has owner-only access before loading notes from it
                */
                NoteSaver.makeDirectorySecure(vault.toString());

                Files.list(vault).forEach(file -> {
                    try {
                        Note note = NoteSaver.loadNote(user, file.toString());
                        user.addNote(note);
                    } catch (IOException | SecurityException e) {
                        System.err.println("Failed to load note: " + file);
                    }
                });

                // Return the resulting construction
                return user;
            }
        } catch(SQLException | IOException | InvalidKeySpecException | NoSuchAlgorithmException e){
            e.printStackTrace();
            throw new UserException();
        }
    }

    /**
     * This function will load every user from the disk and return it as a list. It is a debug function because it will bypass passwords
     * @return
     */
    private static ArrayList<String> getAllUsernames(){
        // Prep a location to keep the users
        ArrayList<String> usernames = new ArrayList<>();

        try {
            // Make sure there is SOMETHING to query
            initTables();
            
            // Query for all the users

            try (Connection conn = DriverManager.getConnection(DB_ADAPTER); Statement stat = conn.createStatement()){
                ResultSet rs = stat.executeQuery("SELECT username FROM users");
    
                // Load each user
                while (rs.next()) {
                    usernames.add(rs.getString("username"));
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return usernames;
    }

    public static void main(String[] args) throws UserException, IOException, SecurityException {
        if(!Files.exists(Paths.get("data"))){
            User user1 = new User("Test", "Password");
            User user2 = new User("John", "abc123");
            User user3 = new User("Doe", "hello");
    
            user1.addNote(new Note("Test", "TestContent"));
            user1.addNote(new Note("Test 2", "Hello World!"));
            user2.addNote(new Note("Secret content", "This should be encrypted"));
    
            saveUser(user1);
            saveUser(user2);
            saveUser(user3);
        }

        System.out.println(getAllUsernames());
        System.out.println(loadUser("John", "abc123").getNotes().get(0).getContent());
    }
}
