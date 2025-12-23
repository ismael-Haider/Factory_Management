package inventory.models;

import inventory.models.enums.ProductLineStatus;
import inventory.threads.ProductionLine_thread;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

public class ProductLine {
    
    static int counter=0;
    private int id;
    private String name; // A, B, C, etc.
    private ProductLineStatus status;
    private double efficiency;
    private Queue<Integer> taskQueue; // Task IDs
    ProductionLine_thread productionLine_thread;

    public ProductLine(String name, double efficiency) {
        counter+=1;
        this.id = counter;
        this.name = name;
        this.status = ProductLineStatus.STOP;
        this.efficiency = efficiency;
        this.taskQueue = new LinkedList<>();
        this.productionLine_thread = new ProductionLine_thread(this);
        this.productionLine_thread.start();
    }

    // For loading from CSV (taskQueue as "id1,id2,id3")
    public ProductLine(int id, String name, ProductLineStatus status, double efficiency, String taskQueueStr) {
        counter=id;
        this.id = id;
        this.name = name;
        this.status = status;
        this.efficiency = efficiency;
        this.taskQueue = parseTaskQueue(taskQueueStr);
        this.productionLine_thread = new ProductionLine_thread(this);
        this.productionLine_thread.start();
    }

    private Queue<Integer> parseTaskQueue(String str) {
        Queue<Integer> queue = new LinkedList<>();
        if (str.isEmpty()) return queue;
        for (String id : str.split(",")) {
            queue.add(Integer.parseInt(id));
        }
        return queue;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public ProductLineStatus getStatus() { return status; }
    public void setStatus(ProductLineStatus status) { this.status = status; }
    public double getEfficiency() { return efficiency; }
    public void setEfficiency(double efficiency) { this.efficiency = efficiency; }
    public Queue<Integer> getTaskQueue() { return new LinkedList<>(taskQueue); }
    public void addTask(int taskId) { taskQueue.add(taskId); }
    public Integer pollTask() { return taskQueue.poll(); } // Remove and return next task
    public Integer peekTask() { return taskQueue.peek(); }
    public boolean isQueueEmpty() { return taskQueue.isEmpty(); }
    public void setThread(ProductionLine_thread productionLine_thread){this.productionLine_thread=productionLine_thread;}
    public void start(){setStatus(ProductLineStatus.RUNNING);}
    public void stop(){setStatus(ProductLineStatus.STOP);}
    public void removeTask(int taskId){taskQueue.remove(taskId);}

    // CSV Serialization
    public String toCSV() {
        StringBuilder sb = new StringBuilder();
        for (Integer taskId : taskQueue) {
            sb.append(taskId).append(",");
        }
        if (sb.length() > 0) sb.setLength(sb.length() - 1); // Remove last ,
        return id + "," + name + "," + status + "," + efficiency + "," + sb.toString();
    }

    public static ProductLine fromCSV(String csvLine) {
        String[] parts = csvLine.split(",", 5);
        return new ProductLine(Integer.parseInt(parts[0]), parts[1], ProductLineStatus.valueOf(parts[2]), Double.parseDouble(parts[3]), parts.length > 4 ? parts[4] : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductLine that = (ProductLine) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "name: "+name+", status: "+status;
    }
}