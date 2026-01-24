package FactoryManagement.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Note {
    static int counter = 0;
    int id;
    int rating = 0;
    String note = "";
    String type;
    LocalDateTime ldt;

    public Note(String type) {
        this.type = type;
    }

    public void newNote(String note, LocalDateTime ldt) {
        this.id = ++counter;
        this.note = note;
        this.ldt = ldt;
    }

    public void newNote(int id, String note, LocalDateTime ldt) {
        this.id = counter = id;
        this.note = note;
        this.ldt = ldt;
    }

    public void newRating(int id, int rating) {
        this.rating = rating;
        this.id = id;
    }

    public String toCSV() {
        if (type.equals("note")) {
            return type + "," + id + "," + ldt.format(DateTimeFormatter.ofPattern("yyyy/MM/dd - HH:mm")) + "," + note;
        } else if (type.equals("rating")) {
            return type + "," + id + "," + rating;
        } else
            throw new IllegalArgumentException("Invalid note type: " + type);
    }

    public String toString() {
        return toCSV();
    }

    public String getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public int getRating() {
        return rating;
    }

    public String getNote() {
        return note;
    }

    public LocalDateTime getLdt() {
        return ldt;
    }

    public void setRating(int rating) {
        if (rating < 0 || rating > 5)
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        this.rating = rating;
    }

    public static Note fromCSV(String line) {
        String[] note = line.split(",");
        Note n = new Note(note[0]);
        if (n.type.equals("note")) {
            n.newNote(Integer.parseInt(note[1]), note[3],
                    LocalDateTime.parse(note[2], DateTimeFormatter.ofPattern("yyyy/MM/dd - HH:mm")));
        } else if (n.type.equals("rating")) {
            n.newRating(Integer.parseInt(note[1]), Integer.parseInt(note[2]));
        }
        return n;
    }
}