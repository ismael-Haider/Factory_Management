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
    private static List<Product> products = new ArrayList<>();

    public static void init() {
        loadProducts();
    }

    public static synchronized void addProduct(Product product) {
        // Validate item IDs using static ItemService
        Map<Integer, Integer> itemQuantities = product.getItemQuantities();
        for (Integer itemId : itemQuantities.keySet()) {
            if (ItemService.getItemById(itemId).isEmpty()) {
                System.out.println("Invalid item ID: " + itemId);
                return;
            }
        }
        products.add(product);
    }

    public static synchronized Optional<Product> getProductById(int id) {
        return products.stream().filter(product -> product.getId() == id).findFirst();
    }

    public static synchronized List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    public static synchronized void updateProduct(Product updatedProduct) {
        products.replaceAll(product -> product.getId() == updatedProduct.getId() ? updatedProduct : product);
    }

    public static synchronized void deleteProduct(int id) {
        products.removeIf(product -> product.getId() == id);
    }

    private static void loadProducts() {
        try {
            products = CsvReader.readProducts(Constants.PRODUCTS_CSV);
        } catch (IOException e) {
            // File might not exist yet
        }
    }

    public static void saveProducts() {
        try {
            CsvWriter.writeToCsv(Constants.PRODUCTS_CSV, products);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}