package FactoryManagement.controllers;

import java.util.ArrayList;

import javax.swing.JFrame;

import FactoryManagement.gui.manager_frame.*;
import FactoryManagement.gui.supervisor_frame.SupervisorFrame;
import FactoryManagement.models.User;
import FactoryManagement.models.enums.UserRole;
import FactoryManagement.services.UserService;
import FactoryManagement.utils.Exceptions;

public class LoginController {

    public LoginController() {
    }

    public boolean login(String username, String password) {
        username = username.toLowerCase();

        return UserService.authenticate(username, password).map(user -> {
            if (user.getRole() == UserRole.MANAGER) {
                openManagerFrame(user);
            } else if (user.getRole() == UserRole.SUPERVISOR) {

                openSupervisorFrame(user);
            }
            return true;
        }).orElse(false);
    }

    private void openManagerFrame(User user) {
        java.awt.EventQueue.invokeLater(() -> {
            ManagerFrame managerFrame = new ManagerFrame(user);
            managerFrame.setVisible(true);
            managerFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        });
    }

    private void openSupervisorFrame(User user) {
        java.awt.EventQueue.invokeLater(() -> {
            SupervisorFrame supervisorFrame = new SupervisorFrame(user);
            supervisorFrame.setVisible(true);
            supervisorFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        });
    }

    public boolean checkRememberMe() {
        ArrayList<User> users = (ArrayList<User>) UserService.getAllUsers();
        for (User user : users) {
            if (user.isRemember()) {
                if (user.getRole() == UserRole.MANAGER) {
                    openManagerFrame(user);
                    return true;
                } else if (user.getRole() == UserRole.SUPERVISOR) {
                    openSupervisorFrame(user);
                    return true;
                }
            }
        }
        return false;
    }

    public void rememberUser(User user) {
        UserService.rememberUser(user);

    }

    public void disrememberUser(User user) {
        UserService.disrememberUser(user);
    }

    public void recordError(String errorMessage) {
        Exceptions.saveError(errorMessage);
    }
}
