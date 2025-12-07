package inventory.threads;

import java.time.LocalDate;
import java.util.logging.Logger;

import inventory.services.FinishedProductService;
import inventory.services.TaskService;

public class InventoryUpdater extends Thread {
    private static final Logger logger = Logger.getLogger(InventoryUpdater.class.getName());
    private final TaskService taskService;
    private final FinishedProductService finishedProductService;
    private volatile boolean running = true;

    public InventoryUpdater(TaskService taskService, FinishedProductService finishedProductService) {
        this.taskService = taskService;
        this.finishedProductService = finishedProductService;
    }

    @Override
    public void run() {
        while (running) {
            try {
                LocalDate today = LocalDate.now();
                // Find finished tasks with today's delivered date
                taskService.getTasksByDeliveredDate(today).forEach(task -> {
                    // Reduce quantity from finished products
                    finishedProductService.reduceQuantity(task.getProductId(), task.getQuantity());
                    logger.info("Reduced inventory for product ID " + task.getProductId() + " by " + task.getQuantity());
                });

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