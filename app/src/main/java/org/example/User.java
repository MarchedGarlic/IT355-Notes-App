package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {
    private final String id;
    private String username;
    private String passwordHash;
    private final List<Note> notes;

    public User(String username, String passwordHash) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.passwordHash = passwordHash;
        this.notes = new ArrayList<>();
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void addNote(Note note) {
        notes.add(note);
    }

    public boolean removeNote(String noteId) {
        return notes.removeIf(n -> n.getId().equals(noteId));
    }

    @Override
    public String toString() {
        return "User{id='" + id + "', username='" + username + "', notes=" + notes.size() + "}";
    }
}
