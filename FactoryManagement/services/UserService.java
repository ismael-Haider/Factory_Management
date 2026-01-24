package FactoryManagement.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import FactoryManagement.config.Constants;
import FactoryManagement.csv.CsvReader;
import FactoryManagement.csv.CsvWriter;
import FactoryManagement.models.User;
import FactoryManagement.models.enums.UserRole;
import FactoryManagement.utils.Exceptions;

public class UserService {
    private static List<User> users = new ArrayList<>();

    public static void init() {
        loadUsers();
    }

    public static synchronized void addUser(User user) {
        users.add(user);
    }

    public static synchronized Optional<User> getUserById(int id) {
        return users.stream().filter(user -> user.getId() == id).findFirst();
    }

    public static synchronized Optional<User> getUserByUsername(String userName) {
        return users.stream().filter(user -> user.getUserName().equals(userName)).findFirst();
    }

    public static synchronized List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public static synchronized void updateUser(User updatedUser) {
        users.replaceAll(user -> user.getId() == updatedUser.getId() ? updatedUser : user);
    }

    public static synchronized void deleteUser(int id) {
        users.removeIf(user -> user.getId() == id);
    }

    public static synchronized void rememberUser(User user) {
        user.setRemember(true);
        updateUser(user);
        saveUsers();
    }

    public static synchronized void disrememberUser(User user) {
        user.setRemember(false);
        updateUser(user);
        saveUsers();
    }

    public static synchronized Optional<User> authenticate(String userName, String password) {
        return users.stream().filter(user -> user.getUserName().equals(userName) && user.getPassword().equals(password))
                .findFirst();
    }

    private static Boolean loadUsers() {
        try {
            users = CsvReader.readUsers(Constants.USERS_CSV);
            if (users.isEmpty()) {
                User defaultManager = new User(1,"manager", "password", UserRole.MANAGER,false);
                users.add(defaultManager);
            } else if (users.getFirst().getRole() != UserRole.MANAGER) {
                throw new Exception("the users file is corrupted, Call The Support Team");
            }
            return true;
        } catch (IOException e) {
            
            User defaultManager = new User(1,"manager", "password", UserRole.MANAGER,false);
            users.add(defaultManager);
            return true;
        } catch (Exception e) {
            Exceptions.saveError(e.getMessage());
            return false;
        }

    }

    public static void saveUsers() {
        CsvWriter.writeToCsv(Constants.USERS_CSV, users);
    }
}