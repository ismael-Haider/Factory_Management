package inventory.models;

import inventory.models.enums.TaskStatus;

import java.time.LocalDate;
import java.util.Objects;

public class Task {
    static int counter = 0;
    private int id;
    private int productId; // Links to Product.Id
    private int quantity;
    private String clientName;
    private LocalDate startDate;
    private LocalDate deliveredDate;
    private TaskStatus status;
    private int productLineId; // Links to ProductLine.Id
    private double percentage;

    public Task(int productId, int quantity, String clientName, LocalDate startDate, LocalDate deliveredDate,
            int productLineId) {
        counter += 1;
        this.id = inventory.utils.IdGenerator.generateId(Task.class, counter);
        this.productId = productId;
        this.quantity = quantity;
        this.clientName = clientName;
        this.startDate = startDate;
        this.deliveredDate = deliveredDate;
        this.status = inventory.models.enums.TaskStatus.IN_QUEUE;
        this.productLineId = productLineId;
        this.percentage = 0.0;
        // Try to add to product line if it exists
        inventory.services.ProductLineService.getProductLineById(productLineId)
                .ifPresent(pl -> pl.addTask(this.getId()));
    }

    // For loading from CSV
    public Task(int id, int productId, int quantity, String clientName, LocalDate startDate, LocalDate deliveredDate,
            TaskStatus status, int productLineId, double percentage) {
        counter += 1;
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.clientName = clientName;
        this.startDate = startDate;
        this.deliveredDate = deliveredDate;
        this.status = status;
        this.productLineId = productLineId;
        this.percentage = percentage;
        // When loading from CSV we don't re-add to the product line queue
        // (the product line's CSV already contains the queue)
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void reduceQuantity(int quantity) {
        this.quantity -= quantity;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getDeliveredDate() {
        return deliveredDate;
    }

    public void setDeliveredDate(LocalDate deliveredDate) {
        this.deliveredDate = deliveredDate;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public int getProductLineId() {
        return productLineId;
    }

    public void setProductLineId(int productLineId) {
        this.productLineId = productLineId;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public void addPercentage(double increment) {
        this.percentage += increment;
        if (this.percentage > 100.0) {
            this.percentage = 100.0;
        }
    }

    // CSV Serialization
    public String toCSV() {
        return id + "," + productId + "," + quantity + "," + clientName + "," + startDate + "," + deliveredDate + ","
                + status + "," + productLineId + "," + percentage;
    }

    public static Task fromCSV(String csvLine) {
        String[] parts = csvLine.split(",");
        return new Task(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), parts[3],
                LocalDate.parse(parts[4]), LocalDate.parse(parts[5]), TaskStatus.valueOf(parts[6]),
                Integer.parseInt(parts[7]), Double.parseDouble(parts[8]));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{" + "id=" + id + ", productId=" + productId + ", quantity=" + quantity + ", clientName='"
                + clientName + '\'' + ", startDate=" + startDate + ", deliveredDate=" + deliveredDate + ", status="
                + status + ", productLineId=" + productLineId + ", percentage=" + percentage + '}';
    }
}