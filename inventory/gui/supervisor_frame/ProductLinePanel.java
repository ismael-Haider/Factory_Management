package inventory.gui.supervisor_frame;

import inventory.controllers.ProLineManageController;
import inventory.models.Task;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class ProductLinePanel extends JPanel {

    private JTable table;
    private TaskTableModel model;
    private JComboBox<String> filterBoxbyTaskStatus;
    private JButton cancelBtn, addTaskBtn;
    private ProLineManageController controller;
    private JComboBox<String> searchOnTasksbyProductLineID;
    private JComboBox<String> searchOntasksbyProductName;

    public ProductLinePanel(ProLineManageController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(10, 10)); // Add some spacing
        setBackground(new Color(245, 245, 245));

        // Create a main container with padding on left and right
        JPanel mainContainer = new JPanel(new BorderLayout(10, 10));
        mainContainer.setBackground(new Color(245, 245, 245));
        mainContainer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Top, left, bottom, right

        // Initialize UI sections
        JPanel topBar = initTop();
        JScrollPane tableScrollPane = initTable();
        JPanel bottomBar = initBottom();

        // Add components to main container
        mainContainer.add(topBar, BorderLayout.NORTH);
        mainContainer.add(tableScrollPane, BorderLayout.CENTER);
        mainContainer.add(bottomBar, BorderLayout.SOUTH);

        // Add main container to this panel
        add(mainContainer, BorderLayout.CENTER);

        enableClearSelectionOnOutsideClick();
        startAutoRefresh();
    }

    private JPanel initTop() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        top.setBackground(new Color(245, 245, 245));
        top.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel filterPanel = new JPanel(new BorderLayout(0, 5));
        JLabel filterLabel = new JLabel("Filter by Status:");
        filterLabel.setFont(filterLabel.getFont().deriveFont(Font.BOLD, 13f));
        filterLabel.setForeground(new Color(60, 60, 60));

        filterBoxbyTaskStatus = new JComboBox<>(new String[] { "ALL", "IN_QUEUE", "FINISHED", "CANCELLED" });
        filterBoxbyTaskStatus.setPreferredSize(new Dimension(180, 35));
        filterBoxbyTaskStatus.addActionListener(e -> refreshTable());

        filterPanel.add(filterLabel, BorderLayout.NORTH);
        filterPanel.add(filterBoxbyTaskStatus, BorderLayout.CENTER);

        // ===== Filter by Product Line =====
        JPanel lineFilterPanel = new JPanel(new BorderLayout(0, 5));
        lineFilterPanel.setOpaque(false);

        JLabel lineLabel = new JLabel("Filter by Product Line:");
        lineLabel.setFont(lineLabel.getFont().deriveFont(Font.BOLD, 13f));

        searchOnTasksbyProductLineID = new JComboBox<>();
        searchOnTasksbyProductLineID.setPreferredSize(new Dimension(200, 35));
        searchOnTasksbyProductLineID.addItem("ALL");

        controller.viewAllProductLines()
                .forEach(pl -> searchOnTasksbyProductLineID.addItem(
                        pl.getId() + " - " + pl.getName()));

        searchOnTasksbyProductLineID.addActionListener(e -> refreshTable());

        lineFilterPanel.add(lineLabel, BorderLayout.NORTH);
        lineFilterPanel.add(searchOnTasksbyProductLineID, BorderLayout.CENTER);

        // أضفهم للـ top
        top.add(filterPanel);
        top.add(lineFilterPanel);
        return top;
    }

    private JScrollPane initTable() {
        model = new TaskTableModel();
        table = new JTable(model);

        // Style the table
        table.setRowHeight(35); // Increased row height
        table.setFont(table.getFont().deriveFont(14f)); // Larger font
        table.setIntercellSpacing(new Dimension(10, 6)); // More spacing between cells

        // Style the table header
        JTableHeader header = table.getTableHeader();
        header.setFont(header.getFont().deriveFont(Font.BOLD, 14f));
        header.setBackground(new Color(52, 73, 94));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));

        // Table styling
        table.setGridColor(new Color(220, 220, 220));
        table.setShowGrid(true);
        table.setSelectionBackground(new Color(52, 152, 219));
        table.setSelectionForeground(Color.WHITE);

        // Prevent column drag
        table.getTableHeader().setReorderingAllowed(false);

        // Center align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != 7 && i != 8) { // Don't apply to Status and Progress columns
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }

        // Custom renderer for Status column
        table.getColumn("Status").setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JLabel label = new JLabel(value.toString());
            label.setOpaque(true);
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setFont(label.getFont().deriveFont(Font.BOLD));

            if (isSelected) {
                label.setBackground(new Color(52, 152, 219));
                label.setForeground(Color.WHITE);
            } else {
                String status = value.toString();
                switch (status) {
                    case "IN_QUEUE":
                        label.setBackground(Color.YELLOW); // Light blue
                        label.setForeground(new Color(41, 128, 185));
                        break;
                    case "FINISHED":
                        label.setBackground(new Color(200, 255, 200)); // Light green
                        label.setForeground(new Color(39, 174, 96));
                        break;
                    case "CANCELLED":
                        label.setBackground(new Color(255, 200, 200)); // Light red
                        label.setForeground(new Color(192, 57, 43));
                        break;

                    case "IN_PROGRESS":
                        label.setBackground(new Color(52, 152, 219));// Light blue
                        label.setForeground(Color.WHITE);
                        break;
                    default:
                        label.setBackground(Color.WHITE);
                        label.setForeground(Color.BLACK);
                }
            }
            return label;
        });

        // Progress bar renderer
        table.getColumn("Progress %").setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JProgressBar bar = new JProgressBar(0, 100);
            bar.setValue((int) value);
            bar.setStringPainted(true);
            bar.setFont(bar.getFont().deriveFont(12f));

            // Color the progress bar based on value
            if ((int) value < 50) {
                bar.setForeground(new Color(192, 57, 43)); // Red
                bar.setBackground(new Color(255, 200, 200));
            } else if ((int) value < 100) {
                bar.setForeground(new Color(241, 196, 15)); // Yellow
                bar.setBackground(new Color(255, 255, 200));
            } else {
                bar.setForeground(new Color(39, 174, 96)); // Green
                bar.setBackground(new Color(200, 255, 200));
            }

            // Style for selection
            if (isSelected) {
                bar.setBorder(BorderFactory.createLineBorder(new Color(52, 152, 219), 2));
            }

            return bar;
        });

        // Create scroll pane with table
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1100, 450)); // Make the table larger
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 0, 10, 0),
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1)));

        return scrollPane;
    }

    private JPanel initBottom() {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottom.setBackground(new Color(245, 245, 245));
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Create styled buttons
        cancelBtn = createStandardButton("Cancel Task");
        addTaskBtn = createStandardButton("Add Task");

        cancelBtn.addActionListener(e -> cancelTask());
        addTaskBtn.addActionListener(e -> openAddTaskFrame());

        bottom.add(cancelBtn);
        bottom.add(addTaskBtn);

        return bottom;
    }

    /**
     * Helper method to create standard buttons
     */
    private JButton createStandardButton(String text) {
        JButton button = new JButton(text);
        button.setFocusable(false);
        button.setBackground(new Color(52, 73, 94));
        button.setForeground(Color.WHITE);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 14f));
        button.setPreferredSize(new Dimension(150, 40));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(44, 62, 80));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(52, 73, 94));
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(41, 57, 74));
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(52, 73, 94));
            }
        });

        return button;
    }

    private void refreshTable() {
        Integer selectedTaskId = null;
        int selectedRow = table.getSelectedRow();

        if (selectedRow != -1) {
            selectedTaskId = (int) model.getValueAt(selectedRow, 0);
        }

        model.setRowCount(0);

        List<Task> tasks = controller.viewAllTasks();
        String statusFilter = filterBoxbyTaskStatus.getSelectedItem().toString();
        if (!"ALL".equals(statusFilter)) {
            tasks = tasks.stream()
                    .filter(t -> t.getStatus().name().equals(statusFilter))
                    .toList();
        }

        // فلترة بالخط
        String lineFilter = (String) searchOnTasksbyProductLineID.getSelectedItem();
        if (!"ALL".equals(lineFilter)) {
            int lineId = Integer.parseInt(lineFilter.split(" - ")[0]);
            tasks = tasks.stream()
                    .filter(t -> t.getProductLineId() == lineId)
                    .toList();
        }

        int rowToSelect = -1;
        int rowIndex = 0;
        model.setRowCount(0);

        for (Task t : tasks) {
            model.addRow(new Object[] {
                    t.getId(),
                    controller.searchProductById(t.getProductId()).getName(),
                    t.getQuantity(),
                    t.getClientName(),
                    t.getStartDate(),
                    t.getDeliveredDate(),
                    controller.searchProductLineById(t.getProductLineId()).getName(),
                    t.getStatus().name(),
                    (int) t.getPercentage()
            });

            if (selectedTaskId != null && t.getId() == selectedTaskId) {
                rowToSelect = rowIndex;
            }
            rowIndex++;
        }

        if (rowToSelect != -1) {
            table.setRowSelectionInterval(rowToSelect, rowToSelect);
        }
    }

    private void cancelTask() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a task first",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int taskId = (int) model.getValueAt(row, 0);

        boolean cancelled = controller.cancelTask(taskId);

        if (!cancelled) {
            JOptionPane.showMessageDialog(
                    this,
                    "This task cannot be cancelled\n(Status is FINISHED or CANCELLED)",
                    "Action not allowed",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        refreshTable();
    }

    private void openAddTaskFrame() {
        new AddTaskFrame(controller, this::refreshTable).setVisible(true);
    }

    private void startAutoRefresh() {
        new Timer(1000, e -> refreshTable()).start();
    }

    private void enableClearSelectionOnOutsideClick() {
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                table.clearSelection();
            }
        });
    }
}

