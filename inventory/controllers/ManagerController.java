package inventory.controllers;

import java.util.HashMap;
import java.util.List;

import inventory.models.ProductLine;
import inventory.models.Task;
import inventory.models.User;
import inventory.models.enums.ProductLineStatus;
import inventory.models.enums.TaskStatus;
import inventory.services.ProductLineService;
import inventory.services.TaskService;
import inventory.services.UserService;

public class ManagerController {

    public boolean addSupervisor(String username, String password) {
        username = username.toLowerCase();
        List<User> users = UserService.getAllUsers();
        for (User user : users) {
            if (user.getUserName().equals(username)) {
                return false;
            }
        }
        UserService.addUser(new User(username, password));
        return true;
    }

    public List<User> viewAllSupervisors() {
        List<User> users = UserService.getAllUsers();
        return users;
    }

    public boolean deleteSupervisor(int id) {
        User user = UserService.getUserById(id).orElse(null);
        if (user == null || !user.getRole().equals(inventory.models.enums.UserRole.SUPERVISOR)) {
            return false;
        }
        UserService.deleteUser(id);
        return true;
    }

    public boolean addProductLine(String name, int efficiency) {
        name = name.toUpperCase();
        if (efficiency<=0) {
            return false;
        }
        for (ProductLine pl : ProductLineService.getAllProductLines()) {
            if (pl.getName().equalsIgnoreCase(name)) {
                return false;
            }
        }
        ProductLineService.addProductLine(new ProductLine(name, efficiency));
        return true;
    }

    public List<ProductLine> viewAllProductLines() {
        return ProductLineService.getAllProductLines();
    }

    public void setTheProductLineMaintenance(int id) {
        ProductLineService.getProductLineById(id).get().setStatus(ProductLineStatus.MAINTENANCE);
    }

    public void setTheProductLineStop(int id) {
        ProductLineService.getProductLineById(id).get().setStatus(ProductLineStatus.STOP);
    }

    public HashMap<ProductLine, Double> viewPerformanceReport() {
        List<Task> tasks = TaskService.getAllTasks();
        HashMap<ProductLine, Double> hm = new HashMap<>();
        for (ProductLine pl : ProductLineService.getAllProductLines()) {
            hm.put(pl, 0.0);
        }
        double total = 0.0;

        for (Task task : tasks) {
            if (task.getStatus().equals(TaskStatus.FINISHED) || task.getStatus().equals(TaskStatus.CANCELLED)) {
                if (!task.isDelivered()) {
                    total += task.getPercentage() * task.getQuantity();
                    if (hm.containsKey(ProductLineService.getProductLineById(task.getProductLineId()))) {
                        hm.replace(ProductLineService.getProductLineById(task.getProductLineId()).get(),
                                hm.get(ProductLineService.getProductLineById(task.getProductLineId()))
                                        + task.getPercentage() * task.getQuantity());

                    }

                }

            }
        }
        for (ProductLine pl : hm.keySet()) {
            double perf = 0.0;
            if (total != 0.0) {
                perf = (hm.get(pl) / total) * 100.0;
            }
            hm.replace(pl, perf);
        }
        return hm;

    }

}
