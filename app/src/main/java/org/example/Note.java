package org.example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class Note implements Serializable {

    private final String id;
    private String title;
    
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Content theContent; //needed this for rule
    
    /*OBJ08-J Do not expose internal state of objects 
    content is private and is mutable, so we encapsulate it properly */
    /*SER05-J: Do not serialize instances of non-static inner classes */
    static class Content implements Serializable {
        private String content;

        public Content(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public Note(String title, String newContent) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        Content c = new Content(newContent);
        this.theContent = c;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public String getId() {

        return id;
    }

    public String getTitle() {
        //OBJ05-J return copies of mutable objects to prevent external modification of internal state
        String copytitle = title; // Create a copy of the title to avoid exposing internal state
        return copytitle;
    }

    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }

    public String getContent() {
        /*OBJ08-J this ensures that the internal state of the Note class is not exposed to
        external modification by returning a copy of the content
        instead of the original reference
        */
        String contentCopy = theContent.getContent(); // Create a copy of the content to avoid exposing internal state
        return contentCopy;
    }

    public void setContent(String content) {
        this.theContent.setContent(content);
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        /*OBJ13-J return copies of objects to prevent external modification of internal state */
        LocalDateTime updatedAtCopy = LocalDateTime.of(updatedAt.toLocalDate(), updatedAt.toLocalTime());
        return updatedAtCopy;
    }

    /**
     * SER01-J: Do not deviate from the proper signatures of serialization methods 
     * The methods must match the correct signature
     * @param stream
     * @throws IOException
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }

    /**
     * SER01-J: Do not deviate from the proper signatures of serialization methods 
     * The method must match the correct signature
     * @param stream
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
    }

    @Override
    public String toString() {
        return "Note{id='" + id + "', title='" + title + "', createdAt=" + createdAt + ", updatedAt=" + updatedAt + "}";
    }
}
