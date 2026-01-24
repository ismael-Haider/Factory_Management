package FactoryManagement.gui.supervisor_frame;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import FactoryManagement.controllers.InvenManageController;
import FactoryManagement.models.Item;

public class InventoryPanel extends JPanel {
    private JTable table;
    private JButton addBtn, deleteBtn, Refresh, save, Update;
    private JTextField searchField;
    private JTextField searchField2;
    private JComboBox<String> filterBox;
    private DefaultTableModel tableModel;

    private class QuantityColorRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            if (column == 4) {
                try {
                    int quantity = (int) table.getValueAt(row, column);
                    int minQuantity = (int) table.getValueAt(row, 5);

                    if (quantity < minQuantity) {
                        c.setBackground(new Color(255, 200, 200));
                        c.setForeground(Color.RED.darker());
                    } else if (quantity > minQuantity) {
                        c.setBackground(new Color(200, 255, 200));
                        c.setForeground(Color.GREEN.darker());
                    } else {
                        c.setBackground(new Color(200, 220, 255));
                        c.setForeground(Color.BLUE.darker());
                    }

                    if (c instanceof JComponent) {
                        ((JComponent) c).setFont(c.getFont().deriveFont(Font.BOLD));
                    }

                    if (isSelected) {
                        c.setBackground(c.getBackground().darker());
                    }
                } catch (Exception e) {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }
            } else {
                if (!isSelected) {
                    c.setBackground(Color.WHITE);
                }
                c.setForeground(Color.BLACK);
            }

            setHorizontalAlignment(JLabel.CENTER);

