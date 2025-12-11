package inventory.threads;

import inventory.models.ProductLine;
import inventory.services.ProductLineService;
import inventory.services.TaskService;

public class ProductionLine_thread extends Thread {
    ProductLine productLine;
    ProductLineService productLineService;
    TaskService taskService;
    private volatile boolean running = true;
    public ProductionLine_thread(ProductLine productLine, ProductLineService productLineService, TaskService taskService) {
        this.productLine = productLine;
        this.taskService = taskService;
        this.productLineService = productLineService;
    }
    @Override
    public void run() {
        while (running){
            if (productLine.getStatus().equals(inventory.models.enums.ProductLineStatus.MAINTENANCE)){
                // try {
                //     Thread.sleep((int)(1000));
                // } catch (InterruptedException e) {
                //     Thread.currentThread().interrupt();
                // }
                terminate();
                continue;
            }
            //stop doesnt mean that pruduction line is closed it is just not working because there is no task
            if (productLine.getStatus().equals(inventory.models.enums.ProductLineStatus.STOP)&&!productLine.isQueueEmpty()){
                
                int taskId=productLine.peekTask();
                inventory.models.Task task=taskService.getTaskById(taskId).get();
                if (task.getStatus().equals(inventory.models.enums.TaskStatus.FINISHED)){
                    productLine.pollTask();
                    System.out.println(productLine);
                    productLineService.updateProductLine(productLine);
                    continue;
                }
                System.out.println("ismail");
                task.setStatus(inventory.models.enums.TaskStatus.IN_PROGRESS);
                productLine.setStatus(inventory.models.enums.ProductLineStatus.RUNNING);
                task.addPercentage(productLine.getEfficiency());
                taskService.updateTask(task);
                if (task.getPercentage()>=100.0){
                    task.setStatus(inventory.models.enums.TaskStatus.FINISHED);
                    productLine.setStatus(inventory.models.enums.ProductLineStatus.STOP);
                    productLine.pollTask();
                    taskService.updateTask(task);
                }
                System.out.println("fuckoff");
                productLineService.updateProductLine(productLine);
            }
            if (productLine.getStatus().equals(inventory.models.enums.ProductLineStatus.RUNNING)&&!productLine.isQueueEmpty()){
                int taskId=productLine.peekTask();
                inventory.models.Task task=taskService.getTaskById(taskId).get();
                if (task.getStatus().equals(inventory.models.enums.TaskStatus.FINISHED)){
                    productLine.pollTask();
                    System.out.println(productLine);
                    productLineService.updateProductLine(productLine);
                    continue;
                }
                task.addPercentage(productLine.getEfficiency());
                taskService.updateTask(task);
                if (task.getPercentage()>=100.0){
                    task.setStatus(inventory.models.enums.TaskStatus.FINISHED);
                    productLine.setStatus(inventory.models.enums.ProductLineStatus.STOP);
                    productLine.pollTask();
                    taskService.updateTask(task);
                }
                productLineService.updateProductLine(productLine);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("hey");
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