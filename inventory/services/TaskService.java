package inventory.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import inventory.config.Constants;
import inventory.csv.CsvReader;
import inventory.csv.CsvWriter;
import inventory.models.Item;
import inventory.models.Task;
import inventory.models.enums.TaskStatus;

public class TaskService {
    private static List<Task> tasks = new ArrayList<>();

    public static void init() {
        loadTasks();
    }

    public static synchronized void finishTask(int id) {
        getTaskById(id).ifPresent(task -> {
            task.setStatus(TaskStatus.FINISHED);
            updateTask(task);
            FinishedProductService.addFinishedProduct(task.getProductId(), task.getQuantity());
        });
    }

    public static synchronized void cancelTask(int id) {
        Task task = getTaskById(id).get();
        HashMap<Integer, Integer> items = ProductService.getProductById(task.getProductId()).get().getItemQuantities();
        for (Integer i : items.keySet()) {
            ItemService.getItemById(i).get()
                    .addQuantity((int) (items.get(i) * task.getQuantity() * (100 - task.getPercentage()) / 100));
        }
        ProductLineService.getProductLineById(task.getProductLineId()).get().removeTask(task.getId());
        ProductLineService.getProductLineById(task.getProductLineId()).get().stop();
        if (task.getStatus().equals(TaskStatus.IN_QUEUE)) {
            task.setStatus(TaskStatus.CANCELLED);
            return;
        }
        task.setStatus(TaskStatus.CANCELLED);
        FinishedProductService.addFinishedProduct(task.getProductId(),
                (int) (task.getQuantity() * task.getPercentage() / 100.0));
    }

    public static synchronized void addTask(Task task) {
        if (!ProductService.getProductById(task.getProductId()).isPresent()) {
            throw new IllegalArgumentException("Product with ID " + task.getProductId() + " does not exist.");
        }

        List<Task> cancelled = tasks.stream().filter(t -> t.getStatus() == TaskStatus.CANCELLED
                && t.getProductId() == task.getProductId() && t.getPercentage() > 0).toList();
        System.out.println(cancelled);
        if (cancelled.size() > 0) {
            System.out.println("hello bitch");
            double q = 0;
            for (Task t : cancelled) {
                q = (t.getQuantity() * t.getPercentage() / 100.0);
                // System.out.println((q - Math.min(task.getQuantity(), q)) * 100 / t.getQuantity());
                task.addPercentage((Math.min(task.getQuantity(), q) * 100 / task.getQuantity()));
                t.setPercentage((q - Math.min(task.getQuantity(), q)) * 100 / t.getQuantity());
                task.setStatus(TaskStatus.IN_QUEUE);
                updateTask(task);
                updateTask(t);
                FinishedProductService.reduceQuantity(task.getProductId(), (int) Math.min(task.getQuantity(), q));
            }
        }
        HashMap<Integer, Integer> items = ProductService.getProductById(task.getProductId()).get().getItemQuantities();
        for (Integer i : items.keySet()) {
            Optional<Item> itemOpt = ItemService.getItemById(i);
            if (itemOpt.isEmpty()) {
                ProductLineService.getProductLineById(task.getProductLineId()).get().removeTask(task.getId());
                throw new IllegalArgumentException("Item with ID " + i + " does not exist.");
            } 
            Item item = itemOpt.get();
            double required = items.get(i) * task.getQuantity() * (100 - task.getPercentage()) / 100.0;
            if (item.getQuantity() < required) {
                ProductLineService.getProductLineById(task.getProductLineId()).get().removeTask(task.getId());
                throw new IllegalArgumentException("Insufficient quantity for item " + item.getName() + ". Required: " + required + ", Available: " + item.getQuantity());
            }
        }
        
        tasks.add(task);
        if (task.getPercentage() == 100) {
            finishTask(task.getId());
            // System.out.println("hi");
            ProductLineService.getProductLineById(task.getProductLineId()).get().removeTask(task.getId());
            return;
        }
        for (Integer i : items.keySet()) {
            ItemService.getItemById(i).get()
                    .reduceQuantity((int) (items.get(i) * task.getQuantity() * (100 - task.getPercentage()) / 100.0));
        }
        task.setStatus(inventory.models.enums.TaskStatus.IN_QUEUE);
        // productLineService.getProductLineById(task.getProductLineId()).get().addTask(task.getId());
        // // add task to product line task.getProductLineId()
    }

    public static synchronized Optional<Task> getTaskById(int id) {
        return tasks.stream().filter(task -> task.getId() == id).findFirst();
    }

    public static synchronized List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    public static synchronized void updateTask(Task updatedTask) {
    tasks.replaceAll(task -> task.getId() == updatedTask.getId() ? updatedTask :
    task);
    }

    public static synchronized void deleteTask(int id) {
        tasks.removeIf(task -> task.getId() == id);
    }

    public static synchronized List<Task> getTasksByDeliveredDate(LocalDate date) {
        return tasks.stream()
                .filter(task -> (task.getDeliveredDate().equals(date) || task.getDeliveredDate().isBefore(date))
                        && task.getStatus() == TaskStatus.FINISHED)
                .toList();
    }

    private static void loadTasks() {
        tasks = CsvReader.readTasks(Constants.TASKS_CSV);
    }

    public static void saveTasks() {
        CsvWriter.writeToCsv(Constants.TASKS_CSV, tasks);
    }
}