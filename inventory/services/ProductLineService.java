package inventory.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import inventory.config.Constants;
import inventory.csv.CsvReader;
import inventory.csv.CsvWriter;
import inventory.models.ProductLine;

public class ProductLineService {
    private static List<ProductLine> productLines = new ArrayList<>();

    public static void init() {
        loadProductLines();
    }

    public static synchronized void addProductLine(ProductLine productLine) {
        productLines.add(productLine);
    }

    public static synchronized Optional<ProductLine> getProductLineById(int id) {
        return productLines.stream().filter(pl -> pl.getId() == id).findFirst();
    }

    public static synchronized List<ProductLine> getAllProductLines() {
        return new ArrayList<>(productLines);
    }

    public static synchronized void updateProductLine(ProductLine updatedProductLine) {
        productLines.replaceAll(pl -> pl.getId() == updatedProductLine.getId() ? updatedProductLine : pl);
    }

    private static void loadProductLines() {
            productLines = CsvReader.readProductLines(Constants.PRODUCT_LINES_CSV);
    }

    public static void saveProductLines() {
            CsvWriter.writeToCsv(Constants.PRODUCT_LINES_CSV, productLines);
    }
}