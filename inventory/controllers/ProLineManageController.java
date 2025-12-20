package inventory.controllers;

import inventory.models.*;
import inventory.models.enums.*;
import inventory.services.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProLineManageController {

    public void addTask(int productId, int quantity, String clientName, LocalDate startDate, LocalDate deliveredDate,
            int productLineId, ProductLineService productLineService) {
        clientName = clientName.toLowerCase();
        Task newTask = new Task(productId, quantity, clientName, startDate, deliveredDate, productLineId);
        TaskService.addTask(newTask);
    }
    public void addTask(String name, HashMap<Integer, Integer> itemQuantities, int quantity, String clientName, LocalDate startDate, LocalDate deliveredDate,
            int productLineId, ProductLineService productLineService) {
        addProduct(name,itemQuantities );
        clientName = clientName.toLowerCase();
        Task newTask = new Task(ProductService.getAllProducts().getLast().getId(), quantity, clientName, startDate, deliveredDate, productLineId);
        TaskService.addTask(newTask);
    }

    public boolean cancelTask(int id) {
        Task task = TaskService.getTaskById(id).get();
        if (task.getStatus().equals(TaskStatus.FINISHED) || task.getStatus().equals(TaskStatus.CANCELLED)) {
            return false;
        }
        TaskService.cancelTask(id);
        return true;
    }

    public List<ProductLine> viewAllProductLines() {
        return ProductLineService.getAllProductLines();
    }

    public List<Product> viewAllProducts() {
        return ProductService.getAllProducts();
    }

    public List<Task> viewAllTasks() {
        return TaskService.getAllTasks();
    }

    public List<Item> viewAllItems() {
        return ItemService.getAllItems();
    }

    public List<Item> viewAvaleableItems() {
        List<Item> allItems = ItemService.getAllItems();
        List<Item> newList = new ArrayList<>();
        for (Item item : allItems) {
            if (item.getQuantity() > 0) {
                newList.add(item);
            }
        }
        return newList;
    }

    public void addProduct(String name, HashMap<Integer, Integer> itemQuantities) {
        name = name.toLowerCase();
        ProductService.addProduct(new Product(name, itemQuantities));
    }

    public List<Task> viewTasksByProductLine(int productLineId) {
        List<Task> allTasks = TaskService.getAllTasks();
        List<Task> newList = new ArrayList<>();
        for (Task t : allTasks)
            if (t.getProductLineId() == productLineId)
                newList.add(t);
        return newList;
    }

    public List<Task> viewTasksByProductId(int productId) {
        List<Task> allTasks = TaskService.getAllTasks();
        List<Task> newList = new ArrayList<>();
        for (Task t : allTasks)
            if (t.getProductId() == productId)
                newList.add(t);
        return newList;
    }

    public List<Task> viewTasksByStatus(TaskStatus status) {
        List<Task> allTasks = TaskService.getAllTasks();
        List<Task> newList = new ArrayList<>();
        for (Task t : allTasks)
            if (t.getStatus().equals(status))
                newList.add(t);
        return newList;
    }

    public List<ProductLine> viewProductLinesByProductId(int productId) {
        List<Task> tasks = viewTasksByProductId(productId);
        List<ProductLine> newList = new ArrayList<>();
        for (Task t : tasks) {
            ProductLine pl = ProductLineService.getProductLineById(t.getProductLineId()).get();
            if (!newList.contains(pl))
                newList.add(pl);
        }
        return newList;
    }

    public List<FinishedProduct> viewFinishedProductsByProductLineId(int productLineId) {
        List<Task> tasks = viewTasksByProductLine(productLineId);
        List<FinishedProduct> newList = new ArrayList<>();
        for (Task t : tasks) {
            FinishedProduct fp = FinishedProductService.getFinishedProductByProductId(t.getProductId()).get();
            if (!newList.contains(fp))
                newList.add(fp);
        }
        return newList;
    }

    public HashMap<ProductLine, List<FinishedProduct>> viewAllProductAndProductLines() {
        HashMap<ProductLine, List<FinishedProduct>> map = new HashMap<>();
        List<ProductLine> productLines = ProductLineService.getAllProductLines();
        for (ProductLine pl : productLines) {
            List<FinishedProduct> fininshedproductsInLine = new ArrayList<>();
            List<Task> tasks = viewTasksByProductLine(pl.getId());
            for (Task t : tasks) {
                FinishedProduct p = FinishedProductService.getFinishedProductByProductId(t.getProductId()).get();
                if (!fininshedproductsInLine.contains(p))
                    fininshedproductsInLine.add(p);
            }
            if (!fininshedproductsInLine.isEmpty())
                map.put(pl, fininshedproductsInLine);
        }
        return map;
    }

    public Product viewTheMostRequestedProduct(LocalDate fld, LocalDate lld) {
        List<Task> allTask = TaskService.getAllTasks();
        List<Integer> ids = new ArrayList<>();
        for (Task t : allTask) {
            if (t.getStartDate().isBefore(fld) || t.getDeliveredDate().isAfter(lld)) {
                allTask.remove(t);
            } else {
                ids.add(t.getProductId());
            }
        }
        int maxCount = 0;
        int mostRequestedId = -1;
        for (int id : ids) {
            int count = 0;
            for (Task t : allTask) {
                if (t.getProductId() == id) {
                    count++;
                }
            }
            if (count > maxCount) {
                maxCount = count;
                mostRequestedId = id;
            }
        }
        return ProductService.getProductById(mostRequestedId).get();
    }

    public void save() {
        ItemService.saveItems();
        ProductService.saveProducts();
        ProductLineService.saveProductLines();
        TaskService.saveTasks();
    }
    public void exit() {
        save();
        System.exit(0);
    }


}
