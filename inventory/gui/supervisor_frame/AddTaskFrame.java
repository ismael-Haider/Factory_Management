package inventory.gui.supervisor_frame;

import inventory.controllers.ProLineManageController;
import inventory.models.Product;
import inventory.models.ProductLine;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AddTaskFrame extends JFrame {

    private JComboBox<Product> productBox; // ComboBox بالـ Product نفسه
    private JComboBox<ProductLine> lineBox;
    private JTextField qtyField, clientField, startDateField, endDateField;
    private JButton saveBtn, cancelBtn;

    private final Color BG = new Color(236, 240, 241);
    private final Color PRIMARY = new Color(44, 62, 80);
    private final Color ACCENT = new Color(26, 188, 156);

    private ProLineManageController controller;
    private Runnable onSuccess;
    private Product selectedProduct;

    // لتخزين بيانات المنتج الجديد قبل حفظه
    private HashMap<Integer, Integer> newProductItems_id_qty;
    private String newProductName;

    public AddTaskFrame(ProLineManageController controller, Runnable onSuccess) {
        this.controller = controller;
        this.onSuccess = onSuccess;

        setTitle("Add Task");
        setSize(450, 480);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG);

        initUI();
    }

    private void initUI() {
        JLabel title = new JLabel("Add Task");
        title.setFont(new Font("Calisto MT", Font.BOLD, 24));
        title.setForeground(PRIMARY);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(6, 2, 10, 10));
        form.setBackground(BG);
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ================= PRODUCT COMBO =================
        productBox = new JComboBox<>();
        productBox.addItem(new Product("New", new HashMap<>()));
        Product.counter--; // كائن جديد يمثل خيار New
        List<Product> products = controller.viewAllProducts();
        for (Product p : products) {
            productBox.addItem(p);
        }

        // عرض الاسم فقط
        productBox.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value.getName());
            label.setOpaque(true);
            if (isSelected)
                label.setBackground(list.getSelectionBackground());
            else
                label.setBackground(list.getBackground());
            return label;
        });

        productBox.addActionListener(e -> {
            Product selected = (Product) productBox.getSelectedItem();
            if (selected == null)
                return;
            if ("New".equals(selected.getName())) {
                // منتج جديد -> اطلب اسم وعناصر
                // openNewProductDialog return hashMap
                newProductItems_id_qty = openNewProductDialog();
                while (newProductItems_id_qty.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "You must add at least one item!");
                    newProductName = null;
                    qtyField.setEditable(false);
                    newProductItems_id_qty = openNewProductDialog();
                }
                qtyField.setEditable(true);
                newProductName = askForProductName();
                if (newProductName == null) {
                    // User cancelled name entry; revert selection and stop processing
                    productBox.setSelectedIndex(0);
                    return;
                }
                System.out.println(newProductName + "" + newProductItems_id_qty);
            } else {
                HashMap<Integer, Integer> totalQty = selected.getItemQuantities();
                newProductItems_id_qty = totalQty;
                newProductName = selected.getName();
                System.out.println(newProductName + "" + newProductItems_id_qty);
            }
            selected.setName(newProductName);
            productBox.repaint();
        });

        // ================= PRODUCT LINE COMBO =================
        if (controller.viewAllProductLines().isEmpty()) {
            productBox.addItem(new Product("No Product Line", new HashMap<>()));
            Product.counter--;
        }
        lineBox = new JComboBox<>(controller.viewAllProductLines().toArray(new ProductLine[0]));

        qtyField = createField();
        clientField = createField();
        startDateField = createField(LocalDate.now().toString());
        startDateField.setEditable(false);
        endDateField = createField();
        endDateField.setText("yyyy-mm-dd");

        endDateField.setForeground(Color.GRAY);
        endDateField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (endDateField.getText().equals("yyyy-mm-dd")) {
                    endDateField.setText("");
                    endDateField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (endDateField.getText().isEmpty()) {
                    endDateField.setText("yyyy-mm-dd");
                    endDateField.setForeground(Color.GRAY);
                }
            }
        });

        qtyField.addActionListener(e -> { // Enter → Client field
            if (!qtyField.getText().equals(""))
                clientField.requestFocus();
        });

        clientField.addActionListener(e -> { // Enter → End Date field
            if (!clientField.getText().equals(""))
                endDateField.requestFocus();
        });

        endDateField.addActionListener(e -> { // Enter → Save button
            if (!endDateField.getText().equals(""))
                saveBtn.doClick();
        });

        form.add(new JLabel("Product:"));
        form.add(productBox);
        form.add(new JLabel("Product Line:"));
        form.add(lineBox);
        form.add(new JLabel("Quantity:"));
        form.add(qtyField);
        form.add(new JLabel("Client:"));
        form.add(clientField);
        form.add(new JLabel("Start Date:"));

        form.add(startDateField);
        form.add(new JLabel("End Date:"));
        form.add(endDateField);

        add(form, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(BG);

        saveBtn = new JButton("Save");
        cancelBtn = new JButton("Cancel");

        style(saveBtn, ACCENT);
        style(cancelBtn, PRIMARY);

        cancelBtn.addActionListener(e -> dispose());
        saveBtn.addActionListener(e -> saveTask());

        bottom.add(cancelBtn);
        bottom.add(saveBtn);

        add(bottom, BorderLayout.SOUTH);
    }

    private JTextField createField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return f;
    }

    private JTextField createField(String def) {
        JTextField f = createField();
        f.setText(def);
        return f;
    }

    private void style(JButton b, Color c) {
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    }

    // ================= SAVE LOGIC =================
    private void saveTask() {
        try {

            ProductLine pl = (ProductLine) lineBox.getSelectedItem();
            System.out.println(pl);
            int quantity = Integer.parseInt(qtyField.getText().trim());
            System.out.println(quantity);
            String client = clientField.getText().trim();
            System.out.println(client);
            LocalDate start = LocalDate.parse(startDateField.getText().trim());
            System.out.println(start);
            String eDate = endDateField.getText().trim();

            if (eDate.equals("yyyy-mm-dd") || eDate.equals("")) {
                JOptionPane.showMessageDialog(this, "End Date is required");
                return;
            }
            LocalDate end = LocalDate.parse(endDateField.getText().trim());
            if (end.isBefore(start)) {
                JOptionPane.showMessageDialog(this, "End Date cannot be before Start Date");
                return;
            }
            
            Product selected = (Product) productBox.getSelectedItem();
            System.out.println(selected);

            

            // If the product name does not already exist, create it; otherwise use existing product id
            if (!controller.productNameExists(newProductName)) {
                controller.addTask(newProductName, newProductItems_id_qty, quantity, client, start, end, pl.getId());
                System.out.println("created");
            } else {
                controller.addTask(selected.getId(), quantity, client, start, end, pl.getId());
            }

            onSuccess.run();
            dispose();
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use yyyy-MM-dd");
            return;
        }

        catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input! " + ex.getMessage());

        }
    }

    // dialog to create new product
    private HashMap<Integer, Integer> openNewProductDialog() {
        DefaultTableModel model = new DefaultTableModel(new String[] { "Item Name", "Quantity" }, 0);
        JTable table = new JTable(model);
        JButton addRow = new JButton("Add Row");
        JButton deleteRow = new JButton("Delete Selected Row");

        /////
        deleteRow.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Please select a row to delete",
                        "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            model.removeRow(selectedRow);
        });

        /////
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.add(addRow);
        buttons.add(deleteRow);
        addRow.addActionListener(e -> model.addRow(new Object[] { "", 0 }));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        panel.add(buttons, BorderLayout.SOUTH);

        int result = JOptionPane.showConfirmDialog(this, panel, "Enter Items for new Product",
                JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION)
            return null;
        HashMap<Integer, Integer> items = new HashMap<>();
        if (result == JOptionPane.OK_OPTION) {
            for (int i = 0; i < model.getRowCount(); i++) {
                String itemName = (String) model.getValueAt(i, 0);
                int qty = Integer.parseInt(model.getValueAt(i, 1).toString().trim());
                // i need to function to search on items by name it
                int id = controller.searchByName(itemName);
                if (qty <= 0 || id == 0) {
                    JOptionPane.showMessageDialog(this, "Invalid Input", "Error", JOptionPane.ERROR_MESSAGE);
                    items.clear();
                    break;
                }
                items.put(id, qty);
            }
        }
        return items;
    }

    private String askForProductName() {
        while (true) {
            String name = JOptionPane.showInputDialog(this, "Enter name for new product:");
            if (name == null) // user cancelled
                return null;
            name = name.trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Product name cannot be empty. Please try again.");
                continue;
            }
            if (controller.productNameExists(name)) {
                JOptionPane.showMessageDialog(this, "Product name already exists, please choose another name.");
                continue;
            }
            return name;
        }
    }
}
