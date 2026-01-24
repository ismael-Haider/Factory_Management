package FactoryManagement.gui.supervisor_frame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import FactoryManagement.controllers.TasksController;
import FactoryManagement.models.Task;

public class TasksPanel extends JPanel {

    CardLayout cardLayout = new CardLayout();
    JPanel mainPanel = new JPanel(cardLayout);
    private JTable table;
    private TaskTableModel model;
    private JComboBox<String> filterBoxbyTaskStatus;
    private JButton cancelBtn, addTaskBtn;
    private TasksController controller;
    private JComboBox<String> searchOnTasksbyProductLineID;
    private JComboBox<String> searchOntasksbyProductName;

    public TasksPanel(TasksController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 245));

        JPanel mainContainer = new JPanel(new BorderLayout(10, 10));
        mainContainer.setBackground(new Color(245, 245, 245));
        mainContainer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel topBar = initTop();
        JScrollPane tableScrollPane = initTable();
        JPanel bottomBar = initBottom();

        mainContainer.add(topBar, BorderLayout.NORTH);
        mainContainer.add(tableScrollPane, BorderLayout.CENTER);
        mainContainer.add(bottomBar, BorderLayout.SOUTH);

        add(mainContainer, BorderLayout.CENTER);

        enableClearSelectionOnOutsideClick();
        startAutoRefresh();
        setupShortcuts();
    }

    private void setupShortcuts() {
        InputMap im = this.getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = this.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.ALT_DOWN_MASK), "addTask");
        am.put("addTask", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTaskBtn.doClick();
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_DOWN_MASK), "cancelTask");
        am.put("cancelTask", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelBtn.doClick();
            }
        });
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

        JPanel productFilterPanel = new JPanel(new BorderLayout(0, 5));
        productFilterPanel.setOpaque(false);

        JLabel productLabel = new JLabel("Filter by Product:");
        productLabel.setFont(productLabel.getFont().deriveFont(Font.BOLD, 13f));

        searchOntasksbyProductName = new JComboBox<>();
        searchOntasksbyProductName.setPreferredSize(new Dimension(200, 35));
        searchOntasksbyProductName.addItem("ALL");

        controller.viewAllProducts()
                .forEach(p -> searchOntasksbyProductName.addItem(
                        p.getId() + " - " + p.getName()));

        searchOntasksbyProductName.addActionListener(e -> refreshTable());

        productFilterPanel.add(productLabel, BorderLayout.NORTH);
        productFilterPanel.add(searchOntasksbyProductName, BorderLayout.CENTER);

        top.add(filterPanel);
        top.add(lineFilterPanel);
        top.add(productFilterPanel);
        return top;
    }

    private JScrollPane initTable() {
        model = new TaskTableModel();
        table = new JTable(model);

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
            if (i != 7 && i != 8) {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }

        table.getColumn("Status").setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JLabel label = new JLabel(value.toString());
            label.setOpaque(true);
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setFont(label.getFont().deriveFont(Font.BOLD));

            String status = value.toString();
            switch (status) {
                case "IN_QUEUE":
                    label.setBackground(Color.gray);
                    label.setForeground(Color.WHITE);
                    break;
                case "FINISHED":
                    label.setBackground(new Color(200, 255, 200));
                    label.setForeground(new Color(39, 174, 96));
                    break;
                case "CANCELLED":
                    label.setBackground(new Color(255, 200, 200));
                    label.setForeground(new Color(192, 57, 43));
                    break;
                case "IN_PROGRESS":
                    label.setBackground(new Color(200, 220, 255));
                    label.setForeground(new Color(41, 128, 185));
                    break;
                default:
                    label.setBackground(Color.WHITE);
                    label.setForeground(Color.BLACK);
            }

            return label;
        });

        table.getColumn("Progress %").setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JProgressBar bar = new JProgressBar(0, 100);
            bar.setValue((int) value);
            bar.setStringPainted(true);
            bar.setFont(bar.getFont().deriveFont(12f));

            if ((int) value < 50) {
                bar.setForeground(new Color(192, 57, 43));
                bar.setBackground(new Color(255, 200, 200));
            } else if ((int) value < 100) {
                bar.setForeground(new Color(241, 196, 15));
                bar.setBackground(new Color(255, 255, 200));
            } else {
                bar.setForeground(new Color(39, 174, 96));
                bar.setBackground(new Color(200, 255, 200));
            }

            return bar;
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1100, 450));
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 0, 10, 0),
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1)));

        return scrollPane;
    }

    private JPanel initBottom() {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottom.setBackground(new Color(245, 245, 245));
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        cancelBtn = createStandardButton("Cancel Task");
        addTaskBtn = createStandardButton("Add Task");

        cancelBtn.addActionListener(e -> cancelTask());
        addTaskBtn.addActionListener(e -> openAddTaskFrame());

        bottom.add(cancelBtn);
        bottom.add(addTaskBtn);

        return bottom;
    }

    private JButton createStandardButton(String text) {
        JButton button = new JButton(text);
        button.setFocusable(false);
        button.setBackground(new Color(52, 73, 94));
        button.setForeground(Color.WHITE);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 14f));
        button.setPreferredSize(new Dimension(150, 40));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

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

        String lineFilter = (String) searchOnTasksbyProductLineID.getSelectedItem();
        if (!"ALL".equals(lineFilter)) {
            int lineId = Integer.parseInt(lineFilter.split(" - ")[0]);
            tasks = tasks.stream()
                    .filter(t -> t.getProductLineId() == lineId)
                    .toList();
        }

        String productFilter = (String) searchOntasksbyProductName.getSelectedItem();
        if (!"ALL".equals(productFilter)) {
            int productId = Integer.parseInt(productFilter.split(" - ")[0]);
            tasks = tasks.stream()
                    .filter(t -> t.getProductId() == productId)
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
            controller.recordError("Task (" + taskId + ") cannot be cancelled : in productLinePanel");
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
            case 0:
            case 2:
                return Integer.class;
            case 8:
                return Integer.class;
            case 4:
            case 5:
                return Object.class;
            default:
                return String.class;
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}