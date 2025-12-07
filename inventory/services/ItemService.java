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
    private List<Item> items = new ArrayList<>();

    public ItemService() {
        loadItems();
    }

    public synchronized void addItem(Item item) {
        items.add(item);
        saveItems();
    }

    public synchronized Optional<Item> getItemById(int id) {
        return items.stream().filter(item -> item.getId() == id).findFirst();
    }

    public synchronized List<Item> getAllItems() {
        return new ArrayList<>(items);
    }

    public synchronized void updateItem(Item updatedItem) {
        items.replaceAll(item -> item.getId() == updatedItem.getId() ? updatedItem : item);
        saveItems();
    }

    public synchronized void deleteItem(int id) {
        items.removeIf(item -> item.getId() == id);
        saveItems();
    }

    private void loadItems() {
        try {
            items = CsvReader.readItems(Constants.ITEMS_CSV);
        } catch (IOException e) {
            // File might not exist yet; start with empty list
        }
    }

    private void saveItems() {
        try {
            CsvWriter.writeToCsv(Constants.ITEMS_CSV, items);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}