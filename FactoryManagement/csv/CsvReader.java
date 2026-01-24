package FactoryManagement.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvReader {
    public static List<FactoryManagement.models.Item> readItems(String fileName) {
        List<FactoryManagement.models.Item> items = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null&&!line.isEmpty()) {
                if (line.isEmpty())
                    continue;
                items.add(FactoryManagement.models.Item.fromCSV(line));
            }
        }catch(IOException e){
            try{CsvWriter.saveError(fileName+" doesn't exist. ");}
            catch(IOException ex){ex.printStackTrace();}
            return new ArrayList<>();
        }
        return items;
    }

    public static List<FactoryManagement.models.Product> readProducts(String fileName) {
        List<FactoryManagement.models.Product> products = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null&&!line.isEmpty()) {
                products.add(FactoryManagement.models.Product.fromCSV(line));
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

    public static List<FactoryManagement.models.FinishedProduct> readFinishedProducts(String fileName) {
        List<FactoryManagement.models.FinishedProduct> finishedProducts = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null&&!line.isEmpty()) {
                finishedProducts.add(FactoryManagement.models.FinishedProduct.fromCSV(line));
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

    public static List<FactoryManagement.models.Task> readTasks(String fileName) {
        List<FactoryManagement.models.Task> tasks = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null&&!line.isEmpty()) {
                tasks.add(FactoryManagement.models.Task.fromCSV(line));
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

    public static List<FactoryManagement.models.ProductLine> readProductLines(String fileName) {
        List<FactoryManagement.models.ProductLine> productLines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null&&!line.isEmpty()) {
                productLines.add(FactoryManagement.models.ProductLine.fromCSV(line));
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

    public static List<FactoryManagement.models.User> readUsers(String fileName) throws IOException {
        List<FactoryManagement.models.User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null&&!line.isEmpty()) {
                users.add(FactoryManagement.models.User.fromCSV(line));
            }
        }
        return users;
    }

    public static List<FactoryManagement.models.Note> readNotes(String fileName) {
        List<FactoryManagement.models.Note> notes = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                notes.add(FactoryManagement.models.Note.fromCSV(line));
            }
        } catch (IOException e) {
            try { CsvWriter.saveError(fileName + " doesn't exist"); } catch (IOException ex) { ex.printStackTrace(); }
            return new ArrayList<>();
        }
        return notes;
    }
}