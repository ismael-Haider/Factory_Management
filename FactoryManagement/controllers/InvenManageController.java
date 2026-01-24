package FactoryManagement.controllers;

import java.util.ArrayList;
import java.util.List;

import FactoryManagement.models.*;
import FactoryManagement.services.*;
import FactoryManagement.utils.Exceptions;

public class InvenManageController {

    public InvenManageController() {

    }

    public boolean add_item(String name, String category, double price, int quantity, int minQuantity) {
        name = name.toLowerCase();
        category = category.toLowerCase();
        if (quantity < minQuantity) {
            return false;
        }
        ItemService.addItem(new Item(name, category, price, quantity, minQuantity));
        return true;
    }

    public List<Item> view_items() {
        return ItemService.getAllItems();
    }

    public void update_item(int id, String name, String category, double price, int quantity, int minQuantity) {
        Item item = ItemService.getItemById(id).orElse(null);
        if (item == null) {
            return;
        }
        if (!name.equals(item.getName()))
            item.setName(name);
        if (!category.equals(item.getCategory()))
            item.setCategory(category);
        if (price != item.getPrice()) {
            item.setPrice(price);
        }
        if (quantity != item.getQuantity())
            item.setQuantity(quantity);
        if (minQuantity != item.getMinQuantity())
            item.setMinQuantity(minQuantity);
    }

    public Item SearchById(int id) {
        return ItemService.getItemById(id).get();
    }

    public void delete_item(int id) {
        ItemService.deleteItem(id);
    }

    public List<Item> searchByName(String name) {
        List<Item> items = ItemService.getAllItems();
        List<Item> newItem = new ArrayList<>();
        for (Item item : items) {
            if (item.getName().contains(name)) {
                newItem.add(item);
            }
        }
        return newItem;
    }

    public List<Item> searchByCategory(String category) {
        List<Item> items = ItemService.getAllItems();
        List<Item> newItem = new ArrayList<>();
        for (Item item : items) {
            if (item.getCategory().contains(category)) {
                newItem.add(item);
            }
        }
        return newItem;
    }

    public List<Item> availableItems() {

        List<Item> items = ItemService.getAllItems();
        List<Item> newItem = new ArrayList<>();
        for (Item item : items) {
            if (item.getQuantity() >= item.getMinQuantity()) {
                newItem.add(item);
            }
        }

        return newItem;

    }

    public List<Item> runOutItems() {

        List<Item> items = ItemService.getAllItems();
        List<Item> newItem = new ArrayList<>();
        for (Item item : items) {
            if (item.getQuantity() == 0) {
                newItem.add(item);
            }
        }

        return newItem;

    }

    public List<Item> unAvailableItems() {

        List<Item> items = ItemService.getAllItems();
        List<Item> newItem = new ArrayList<>();
        for (Item item : items) {
            if (item.getQuantity() < item.getMinQuantity()) {
                newItem.add(item);
            }
        }
        return newItem;

    }

    public void saveItems() {
        TaskService.saveTasks();
        FinishedProductService.saveFinishedProducts();
        ItemService.saveItems();
        ProductLineService.saveProductLines();
        ProductService.saveProducts();
        UserService.saveUsers();
    }

    public void exit() {
        saveItems();
        ProductLineService.getAllProductLines()
                .forEach(pl -> pl.setStatus(FactoryManagement.models.enums.ProductLineStatus.STOP));
        System.exit(0);
    }

    public void disrememberUser(User user) {
        UserService.disrememberUser(user);
    }

    public void recordError(String errorMessage) {
        Exceptions.saveError(errorMessage);
    }

}
