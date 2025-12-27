package inventory.threads;

import inventory.models.ProductLine;
import inventory.services.ProductLineService;
import inventory.services.TaskService;

public class ProductionLine_thread extends Thread {
    ProductLine productLine;
    private volatile boolean running = true;

    public ProductionLine_thread(ProductLine productLine) {
        this.productLine = productLine;
    }

    @Override
    public void run() {
        while (running) {
            if (productLine.getStatus().equals(inventory.models.enums.ProductLineStatus.MAINTENANCE)) {
                try {
                Thread.sleep((int)(1000));
                } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                }
                continue;
            }

            // stop doesnt mean that pruduction line is closed it is just not working
            // because there is no task
            if (productLine.getStatus().equals(inventory.models.enums.ProductLineStatus.STOP)
                    && !productLine.isQueueEmpty()) {

                int taskId = productLine.peekTask();
                inventory.models.Task task = TaskService.getTaskById(taskId).get();
                if (task.getStatus().equals(inventory.models.enums.TaskStatus.CANCELLED)) {
                    productLine.pollTask();
                    // add update after edit the task queue
                    ProductLineService.updateProductLine(productLine);
                    continue;
                }
                if (task.getStatus().equals(inventory.models.enums.TaskStatus.FINISHED)) {
                    productLine.pollTask();
                    ProductLineService.updateProductLine(productLine);
                    continue;
                }
                task.setStatus(inventory.models.enums.TaskStatus.IN_PROGRESS);
                productLine.start();
                task.addPercentage(productLine.getEfficiency() * 100 / task.getQuantity());
                TaskService.updateTask(task);
                if (task.getPercentage() >= 100.0) {
                    task.setStatus(inventory.models.enums.TaskStatus.FINISHED);
                    productLine.setStatus(inventory.models.enums.ProductLineStatus.STOP);
                    productLine.pollTask();
                    TaskService.updateTask(task);
                }
                ProductLineService.updateProductLine(productLine);
            }
            if (productLine.getStatus().equals(inventory.models.enums.ProductLineStatus.RUNNING)
                    && !productLine.isQueueEmpty()) {
                int taskId = productLine.peekTask();
                inventory.models.Task task = TaskService.getTaskById(taskId).get();
                if (task.getStatus().equals(inventory.models.enums.TaskStatus.FINISHED)) {
                    productLine.pollTask();
                    ProductLineService.updateProductLine(productLine);
                    continue;
                }
                if (task.getStatus().equals(inventory.models.enums.TaskStatus.CANCELLED)) {
                    productLine.pollTask();
                    // here update productLine 
                    ProductLineService.updateProductLine(productLine);
                    continue;
                }
                task.addPercentage(productLine.getEfficiency() * 100 / task.getQuantity());
                TaskService.updateTask(task);
                if (task.getPercentage() >= 100.0) {
                    TaskService.finishTask(task.getId());
                    productLine.setStatus(inventory.models.enums.ProductLineStatus.STOP);
                    productLine.pollTask();
                    TaskService.updateTask(task);
                }
                ProductLineService.updateProductLine(productLine);

            }// add this to check 
            else if(productLine.isQueueEmpty()){
                productLine.setStatus(inventory.models.enums.ProductLineStatus.STOP);
                ProductLineService.updateProductLine(productLine);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public synchronized void terminate() {
        running = false;
    }

    public synchronized void resumeProduction() {
        running = true;
    }
}