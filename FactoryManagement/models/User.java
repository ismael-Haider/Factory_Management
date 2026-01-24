package FactoryManagement.models;

import java.util.Objects;

import FactoryManagement.models.enums.UserRole;

public class User {
    public static int counter = 0;
    private int id;
    private String userName;
    private String password;
    private UserRole role;
    private boolean remember;

    public User(String userName, String password) {
        counter += 1;
        this.id = counter;
        this.userName = userName;
        this.password = password;
        this.role = UserRole.SUPERVISOR;
        this.remember = false;
    }

    public User(String userName, String password, UserRole role) {
        counter = id;
        this.id = counter;
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.remember = false;
    }

    // For loading from CSV
    public User(int id, String userName, String password, UserRole role, boolean remember) {
        counter=id;
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.remember = remember;
    }

    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isRemember() {
        return remember;
    }

    public void setRemember(boolean remember) {
        this.remember = remember;
    }

    public String toCSV() {
        return id + "," + userName + "," + password + "," + role + "," + remember;
    }

    public static User fromCSV(String csvLine) {
        String[] parts = csvLine.split(",");
        return new User(Integer.parseInt(parts[0]), parts[1], parts[2], UserRole.valueOf(parts[3]),
                Boolean.parseBoolean(parts[4]));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", userName='" + userName + '\'' + ", password='" + password + '\'' + ", role="
                + role + '}';
    }
}