package inventory.threads;

import java.time.LocalDate;
import java.util.logging.Logger;

import inventory.models.enums.TaskStatus;
import inventory.services.FinishedProductService;
import inventory.services.ItemService;
import inventory.services.ProductService;
import inventory.services.TaskService;
import inventory.services.UserService;
import inventory.services.ProductLineService;

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