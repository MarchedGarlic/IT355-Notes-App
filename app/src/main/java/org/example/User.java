package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {

    
    private final String id;
    private String username;
    private String password;
    private final List<Note> notes;


    public User(String username, String password) {
        /* OBJ11-J constructors must be fully constructed before returning 
        to the caller. In this code we check if the username or password is valid
        before constructing as to ensure that a incomplete object is NOT returned
        */
        if(username == null || password == null){
            throw new IllegalArgumentException("Username and password cannot be null");
        } else{
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.password = password;
        this.notes = new ArrayList<>();
        }
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
