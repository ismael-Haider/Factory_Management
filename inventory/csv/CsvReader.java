package inventory.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvReader {
    public static List<inventory.models.Item> readItems(String fileName) throws IOException {
        List<inventory.models.Item> items = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty())
                    continue;
                items.add(inventory.models.Item.fromCSV(line));
            }
        }
        return items;
    }

    public static List<inventory.models.Product> readProducts(String fileName) throws IOException {
        List<inventory.models.Product> products = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                products.add(inventory.models.Product.fromCSV(line));
            }
        }
        return products;
    }

    public static List<inventory.models.FinishedProduct> readFinishedProducts(String fileName) throws IOException {
        List<inventory.models.FinishedProduct> finishedProducts = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                finishedProducts.add(inventory.models.FinishedProduct.fromCSV(line));
            }
        }
        return finishedProducts;
    }

    public static List<inventory.models.Task> readTasks(String fileName) throws IOException {
        List<inventory.models.Task> tasks = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                tasks.add(inventory.models.Task.fromCSV(line));
            }
        }
        return tasks;
    }

    public static List<inventory.models.ProductLine> readProductLines(String fileName) throws IOException {
        List<inventory.models.ProductLine> productLines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                productLines.add(inventory.models.ProductLine.fromCSV(line));
            }
        }
        return productLines;
    }

    public static List<inventory.models.User> readUsers(String fileName) throws IOException {
        List<inventory.models.User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                users.add(inventory.models.User.fromCSV(line));
            }
        }
        return users;
    }
}