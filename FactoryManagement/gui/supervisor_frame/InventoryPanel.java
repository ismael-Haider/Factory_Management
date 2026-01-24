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
    // === UI Components ===
    private JTable table;                  // Table to display items
    private JButton addBtn, deleteBtn, Refresh, save, Update; // Action buttons
    private JTextField searchField;        // Search by name
    private JTextField searchField2;       // Search by category
    private JComboBox<String> filterBox;   // Filter dropdown
    private DefaultTableModel tableModel;  // Table model (non-editable)
    
    // === Custom renderer for coloring quantity cells ===
    private class QuantityColorRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column
            );
            
            // Only color the "Quantity" column (index 4)
            if (column == 4) {
                try {
                    int quantity = (int) table.getValueAt(row, column);
                    int minQuantity = (int) table.getValueAt(row, 5); // Min Qty column index
                    
                    // Set colors based on comparison
                    if (quantity < minQuantity) {
                        c.setBackground(new Color(255, 200, 200)); // Light red
                        c.setForeground(Color.RED.darker());
                    } else if (quantity > minQuantity) {
                        c.setBackground(new Color(200, 255, 200)); // Light green
                        c.setForeground(Color.GREEN.darker());
                    } else { // quantity == minQuantity
                        c.setBackground(new Color(200, 220, 255)); // Light blue
                        c.setForeground(Color.BLUE.darker());
                    }
                    
                    // Make text bold for better visibility
                    if (c instanceof JComponent) {
                        ((JComponent) c).setFont(c.getFont().deriveFont(Font.BOLD));
                    }
                    
                    // Ensure selection color still visible
                    if (isSelected) {
                        c.setBackground(c.getBackground().darker());
                    }
                } catch (Exception e) {
                    // If there's an error parsing values, use default colors
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }
            } else {
                // Default colors for other columns
                if (!isSelected) {
                    c.setBackground(Color.WHITE);
                }
                c.setForeground(Color.BLACK);
            }
            
            // Center align all text
            setHorizontalAlignment(JLabel.CENTER);
            
            return c;
        }
    }

    // === Controller Reference ===
    private InvenManageController controller;

    /**
     * Constructor: initializes the panel with controller reference.
     */
    public InventoryPanel(InvenManageController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(10, 10)); // Add some spacing
        setBackground(new Color(245, 245, 245));
        
        // Create a main container with padding on left and right
        JPanel mainContainer = new JPanel(new BorderLayout(10, 10));
        mainContainer.setBackground(new Color(245, 245, 245));
        mainContainer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Top, left, bottom, right

        // Initialize UI sections
        JPanel topBar = initTopBar();
        JScrollPane tableScrollPane = initTable();
        JPanel bottomBar = initBottomBar();

        setupShortcuts();

        // Add components to main container
        mainContainer.add(topBar, BorderLayout.NORTH);
        mainContainer.add(tableScrollPane, BorderLayout.CENTER);
        mainContainer.add(bottomBar, BorderLayout.SOUTH);

        // Add main container to this panel
        add(mainContainer, BorderLayout.CENTER);

        // Load all items at startup
        loadAllItems();
    }
