package inventory.services;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import inventory.config.Constants;
import inventory.csv.CsvReader;
import inventory.csv.CsvWriter;
import inventory.models.Task;
import inventory.models.enums.TaskStatus;

public class TaskService {
    private List<Task> tasks = new ArrayList<>();

    public TaskService() {
        loadTasks();
    }

    public synchronized void addTask(Task task) {
        tasks.add(task);
        saveTasks();
    }

    public synchronized Optional<Task> getTaskById(int id) {
        return tasks.stream().filter(task -> task.getId() == id).findFirst();
    }

    public synchronized List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    public synchronized void updateTask(Task updatedTask) {
        tasks.replaceAll(task -> task.getId() == updatedTask.getId() ? updatedTask : task);
        saveTasks();
    }

    public synchronized void deleteTask(int id) {
        tasks.removeIf(task -> task.getId() == id);
        saveTasks();
    }

    public synchronized List<Task> getTasksByDeliveredDate(LocalDate date) {
        return tasks.stream().filter(task -> task.getDeliveredDate().equals(date) && task.getStatus() == TaskStatus.FINISHED).toList();
    }

    private void loadTasks() {
        try {
            tasks = CsvReader.readTasks(Constants.TASKS_CSV);
        } catch (IOException e) {
            // File might not exist yet
        }
    }

    private void saveTasks() {
        try {
            CsvWriter.writeToCsv(Constants.TASKS_CSV, tasks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}