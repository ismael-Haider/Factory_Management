package inventory.services;

import inventory.config.Constants;
import inventory.csv.CsvReader;
import inventory.csv.CsvWriter;
import inventory.models.Item;
import inventory.models.Note;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NoteService {
    public static List<Note> notes = new ArrayList<>();
    public static List<Note> ratings = new ArrayList<>();

    public static void init() {
        LoadAllNotes();
    }

    public static void addNewNote(String note ,LocalDateTime ldt) {
        Note newNote = new Note("note");
        newNote.newNote(note, ldt);
        notes.add(newNote);
    }
    public static void addNewRating(int rating,int id) {
        Note newRate = new Note("rating");
        newRate.newRating(rating,id);
        ratings.add(newRate);
    }

    public static synchronized void LoadAllNotes() {
        List<Note> allNotes = CsvReader.readNotes(Constants.NOTES_CSV);
        for (Note n:allNotes){
            if (n.getType().equals("note")){
                notes.add(n);
            }
            else if (n.getType().equals("rating")){
                ratings.add(n);
            }
            else{
                throw new IllegalArgumentException("Invalid note type: " + n.getType());
            }
        }
    }
    public static synchronized Optional<Note> getNoteById(int id) {
        return notes.stream().filter(note -> note.getId() == id).findFirst();
    }
    public static synchronized Optional<Note> getRatingById(int id) {
        return ratings.stream().filter(rate -> rate.getId() == id).findFirst();
    }
    public static synchronized void updateNote(Note updatedNote) {
        notes.replaceAll(n -> n.getId() == updatedNote.getId() ? updatedNote : n);
    }
    public static synchronized void updateRating(Note updatedRate) {
        ratings.replaceAll(r -> r.getId() == updatedRate.getId() ? updatedRate : r);
    }

    public static synchronized List<Note> getAllNotes() {
        return new ArrayList<>(notes);
    }
    public static synchronized List<Note> getAllRatings() {
        return new ArrayList<>(ratings);
    }
    public static synchronized void deleteNote(int id) {
        notes.removeIf(note -> note.getId() == id);
    }
    public static void saveNotes() {
        CsvWriter.writeToCsv(Constants.NOTES_CSV, notes);
    }
    public static void saveratings() {
        CsvWriter.writeToCsv(Constants.NOTES_CSV, ratings);
    }

}
