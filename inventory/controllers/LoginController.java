package inventory.controllers;

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
            ManagerFrame managerFrame = new ManagerFrame();
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
}
