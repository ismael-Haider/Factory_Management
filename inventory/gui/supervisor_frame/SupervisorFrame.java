package inventory.gui.supervisor_frame;

import inventory.controllers.InvenManageController;
import inventory.controllers.LoginController;
import inventory.controllers.ProLineManageController;
import inventory.gui.Login;
import inventory.models.*;
import java.awt.*;
import javax.swing.*;

public class SupervisorFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    private final Color ACTIVE_COLOR = new Color(26, 188, 156);
    private final Color INACTIVE_COLOR = new Color(44, 62, 80);

    private InvenManageController itemController;
    private ProLineManageController productLineController;
    private InventoryPanel inventoryPanel;
    private JButton manageInventoryBtn;
    private JButton manageProductLineBtn;
    private JLabel supervisorLabel;
    private JLabel supervisorNameLabel;
    private JScrollPane mainScrollPane;
    private User user;

    public SupervisorFrame(User user) {
        itemController = new InvenManageController();
        inventoryPanel = new InventoryPanel(itemController);
        productLineController = new ProLineManageController();
        this.user = user;
        initUI();
        initPanels();
        setActiveButton(manageInventoryBtn, manageProductLineBtn);
        cardLayout.show(mainContentPanel, "INVENTORY");
    }

    private void initUI() {
        setTitle("Supervisor Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 600);
        setLocationRelativeTo(null);

        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(INACTIVE_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Left panel - Supervisor name
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        
        supervisorLabel = new JLabel("Supervisor:");
        supervisorLabel.setFont(new Font("Calisto MT", Font.PLAIN, 24));
        supervisorLabel.setForeground(Color.WHITE);
        
        supervisorNameLabel = new JLabel(user.getUserName());
        supervisorNameLabel.setFont(new Font("Calisto MT", Font.PLAIN, 24));
        supervisorNameLabel.setForeground(Color.WHITE);
        
        leftPanel.add(supervisorLabel);
        leftPanel.add(supervisorNameLabel);

        // Center panel - Buttons
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        centerPanel.setOpaque(false);
        
        manageInventoryBtn = new JButton("Manage Inventory");
        manageInventoryBtn.setFont(new Font("Calisto MT", Font.PLAIN, 24));
        manageInventoryBtn.setForeground(Color.WHITE);
        manageInventoryBtn.setBackground(ACTIVE_COLOR);
        manageInventoryBtn.setBorder(null);
        manageInventoryBtn.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "INVENTORY");
            setActiveButton(manageInventoryBtn, manageProductLineBtn);
        });

        manageProductLineBtn = new JButton("Manage Product Line");
        manageProductLineBtn.setFont(new Font("Calisto MT", Font.PLAIN, 24));
        manageProductLineBtn.setForeground(Color.WHITE);
        manageProductLineBtn.setBackground(INACTIVE_COLOR);
        manageProductLineBtn.setBorder(null);
        manageProductLineBtn.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "PRODUCT_LINE");
            setActiveButton(manageProductLineBtn, manageInventoryBtn);
        });
        
        centerPanel.add(manageInventoryBtn);
        centerPanel.add(manageProductLineBtn);

        // Right panel - Logout button
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(192, 57, 43));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.addActionListener(e -> {
            productLineController.save(); 
            dispose();
            new Login(new LoginController()).setVisible(true);
        });
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.add(logoutBtn);

        // Add all panels to top panel
        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(centerPanel, BorderLayout.CENTER);
        topPanel.add(rightPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);


        // Main content panel
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainScrollPane = new JScrollPane(mainContentPanel);
        manageInventoryBtn.setFocusPainted(false);
        manageInventoryBtn.setBorderPainted(false);
        manageInventoryBtn.setContentAreaFilled(true);

        manageProductLineBtn.setFocusPainted(false);
        manageProductLineBtn.setBorderPainted(false);
        manageProductLineBtn.setContentAreaFilled(true);
        // Layout main frame
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(mainScrollPane, BorderLayout.CENTER);
    }

    private void initPanels() {
        // InventoryPanel inventoryPanel = new InventoryPanel(itemController);
        ProductLinePanel productLinePanel = new ProductLinePanel(productLineController);
        mainContentPanel.add(inventoryPanel, "INVENTORY");
        mainContentPanel.add(productLinePanel, "PRODUCT_LINE");
    }

    private void setActiveButton(JButton active, JButton inactive) {
        active.setBackground(ACTIVE_COLOR);
        active.setForeground(Color.WHITE);
        inactive.setBackground(INACTIVE_COLOR);
        inactive.setForeground(Color.WHITE);
    }
}