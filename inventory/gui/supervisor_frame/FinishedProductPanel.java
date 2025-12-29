package inventory.gui.supervisor_frame;

import inventory.controllers.ProLineManageController;
import inventory.models.FinishedProduct;
import inventory.models.Product;
import inventory.models.ProductLine;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

public class FinishedProductPanel extends JPanel {
    private JTable table;          
    private JComboBox<String> productLineFilter; 
    private JSpinner fromDate, toDate;    
    private JLabel mostRequestedLabel;    
    private JButton mostRequestedBtn;  
    
    // === Custom renderer for coloring quantity cells ===
    private class QuantityColorRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column
            );
            
            // Only color the "Quantity" column (index 2)
            if (column == 2) {
                try {
                    int quantity = (int) table.getValueAt(row, column);
                    
                    // Set colors based on quantity
                    if (quantity < 10) {
                        c.setBackground(new Color(255, 200, 200)); // Light red for low quantity
                        c.setForeground(Color.RED.darker());
                    } else if (quantity < 50) {
                        c.setBackground(new Color(255, 255, 200)); // Light yellow for medium
                        c.setForeground(new Color(150, 150, 0));
                    } else {
                        c.setBackground(new Color(200, 255, 200)); // Light green for high
                        c.setForeground(Color.GREEN.darker());
                    }
                    
                    if (c instanceof JComponent) {
                        ((JComponent) c).setFont(c.getFont().deriveFont(Font.BOLD));
                    }
                    
                    // Ensure selection color still visible
                    if (isSelected) {
                        c.setBackground(c.getBackground().darker());
                    }
                } catch (Exception e) {
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
            
            setHorizontalAlignment(JLabel.CENTER);
            
            return c;
        }
    }

    private ProLineManageController controller;
    private FinishedTableModel tableModel;


    //Constructor: initializes the panel with controller reference.
    public FinishedProductPanel(ProLineManageController controller) {
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
        JPanel bottomPanel = initBottomPanel();

        // Add components to main container
        mainContainer.add(topBar, BorderLayout.NORTH);
        mainContainer.add(tableScrollPane, BorderLayout.CENTER);
        mainContainer.add(bottomPanel, BorderLayout.SOUTH);

        // Add main container to this panel
        add(mainContainer, BorderLayout.CENTER);

        // Load all finished products at startup
        loadAllData();
        setupShortcuts();
    }


    private void setupShortcuts() {
        InputMap im = this.getInputMap(this.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = this.getActionMap();
        // Ctrl + A → Add
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK), "display");
        am.put("display", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
             mostRequestedBtn.doClick();
            }
        });
    }


    /**
     * Top bar: contains filters and date range selectors.
     */
    private JPanel initTopBar() {
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topBar.setBackground(new Color(245, 245, 245));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // === Product Line Filter ===
        JPanel lineFilterPanel = new JPanel(new BorderLayout(0, 5));
        JLabel lineLabel = new JLabel("Product Line:");
        lineLabel.setFont(lineLabel.getFont().deriveFont(Font.BOLD, 13f));
        lineLabel.setForeground(new Color(60, 60, 60));
        
        productLineFilter = new JComboBox<>();
        productLineFilter.addItem("All Lines");
        controller.viewAllProductLines()
                .forEach(pl -> productLineFilter.addItem(pl.getName()));
        productLineFilter.setPreferredSize(new Dimension(150, 35));
        productLineFilter.addActionListener(e -> applyFilter());
        
        lineFilterPanel.add(lineLabel, BorderLayout.NORTH);
        lineFilterPanel.add(productLineFilter, BorderLayout.CENTER);

        // === From Date Filter ===
        JPanel fromDatePanel = new JPanel(new BorderLayout(0, 5));
        JLabel fromLabel = new JLabel("From:");
        fromLabel.setFont(fromLabel.getFont().deriveFont(Font.BOLD, 13f));
        fromLabel.setForeground(new Color(60, 60, 60));
        
        fromDate = new JSpinner(new SpinnerDateModel());
        fromDate.setPreferredSize(new Dimension(120, 35));
        JSpinner.DateEditor fromEditor = new JSpinner.DateEditor(fromDate, "yyyy-MM-dd");
        fromDate.setEditor(fromEditor);
        
        fromDatePanel.add(fromLabel, BorderLayout.NORTH);
        fromDatePanel.add(fromDate, BorderLayout.CENTER);

        // === To Date Filter ===
        JPanel toDatePanel = new JPanel(new BorderLayout(0, 5));
        JLabel toLabel = new JLabel("To:");
        toLabel.setFont(toLabel.getFont().deriveFont(Font.BOLD, 13f));
        toLabel.setForeground(new Color(60, 60, 60));
        
        toDate = new JSpinner(new SpinnerDateModel());
        toDate.setPreferredSize(new Dimension(120, 35));
        JSpinner.DateEditor toEditor = new JSpinner.DateEditor(toDate, "yyyy-MM-dd");
        toDate.setEditor(toEditor);
        
        toDatePanel.add(toLabel, BorderLayout.NORTH);
        toDatePanel.add(toDate, BorderLayout.CENTER);

        // === Most Requested Button ===
        mostRequestedBtn = createStandardButton("Most Requested Product");
        mostRequestedBtn.addActionListener(e -> showMostRequestedProduct());

        // Add components to top bar
        topBar.add(lineFilterPanel);
        topBar.add(Box.createHorizontalStrut(15)); // Spacer
        topBar.add(fromDatePanel);
        topBar.add(Box.createHorizontalStrut(15)); // Spacer
        topBar.add(toDatePanel);
        topBar.add(Box.createHorizontalStrut(25)); // Spacer
        topBar.add(mostRequestedBtn);

        return topBar;
    }

    /**
     * Table initialization.
     */
    private JScrollPane initTable() {
        tableModel = new FinishedTableModel();
        table = new JTable(tableModel);
        
        // Apply custom renderer for coloring quantity cells
        QuantityColorRenderer colorRenderer = new QuantityColorRenderer();
        table.getColumnModel().getColumn(2).setCellRenderer(colorRenderer); // Quantity column
        
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
        table.setSelectionBackground(new Color(52, 152, 219));
        table.setSelectionForeground(Color.WHITE);
        
        // Prevent column drag
        table.getTableHeader().setReorderingAllowed(false);
        
        // Center align all columns except quantity (which has custom renderer)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != 2) { // Don't apply to quantity column (already has custom renderer)
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }
        
        // Create scroll pane with table
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(900, 450));
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 20, 10, 20),
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1)
        ));
        
        return scrollPane;
    }

    /**
     * Bottom panel: shows most requested product information.
     */
    private JPanel initBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        bottomPanel.setBackground(new Color(240, 248, 255));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        mostRequestedLabel = new JLabel("Most Requested Product: -");
        mostRequestedLabel.setFont(new Font("Arial", Font.BOLD, 14));
        mostRequestedLabel.setForeground(new Color(52, 73, 94));
        
        bottomPanel.add(mostRequestedLabel);
        
        return bottomPanel;
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
        button.setPreferredSize(new Dimension(200, 40));
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
     * Load all finished products from controller into table.
     */
    private void loadAllData() {
        tableModel.clear();
        HashMap<ProductLine, List<FinishedProduct>> map =
                controller.viewFinishedProductsByAllProductLine();

        for (var entry : map.entrySet()) {
            ProductLine line = entry.getKey();
            for (FinishedProduct fp : entry.getValue()) {
                tableModel.addRow(
                        fp.getProductId(),
                        fp.getName(),
                        fp.getQuantity(),
                        line.getName()
                );
            }
        }
    }

    /**
     * Apply filters based on dropdown selection.
     */
    private void applyFilter() {
        tableModel.clear();
        String selected = (String) productLineFilter.getSelectedItem();

        HashMap<ProductLine, List<FinishedProduct>> map =
                controller.viewFinishedProductsByAllProductLine();

        for (var entry : map.entrySet()) {
            ProductLine line = entry.getKey();

            if (!"All Lines".equals(selected) &&
                !line.getName().equals(selected)) continue;

            for (FinishedProduct fp : entry.getValue()) {
                tableModel.addRow(
                        fp.getProductId(),
                        fp.getName(),
                        fp.getQuantity(),
                        line.getName()
                );
            }
        }
    }

    /**
     * Show most requested product based on date range.
     */
    private void showMostRequestedProduct() {
        LocalDate from = LocalDate.ofInstant(
                ((java.util.Date) fromDate.getValue()).toInstant(),
                java.time.ZoneId.systemDefault()
        );
        LocalDate to = LocalDate.ofInstant(
                ((java.util.Date) toDate.getValue()).toInstant(),
                java.time.ZoneId.systemDefault()
        );

        Product p = controller.viewTheMostRequestedProduct(from, to);
        mostRequestedLabel.setText(
                "Most Requested Product: " + (p != null ? p.getName() : "no product in this period")
        );
        
        // Highlight the button after click
        mostRequestedBtn.setBackground(new Color(41, 128, 185));
        Timer timer = new Timer(500, e -> {
            mostRequestedBtn.setBackground(new Color(52, 73, 94));
        });
        timer.setRepeats(false);
        timer.start();
    }

    /* ================= TABLE MODEL ================= */

    private static class FinishedTableModel extends DefaultTableModel {
        private final String[] COLUMNS = {
            "Product ID", "Product Name", "Quantity", "Product Line"
        };

        FinishedTableModel() {
            super();
        }

        @Override
        public int getColumnCount() {
            return COLUMNS.length;
        }
        
        @Override
        public String getColumnName(int column) {
            return COLUMNS[column];
        }

        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
        
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0 || columnIndex == 2) {
                return Integer.class;
            }
            return String.class;
        }

        void clear() {
            setRowCount(0);
        }

        void addRow(int id, String name, int qty, String line) {
            super.addRow(new Object[]{id, name, qty, line});
        }
    }
}