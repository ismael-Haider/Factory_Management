package inventory;


import inventory.controllers.*;
import inventory.gui.*;
import inventory.models.*;
import inventory.services.*;
import inventory.threads.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Constants a=new Constants();
        
        // Initialize services in correct order
        ItemService.init();
        ProductService.init();
        FinishedProductService.init();
        TaskService.init();
        NoteService.init();
        ProductLineService.init();
        UserService.init();
        LoginController loginController = new LoginController();
        Login login = new Login(loginController);
        login.setVisible(true);
        // Start threads
        // TaskProcessor taskProcessor = new TaskProcessor();
        InventoryUpdater inventoryUpdater = new InventoryUpdater();
        // taskProcessor.start();
        inventoryUpdater.start();

        // Sample data creation (for testing)
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Inventory Management System!");

        // // Add sample items
        // Item item1 = new Item("Steel", "Metal", 10.0, 100, 10);
        // Item item2 = new Item("Plastic", "Polymer", 5.0, 200, 20);
        // itemService.addItem(item1);
        // itemService.addItem(item2);

        // // Add sample product
        // Map<Integer, Integer> itemQuantities = new HashMap<>();
        // itemQuantities.put(1, 2); // 2 units of item 1
        // itemQuantities.put(2, 1); // 1 unit of item 2
        // Product product1 = new Product("Widget", itemQuantities);
        // productService.addProduct(product1);

        // // Add sample finished product
        // FinishedProduct fp1 = new FinishedProduct(1, "Widget", 50);
        // finishedProductService.addFinishedProduct(fp1);

        // // Add sample product line
        // ProductLine pl2 = new ProductLine("B", 2);
        // ProductLineService.addProductLine(pl2);

        // // Add sample task
        // Task task1 = new Task(1, 10, "Client1", LocalDate.now(), LocalDate.now().plusDays(1), TaskStatus.IN_QUEUE, 1, 0.0);
        // taskService.addTask(task1);
        // pl1.addTask(1); // Add to queue
        // productLineService.updateProductLine(pl1);

        // // Add sample user
        // User user1 = new User("supervisor", "pass", UserRole.SUPERVISOR);
        // userService.addUser(user1);

        System.out.println("Sample data loaded. System running...");

        // Simple CLI loop (for demo)
        while (true) {
            System.out.println("Enter command (add_item, list_items, exit,tasks,add_task): ");
            String command = scanner.nextLine();
            if ("add_item".equals(command)) {
                System.out.println("Enter name, category, price, quantity, min_quantity:");
                String name = scanner.next();
                String category = scanner.next();
                double price = scanner.nextDouble();
                int qty = scanner.nextInt();
                int minQty = scanner.nextInt();
                Item newItem = new Item(name, category, price, qty, minQty);
                ItemService.addItem(newItem);
                System.out.println("Item added.");
            } else if ("list_items".equals(command)) {
                ItemService.getAllItems().forEach(System.out::println);
            }
            else if("tasks".equals(command)){
                TaskService.getAllTasks().forEach(System.out::println);
            }
            else if("pro".equals(command)){
                for (ProductLine pr : ProductLineService.getAllProductLines()) {
                    System.out.println(pr + " " + pr.getEfficiency() + " "+ pr.getTaskQueue() + "");
                }
            } else if ("add_task".equals(command)){
                System.out.println("1- new");
                List<Product> p=ProductService.getAllProducts();
                for (int i=2;i<p.size()+2;i++){
                    System.out.println(i+"- "+ p.get(i-2));
                }
                command=scanner.next();
                if (command.equals("1")){
                    System.out.println("enter the name of the pruduct:");
                    String name =scanner.next();
                    for (Product x : p){
                        if (name.equals(x.getName())) {
                            System.out.println("this product name is already exist,pls change the name of the new product");
                            continue;
                        }
                    }
                    System.out.println("how many raw meterials do you need:");
                    int n=scanner.nextInt();
                    HashMap<Integer,Integer> hm=new HashMap<>();
                    List<Item> is=ItemService.getAllItems();
                    for (int i=0;i<n;i++){
                        for (int j=1 ;j<is.size()+1;j++){
                            System.out.println(j+"- "+is.get(j-1));
                        }
                        System.out.println("choose any of the next items,enter the quantity of the this item");
                        hm.put(scanner.nextInt(),scanner.nextInt());
                    }
                    System.out.println("enter the quantity of the product:");
                    int q=scanner.nextInt();
                    ProductService.addProduct(new Product(name, hm));
                    List<Product> all = ProductService.getAllProducts();
                    int lastId = all.get(all.size()-1).getId();
                    System.out.println("what is the id of the product line you want to add the task to?");
                    int plid=scanner.nextInt();
                    TaskService.addTask(new Task(lastId,q,"client_bitch",LocalDate.now(),LocalDate.now().plusDays(1),plid));
                }
                else {
                    int c=Integer.parseInt(command)-1;
                    Product pr=null;
                    System.out.println(c);
                    for (int i=0 ;i<p.size();i++){
                        
                        if (c==p.get(i).getId()){
                            pr=p.get(i);
                        }
                    }
                    if (pr==null){
                        System.out.println("write a valid number");
                        continue;
                    }
                    System.out.println("enter the quantity of the product:");
                    int q=scanner.nextInt();
                    System.out.println("what is the id of the product line you want to add the task to?");
                    int plid=scanner.nextInt();
                    TaskService.addTask(new Task(pr.getId(),q ,"client_bitch",LocalDate.now(),LocalDate.now().plusDays(1),plid));
                }
            }
            else if ("products".equals(command)) {
                    List<Product> p = ProductService.getAllProducts();
                    for (Product product : p) {
                        System.out.println(product);
                    }



            } else if ("exit".equals(command)) {
                // taskProcessor.stopProcessing();
                inventoryUpdater.stopUpdating();
                ProductLineService.getAllProductLines().forEach(pl-> pl.setStatus(inventory.models.enums.ProductLineStatus.STOP));
                break;
            }
        }

        scanner.close();
        TaskService.saveTasks();
        FinishedProductService.saveFinishedProducts();
        ItemService.saveItems();
        ProductLineService.saveProductLines();
        ProductService.saveProducts();
        UserService.saveUsers();
        System.exit(0);

    }
}