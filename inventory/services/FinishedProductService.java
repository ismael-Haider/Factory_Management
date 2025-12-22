package inventory.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import inventory.config.Constants;
import inventory.csv.CsvReader;
import inventory.csv.CsvWriter;
import inventory.models.FinishedProduct;
import inventory.utils.Exceptions;

public class FinishedProductService {
    private static List<FinishedProduct> finishedProducts = new ArrayList<>();

    // Call once at startup
    public static void init() {
        loadFinishedProducts();
    }

    public static synchronized void addFinishedProduct(FinishedProduct finishedProduct) {
        finishedProducts.add(finishedProduct);
    }

    public static synchronized void addFinishedProduct(int productId, int quantity) {
        FinishedProduct finishedProduct = getFinishedProductByProductId(productId).orElse(null);
        if (finishedProduct != null) {
            finishedProduct.setQuantity(finishedProduct.getQuantity() + quantity);
            // updateFinishedProduct(finishedProduct);
            return;
        }
        finishedProduct = new FinishedProduct(productId, ProductService.getProductById(productId).get().getName(),
                quantity);
        finishedProducts.add(finishedProduct);
    }

    public static synchronized Optional<FinishedProduct> getFinishedProductByProductId(int productId) {
        return finishedProducts.stream().filter(fp -> fp.getProductId() == productId).findFirst();
    }

    public static synchronized List<FinishedProduct> getAllFinishedProducts() {
        return new ArrayList<>(finishedProducts);
    }

    // public static synchronized void updateFinishedProduct(FinishedProduct
    // updatedFinishedProduct) {
    // finishedProducts.replaceAll(fp -> fp.getProductId() ==
    // updatedFinishedProduct.getProductId() ? updatedFinishedProduct : fp);
    // }

    public static synchronized void deleteFinishedProduct(int productId) {
        finishedProducts.removeIf(fp -> fp.getProductId() == productId);
    }

    public static synchronized void reduceQuantity(int productId, int quantity) {
        getFinishedProductByProductId(productId).ifPresent(fp -> {
            fp.reduceQuantity(quantity);
            // updateFinishedProduct(fp);
        });
    }

    private static void loadFinishedProducts() {
        finishedProducts = CsvReader.readFinishedProducts(Constants.FINISHED_PRODUCTS_CSV);
    }

    public static void saveFinishedProducts() {
        CsvWriter.writeToCsv(Constants.FINISHED_PRODUCTS_CSV, finishedProducts);
    }
}