package FactoryManagement.gui.manager_frame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

import FactoryManagement.controllers.LoginController;
import FactoryManagement.controllers.ManagerController;
import FactoryManagement.gui.Login;
import FactoryManagement.models.User;

public class ManagerFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    private final Color ACTIVE_COLOR = new Color(26, 188, 156);
    private final Color INACTIVE_COLOR = new Color(44, 62, 80);
    private ManagerController managerController;
    private JButton manageSupervisorsBtn;
    private JButton manageProductLinesBtn;
    private JButton manageNotesBtn;
    private JLabel managerLabel;
    private JLabel managerNameLabel;
    private JScrollPane mainScrollPane;
    private User user;
    private JButton logoutBtn;

    public ManagerFrame(User user) {
        managerController = new ManagerController();
        this.user = user;
        initUI();
        initPanels();
        setActiveButton(manageSupervisorsBtn, manageProductLinesBtn, manageNotesBtn);
        cardLayout.show(mainContentPanel, "SUPERVISORS");
        setupShortcut();
        setupCtrlZ();
    }

    private void initUI() {
        setTitle("Manager Dashboard");
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
                    managerController.save();
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

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 20));
        leftPanel.setOpaque(false);

        managerLabel = new JLabel("Manager:");
        managerLabel.setFont(new Font("Calisto MT", Font.PLAIN, 24));
        managerLabel.setForeground(Color.WHITE);

        managerNameLabel = new JLabel(user.getUserName());
        managerNameLabel.setFont(new Font("Calisto MT", Font.PLAIN, 24));
        managerNameLabel.setForeground(Color.WHITE);

        leftPanel.add(managerLabel);
        leftPanel.add(managerNameLabel);

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10));
        centerPanel.setOpaque(false);

        manageSupervisorsBtn = new JButton("Manage Supervisors");
        manageSupervisorsBtn.setFont(new Font("Calisto MT", Font.PLAIN, 24));
        manageSupervisorsBtn.setForeground(Color.WHITE);
        manageSupervisorsBtn.setBackground(ACTIVE_COLOR);
        manageSupervisorsBtn.setFocusable(false);
        manageSupervisorsBtn.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        manageSupervisorsBtn.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "SUPERVISORS");
            setActiveButton(manageSupervisorsBtn, manageProductLinesBtn, manageNotesBtn);
        });

        manageProductLinesBtn = new JButton("Manage Product Lines");
        manageProductLinesBtn.setFont(new Font("Calisto MT", Font.PLAIN, 24));
        manageProductLinesBtn.setForeground(Color.WHITE);
        manageProductLinesBtn.setBackground(INACTIVE_COLOR);
        manageProductLinesBtn.setFocusable(false);
        manageProductLinesBtn.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        manageProductLinesBtn.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "PRODUCT_LINES");
            setActiveButton(manageProductLinesBtn, manageSupervisorsBtn, manageNotesBtn);
        });

        manageNotesBtn = new JButton("Manage Notes");
        manageNotesBtn.setFont(new Font("Calisto MT", Font.PLAIN, 24));
        manageNotesBtn.setForeground(Color.WHITE);
        manageNotesBtn.setBackground(INACTIVE_COLOR);
        manageNotesBtn.setFocusable(false);
        manageNotesBtn.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        manageNotesBtn.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "NOTES");
            setActiveButton(manageNotesBtn, manageSupervisorsBtn, manageProductLinesBtn);
        });

        centerPanel.add(manageSupervisorsBtn);
        centerPanel.add(manageProductLinesBtn);
        centerPanel.add(manageNotesBtn);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 18));
        rightPanel.setOpaque(false);

        logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(192, 57, 43));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Calisto MT", Font.PLAIN, 18));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        logoutBtn.addActionListener(e -> {
            managerController.save();
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

    private int currentIndex = 0;
    private final String[] panelNames = { "SUPERVISORS", "PRODUCT_LINES", "NOTES" };

    private void setupCtrlZ() {
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
            case "SUPERVISORS":
                setActiveButton(manageSupervisorsBtn, manageProductLinesBtn, manageNotesBtn);
                break;
            case "PRODUCT_LINES":
                setActiveButton(manageProductLinesBtn, manageSupervisorsBtn, manageNotesBtn);
                break;
            case "NOTES":
                setActiveButton(manageNotesBtn, manageSupervisorsBtn, manageProductLinesBtn);
                break;
        }
    }

    private void initPanels() {
        SupervisorPanel supervisorPanel = new SupervisorPanel(managerController);
        ProductLinePanel productLinePanel = new ProductLinePanel(managerController);
        NotesPanel notesPanel = new NotesPanel(managerController);
        mainContentPanel.add(supervisorPanel, "SUPERVISORS");
        mainContentPanel.add(productLinePanel, "PRODUCT_LINES");
        mainContentPanel.add(notesPanel, "NOTES");
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