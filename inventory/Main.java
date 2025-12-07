package inventory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import inventory.models.*;
import inventory.models.enums.*;
import inventory.services.*;
import inventory.threads.*;

public class Main {
    public static void main(String[] args) {
        // Initialize services
        ItemService itemService = new ItemService();
        ProductService productService = new ProductService(itemService);
        FinishedProductService finishedProductService = new FinishedProductService();
        TaskService taskService = new TaskService();
        ProductLineService productLineService = new ProductLineService();
        UserService userService = new UserService();

        // Start threads
        TaskProcessor taskProcessor = new TaskProcessor(productLineService, taskService);
        InventoryUpdater inventoryUpdater = new InventoryUpdater(taskService, finishedProductService);
        taskProcessor.start();
        inventoryUpdater.start();

        // Sample data creation (for testing)
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Inventory Management System!");

        // Add sample items
        Item item1 = new Item("Steel", "Metal", 10.0, 100, 10);
        Item item2 = new Item("Plastic", "Polymer", 5.0, 200, 20);
        itemService.addItem(item1);
        itemService.addItem(item2);

        // Add sample product
        Map<Integer, Integer> itemQuantities = new HashMap<>();
        itemQuantities.put(1, 2); // 2 units of item 1
        itemQuantities.put(2, 1); // 1 unit of item 2
        Product product1 = new Product("Widget", itemQuantities);
        productService.addProduct(product1);

        // Add sample finished product
        FinishedProduct fp1 = new FinishedProduct(1, "Widget", 50);
        finishedProductService.addFinishedProduct(fp1);

        // Add sample product line
        ProductLine pl1 = new ProductLine("A", ProductLineStatus.RUNNING, 0.8);
        productLineService.addProductLine(pl1);

        // Add sample task
        Task task1 = new Task(1, 10, "Client1", LocalDate.now(), LocalDate.now().plusDays(1), TaskStatus.IN_QUEUE, 1, 0.0);
        taskService.addTask(task1);
        pl1.addTask(1); // Add to queue
        productLineService.updateProductLine(pl1);

        // Add sample user
        User user1 = new User("supervisor", "pass", UserRole.SUPERVISOR);
        userService.addUser(user1);

        System.out.println("Sample data loaded. System running...");

        // Simple CLI loop (for demo)
        while (true) {
            System.out.println("Enter command (add_item, list_items, exit): ");
            String command = scanner.nextLine();
            if ("add_item".equals(command)) {
                System.out.println("Enter name, category, price, quantity, min_quantity:");
                String name = scanner.next();
                String category = scanner.next();
                double price = scanner.nextDouble();
                int qty = scanner.nextInt();
                int minQty = scanner.nextInt();
                Item newItem = new Item(name, category, price, qty, minQty);
                itemService.addItem(newItem);
                System.out.println("Item added.");
            } else if ("list_items".equals(command)) {
                itemService.getAllItems().forEach(System.out::println);
            } else if ("exit".equals(command)) {
                taskProcessor.stopProcessing();
                inventoryUpdater.stopUpdating();
                break;
            }
        }

        scanner.close();
        // taskService.saveTasks();
    }
}