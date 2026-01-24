package FactoryManagement.threads;

import java.time.LocalDate;
import FactoryManagement.models.enums.TaskStatus;
import FactoryManagement.services.FinishedProductService;
import FactoryManagement.services.ItemService;
import FactoryManagement.services.ProductLineService;
import FactoryManagement.services.ProductService;
import FactoryManagement.services.TaskService;
import FactoryManagement.services.UserService;

public class InventoryUpdater extends Thread {
    private volatile boolean running = true;

    public InventoryUpdater() {
    }

    @Override
    public void run() {
        while (running) {
            try {
                LocalDate today = LocalDate.now();
                TaskService.getTasksByDeliveredDate(today).forEach(task -> {
                    if (task.getStatus() == TaskStatus.FINISHED && !task.isDelivered()) {
                        FinishedProductService.reduceQuantity(task.getProductId(), task.getQuantity());
                        task.setDelivered(true);
                        if (FinishedProductService.getFinishedProductByProductId(task.getProductId()).get()
                                .getQuantity() <= 0) {
                            FinishedProductService.deleteFinishedProduct(task.getProductId());
                        }
                    }
                });
                ItemService.saveItems();
                TaskService.saveTasks();
                ProductLineService.saveProductLines();
                ProductService.saveProducts();
                UserService.saveUsers();
                FinishedProductService.saveFinishedProducts();
                Thread.sleep(86400000);
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