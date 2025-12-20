package inventory.gui.supervisor_frame;

import inventory.controllers.InvenManageController;
import inventory.models.Item;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class InventoryPanel extends JPanel {
    private JTable table;
    private JButton addBtn, deleteBtn, Save;
    private JTextField searchField;
    private JComboBox<String> filterBox;
    private InvenManageController controller;
    private DefaultTableModel tableModel;

    public InventoryPanel(InvenManageController controller) {
        this.controller = controller;
        setLayout(new BorderLayout());
        setBackground(new Color(236, 240, 241));

        initTopBar();
        initTable();
        initBottomBar();

        loadAllItems(); // تحميل البيانات عند بدء التشغيل
    }

    private void initTopBar() {
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(15);
        filterBox = new JComboBox<>(new String[]{"All Items", "Available", "Low Stock", "Finished"});

        topBar.add(new JLabel("Search:"));
        topBar.add(searchField);
        topBar.add(filterBox);

        // أضافة الأحداث للبحث والتصفية
        filterBox.addActionListener(e -> filterItems());

        add(topBar, BorderLayout.NORTH);
    }

    private void initTable() {
        tableModel = new DefaultTableModel(new String[]{"ID","Name","Category","Price","Qty","Min Qty"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // منع التعديل مباشرة على الجدول
            }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void initBottomBar() {
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addBtn = new JButton("Add");
        deleteBtn = new JButton("Delete");
        Save = new JButton("Refresh");

        bottomBar.add(addBtn);
        bottomBar.add(deleteBtn);
        bottomBar.add(Save);

        add(bottomBar, BorderLayout.SOUTH);

        // أحداث الأزرار
        addBtn.addActionListener(e -> addItem());
        deleteBtn.addActionListener(e -> deleteSelectedItem());
        Save.addActionListener(e -> loadAllItems());
    }

    private void loadAllItems() {
        tableModel.setRowCount(0); // مسح الجدول
        List<Item> items = controller.view_items();
        for (Item item : items) {
            tableModel.addRow(new Object[]{
                item.getId(),
                item.getName(),
                item.getCategory(),
                item.getPrice(),
                item.getQuantity(),
                item.getMinQuantity()
            });
        }
    }

    private void filterItems() {
        String filter = (String) filterBox.getSelectedItem();
        List<Item> filteredItems;
        filteredItems = switch (filter) {
            case "Available" -> controller.availableItems();
            case "Low Stock" -> controller.unAvailableItems();
            case "Run Out" -> controller.runOutItems();
            default -> controller.view_items();
        };
        tableModel.setRowCount(0);
        for (Item item : filteredItems) {
            tableModel.addRow(new Object[]{
                item.getId(),
                item.getName(),
                item.getCategory(),
                item.getPrice(),
                item.getQuantity(),
                item.getMinQuantity()
            });
        }
    }

private void addItem() {
    AddItemFrame frame = new AddItemFrame(controller, this::loadAllItems);
    frame.setVisible(true);
}


    private void deleteSelectedItem() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;
        int id = (int) table.getValueAt(selectedRow, 0);
        controller.delete_item(id);
        loadAllItems();
    }
}
