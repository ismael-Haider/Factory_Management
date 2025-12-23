package inventory.gui.supervisor_frame;


import javax.swing.table.DefaultTableModel;

public class TaskTableModel extends DefaultTableModel {
    public TaskTableModel() {
        super(new String[]{
                "ID", "Product Name","Quantity", "Client",
                "Start Date", "Delivery Date","Product Line Name",
                "Status", "Progress %"
        }, 0);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 8) return Integer.class; // progress
        return String.class;
    }

}
