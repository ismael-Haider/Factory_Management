package inventory.threads;

import java.util.logging.Logger;

import inventory.models.enums.ProductLineStatus;
import inventory.models.enums.TaskStatus;
import inventory.services.ProductLineService;
import inventory.services.TaskService;

public class TaskProcessor extends Thread {
    private static final Logger logger = Logger.getLogger(TaskProcessor.class.getName());
    private volatile boolean running = true;

    public TaskProcessor() {
    }

    @Override
    public void run() { 
        while (running) {
            try {
                // Process each product line
                ProductLineService.getAllProductLines().forEach(productLine -> {
                    if (productLine.getStatus() == ProductLineStatus.RUNNING && !productLine.isQueueEmpty()) {
                        Integer taskId = productLine.pollTask();
                        TaskService.getTaskById(taskId).ifPresent(task -> {
                            // Simulate processing: update status to IN_PROGRESS, then FINISHED after some time
                            task.setStatus(TaskStatus.IN_PROGRESS);
                            TaskService.updateTask(task);
                            logger.info("Processing task ID: " + taskId);

                            // Simulate production time (e.g., based on efficiency)
                            try {
                                Thread.sleep((long) (1000 / productLine.getEfficiency())); // Shorter sleep for demo
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }

                            task.setStatus(TaskStatus.FINISHED);
                            task.setPercentage(100.0); // Full completion
                            TaskService.updateTask(task);
                            logger.info("Task ID " + taskId + " finished.");

                            // Update product line if queue is empty
                            if (productLine.isQueueEmpty()) {
                                productLine.setStatus(ProductLineStatus.STOP);
                                ProductLineService.updateProductLine(productLine);
                            }
                        });
                    }
                });

                // Sleep before next cycle
                Thread.sleep(5000); // Check every 5 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void stopProcessing() {
        running = false;
    }
}