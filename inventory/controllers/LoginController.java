package inventory.controllers;

import inventory.services.UserService;
import inventory.models.User;
import inventory.models.enums.UserRole;

public class LoginController {

    private UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    public Boolean login(String username, String password) {

        // محاكاة محاولة تسجيل الدخول
        userService.authenticate(username, password).ifPresentOrElse(user -> {
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
        System.out.println("wilcom" + user.getUserName());
        // افتح صفحة المدير
        // ManagerDashboard.show();
    }

    private void openSupervisorPage(User user) {
        System.out.println("wilcom" + user.getUserName());
        // افتح صفحة المشرف
        // SupervisorDashboard.show();
    }
}
