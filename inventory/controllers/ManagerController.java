package inventory.controllers;

import inventory.models.Note;
import inventory.models.ProductLine;
import inventory.models.Task;
import inventory.models.User;
import inventory.models.enums.ProductLineStatus;
import inventory.models.enums.TaskStatus;
import inventory.services.FinishedProductService;
import inventory.services.ItemService;
import inventory.services.NoteService;
import inventory.services.ProductLineService;
import inventory.services.ProductService;
import inventory.services.TaskService;
import inventory.services.UserService;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

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

    public void updateSupervisorPassword(int id, String newPassword) {
        UserService.getUserById(id).ifPresent(user -> {
            user.setPassword(newPassword);
            UserService.updateUser(user);
        });
        
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
        // ///////////////////////AGHIAD
        NoteService.addNewRating(ProductLineService.getAllProductLines().getLast().getId(),0 );
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
                    total += task.getPercentage() * task.getQuantity()/100.0;
                    ProductLine pl = ProductLineService.getProductLineById(task.getProductLineId()).orElse(null);
                    if (pl != null && hm.containsKey(pl)) {
                        hm.replace(pl,
                                hm.get(pl)
                                        + (task.getPercentage() * task.getQuantity()/100.0));

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

    //////////////////////////////////AGHIAD
    public List<Note> getAllNotes() {
        return NoteService.getAllNotes();
    }
    public List<Note> getAllRatings(){
        return NoteService.getAllRatings();
    }

    public void addNote(String text){
        NoteService.addNewNote(text, LocalDateTime.now());
    }
    
    public boolean setRating(int rating,int ProductLineId){
        Note updaterate=NoteService.getRatingById(ProductLineId).get();
        updaterate.setRating(rating);
        NoteService.updateRating(updaterate);
        return true;
    }
    
    public HashMap<ProductLine,Note> getAllProductLinesWithRatings(){
        HashMap<ProductLine,Note> map=new HashMap<>(); 
        List<ProductLine> pls=ProductLineService.getAllProductLines();
        System.out.println(pls);
        System.out.println(getAllRatings());
        List<Note> ratings=getAllRatings();
        for (ProductLine pl:pls){
            for (Note r:ratings){
                System.out.println(pl.getId()+" "+r.getId());
                if (pl.getId()==r.getId()){
                    map.put(pl, r);
                }
            }
        }
        System.out.println(map);
        return map;
    }



    public void save() {
        TaskService.saveTasks();
        FinishedProductService.saveFinishedProducts();
        ItemService.saveItems();
        ProductLineService.saveProductLines();
        ProductService.saveProducts();
        UserService.saveUsers();
        NoteService.saveNotes();
    }

        public void exit() {
        save();
        ProductLineService.getAllProductLines()
                .forEach(pl -> pl.setStatus(inventory.models.enums.ProductLineStatus.STOP));
        System.exit(0);
    }

}