// TaskTableModel class (assuming it exists)
class TaskTableModel extends DefaultTableModel {
    private final String[] COLUMN_NAMES = {
            "ID", "Product", "Quantity", "Client", "Start Date",
            "Delivered Date", "Product Line", "Status", "Progress %"
    };

    public TaskTableModel() {
        super();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0: // ID
            case 2: // Quantity
                return Integer.class;
            case 8: // Progress %
                return Integer.class;
            case 4: // Start Date
            case 5: // Delivered Date
                return Object.class; // Assuming these are Date objects
            default:
                return String.class;
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false; // Make table non-editable
    }
}

// TaskCellRenderer class (assuming it exists)
class TaskCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Center align text
        setHorizontalAlignment(JLabel.CENTER);

        // Status column styling
        if (column == 7 && value != null) {
            String status = value.toString();
            c.setFont(c.getFont().deriveFont(Font.BOLD));

            if (!isSelected) {
                switch (status) {
                    case "IN_QUEUE":
                        c.setBackground(new Color(200, 220, 255));
                        c.setForeground(new Color(41, 128, 185));
                        break;
                    case "FINISHED":
                        c.setBackground(new Color(200, 255, 200));
                        c.setForeground(new Color(39, 174, 96));
                        break;
                    case "CANCELLED":
                        c.setBackground(new Color(255, 200, 200));
                        c.setForeground(new Color(192, 57, 43));
                        break;
                    default:
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                }
            }
        }
        return c;
    }
}