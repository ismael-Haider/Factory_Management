package FactoryManagement.gui.supervisor_frame;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

import FactoryManagement.controllers.TasksController;
import FactoryManagement.models.FinishedProduct;
import FactoryManagement.models.Product;
import FactoryManagement.models.ProductLine;

public class FinishedProductPanel extends JPanel {
    private JTable table;
    private JComboBox<String> productLineFilter;
    private JSpinner fromDate, toDate;
    private JLabel mostRequestedLabel;
    private JButton mostRequestedBtn, refreshBtn;

    private class QuantityColorRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            if (column == 2) {
                try {
                    int quantity = (int) table.getValueAt(row, column);

                    if (quantity < 10) {
                        c.setBackground(new Color(255, 200, 200));
                        c.setForeground(Color.RED.darker());
                    } else if (quantity < 50) {
                        c.setBackground(new Color(255, 255, 200));
                        c.setForeground(new Color(150, 150, 0));
                    } else {
                        c.setBackground(new Color(200, 255, 200));
                        c.setForeground(Color.GREEN.darker());
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

    private TasksController controller;
    private FinishedTableModel tableModel;

    public FinishedProductPanel(TasksController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 245));

        JPanel mainContainer = new JPanel(new BorderLayout(10, 10));
        mainContainer.setBackground(new Color(245, 245, 245));
        mainContainer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel topBar = initTopBar();
        JScrollPane tableScrollPane = initTable();
        JPanel bottomPanel = initBottomPanel();

        mainContainer.add(topBar, BorderLayout.NORTH);
        mainContainer.add(tableScrollPane, BorderLayout.CENTER);
        mainContainer.add(bottomPanel, BorderLayout.SOUTH);

        add(mainContainer, BorderLayout.CENTER);

        loadAllData();
        setupShortcuts();
    }

    private void setupShortcuts() {
        InputMap im = this.getInputMap(FinishedProductPanel.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = this.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.ALT_DOWN_MASK), "display");
        am.put("display", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostRequestedBtn.doClick();
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

    private JPanel initTopBar() {
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topBar.setBackground(new Color(245, 245, 245));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

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

        mostRequestedBtn = createStandardButton("Most Requested Product");
        mostRequestedBtn.setPreferredSize(new Dimension(250, 40));
        mostRequestedBtn.addActionListener(e -> showMostRequestedProduct());

        refreshBtn = createStandardButton("Refresh");
        refreshBtn.addActionListener(e -> refreshData());
        topBar.add(lineFilterPanel);
        topBar.add(Box.createHorizontalStrut(15));
        topBar.add(fromDatePanel);
        topBar.add(Box.createHorizontalStrut(15));
        topBar.add(toDatePanel);
        topBar.add(Box.createHorizontalStrut(25));
        topBar.add(mostRequestedBtn);
        topBar.add(refreshBtn);

        return topBar;
    }

    private JScrollPane initTable() {
        tableModel = new FinishedTableModel();
        table = new JTable(tableModel);

        QuantityColorRenderer colorRenderer = new QuantityColorRenderer();
        table.getColumnModel().getColumn(2).setCellRenderer(colorRenderer);

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
            if (i != 2) {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(900, 450));
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 20, 10, 20),
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1)));

        return scrollPane;
    }

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

    private JButton createStandardButton(String text) {
        JButton button = new JButton(text);
        button.setFocusable(false);
        button.setBackground(new Color(52, 73, 94));
        button.setForeground(Color.WHITE);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 14f));
        button.setPreferredSize(new Dimension(200, 40));
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

    private void loadAllData() {
        tableModel.clear();
        HashMap<ProductLine, List<FinishedProduct>> map = controller.viewFinishedProductsByAllProductLine();

        for (var entry : map.entrySet()) {
            ProductLine line = entry.getKey();
            for (FinishedProduct fp : entry.getValue()) {
                tableModel.addRow(
                        fp.getProductId(),
                        fp.getName(),
                        fp.getQuantity(),
                        line.getName());
            }
        }
    }

    private void applyFilter() {
        tableModel.clear();
        String selected = (String) productLineFilter.getSelectedItem();

        HashMap<ProductLine, List<FinishedProduct>> map = controller.viewFinishedProductsByAllProductLine();

        for (var entry : map.entrySet()) {
            ProductLine line = entry.getKey();

            if (!"All Lines".equals(selected) &&
                    !line.getName().equals(selected))
                continue;

            for (FinishedProduct fp : entry.getValue()) {
                tableModel.addRow(
                        fp.getProductId(),
                        fp.getName(),
                        fp.getQuantity(),
                        line.getName());
            }
        }
    }

    private void showMostRequestedProduct() {
        LocalDate from = LocalDate.ofInstant(
                ((java.util.Date) fromDate.getValue()).toInstant(),
                java.time.ZoneId.systemDefault());
        LocalDate to = LocalDate.ofInstant(
                ((java.util.Date) toDate.getValue()).toInstant(),
                java.time.ZoneId.systemDefault());

        Product p = controller.viewTheMostRequestedProduct(from, to);
        mostRequestedLabel.setText(
                "Most Requested Product: " + (p != null ? p.getName() : "no product in this period"));
    }

    private void refreshData() {
        productLineFilter.setSelectedIndex(0);

        fromDate.setValue(new java.util.Date());
        toDate.setValue(new java.util.Date());

        mostRequestedLabel.setText("Most Requested Product: -");

        loadAllData();
    }

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
            super.addRow(new Object[] { id, name, qty, line });
        }
    }
}