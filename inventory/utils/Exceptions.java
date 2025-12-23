package inventory.utils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import inventory.csv.CsvWriter;

public class Exceptions {

    public static void saveError(String message) {
        LocalDateTime dt = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd - HH:mm:ss");
        try{
        CsvWriter.saveError(dt.format(formatter) + " - " + message);}
        catch(IOException e){
            System.err.println("the error file doesn't exist. ");
        }
    }
}
