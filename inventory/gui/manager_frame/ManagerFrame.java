package inventory.gui.manager_frame;

import inventory.controllers.LoginController;
import inventory.controllers.ManagerController;
import inventory.gui.Login;
import inventory.models.User;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

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

    public ManagerFrame(User user) {
        managerController = new ManagerController();
        this.user = user;
        initUI();
        initPanels();
        setActiveButton(manageSupervisorsBtn, manageProductLinesBtn, manageNotesBtn);
        cardLayout.show(mainContentPanel, "SUPERVISORS");
    }

    private void initUI() {
        setTitle("Manager Dashboard");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(true);
        setSize(1300, 800);
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
                    // Save notes and ratings
                    inventory.services.NoteService.saveNotes();
                }
                dispose();
                System.exit(0);
            }
        });

        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(INACTIVE_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        // Left panel - Manager name
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

        // Center panel - Buttons
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

        // Right panel - Logout button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 18));
        rightPanel.setOpaque(false);

        JButton logoutBtn = new JButton("Logout");
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