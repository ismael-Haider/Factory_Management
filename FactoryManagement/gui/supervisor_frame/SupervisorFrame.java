package FactoryManagement.gui.supervisor_frame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

import FactoryManagement.controllers.InvenManageController;
import FactoryManagement.controllers.LoginController;
import FactoryManagement.controllers.TasksController;
import FactoryManagement.gui.Login;
import FactoryManagement.models.*;

public class SupervisorFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    private final Color ACTIVE_COLOR = new Color(26, 188, 156);
    private final Color INACTIVE_COLOR = new Color(44, 62, 80);

    private InvenManageController itemController;
    private TasksController tasksController;
    private InventoryPanel inventoryPanel;
    private JButton manageInventoryBtn;
    private JButton manageTasksBtn;
    JButton finishedProductsBtn = new JButton(" Finished Products");
    private JButton logoutBtn;
    private JLabel supervisorLabel;
    private JLabel supervisorNameLabel;
    private JScrollPane mainScrollPane;
    private User user;

    public SupervisorFrame(User user) {
        itemController = new InvenManageController();
        inventoryPanel = new InventoryPanel(itemController);
        tasksController = new TasksController();
        this.user = user;
        initUI();
        initPanels();
        setActiveButton(manageInventoryBtn, manageTasksBtn);
        cardLayout.show(mainContentPanel, "INVENTORY");
        setupCtrlTab();
        setupShortcut();

    }

    private String[] panelNames = { "INVENTORY", "TASKS", "FINISHED_PRODUCTS" };
    private int currentIndex = 0;

    private void setupCtrlTab() {
        InputMap im = mainContentPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = mainContentPanel.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "switchPanel");
        am.put("switchPanel", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentIndex = (currentIndex + 1) % panelNames.length;
                cardLayout.show(mainContentPanel, panelNames[currentIndex]);
                updateButtonColors();
            }
        });
    }

    private void updateButtonColors() {
        switch (panelNames[currentIndex]) {
            case "INVENTORY":
                setActiveButton(manageInventoryBtn, manageTasksBtn, finishedProductsBtn);
                break;
            case "TASKS":
                setActiveButton(manageTasksBtn, manageInventoryBtn, finishedProductsBtn);
                break;
            case "FINISHED_PRODUCTS":
                setActiveButton(finishedProductsBtn, manageInventoryBtn, manageTasksBtn);
                break;
        }
    }

    private void initUI() {
        setTitle("Supervisor Dashboard");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(true);

        setSize(1300, 820);
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                        null,
                        "Do you want to save before exiting?",
                        "Confirm Exit",
                        JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    tasksController.save();
                    dispose();
                    System.exit(0);
                } else if (choice == JOptionPane.NO_OPTION) {
                    dispose();
                    System.exit(0);
                }
            }
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(INACTIVE_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        leftPanel.setOpaque(false);

        supervisorLabel = new JLabel("Supervisor:");
        supervisorLabel.setFont(new Font("Calisto MT", Font.PLAIN, 24));
        supervisorLabel.setForeground(Color.WHITE);
        supervisorLabel.setVerticalAlignment(SwingConstants.CENTER);
        supervisorNameLabel = new JLabel(user.getUserName());
        supervisorNameLabel.setFont(new Font("Calisto MT", Font.PLAIN, 24));
        supervisorNameLabel.setForeground(Color.WHITE);

        leftPanel.add(supervisorLabel);
        leftPanel.add(supervisorNameLabel);

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
            setActiveButton(manageInventoryBtn, manageTasksBtn, finishedProductsBtn);
        });

        manageTasksBtn = new JButton("Manage Tasks");
        manageTasksBtn.setFont(new Font("Calisto MT", Font.PLAIN, 24));
        manageTasksBtn.setForeground(Color.WHITE);
        manageTasksBtn.setBackground(INACTIVE_COLOR);
        manageTasksBtn.setFocusable(false);
        manageTasksBtn.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        manageTasksBtn.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "TASKS");
            setActiveButton(manageTasksBtn, manageInventoryBtn, finishedProductsBtn);
        });

        finishedProductsBtn.setFont(new Font("Calisto MT", Font.PLAIN, 24));
        finishedProductsBtn.setForeground(Color.WHITE);
        finishedProductsBtn.setBackground(INACTIVE_COLOR);
        finishedProductsBtn.setFocusable(false);
        finishedProductsBtn.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        finishedProductsBtn.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "FINISHED_PRODUCTS");
            setActiveButton(finishedProductsBtn, manageInventoryBtn, manageTasksBtn);
        });

        centerPanel.add(manageInventoryBtn);
        centerPanel.add(manageTasksBtn);
        centerPanel.add(finishedProductsBtn);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 18));
        rightPanel.setOpaque(false);

        logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(192, 57, 43));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Calisto MT", Font.PLAIN, 18));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        logoutBtn.addActionListener(e -> {
            tasksController.save();
            dispose();
            new Login(new LoginController()).setVisible(true);
        });

        rightPanel.add(logoutBtn);

        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(centerPanel, BorderLayout.CENTER);
        topPanel.add(rightPanel, BorderLayout.EAST);

        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(new Color(245, 245, 245));

        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(new Color(245, 245, 245));
        contentWrapper.add(mainContentPanel, BorderLayout.CENTER);

        mainScrollPane = new JScrollPane(contentWrapper);
        mainScrollPane.setBorder(null);
        mainScrollPane.getViewport().setBackground(new Color(245, 245, 245));

        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(mainScrollPane, BorderLayout.CENTER);
    }

    private void setupShortcut() {
        InputMap im = mainContentPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = mainContentPanel.getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK), "logout");
        am.put("logout", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (logoutBtn != null) {
                    logoutBtn.doClick();
                }
            }
        });
    }

    private void initPanels() {
        TasksPanel tasksPanel = new TasksPanel(tasksController);
        FinishedProductPanel finishedProductPanel = new FinishedProductPanel(tasksController);
        mainContentPanel.add(inventoryPanel, "INVENTORY");
        mainContentPanel.add(tasksPanel, "TASKS");
        mainContentPanel.add(finishedProductPanel, "FINISHED_PRODUCTS");
    }

    private void setActiveButton(JButton active, JButton... inactives) {
        active.setBackground(ACTIVE_COLOR);
        active.setForeground(Color.WHITE);
        for (JButton inactive : inactives) {
            inactive.setBackground(INACTIVE_COLOR);
            inactive.setForeground(Color.WHITE);
        }
    }
}