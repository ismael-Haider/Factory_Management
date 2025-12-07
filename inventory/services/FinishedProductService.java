package inventory.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import inventory.config.Constants;
import inventory.csv.CsvReader;
import inventory.csv.CsvWriter;
import inventory.models.FinishedProduct;

public class FinishedProductService {
    private List<FinishedProduct> finishedProducts = new ArrayList<>();

    public FinishedProductService() {
        loadFinishedProducts();
    }

    public synchronized void addFinishedProduct(FinishedProduct finishedProduct) {
        
        finishedProducts.add(finishedProduct);
        saveFinishedProducts();
    }

    public synchronized Optional<FinishedProduct> getFinishedProductByProductId(int productId) {
        return finishedProducts.stream().filter(fp -> fp.getProductId() == productId).findFirst();
    }

    public synchronized List<FinishedProduct> getAllFinishedProducts() {
        return new ArrayList<>(finishedProducts);
    }

    public synchronized void updateFinishedProduct(FinishedProduct updatedFinishedProduct) {
        finishedProducts.replaceAll(fp -> fp.getProductId() == updatedFinishedProduct.getProductId() ? updatedFinishedProduct : fp);
        saveFinishedProducts();
    }

    public synchronized void deleteFinishedProduct(int productId) {
        finishedProducts.removeIf(fp -> fp.getProductId() == productId);
        saveFinishedProducts();
    }

    public synchronized void reduceQuantity(int productId, int quantity) {
        getFinishedProductByProductId(productId).ifPresent(fp -> {
            fp.setQuantity(fp.getQuantity() - quantity);
            updateFinishedProduct(fp);
        });
    }

    private void loadFinishedProducts() {
        try {
            finishedProducts = CsvReader.readFinishedProducts(Constants.FINISHED_PRODUCTS_CSV);
        } catch (IOException e) {
            // File might not exist yet
        }
    }

    private void saveFinishedProducts() {
        try {
            CsvWriter.writeToCsv(Constants.FINISHED_PRODUCTS_CSV, finishedProducts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}