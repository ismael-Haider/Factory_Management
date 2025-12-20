package inventory.gui.supervisor_frame;

import inventory.controllers.ProLineManageController;
import inventory.models.Task;
import inventory.services.TaskService;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ProductLinePanel extends JPanel {

    private JTable table;
    private JButton addTaskBtn, removeTaskBtn, refreshBtn;
    private DefaultTableModel tableModel;
    private ProLineManageController controller;

    public ProductLinePanel(ProLineManageController controller) {
        this.controller = controller;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        initTopBar();
        initTable();
        initBottomBar();

        loadAllTasks(); // تحميل البيانات عند بدء التشغيل
    }

    private void initTopBar() {
        JLabel title = new JLabel("Product Line Management");
        title.setFont(new Font("Calisto MT", Font.BOLD, 22));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);
    }

    private void initTable() {
        tableModel = new DefaultTableModel(new String[]{"Task ID", "Product ID", "Quantity", "Client", "Start Date", "Delivered Date", "Product Line ID", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void initBottomBar() {
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addTaskBtn = new JButton("Add Task");
        removeTaskBtn = new JButton("Remove Task");
        refreshBtn = new JButton("Refresh");

        bottomBar.add(addTaskBtn);
        bottomBar.add(removeTaskBtn);
        bottomBar.add(refreshBtn);

        add(bottomBar, BorderLayout.SOUTH);


        addTaskBtn.addActionListener(e -> addTask());
        removeTaskBtn.addActionListener(e -> removeSelectedTask());
        refreshBtn.addActionListener(e -> loadAllTasks());
    }

    private void loadAllTasks() {
        tableModel.setRowCount(0);
        List<Task> tasks = TaskService.getAllTasks(); // مباشرة من الخدمة
        for (Task task : tasks) {
            tableModel.addRow(new Object[]{
                task.getId(),
                task.getProductId(),
                task.getQuantity(),
                task.getClientName(),
                task.getStartDate(),
                task.getDeliveredDate(),
                task.getProductLineId(),
                task.getStatus()
            });
        }
    }

    private void addTask() {
        try {
            int productId = Integer.parseInt(JOptionPane.showInputDialog(this, "Product ID:"));
            int quantity = Integer.parseInt(JOptionPane.showInputDialog(this, "Quantity:"));
            String clientName = JOptionPane.showInputDialog(this, "Client Name:");
            LocalDate startDate = LocalDate.parse(JOptionPane.showInputDialog(this, "Start Date (YYYY-MM-DD):"));
            LocalDate deliveredDate = LocalDate.parse(JOptionPane.showInputDialog(this, "Delivered Date (YYYY-MM-DD):"));
            int productLineId = Integer.parseInt(JOptionPane.showInputDialog(this, "Product Line ID:"));

            controller.addTask(productId, quantity, clientName, startDate, deliveredDate, productLineId, null);
            loadAllTasks();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
        }
    }

    private void removeSelectedTask() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;
        int taskId = (int) table.getValueAt(selectedRow, 0);

        // boolean success = controller.deleteTask(taskId);
        // if (!success) {
        //     JOptionPane.showMessageDialog(this, "Cannot delete finished or cancelled tasks.");
        // }
        loadAllTasks();
    }
}
