package inventory.controllers;

import inventory.services.UserService;
import inventory.models.User;
import inventory.models.enums.UserRole;

public class LoginController {

    public LoginController() {
    }

    public Boolean login(String username, String password) {

        // محاكاة محاولة تسجيل الدخول
        UserService.authenticate(username, password).ifPresentOrElse(user -> {
            // التحقق من نوع المستخدم
            if (user.getRole() == UserRole.MANAGER) {
                openManagerPage(user);
            } else if (user.getRole() == UserRole.SUPERVISOR) {
                openSupervisorPage(user);
            }

        }, () -> {
            System.out.println("enter a valid username or password");
        });
            return true;

    }

    private void openManagerPage(User user) {
        System.out.println("welcome " + user.getUserName());
        // افتح صفحة المدير
        // ManagerDashboard.show();
    }

    private void openSupervisorPage(User user) {
        System.out.println("welcome " + user.getUserName());
        // افتح صفحة المشرف
        // SupervisorDashboard.show();
    }
}
