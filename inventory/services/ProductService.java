package inventory.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


import inventory.config.Constants;
import inventory.csv.CsvReader;
import inventory.csv.CsvWriter;
import inventory.models.Product;

public class ProductService {
    private List<Product> products = new ArrayList<>();
    private ItemService itemService; // To validate item IDs

    public ProductService(ItemService itemService) {
        this.itemService = itemService;
        loadProducts();
    }

    public synchronized void addProduct(Product product) {
        // Prompt for item quantities (simulate user input)
        Map<Integer, Integer> itemQuantities = product.getItemQuantities();
        for (Integer itemId : itemQuantities.keySet()) {
            if (itemService.getItemById(itemId).isEmpty()) {
                System.out.println("Invalid item ID: " + itemId);
                return;
            }
        }
        products.add(product);
        saveProducts();
    }

    public synchronized Optional<Product> getProductById(int id) {
        return products.stream().filter(product -> product.getId() == id).findFirst();
    }

    public synchronized List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    public synchronized void updateProduct(Product updatedProduct) {
        products.replaceAll(product -> product.getId() == updatedProduct.getId() ? updatedProduct : product);
        saveProducts();
    }

    public synchronized void deleteProduct(int id) {
        products.removeIf(product -> product.getId() == id);
        saveProducts();
    }

    private void loadProducts() {
        try {
            products = CsvReader.readProducts(Constants.PRODUCTS_CSV);
        } catch (IOException e) {
            // File might not exist yet
        }
    }

    private void saveProducts() {
        try {
            CsvWriter.writeToCsv(Constants.PRODUCTS_CSV, products);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}