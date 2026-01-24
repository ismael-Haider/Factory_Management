package FactoryManagement;

import FactoryManagement.controllers.*;
import FactoryManagement.gui.*;
import FactoryManagement.services.*;
import FactoryManagement.threads.*;

public class Main {
    public static void main(String[] args) {
        ItemService.init();
        ProductService.init();
        FinishedProductService.init();
        TaskService.init();
        NoteService.init();
        ProductLineService.init();
        UserService.init();
        LoginController loginController = new LoginController();
        if (loginController.checkRememberMe()) {
            return;
        }

        Login login = new Login(loginController);
        login.setVisible(true);
        InventoryUpdater inventoryUpdater = new InventoryUpdater();
        inventoryUpdater.start();
    }
}