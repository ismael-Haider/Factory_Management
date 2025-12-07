package inventory.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import inventory.config.Constants;
import inventory.csv.CsvReader;
import inventory.csv.CsvWriter;
import inventory.models.User;
import inventory.models.enums.UserRole;

public class UserService {
    private List<User> users = new ArrayList<>();

    public UserService() {
        loadUsers();
    }

    public synchronized void addUser(User user) {
        users.add(user);
        saveUsers();
    }

    public synchronized Optional<User> getUserById(int id) {
        return users.stream().filter(user -> user.getId() == id).findFirst();
    }

    public synchronized List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public synchronized void updateUser(User updatedUser) {
        users.replaceAll(user -> user.getId() == updatedUser.getId() ? updatedUser : user);
        saveUsers();
    }

    public synchronized void deleteUser(int id) {
        users.removeIf(user -> user.getId() == id);
        saveUsers();
    }

    // Authenticate user (for login simulation)
    public synchronized Optional<User> authenticate(String userName, String password) {
        return users.stream().filter(user -> user.getUserName().equals(userName) && user.getPassword().equals(password)).findFirst();
    }

    private void loadUsers() {
        try {
            users = CsvReader.readUsers(Constants.USERS_CSV);
            // Ensure at least one manager exists (as per your spec)
            if (users.stream().noneMatch(user -> user.getRole() == UserRole.MANAGER)) {
                // Add a default manager if none exists
                User defaultManager = new User("manager", "password", UserRole.MANAGER);
                users.add(defaultManager);
                saveUsers();
            } 
        } catch (IOException e) {
            // File might not exist yet; add default manager
            User defaultManager = new User("manager", "password", UserRole.MANAGER);
            users.add(defaultManager);
            saveUsers();
        }
    }

    private void saveUsers() {
        try {
            CsvWriter.writeToCsv(Constants.USERS_CSV, users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}