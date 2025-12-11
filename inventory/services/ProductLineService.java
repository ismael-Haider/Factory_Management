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
    private TaskService taskService;

    public ProductLineService(TaskService taskService) {
        this.taskService = taskService;
        if (taskService != null) {
            loadProductLines(taskService);
        }
    }
    
    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
        loadProductLines(taskService);
    }

    public synchronized void addProductLine(ProductLine productLine) {
        productLines.add(productLine);
    }

    public synchronized Optional<ProductLine> getProductLineById(int id) {
        return productLines.stream().filter(pl -> pl.getId() == id).findFirst();
    }

    public synchronized List<ProductLine> getAllProductLines() {
        return new ArrayList<>(productLines);
    }

    public synchronized void updateProductLine(ProductLine updatedProductLine) {
        productLines.replaceAll(pl -> pl.getId() == updatedProductLine.getId() ? updatedProductLine : pl);
    }

    private void loadProductLines(TaskService taskService) {
        try {
            productLines = CsvReader.readProductLines(Constants.PRODUCT_LINES_CSV,taskService,this);
        } catch (IOException e) {
            // File might not exist yet
        }
    }

    public void saveProductLines() {
        try {
            CsvWriter.writeToCsv(Constants.PRODUCT_LINES_CSV, productLines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}