private void setupShortcuts() {
    InputMap im = this.getInputMap(this.WHEN_IN_FOCUSED_WINDOW);
    ActionMap am = this.getActionMap();

    // Alt + A → Add
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.ALT_DOWN_MASK), "addItem");
    am.put("addItem", new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            addBtn.doClick();
        }
    });

    // Alt + D → Delete
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.ALT_DOWN_MASK), "deleteItem");
    am.put("deleteItem", new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            deleteBtn.doClick();
        }
    });

    // Alt + R → Refresh
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.ALT_DOWN_MASK), "refreshItems");
    am.put("refreshItems", new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Refresh.doClick();
        }
    });

    // Alt + U → Update
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.ALT_DOWN_MASK), "updateItem");
    am.put("updateItem", new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Update.doClick();
        }
    });

    // Alt + S → Save
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_DOWN_MASK), "saveItems");
    am.put("saveItems", new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            save.doClick();
        }
    });
}

    /**
     * Top bar: contains search fields and filter dropdown.
     */
    private JPanel initTopBar() {
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topBar.setBackground(new Color(245, 245, 245));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // === Search by Name field ===
        JPanel nameSearchPanel = new JPanel(new BorderLayout(0, 5));
        JLabel nameLabel = new JLabel("Search by Name:");
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 13f));
        nameLabel.setForeground(new Color(60, 60, 60));
        
        searchField = new JTextField(25); // Larger field
        searchField.setPreferredSize(new Dimension(250, 35));
        searchField.setText("Enter Name");
        searchField.setForeground(Color.GRAY);

        // Placeholder behavior for Name field
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

        // Action: search by name when Enter is pressed
        searchField.addActionListener(e -> {
            String query = searchField.getText();
            if (query.isEmpty() || query.equals("Enter Name")){
                loadAllItems();
                this.requestFocus(true);
                return;
            }
            List<Item> searchItem = controller.searchByName(query);
            tableModel.setRowCount(0); // Clear table
            for (Item item : searchItem) {
                tableModel.addRow(new Object[] {
                    item.getId(), item.getName(), item.getCategory(),
                    item.getPrice(), item.getQuantity(), item.getMinQuantity()
                });
            }
        });

        nameSearchPanel.add(nameLabel, BorderLayout.NORTH);
        nameSearchPanel.add(searchField, BorderLayout.CENTER);

        // === Search by Category field ===
        JPanel categorySearchPanel = new JPanel(new BorderLayout(0, 5));
        JLabel categoryLabel = new JLabel("Search by Category:");
        categoryLabel.setFont(categoryLabel.getFont().deriveFont(Font.BOLD, 13f));
        categoryLabel.setForeground(new Color(60, 60, 60));
        
        searchField2 = new JTextField(25); // Larger field
        searchField2.setPreferredSize(new Dimension(250, 35));
        searchField2.setText("Enter Category");
        searchField2.setForeground(Color.GRAY);

        // Placeholder behavior for Category field
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

        // Action: search by category when Enter is pressed
        searchField2.addActionListener(e -> {
            String query = searchField2.getText();
            if (query.isEmpty() || query.equals("Enter Category")){
                loadAllItems();
                this.requestFocus(true);
                return;
            }
            List<Item> searchItem2 = controller.searchByCategory(query);
            tableModel.setRowCount(0); // Clear table
            for (Item item : searchItem2) {
                tableModel.addRow(new Object[] {
                    item.getId(), item.getName(), item.getCategory(),
                    item.getPrice(), item.getQuantity(), item.getMinQuantity()
                });
            }
        });

        categorySearchPanel.add(categoryLabel, BorderLayout.NORTH);
        categorySearchPanel.add(searchField2, BorderLayout.CENTER);

        // === Filter dropdown ===
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

        // Add components to top bar
        topBar.add(nameSearchPanel);
        topBar.add(Box.createHorizontalStrut(25)); // Spacer
        topBar.add(categorySearchPanel);
        topBar.add(Box.createHorizontalStrut(25)); // Spacer
        topBar.add(filterPanel);

        return topBar;
    }

    /**
     * Table initialization: non-editable, no column reordering.
     */
    private JScrollPane initTable() {
        tableModel = new DefaultTableModel(
                new String[] { "ID", "Name", "Category", "Price", "Quantity", "Min Quantity" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevent editing cells directly
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 4 || columnIndex == 5) {
                    return Integer.class; // ID, Quantity, and Min Quantity are integers
                } else if (columnIndex == 3) {
                    return Double.class; // Price is double
                }
                return String.class;
            }
        };

        table = new JTable(tableModel);
        
        // Apply custom renderer for coloring quantity cells
        QuantityColorRenderer colorRenderer = new QuantityColorRenderer();
        table.getColumnModel().getColumn(4).setCellRenderer(colorRenderer); // Quantity column
        
        // Make table larger and more visible
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
        table.setSelectionBackground(table.getSelectionBackground());

        // Prevent column drag
        table.getTableHeader().setReorderingAllowed(false);
        
        // Center align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != 4) { // Don't apply to quantity column (already has custom renderer)
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }
        
        // Create scroll pane with table
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1100, 450)); // Make the table larger
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 0, 10, 0),
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1)
        ));
        
        return scrollPane;
    }

    /**
     * Bottom bar: contains Add, Delete, Save buttons.
     */
    private JPanel initBottomBar() {
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomBar.setBackground(new Color(245, 245, 245));
        bottomBar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Create buttons with standard style
        addBtn = createStandardButton("Add");
        deleteBtn = createStandardButton("Delete");
        Refresh = createStandardButton("Refresh");
        Update = createStandardButton("Update");
        save = createStandardButton("Save");

        // Button actions
        addBtn.addActionListener(e -> addItem(e));
        deleteBtn.addActionListener(e -> deleteSelectedItem());
        Update.addActionListener(e -> addItem(e));
        Refresh.addActionListener(e -> loadAllItems());
        save.addActionListener(e -> controller.saveItems());

        // Add buttons to bottom bar
        bottomBar.add(addBtn);
        bottomBar.add(deleteBtn);
        bottomBar.add(Refresh);
        bottomBar.add(Update);
        bottomBar.add(save);

        return bottomBar;
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
        button.setPreferredSize(new Dimension(100, 40));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Hover effect
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

    /**
     * Load all items from controller into table.
     */
    private void loadAllItems() {
        tableModel.setRowCount(0); // Clear table
        List<Item> items = controller.view_items();
        for (Item item : items) {
            tableModel.addRow(new Object[] {
                item.getId(), item.getName(), item.getCategory(),
                item.getPrice(), item.getQuantity(), item.getMinQuantity()
            });
        }
    }

    /**
     * Filter items based on dropdown selection.
     */
    private void filterItems() {
        String filter = (String) filterBox.getSelectedItem();
        List<Item> filteredItems = switch (filter) {
            case "Available" -> controller.availableItems();
            case "Low Stock" -> controller.unAvailableItems();
            case "Run Out" -> controller.runOutItems();
            default -> controller.view_items();
        };

        tableModel.setRowCount(0); // Clear table
        for (Item item : filteredItems) {
            tableModel.addRow(new Object[] {
                item.getId(), item.getName(), item.getCategory(),
                item.getPrice(), item.getQuantity(), item.getMinQuantity()
            });
        }
    }

    /**
     * Open AddItemFrame to add new item.
     */
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

    /**
     * Delete selected item from table and controller.
     */
    private void deleteSelectedItem() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1)
            return; // No selection

        int id = (int) table.getValueAt(selectedRow, 0);
        controller.delete_item(id);
        loadAllItems();
    }
}