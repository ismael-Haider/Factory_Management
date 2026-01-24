package FactoryManagement.gui.supervisor_frame;

import java.awt.*;
import javax.swing.*;

import FactoryManagement.controllers.InvenManageController;
import FactoryManagement.models.Item;

public class AddItemFrame extends JFrame {

    private JTextField nameField, categoryField, priceField, qtyField, minQtyField;
    private JButton saveBtn, cancelBtn;
    private InvenManageController controller;
    private Runnable onSuccess;

    private final Color BG = new Color(236, 240, 241);
    private final Color PRIMARY = new Color(44, 62, 80);
    private final Color ACCENT = new Color(26, 188, 156);
    private Robot robot;
    private Timer lockTimer;
    private Item item;

    public AddItemFrame(InvenManageController controller, Runnable onSuccess, Item item) {
        this.controller = controller;
        this.onSuccess = onSuccess;
        this.item = item;

        setTitle(item == null ? "Add Item" : "Update Item");
        setSize(400, 420);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        initUI();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (lockTimer != null)
                    lockTimer.stop();
            }
        });
    }

    private void initUI() {

        JLabel title = new JLabel(item == null ? "Add Item" : "Update Item");
        title.setFont(new Font("Calisto MT", Font.BOLD, 24));
        title.setForeground(PRIMARY);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
        form.setBackground(BG);
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        nameField = createField(item == null ? "" : item.getName());
        categoryField = createField(item == null ? "" : item.getCategory());
        priceField = createField(item == null ? "" : String.valueOf(item.getPrice()));
        qtyField = createField(item == null ? "" : String.valueOf(item.getQuantity()));
        minQtyField = createField(item == null ? "" : String.valueOf(item.getMinQuantity()));

        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Category:"));
        form.add(categoryField);
        form.add(new JLabel("Price:"));
        form.add(priceField);
        form.add(new JLabel("Quantity:"));
        form.add(qtyField);
        form.add(new JLabel("Min Quantity:"));
        form.add(minQtyField);

        add(form, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(BG);

        saveBtn = new JButton("Save");
        cancelBtn = new JButton("Cancel");

        styleButton(saveBtn, ACCENT);
        styleButton(cancelBtn, PRIMARY);

        bottom.add(cancelBtn);
        bottom.add(saveBtn);
        add(bottom, BorderLayout.SOUTH);

        nameField.addActionListener(e -> {
            if (!nameField.getText().equals(""))
                categoryField.requestFocus();
        });
        categoryField.addActionListener(e -> {
            if (!categoryField.getText().equals(""))
                priceField.requestFocus();
        });
        priceField.addActionListener(e -> {
            if (!priceField.getText().equals(""))
                qtyField.requestFocus();
        });
        qtyField.addActionListener(e -> {
            if (!qtyField.getText().equals("")) {
                minQtyField.requestFocus();
            }
        });
        minQtyField.addActionListener(e -> {
            if (!minQtyField.getText().equals(""))
                saveBtn.doClick();
        });

        cancelBtn.addActionListener(e -> dispose());

        saveBtn.addActionListener(e -> saveItem());
    }

    private JTextField createField(String s) {
        JTextField f = new JTextField(s);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return f;
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    }

    private void saveItem() {
        try {
            String name = nameField.getText().trim();
            String category = categoryField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            int qty = Integer.parseInt(qtyField.getText().trim());
            int minQty = Integer.parseInt(minQtyField.getText().trim());

            if (name.isEmpty() || category.isEmpty() || price <= 0 || qty <= 0 || minQty < 0) {
                JOptionPane.showMessageDialog(this, "Fill a valid items");
                controller.recordError("Missing details when adding an item");
                return;
            }
            if (item == null) {
                boolean sucsses = controller.add_item(name, category, price, qty, minQty);
                if (!sucsses) {
                    JOptionPane.showMessageDialog(this, "Quantity must be greater than or equal to Min Quantity");
                    controller.recordError(
                            "Quantity" + qty + " is less than Min Quantity " + minQty + ": in AddItemFrame");
                    return;
                }
                onSuccess.run();
                dispose();
            } else {
                controller.update_item(item.getId(), name, category, price, qty, minQty);
                onSuccess.run();
                dispose();

            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number format");
            controller.recordError("Invalid number format in AddItemFrame: " + ex.getMessage());
        }
    }
}
