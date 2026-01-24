package FactoryManagement.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import FactoryManagement.models.*;
import FactoryManagement.models.enums.*;
import FactoryManagement.services.*;
import FactoryManagement.utils.Exceptions;

public class TasksController {

    public void addTask(int productId, int quantity, String clientName, LocalDate startDate, LocalDate deliveredDate,
            int productLineId) {
        clientName = clientName.toLowerCase();
        Task newTask = new Task(productId, quantity, clientName, startDate, deliveredDate, productLineId);
        TaskService.addTask(newTask);
    }

    public void addTask(String name, HashMap<Integer, Integer> itemQuantities, int quantity, String clientName,
            LocalDate startDate, LocalDate deliveredDate,
            int productLineId) {
        addProduct(name, itemQuantities);
        clientName = clientName.toLowerCase();
        Task newTask = new Task(ProductService.getAllProducts().getLast().getId(), quantity, clientName, startDate,
                deliveredDate, productLineId);
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

    // here why i need this function
    // public List<Item> viewAvaleableItems() {
    //     List<Item> allItems = ItemService.getAllItems();
    //     List<Item> newList = new ArrayList<>();
    //     for (Item item : allItems) {
    //         if (item.getQuantity() > 0) {
    //             newList.add(item);
    //         }
    //     }
    //     return newList;
    // }

    // search on item by name on it and return the id for the hash map for the
    public int searchByName(String name) {
        List<Item> items = ItemService.getAllItems();
        for (Item item : items) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item.getId();
            }
        }
        return 0;
    }
    public boolean productNameExists(String name) {
        if (name == null)
            return false;
        String q = name.trim();
        if (q.isEmpty())
            return false;
        List<Product> products = ProductService.getAllProducts();
        for (Product p : products) {
            if (p.getName() != null && p.getName().equalsIgnoreCase(q))
                return true;
        }
        return false;
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
    public Product searchProductById(int id){
        return ProductService.getProductById(id).get();
    }
    public ProductLine searchProductLineById(int id) {
        return ProductLineService.getProductLineById(id).get();
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
        List<Task> filtered = new ArrayList<>();
        for (Task t : allTask) {
            boolean startsOnOrAfterFrom = !t.getStartDate().isBefore(fld);
            boolean deliveredOnOrBeforeTo = !t.getDeliveredDate().isAfter(lld);
            if (startsOnOrAfterFrom && deliveredOnOrBeforeTo) {
                filtered.add(t);
            }
        }
        if (filtered.isEmpty()) {
            return null;
        }
        Map<Integer, Integer> counts = new HashMap<>();
        for (Task t : filtered) {
            counts.merge(t.getProductId(), 1, Integer::sum);
        }
        int mostRequestedId = counts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(-1);
        if (mostRequestedId == -1) return null;
        return ProductService.getProductById(mostRequestedId).orElse(null);
    }

    public HashMap<ProductLine,List<FinishedProduct>> viewFinishedProductsByAllProductLine(){
        HashMap<ProductLine, List<FinishedProduct>> map = new HashMap<>();
        List<ProductLine> productLines = ProductLineService.getAllProductLines();
        System.out.println(productLines);
        for (ProductLine pl : productLines) {
            List<FinishedProduct> fininshedproductsInLine = new ArrayList<>();
            List<Task> tasks = viewTasksByProductLine(pl.getId());
            
            tasks=tasks.stream().filter(t->(t.getStatus().equals(TaskStatus.FINISHED)||t.getStatus().equals(TaskStatus.CANCELLED))&&
                !t.isDelivered()&&
                t.getPercentage()>0).toList();
                
            for (Task t:tasks){
                FinishedProduct p = new FinishedProduct(t.getProductId(), ProductService.getProductById(t.getProductId()).get().getName(),(int)(t.getQuantity()*t.getPercentage()/100.0));
                if (fininshedproductsInLine.stream().anyMatch(o->o.getName().equals(p.getName()))) {
                    FinishedProduct old=fininshedproductsInLine.get(fininshedproductsInLine.indexOf(p));
                    old.setQuantity(old.getQuantity()+p.getQuantity());
                    continue;
                }
                fininshedproductsInLine.add(p);
            }
            if (!fininshedproductsInLine.isEmpty()) {
                map.put(pl, fininshedproductsInLine);
            }
        }
        return map;
    }

    public void save() {
        TaskService.saveTasks();
        FinishedProductService.saveFinishedProducts();
        ItemService.saveItems();
        ProductLineService.saveProductLines();
        ProductService.saveProducts();
        UserService.saveUsers();
    }

    public void exit() {
        save();
        ProductLineService.getAllProductLines()
                .forEach(pl -> pl.setStatus(FactoryManagement.models.enums.ProductLineStatus.STOP));
        System.exit(0);
    }
    public void recordError(String errorMessage) {
       Exceptions.saveError(errorMessage);
    }
}
