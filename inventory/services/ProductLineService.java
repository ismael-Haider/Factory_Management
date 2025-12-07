package inventory.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import inventory.config.Constants;
import inventory.csv.CsvReader;
import inventory.csv.CsvWriter;
import inventory.models.ProductLine;

public class ProductLineService {
    private List<ProductLine> productLines = new ArrayList<>();

    public ProductLineService() {
        loadProductLines();
    }

    public synchronized void addProductLine(ProductLine productLine) {
        productLines.add(productLine);
        saveProductLines();
    }

    public synchronized Optional<ProductLine> getProductLineById(int id) {
        return productLines.stream().filter(pl -> pl.getId() == id).findFirst();
    }

    public synchronized List<ProductLine> getAllProductLines() {
        return new ArrayList<>(productLines);
    }

    public synchronized void updateProductLine(ProductLine updatedProductLine) {
        productLines.replaceAll(pl -> pl.getId() == updatedProductLine.getId() ? updatedProductLine : pl);
        saveProductLines();
    }

    public synchronized void deleteProductLine(int id) {
        productLines.removeIf(pl -> pl.getId() == id);
        saveProductLines();
    }

    private void loadProductLines() {
        try {
            productLines = CsvReader.readProductLines(Constants.PRODUCT_LINES_CSV);
        } catch (IOException e) {
            // File might not exist yet
        }
    }

    private void saveProductLines() {
        try {
            CsvWriter.writeToCsv(Constants.PRODUCT_LINES_CSV, productLines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}