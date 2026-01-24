package FactoryManagement.threads;

import FactoryManagement.models.ProductLine;
import FactoryManagement.services.ProductLineService;
import FactoryManagement.services.TaskService;

public class ProductionLine_thread extends Thread {
    ProductLine productLine;
    private volatile boolean running = true;

    public ProductionLine_thread(ProductLine productLine) {
        this.productLine = productLine;
    }

    @Override
    public void run() {
        while (running) {
            if (productLine.getStatus().equals(FactoryManagement.models.enums.ProductLineStatus.MAINTENANCE)) {
                try {
                    int taskId = productLine.peekTask();
                    FactoryManagement.models.Task task = TaskService.getTaskById(taskId).get();
                    task.setStatus(FactoryManagement.models.enums.TaskStatus.IN_QUEUE);
                    Thread.sleep((int) (1000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                continue;
            }

            if (productLine.getStatus().equals(FactoryManagement.models.enums.ProductLineStatus.STOP)
                    && !productLine.isQueueEmpty()) {
                int taskId = productLine.peekTask();
                FactoryManagement.models.Task task = TaskService.getTaskById(taskId).get();
                if (task.getStatus().equals(FactoryManagement.models.enums.TaskStatus.CANCELLED)) {
                    productLine.pollTask();
                    ProductLineService.updateProductLine(productLine);
                    continue;
                }
                if (task.getStatus().equals(FactoryManagement.models.enums.TaskStatus.FINISHED)) {
                    productLine.pollTask();
                    ProductLineService.updateProductLine(productLine);
                    continue;
                }
                task.setStatus(FactoryManagement.models.enums.TaskStatus.IN_PROGRESS);
                productLine.start();
                task.addPercentage(productLine.getEfficiency() * 100 / task.getQuantity());
                TaskService.updateTask(task);
                if (task.getPercentage() >= 100.0) {
                    task.setStatus(FactoryManagement.models.enums.TaskStatus.FINISHED);
                    productLine.setStatus(FactoryManagement.models.enums.ProductLineStatus.STOP);
                    productLine.pollTask();
                    TaskService.updateTask(task);
                }
                ProductLineService.updateProductLine(productLine);
            }
            if (productLine.getStatus().equals(FactoryManagement.models.enums.ProductLineStatus.RUNNING)
                    && !productLine.isQueueEmpty()) {
                int taskId = productLine.peekTask();
                FactoryManagement.models.Task task = TaskService.getTaskById(taskId).get();
                if (task.getStatus().equals(FactoryManagement.models.enums.TaskStatus.FINISHED)) {
                    productLine.pollTask();
                    ProductLineService.updateProductLine(productLine);
                    continue;
                }
                if (task.getStatus().equals(FactoryManagement.models.enums.TaskStatus.CANCELLED)) {
                    productLine.pollTask();
                    ProductLineService.updateProductLine(productLine);
                    continue;
                }
                task.addPercentage(productLine.getEfficiency() * 100 / task.getQuantity());
                TaskService.updateTask(task);
                if (task.getPercentage() >= 100.0) {
                    TaskService.finishTask(task.getId());
                    productLine.setStatus(FactoryManagement.models.enums.ProductLineStatus.STOP);
                    productLine.pollTask();
                    TaskService.updateTask(task);
                }
                ProductLineService.updateProductLine(productLine);
            } else if (productLine.isQueueEmpty()) {
                productLine.setStatus(FactoryManagement.models.enums.ProductLineStatus.STOP);
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