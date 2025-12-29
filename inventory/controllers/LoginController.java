package inventory.controllers;

import java.util.ArrayList;

import inventory.gui.ManagerFrame;
import inventory.gui.supervisor_frame.SupervisorFrame;
import inventory.models.User;
import inventory.models.enums.UserRole;
import inventory.services.UserService;

public class LoginController {

    public LoginController() {
    }

    public boolean login(String username, String password) {
        username=username.toLowerCase();
        
        // محاكاة محاولة تسجيل الدخول
        return UserService.authenticate(username, password).map(user -> {
            // تحقق من نوع المستخدم وافتح النافذة المناسبة
            if (user.getRole() == UserRole.MANAGER) {
                openManagerFrame(user);
            } else if (user.getRole() == UserRole.SUPERVISOR) {
                
                openSupervisorFrame(user);
            }
            return true;
        }).orElse(false); // إذا فشل تسجيل الدخول
    }

    private void openManagerFrame(User user) {
        // إنشاء وإظهار ManagerFrame
        java.awt.EventQueue.invokeLater(() -> {
            ManagerFrame managerFrame = new ManagerFrame(user);
            managerFrame.setVisible(true);
        });
    }

    private void openSupervisorFrame(User user) {
        // إنشاء وإظهار SupervisorFrame
        java.awt.EventQueue.invokeLater(() -> {
            SupervisorFrame supervisorFrame = new SupervisorFrame(user);
            supervisorFrame.setVisible(true);
        });
    }

    // check if any user has " remember me " enabled
    public boolean checkRememberMe() {
        ArrayList <User> users = (ArrayList<User>)UserService.getAllUsers();
        for (User user : users) {
            if (user.isRemember()) {
                if(user.getRole() == UserRole.MANAGER){
                    openManagerFrame(user);
                    return true;}
                else if (user.getRole() == UserRole.SUPERVISOR){
                    openSupervisorFrame(user);
                    return true;
                }
            }
        }
        return false;
    }
// to method in UserService
    public void rememberUser(User user) {
        UserService.rememberUser(user);
        
    }
// to method in UserService
    public void disrememberUser(User user) {
        UserService.disrememberUser(user);
    }
}
