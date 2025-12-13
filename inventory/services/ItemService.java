package inventory.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import inventory.config.Constants;
import inventory.csv.CsvReader;
import inventory.csv.CsvWriter;
import inventory.models.Item;

public class ItemService {
    private static List<Item> items = new ArrayList<>();

    public static void init() {
        loadItems();
    }

    ///kais
    public static synchronized void addItem(Item item) {
        for (Item i : items) {
            if (item.getName() == i.getName()) {
                i.addQuantity(item.getQuantity() );
                return;
            }
        }
        items.add(item);
    }

    public static synchronized Optional<Item> getItemById(int id) {
        return items.stream().filter(item -> item.getId() == id).findFirst();
    }

    public static synchronized List<Item> getAllItems() {
        return new ArrayList<>(items);
    }

    public static synchronized void updateItem(Item updatedItem) {
        items.replaceAll(item -> item.getId() == updatedItem.getId() ? updatedItem : item);
    }

    public static synchronized void deleteItem(int id) {
        items.removeIf(item -> item.getId() == id);
    }

    private static void loadItems() {
        try {
            items = CsvReader.readItems(Constants.ITEMS_CSV);
        } catch (IOException e) {
            // File might not exist yet; start with empty list
        }
    }

    public static void saveItems() {
        try {
            CsvWriter.writeToCsv(Constants.ITEMS_CSV, items);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}