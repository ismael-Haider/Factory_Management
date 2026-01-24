package FactoryManagement.gui.manager_frame;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import FactoryManagement.controllers.ManagerController;
import FactoryManagement.gui.supervisor_frame.FinishedProductPanel;
import FactoryManagement.models.User;

public class SupervisorPanel extends JPanel {
    private JTable table;
    private JButton addBtn, deleteBtn, updatePasswordBtn;
    private DefaultTableModel tableModel;
    private ManagerController controller;

    public SupervisorPanel(ManagerController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 245));

        JPanel mainContainer = new JPanel(new BorderLayout(10, 10));
        mainContainer.setBackground(new Color(245, 245, 245));
        mainContainer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JScrollPane tableScrollPane = initTable();
        JPanel bottomBar = initBottomBar();

        mainContainer.add(tableScrollPane, BorderLayout.CENTER);
        mainContainer.add(bottomBar, BorderLayout.SOUTH);

        add(mainContainer, BorderLayout.CENTER);
        loadAllSupervisors();
        setupShortcuts();
    }

    private JScrollPane initTable() {
        tableModel = new DefaultTableModel(
            new String[] { "ID", "Username","Password", "Role" }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.setFont(table.getFont().deriveFont(14f));
        table.setIntercellSpacing(new Dimension(10, 6));

        JTableHeader header = table.getTableHeader();
        header.setFont(header.getFont().deriveFont(Font.BOLD, 14f));
        header.setBackground(new Color(52, 73, 94));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));

        table.setGridColor(new Color(220, 220, 220));
        table.setShowGrid(true);
        table.setSelectionBackground(table.getSelectionBackground());
        table.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1100, 400));
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 0, 10, 0),
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1)
        ));

        return scrollPane;
    }

private void setupShortcuts() {
    InputMap im = this.getInputMap(FinishedProductPanel.WHEN_IN_FOCUSED_WINDOW);
    ActionMap am = this.getActionMap();

    // Alt + D → Add Supervisor
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.ALT_DOWN_MASK), "addSupervisor");
    am.put("addSupervisor", new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            addBtn.doClick();
        }
    });

    // Alt + D → Delete
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.ALT_DOWN_MASK), "delete");
    am.put("delete", new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            deleteBtn.doClick();
        }
    });

    // Alt + U → Update Password
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.ALT_DOWN_MASK), "updatePass");
    am.put("updatePass", new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            updatePasswordBtn.doClick();
        }
    });
}




    private JPanel initBottomBar() {
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomBar.setBackground(new Color(245, 245, 245));
        bottomBar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        addBtn = createStandardButton("Add Supervisor");
        deleteBtn = createStandardButton("Delete");
        updatePasswordBtn = createStandardButton("Update Password");
        updatePasswordBtn.setPreferredSize(new Dimension(200, 40));

        addBtn.addActionListener(e -> addSupervisor());
        deleteBtn.addActionListener(e -> deleteSelectedSupervisor());
        updatePasswordBtn.addActionListener(e -> updatePassword());

        bottomBar.add(addBtn);
        bottomBar.add(deleteBtn);
        bottomBar.add(updatePasswordBtn);

        return bottomBar;
    }

    private JButton createStandardButton(String text) {
        JButton button = new JButton(text);
        button.setFocusable(false);
        button.setBackground(new Color(52, 73, 94));
        button.setForeground(Color.WHITE);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 14f));
        button.setPreferredSize(new Dimension(150, 40));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(44, 62, 80));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(52, 73, 94));
            }
        });

        return button;
    }

    private void loadAllSupervisors() {
        tableModel.setRowCount(0);
        List<User> supervisors = controller.viewAllSupervisors();
        for (User user : supervisors) {
            if (user.getRole().equals(FactoryManagement.models.enums.UserRole.SUPERVISOR)) {
                tableModel.addRow(new Object[] { user.getId(), user.getUserName(),user.getPassword(), user.getRole()});
            }
        }
    }
private void addSupervisor() {
    String username = JOptionPane.showInputDialog(this, "Enter username:");
    if (username == null) {
        return; // Cancel / Exit
    }
    username = username.trim();
    if (username.isEmpty()) {
        JOptionPane.showMessageDialog(
                this,
                "Username cannot be empty",
                "Invalid Input",
                JOptionPane.ERROR_MESSAGE
        );
        controller.recordError("Empty username when adding Supervisor");
        return;
    }
    String password = JOptionPane.showInputDialog(this, "Enter password:");
    if (password == null) {
        return;
    }
    password = password.trim();
    if (password.isEmpty()) {
        JOptionPane.showMessageDialog(
                this,
                "Password cannot be empty",
                "Invalid Input",
                JOptionPane.ERROR_MESSAGE
        );
        controller.recordError("Empty password when adding Supervisor");
        return;
    }
    boolean added = controller.addSupervisor(username, password);
    if (added) {
        loadAllSupervisors();
    } else {
        JOptionPane.showMessageDialog(
                this,
                "Supervisor already exists",
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
        controller.recordError("The user (" + username + ") already exists");
    }
}
    private void deleteSelectedSupervisor() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;
        int id = (int) table.getValueAt(selectedRow, 0);
        if (controller.deleteSupervisor(id)) {
            loadAllSupervisors();
        } else {
            JOptionPane.showMessageDialog(this, "you cannot delete the manager account!");
        }
        table.repaint();
    }

    private void updatePassword() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;
        int id = (int) table.getValueAt(selectedRow, 0);
        String newPassword = JOptionPane.showInputDialog("Enter new password:");
        if (newPassword != null) {
            controller.updateSupervisorPassword(id, newPassword);
            JOptionPane.showMessageDialog(this, "Password updated.");
            loadAllSupervisors();
        }
    }
}