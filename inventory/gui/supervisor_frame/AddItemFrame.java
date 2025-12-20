package inventory.gui.supervisor_frame;

import inventory.controllers.InvenManageController;
import java.awt.*;
import javax.swing.*;

public class AddItemFrame extends JFrame {

    private JTextField nameField, categoryField, priceField, qtyField, minQtyField;
    private JButton saveBtn, cancelBtn;
    private InvenManageController controller;
    private Runnable onSuccess;

    private final Color BG = new Color(236,240,241);
    private final Color PRIMARY = new Color(44,62,80);
    private final Color ACCENT = new Color(26,188,156);

    public AddItemFrame(InvenManageController controller, Runnable onSuccess) {
        this.controller = controller;
        this.onSuccess = onSuccess;

        setTitle("Add New Item");
        setSize(400, 420);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {

        JLabel title = new JLabel("Add Item");
        title.setFont(new Font("Calisto MT", Font.BOLD, 24));
        title.setForeground(PRIMARY);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(15,0,15,0));
        add(title, BorderLayout.NORTH);


        JPanel form = new JPanel(new GridLayout(5,2,10,10));
        form.setBackground(BG);
        form.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        nameField = createField();
        categoryField = createField();
        priceField = createField();
        qtyField = createField();
        minQtyField = createField();

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

        nameField.addActionListener(e->{
            if (!nameField.getText().equals(""))
                categoryField.requestFocus();
            });
        categoryField.addActionListener(e->{
            if (!categoryField.getText().equals(""))
                priceField.requestFocus();
        });
        priceField.addActionListener(e->{
            if (!priceField.getText().equals(""))
                qtyField.requestFocus();
            });
        qtyField.addActionListener(e->{
            if (!qtyField.getText().equals("")){
                minQtyField.requestFocus();
            }
        });
        minQtyField.addActionListener(e->{
            if (!minQtyField.getText().equals(""))
                saveBtn.doClick();
            });

        cancelBtn.addActionListener(e -> dispose());

        saveBtn.addActionListener(e -> saveItem());
    }

    private JTextField createField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return f;
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8,15,8,15));
    }

    private void saveItem() {
        try {
            String name = nameField.getText().trim();
            String category = categoryField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            int qty = Integer.parseInt(qtyField.getText().trim());
            int minQty = Integer.parseInt(minQtyField.getText().trim());

            if (name.isEmpty() || category.isEmpty()|| price <= 0 || qty <= 0 || minQty <= 0) {
                JOptionPane.showMessageDialog(this, "Fill a valid items");
                return;
            }

            controller.add_item(name, category, price, qty, minQty);
            onSuccess.run(); // تحديث الجدول
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number format");
        }
    }
}
