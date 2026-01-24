package FactoryManagement.csv;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import FactoryManagement.config.Constants;
import FactoryManagement.models.*;
public class CsvWriter {
    public static <T> void writeToCsv(String fileName, List<T> objects) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName,false),true)) {
            for (T obj : objects) {
                // Assuming each object has a toCSV() method
                if (obj instanceof Item) {
                    writer.write(((Item) obj).toCSV() + "\n");
                } else if (obj instanceof Product) {
                    writer.write(((Product) obj).toCSV() + "\n");
                } else if (obj instanceof FinishedProduct) {
                    writer.write(((FinishedProduct) obj).toCSV() + "\n");
                } else if (obj instanceof Task) {
                    writer.write(((Task) obj).toCSV() + "\n");
                } else if (obj instanceof ProductLine) {
                    writer.write(((ProductLine) obj).toCSV() + "\n");
                } else if (obj instanceof User) {
                    writer.write(((User) obj).toCSV() + "\n");
                } else if (obj instanceof Note) {
                    writer.write(((Note) obj).toCSV() + "\n");
                }
            }
        }
        catch (IOException e) {
            try{
                saveError(fileName+" doesn't exist.");}
            catch(IOException ex){ex.printStackTrace();}
        }
    }
    public static void saveError(String message) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(Constants.ERROR_TXT,true),true)) {
            writer.println(message);
        }
    }
}