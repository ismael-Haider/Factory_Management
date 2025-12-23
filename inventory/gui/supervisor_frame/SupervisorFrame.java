package inventory.gui.supervisor_frame;

import inventory.controllers.InvenManageController;
import inventory.controllers.LoginController;
import inventory.controllers.ProLineManageController;
import inventory.gui.Login;
import inventory.models.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(true);
        
        setSize(1300, 800); // Increased size to accommodate larger table
        setLocationRelativeTo(null);
        // Save on exit
        addWindowListener(new WindowAdapter() {
            
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                        null,
                        "Do you want to save before exiting?",
                        "Confirm Exit",
                        JOptionPane.YES_NO_OPTION);

                if (choice == JOptionPane.YES_OPTION) {
                    productLineController.save();
                }
                dispose();
                System.exit(0);
            }
        });

        // setLocationRelativeTo(null);

        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(INACTIVE_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25)); // Added left and right padding

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
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10));
        centerPanel.setOpaque(false);

        manageInventoryBtn = new JButton("Manage Inventory");
        manageInventoryBtn.setFont(new Font("Calisto MT", Font.PLAIN, 24));
        manageInventoryBtn.setForeground(Color.WHITE);
        manageInventoryBtn.setBackground(ACTIVE_COLOR);
        manageInventoryBtn.setFocusable(false);
        manageInventoryBtn.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        manageInventoryBtn.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "INVENTORY");
            setActiveButton(manageInventoryBtn, manageProductLineBtn);
        });

        manageProductLineBtn = new JButton("Manage Product Line");
        manageProductLineBtn.setFont(new Font("Calisto MT", Font.PLAIN, 24));
        manageProductLineBtn.setForeground(Color.WHITE);
        manageProductLineBtn.setBackground(INACTIVE_COLOR);
        manageProductLineBtn.setFocusable(false);
        manageProductLineBtn.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        manageProductLineBtn.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "PRODUCT_LINE");
            setActiveButton(manageProductLineBtn, manageInventoryBtn);
        });

        centerPanel.add(manageInventoryBtn);
        centerPanel.add(manageProductLineBtn);

        // Right panel - Logout button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(192, 57, 43));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Calisto MT", Font.PLAIN, 18));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        logoutBtn.addActionListener(e -> {
            productLineController.save();
            dispose();
            new Login(new LoginController()).setVisible(true);
        });

        rightPanel.add(logoutBtn);

        // Add all panels to top panel
        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(centerPanel, BorderLayout.CENTER);
        topPanel.add(rightPanel, BorderLayout.EAST);

        // Main content panel
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(new Color(245, 245, 245));
        
        // Create a wrapper panel with padding
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(new Color(245, 245, 245));
        contentWrapper.add(mainContentPanel, BorderLayout.CENTER);
        
        mainScrollPane = new JScrollPane(contentWrapper);
        mainScrollPane.setBorder(null);
        mainScrollPane.getViewport().setBackground(new Color(245, 245, 245));
        
        // Layout main frame
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(mainScrollPane, BorderLayout.CENTER);
    }

    private void initPanels() {
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