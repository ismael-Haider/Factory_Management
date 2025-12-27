package inventory.gui.manager_frame;

import inventory.controllers.ManagerController;
import inventory.models.Note;
import inventory.models.ProductLine;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.table.*;

public class ProductLinePanel extends JPanel {
    private JTable table;
    private JButton addBtn, setMaintenanceBtn, setUnMentenenceBtn, refreshBtn;
    private DefaultTableModel tableModel;
    private ManagerController controller;
    Font starFont = UIManager.getFont("Label.font").deriveFont(Font.PLAIN, 24f);

    public ProductLinePanel(ManagerController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 245));

        JPanel mainContainer = new JPanel(new BorderLayout(10, 10));
        mainContainer.setBackground(new Color(245, 245, 245));
        mainContainer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JScrollPane tableScrollPane = initTable();
        JPanel bottomBar = initBottomBar();

        mainContainer.add(tableScrollPane, BorderLayout.CENTER);
        mainContainer.add(bottomBar, BorderLayout.SOUTH);

        add(mainContainer, BorderLayout.CENTER);
        loadAllProductLines();
    }

    private JScrollPane initTable() {
        tableModel = new DefaultTableModel(
            new String[] { "ID", "Name", "Efficiency", "Status", "Performance","Rating" }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // جعل عمود الـ Rating قابل للتعديل فقط
                return column == 5;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 2 || columnIndex == 5) {
                    return Integer.class;
                } else if (columnIndex == 4) {
                    return Double.class;
                }
                return String.class;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(40); // Increased for better star visibility
        table.setFont(table.getFont().deriveFont(14f));
        table.setIntercellSpacing(new Dimension(10, 6));

        JTableHeader header = table.getTableHeader();
        header.setFont(header.getFont().deriveFont(Font.BOLD, 14f));
        header.setBackground(new Color(52, 73, 94));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));

        table.setGridColor(new Color(220, 220, 220));
        table.setShowGrid(true);
        table.setSelectionBackground(new Color(52, 152, 219));
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);
        
        // Create a center-aligned renderer
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        // Apply center alignment to all columns except Rating
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != 5) { // Don't apply to Rating column (index 5)
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }
        
        // Custom renderer AND editor for Rating column
        RatingCellRendererEditor ratingHandler = new RatingCellRendererEditor(controller);
        table.getColumnModel().getColumn(5).setCellRenderer(ratingHandler);
        table.getColumnModel().getColumn(5).setCellEditor(ratingHandler);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1100, 450));
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 0, 10, 0),
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1)
        ));

        return scrollPane;
    }

    // Class that handles both rendering AND editing for rating cells
    private class RatingCellRendererEditor extends AbstractCellEditor 
                                        implements TableCellRenderer, TableCellEditor {
        
        private ManagerController controller;
        private JPanel panel;
        private JLabel[] stars = new JLabel[5];
        private int currentRating;
        private int currentProductLineId;
        private boolean isEditing = false;
        
        public RatingCellRendererEditor(ManagerController controller) {
            this.controller = controller;
            initializeStars();
        }
        
        private void initializeStars() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            panel.setBackground(Color.WHITE);
            
            for (int i = 0; i < 5; i++) {
                final int starIndex = i + 1;
                JLabel star = new JLabel("☆");
                star.setFont(starFont);
                star.setForeground(Color.LIGHT_GRAY);
                star.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                
                star.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (isEditing) {
                            // Update the rating
                            currentRating = starIndex;
                            
                            // Call controller to save rating
                            if (controller.setRating(currentRating, currentProductLineId)) {
                                // Update star colors immediately
                                updateStarColors();
                                
                                // Stop editing
                                stopCellEditing();
                                
                                // Refresh the table to show updated rating
                                loadAllProductLines();
                            }
                        }
                    }
                    
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (isEditing) {
                            // Show hover effect - preview the rating
                            for (int j = 0; j < 5; j++) {
                                if (j < starIndex) {
                                    stars[j].setText("★");
                                    stars[j].setForeground(Color.ORANGE);
                                } else {
                                    stars[j].setText("☆");
                                    stars[j].setForeground(Color.LIGHT_GRAY);
                                }
                            }
                        }
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        if (isEditing) {
                            // Restore actual rating
                            updateStarColors();
                        }
                    }
                });
                
                stars[i] = star;
                panel.add(star);
            }
        }
        
        private void updateStarColors() {
            for (int i = 0; i < 5; i++) {
                if (i < currentRating) {
                    stars[i].setText("★");
                    stars[i].setForeground(Color.ORANGE);
                } else {
                    stars[i].setText("☆");
                    stars[i].setForeground(Color.LIGHT_GRAY);
                }
            }
        }

        // ===== TableCellRenderer Methods =====
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            
            if (value instanceof Integer) {
                currentRating = (Integer) value;
                try {
                    currentProductLineId = (Integer) table.getValueAt(row, 0);
                } catch (Exception e) {
                    currentProductLineId = -1;
                }
                updateStarColors();
            }
            
            return panel;
        }

        // ===== TableCellEditor Methods =====
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            
            isEditing = true;
            panel.setBackground(table.getSelectionBackground());
            
            if (value instanceof Integer) {
                currentRating = (Integer) value;
                try {
                    currentProductLineId = (Integer) table.getValueAt(row, 0);
                } catch (Exception e) {
                    currentProductLineId = -1;
                }
                updateStarColors();
            }
            
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return currentRating;
        }

        @Override
        public boolean stopCellEditing() {
            isEditing = false;
            fireEditingStopped();
            return true;
        }

        @Override
        public void cancelCellEditing() {
            isEditing = false;
            fireEditingCanceled();
        }
    }

    private JPanel initBottomBar() {
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomBar.setBackground(new Color(245, 245, 245));
        bottomBar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        addBtn = createStandardButton("Add Product Line");
        setMaintenanceBtn = createStandardButton("Set Maintenance");
        setUnMentenenceBtn = createStandardButton("Set Active");
        refreshBtn = createStandardButton("Refresh");

        addBtn.addActionListener(e -> addProductLine());
        setMaintenanceBtn.addActionListener(e -> setMaintenance());
        setUnMentenenceBtn.addActionListener(e -> setUnMentenence());
        refreshBtn.addActionListener(e -> loadAllProductLines());

        bottomBar.add(addBtn);
        bottomBar.add(setMaintenanceBtn);
        bottomBar.add(setUnMentenenceBtn);
        bottomBar.add(refreshBtn);

        return bottomBar;
    }

    private JButton createStandardButton(String text) {
        JButton button = new JButton(text);
        button.setFocusable(false);
        button.setBackground(new Color(52, 73, 94));
        button.setForeground(Color.WHITE);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 14f));
        button.setPreferredSize(new Dimension(150, 40));
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
        });

        return button;
    }

    private void loadAllProductLines() {
        tableModel.setRowCount(0);
        HashMap<ProductLine, Note> productLinesWithRatings = controller.getAllProductLinesWithRatings();
        HashMap<ProductLine, Double> productlinewithperformance = controller.viewPerformanceReport();
        
        for (ProductLine pl : productLinesWithRatings.keySet()) {
            double performance = productlinewithperformance.get(pl);
            Note ratingNote = productLinesWithRatings.get(pl);
            int rating = (ratingNote != null) ? ratingNote.getRating() : 0;
            String x=pl.getStatus().toString();
            if (x=="STOP" ||x=="RUNNING"){
                x="ACTIVE";
            }
            else{
                x="MENTENENCE";
            }
            tableModel.addRow(new Object[] { 
                pl.getId(), 
                pl.getName(), 
                pl.getEfficiency(), 
                x ,
                String.format("%.2f", performance), // Format performance
                rating 
            });
        }
    }

    private void addProductLine() {
        String name = JOptionPane.showInputDialog("Enter product line name:");
        if (name == null || name.trim().isEmpty()) return;
        
        String efficiencyStr = JOptionPane.showInputDialog("Enter efficiency:");
        if (efficiencyStr == null) return;
        
        try {
            int efficiency = Integer.parseInt(efficiencyStr);
            if (controller.addProductLine(name, efficiency)) {
                loadAllProductLines();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add product line.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for efficiency.");
        }
    }

    private void setMaintenance() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;
        int id = (int) table.getValueAt(selectedRow, 0);
        controller.setTheProductLineMaintenance(id);
        loadAllProductLines();
    }

    private void setUnMentenence() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;
        int id = (int) table.getValueAt(selectedRow, 0);
        controller.setTheProductLineStop(id);
        loadAllProductLines();
    }
}