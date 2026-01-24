package FactoryManagement.threads;

import java.time.LocalDate;
import java.util.logging.Logger;

import FactoryManagement.models.enums.TaskStatus;
import FactoryManagement.services.FinishedProductService;
import FactoryManagement.services.ItemService;
import FactoryManagement.services.ProductLineService;
import FactoryManagement.services.ProductService;
import FactoryManagement.services.TaskService;
import FactoryManagement.services.UserService;

public class InventoryUpdater extends Thread {
    private static final Logger logger = Logger.getLogger(InventoryUpdater.class.getName());
    private volatile boolean running = true;

    public InventoryUpdater() {
    }

    @Override
    public void run() {
        while (running) {
            try {
                LocalDate today = LocalDate.now();
                // Find finished tasks with today's delivered date
                TaskService.getTasksByDeliveredDate(today).forEach(task -> {
                    // Reduce quantity from finished products
                    if (task.getStatus() == TaskStatus.FINISHED&&!task.isDelivered()){
                        FinishedProductService.reduceQuantity(task.getProductId(), task.getQuantity());
                        task.setDelivered(true);
                        if (FinishedProductService.getFinishedProductByProductId(task.getProductId()).get().getQuantity() <= 0){
                            FinishedProductService.deleteFinishedProduct(task.getProductId());
                        }
                    }
                    logger.info("Reduced inventory for product ID " + task.getProductId() + " by " + task.getQuantity());
                });
                ItemService.saveItems();
                TaskService.saveTasks();
                ProductLineService.saveProductLines();
                ProductService.saveProducts();
                UserService.saveUsers();
                FinishedProductService.saveFinishedProducts();
                
                // Sleep for a day (simulate daily check)
                Thread.sleep(86400000); // 24 hours in milliseconds (adjust for testing)
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void stopUpdating() {
        running = false;
    }
}