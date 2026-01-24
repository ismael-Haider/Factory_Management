package FactoryManagement.gui.manager_frame;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.table.*;

import FactoryManagement.controllers.ManagerController;
import FactoryManagement.models.Note;
import FactoryManagement.models.ProductLine;

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
        setupShortcuts();
    }

    private void setupShortcuts() {
        InputMap im = this.getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = this.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.ALT_DOWN_MASK), "addProductLine");
        am.put("addProductLine", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBtn.doClick();
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.ALT_DOWN_MASK), "setMaintenance");
        am.put("setMaintenance", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setMaintenanceBtn.doClick();
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_DOWN_MASK), "setActive");
        am.put("setActive", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setUnMentenenceBtn.doClick();
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.ALT_DOWN_MASK), "refresh");
        am.put("refresh", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshBtn.doClick();
            }
        });
    }

    private JScrollPane initTable() {

        tableModel = new DefaultTableModel(
                new String[] { "ID", "Name", "Efficiency", "Status", "Performance", "Rating" }, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 2 || columnIndex == 5)
                    return Integer.class;
                if (columnIndex == 4)
                    return Double.class;
                return String.class;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setFont(table.getFont().deriveFont(14f));
        table.setIntercellSpacing(new Dimension(10, 6));
        table.getTableHeader().setReorderingAllowed(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(header.getFont().deriveFont(Font.BOLD, 14f));
        header.setBackground(new Color(52, 73, 94));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != 5)
                table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        table.getColumnModel().getColumn(5)
                .setCellRenderer(new RatingCellRenderer());

        table.getColumnModel().getColumn(5)
                .setCellEditor(new RatingCellEditor());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1100, 450));

        return scrollPane;
    }

    private class RatingCellRenderer extends JPanel implements TableCellRenderer {

        private JLabel[] stars = new JLabel[5];

        public RatingCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            setOpaque(true);

            for (int i = 0; i < 5; i++) {
                stars[i] = new JLabel("☆");
                stars[i].setFont(starFont);
                stars[i].setForeground(Color.LIGHT_GRAY);
                add(stars[i]);
            }
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            int rating = value instanceof Integer ? (Integer) value : 0;

            for (int i = 0; i < 5; i++) {
                if (i < rating) {
                    stars[i].setText("★");
                    stars[i].setForeground(Color.ORANGE);
                } else {
                    stars[i].setText("☆");
                    stars[i].setForeground(Color.LIGHT_GRAY);
                }
            }

            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return this;
        }
    }

    private class RatingCellEditor extends AbstractCellEditor implements TableCellEditor {

        private JPanel panel;
        private JLabel[] stars = new JLabel[5];
        private int rating;
        private int productLineId;

        public RatingCellEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            panel.setOpaque(true);

            for (int i = 0; i < 5; i++) {
                final int value = i + 1;
                JLabel star = new JLabel("☆");
                star.setFont(starFont);
                star.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                star.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        rating = value;
                        controller.setRating(rating, productLineId);
                        stopCellEditing();
                        loadAllProductLines();
                    }
                });

                stars[i] = star;
                panel.add(star);
            }
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column) {

            rating = value instanceof Integer ? (Integer) value : 0;
            productLineId = (Integer) table.getValueAt(row, 0);

            for (int i = 0; i < 5; i++) {
                if (i < rating) {
                    stars[i].setText("★");
                    stars[i].setForeground(Color.ORANGE);
                } else {
                    stars[i].setText("☆");
                    stars[i].setForeground(Color.LIGHT_GRAY);
                }
            }

            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return rating;
        }
    }

    private JPanel initBottomBar() {

        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomBar.setBackground(new Color(245, 245, 245));

        addBtn = createButton("Add Product Line");
        setMaintenanceBtn = createButton("Set Maintenance");
        setUnMentenenceBtn = createButton("Set Active");
        refreshBtn = createButton("Refresh");

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

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusable(false);
        btn.setBackground(new Color(52, 73, 94));
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(180, 40));
        return btn;
    }

    private void loadAllProductLines() {

        tableModel.setRowCount(0);

        HashMap<ProductLine, Note> ratings = controller.getAllProductLinesWithRatings();
        HashMap<ProductLine, Double> performance = controller.viewPerformanceReport();

        for (ProductLine pl : ratings.keySet()) {

            int rating = ratings.get(pl) != null ? ratings.get(pl).getRating() : 0;
            double perf = performance.get(pl);

            tableModel.addRow(new Object[] {
                    pl.getId(),
                    pl.getName(),
                    pl.getEfficiency(),
                    pl.getStatus().toString(),
                    perf,
                    rating
            });
        }
    }

    private void addProductLine() {

        String name = JOptionPane.showInputDialog(this, "Enter product line name:");

        if (name == null) {
            return;
        }

        name = name.trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid name",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
            controller.recordError("Empty name when adding Product Line");
            return;
        }

        Integer efficiency = null;

        while (efficiency == null) {
            String input = JOptionPane.showInputDialog(this, "Enter efficiency:");

            if (input == null) {
                return;
            }

            try {
                efficiency = Integer.parseInt(input);

                if (efficiency <= 0) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Efficiency must be greater than zero",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                    controller.recordError("Efficiency must be greater than zero");
                    efficiency = null;
                }

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Efficiency must be a number",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                controller.recordError("Efficiency must be number format");
            }
        }
        controller.addProductLine(name, efficiency);
        loadAllProductLines();
    }

    private void setMaintenance() {
        int row = table.getSelectedRow();
        if (row != -1)
            controller.setTheProductLineMaintenance((int) table.getValueAt(row, 0));
        loadAllProductLines();
    }

    private void setUnMentenence() {
        int row = table.getSelectedRow();
        if (row != -1)
            controller.setTheProductLineStop((int) table.getValueAt(row, 0));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        loadAllProductLines();
    }
}
