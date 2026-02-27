package org.example;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class NoteSaver {
    /**
     * Saves the note to a file
     * @param note
     * @param file
     * @throws IOException
     */
    static void saveNote(Note note, String file) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
        out.writeObject(note);
        out.close();
    }

    /**
     * Loads the file to a Note object if the user ID matches
     * @param file
     * @param userID
     * @return
     * @throws IOException
     * @throws SecurityException
     * @throws ClassNotFoundException
     */
    static Note loadNote(String file, String userID) throws IOException, SecurityException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
        Note note = (Note)in.readObject();

        // OBJ14-J: Do not use an object that has been freed
        // This one here is avoids this, as in is no longer used once its been freed
        in.close();

        // Right here the security manager is not bypassed
        note.validateForUser(userID);

        return note;
    }
}
