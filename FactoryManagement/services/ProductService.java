package FactoryManagement.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import FactoryManagement.config.Constants;
import FactoryManagement.csv.CsvReader;
import FactoryManagement.csv.CsvWriter;
import FactoryManagement.models.Product;

public class ProductService {
    private static List<Product> products = new ArrayList<>();

    public static void init() {
        loadProducts();
    }

    public static synchronized void addProduct(Product product) {
        Map<Integer, Integer> itemQuantities = product.getItemQuantities();
        for (Integer itemId : itemQuantities.keySet()) {
            if (ItemService.getItemById(itemId).isEmpty()) {
                return;
            }
        }
        products.add(product);
    }

    public static synchronized Optional<Product> getProductById(int id) {
        return products.stream().filter(product -> product.getId() == id).findFirst();
    }

    public static synchronized Optional<Product> getProductByName(String name) {
        return products.stream().filter(p -> p.getName().equals(name)).findFirst();
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
        products = CsvReader.readProducts(Constants.PRODUCTS_CSV);
    }

    public static void saveProducts() {
        CsvWriter.writeToCsv(Constants.PRODUCTS_CSV, products);
    }
}