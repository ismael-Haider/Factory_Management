package inventory.services;

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
        String newName = item.getName() == null ? "" : item.getName().trim();
        for (Item i : items) {
            String existingName = i.getName() == null ? "" : i.getName().trim();
            if (newName.equalsIgnoreCase(existingName)) {
                i.addQuantity(item.getQuantity());
                return;
            }
        }
        items.add(item);
    }

    public static synchronized Optional<Item> getItemById(int id) {
        return items.stream().filter(item -> item.getId() == id).findFirst();
    }

    public static synchronized Optional<Item> getItemByName(String name) {
        if (name == null) return Optional.empty();
        String q = name.trim();
        return items.stream().filter(item -> item.getName() != null && item.getName().equalsIgnoreCase(q)).findFirst();
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
        items = CsvReader.readItems(Constants.ITEMS_CSV);
    }

    public static void saveItems() {
        CsvWriter.writeToCsv(Constants.ITEMS_CSV, items);
    }
}