            return c;
        }
    }

    private InvenManageController controller;

    public InventoryPanel(InvenManageController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 245));

        JPanel mainContainer = new JPanel(new BorderLayout(10, 10));
        mainContainer.setBackground(new Color(245, 245, 245));
        mainContainer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel topBar = initTopBar();
        JScrollPane tableScrollPane = initTable();
        JPanel bottomBar = initBottomBar();

        setupShortcuts();

        mainContainer.add(topBar, BorderLayout.NORTH);
        mainContainer.add(tableScrollPane, BorderLayout.CENTER);
        mainContainer.add(bottomBar, BorderLayout.SOUTH);

        add(mainContainer, BorderLayout.CENTER);

        loadAllItems();
    }

    private void setupShortcuts() {
        InputMap im = this.getInputMap(this.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = this.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.ALT_DOWN_MASK), "addItem");
        am.put("addItem", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBtn.doClick();
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.ALT_DOWN_MASK), "deleteItem");
        am.put("deleteItem", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteBtn.doClick();
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.ALT_DOWN_MASK), "refreshItems");
        am.put("refreshItems", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Refresh.doClick();
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.ALT_DOWN_MASK), "updateItem");
        am.put("updateItem", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Update.doClick();
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_DOWN_MASK), "saveItems");
        am.put("saveItems", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                save.doClick();
            }
        });
    }

    private JPanel initTopBar() {
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topBar.setBackground(new Color(245, 245, 245));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel nameSearchPanel = new JPanel(new BorderLayout(0, 5));
        JLabel nameLabel = new JLabel("Search by Name:");
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 13f));
        nameLabel.setForeground(new Color(60, 60, 60));

        searchField = new JTextField(25);
        searchField.setPreferredSize(new Dimension(250, 35));
        searchField.setText("Enter Name");
        searchField.setForeground(Color.GRAY);

        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Enter Name")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Enter Name");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        searchField.addActionListener(e -> {
            String query = searchField.getText();
            if (query.isEmpty() || query.equals("Enter Name")) {
                loadAllItems();
                this.requestFocus(true);
                return;
            }
            List<Item> searchItem = controller.searchByName(query);
            tableModel.setRowCount(0);
            for (Item item : searchItem) {
                tableModel.addRow(new Object[] {
                        item.getId(), item.getName(), item.getCategory(),
                        item.getPrice(), item.getQuantity(), item.getMinQuantity()
                });
            }
        });

        nameSearchPanel.add(nameLabel, BorderLayout.NORTH);
        nameSearchPanel.add(searchField, BorderLayout.CENTER);

        JPanel categorySearchPanel = new JPanel(new BorderLayout(0, 5));
        JLabel categoryLabel = new JLabel("Search by Category:");
        categoryLabel.setFont(categoryLabel.getFont().deriveFont(Font.BOLD, 13f));
        categoryLabel.setForeground(new Color(60, 60, 60));

        searchField2 = new JTextField(25);
        searchField2.setPreferredSize(new Dimension(250, 35));
        searchField2.setText("Enter Category");
        searchField2.setForeground(Color.GRAY);

        searchField2.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField2.getText().equals("Enter Category")) {
                    searchField2.setText("");
                    searchField2.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField2.getText().isEmpty()) {
                    searchField2.setText("Enter Category");
                    searchField2.setForeground(Color.GRAY);
                }
            }
        });

        searchField2.addActionListener(e -> {
            String query = searchField2.getText();
            if (query.isEmpty() || query.equals("Enter Category")) {
                loadAllItems();
                this.requestFocus(true);
                return;
            }
            List<Item> searchItem2 = controller.searchByCategory(query);
            tableModel.setRowCount(0);
            for (Item item : searchItem2) {
                tableModel.addRow(new Object[] {
                        item.getId(), item.getName(), item.getCategory(),
                        item.getPrice(), item.getQuantity(), item.getMinQuantity()
                });
            }
        });

        categorySearchPanel.add(categoryLabel, BorderLayout.NORTH);
        categorySearchPanel.add(searchField2, BorderLayout.CENTER);

        JPanel filterPanel = new JPanel(new BorderLayout(0, 5));
        JLabel filterLabel = new JLabel("Filter by Status:");
        filterLabel.setFont(filterLabel.getFont().deriveFont(Font.BOLD, 13f));
        filterLabel.setForeground(new Color(60, 60, 60));

        filterBox = new JComboBox<>(new String[] {
                "All Items", "Available", "Low Stock", "Run Out"
        });
        filterBox.setPreferredSize(new Dimension(180, 35));
        filterBox.addActionListener(e -> filterItems());

        filterPanel.add(filterLabel, BorderLayout.NORTH);
        filterPanel.add(filterBox, BorderLayout.CENTER);

        topBar.add(nameSearchPanel);
        topBar.add(Box.createHorizontalStrut(25));
        topBar.add(categorySearchPanel);
        topBar.add(Box.createHorizontalStrut(25));
        topBar.add(filterPanel);

        return topBar;
    }

    private JScrollPane initTable() {
        tableModel = new DefaultTableModel(
                new String[] { "ID", "Name", "Category", "Price", "Quantity", "Min Quantity" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 4 || columnIndex == 5) {
                    return Integer.class;
                } else if (columnIndex == 3) {
                    return Double.class;
                }
                return String.class;
            }
        };

        table = new JTable(tableModel);

        QuantityColorRenderer colorRenderer = new QuantityColorRenderer();
        table.getColumnModel().getColumn(4).setCellRenderer(colorRenderer);

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
            if (i != 4) {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1100, 450));
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 0, 10, 0),
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1)));

        return scrollPane;
    }

    private JPanel initBottomBar() {
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomBar.setBackground(new Color(245, 245, 245));
        bottomBar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        addBtn = createStandardButton("Add");
        deleteBtn = createStandardButton("Delete");
        Refresh = createStandardButton("Refresh");
        Update = createStandardButton("Update");
        save = createStandardButton("Save");

        addBtn.addActionListener(e -> addItem(e));
        deleteBtn.addActionListener(e -> deleteSelectedItem());
        Update.addActionListener(e -> addItem(e));
        Refresh.addActionListener(e -> loadAllItems());
        save.addActionListener(e -> controller.saveItems());

        bottomBar.add(addBtn);
        bottomBar.add(deleteBtn);
        bottomBar.add(Refresh);
        bottomBar.add(Update);
        bottomBar.add(save);

        return bottomBar;
    }

    private JButton createStandardButton(String text) {
        JButton button = new JButton(text);
        button.setFocusable(false);
        button.setBackground(new Color(52, 73, 94));
        button.setForeground(Color.WHITE);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 14f));
        button.setPreferredSize(new Dimension(100, 40));
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

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(new Color(41, 57, 74));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(new Color(52, 73, 94));
            }
        });

        return button;
    }

    private void loadAllItems() {
        tableModel.setRowCount(0);
        List<Item> items = controller.view_items();
        for (Item item : items) {
            tableModel.addRow(new Object[] {
                    item.getId(), item.getName(), item.getCategory(),
                    item.getPrice(), item.getQuantity(), item.getMinQuantity()
            });
        }
    }

    private void filterItems() {
        String filter = (String) filterBox.getSelectedItem();
        List<Item> filteredItems = switch (filter) {
            case "Available" -> controller.availableItems();
            case "Low Stock" -> controller.unAvailableItems();
            case "Run Out" -> controller.runOutItems();
            default -> controller.view_items();
        };

        tableModel.setRowCount(0);
        for (Item item : filteredItems) {
            tableModel.addRow(new Object[] {
                    item.getId(), item.getName(), item.getCategory(),
                    item.getPrice(), item.getQuantity(), item.getMinQuantity()
            });
        }
    }

    private void addItem(ActionEvent e) {
        AddItemFrame frame;
        if (e.getSource() == addBtn) {
            frame = new AddItemFrame(controller, this::loadAllItems, null);
        } else {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1)
                return;
            int id = (int) table.getValueAt(selectedRow, 0);
            Item item = controller.SearchById(id);
            frame = new AddItemFrame(controller, this::loadAllItems, item);
        }
        frame.setVisible(true);
    }

    private void deleteSelectedItem() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1)
            return;

        int id = (int) table.getValueAt(selectedRow, 0);
        controller.delete_item(id);
        loadAllItems();
    }
}