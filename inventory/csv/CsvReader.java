package inventory.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvReader {
    public static List<inventory.models.Item> readItems(String fileName) {
        List<inventory.models.Item> items = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null&&!line.isEmpty()) {
                if (line.isEmpty())
                    continue;
                items.add(inventory.models.Item.fromCSV(line));
            }
        }catch(IOException e){
            try{CsvWriter.saveError(fileName+" doesn't exist. ");}
            catch(IOException ex){ex.printStackTrace();}
            return new ArrayList<>();
        }
        return items;
    }

    public static List<inventory.models.Product> readProducts(String fileName) {
        List<inventory.models.Product> products = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null&&!line.isEmpty()) {
                products.add(inventory.models.Product.fromCSV(line));
            }
        }
        catch(IOException e){
            try{
                CsvWriter.saveError(fileName+" doesn't exist");
            }
            catch(IOException ex){ex.printStackTrace();}
            return new ArrayList<>();
            }
        return products;
    }

    public static List<inventory.models.FinishedProduct> readFinishedProducts(String fileName) {
        List<inventory.models.FinishedProduct> finishedProducts = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null&&!line.isEmpty()) {
                finishedProducts.add(inventory.models.FinishedProduct.fromCSV(line));
            }
        }
        catch(IOException e){
            try{
                CsvWriter.saveError(fileName+" doesn't exist");
            }
            catch(IOException ex){ex.printStackTrace();}
            return new ArrayList<>();
            }
        return finishedProducts;
    }

    public static List<inventory.models.Task> readTasks(String fileName) {
        List<inventory.models.Task> tasks = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null&&!line.isEmpty()) {
                tasks.add(inventory.models.Task.fromCSV(line));
            }
        }
        catch(IOException e){
            try{
                CsvWriter.saveError(fileName+" doesn't exist");
            }
            catch(IOException ex){ex.printStackTrace();}
            return new ArrayList<>();
            }
        return tasks;
    }

    public static List<inventory.models.ProductLine> readProductLines(String fileName) {
        List<inventory.models.ProductLine> productLines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null&&!line.isEmpty()) {
                productLines.add(inventory.models.ProductLine.fromCSV(line));
            }
        }
        catch(IOException e){
            try{
                CsvWriter.saveError(fileName+" doesn't exist");
            }
            catch(IOException ex){ex.printStackTrace();}
            return new ArrayList<>();
            }
        return productLines;
    }

    public static List<inventory.models.User> readUsers(String fileName) throws IOException {
        List<inventory.models.User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null&&!line.isEmpty()) {
                users.add(inventory.models.User.fromCSV(line));
            }
        }
        return users;
    }

    public static List<inventory.models.Note> readNotes(String fileName) {
        List<inventory.models.Note> notes = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                notes.add(inventory.models.Note.fromCSV(line));
            }
        } catch (IOException e) {
            try { CsvWriter.saveError(fileName + " doesn't exist"); } catch (IOException ex) { ex.printStackTrace(); }
            return new ArrayList<>();
        }
        return notes;
    }
}