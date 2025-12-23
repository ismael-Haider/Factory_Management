package inventory.gui.supervisor_frame;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class TaskCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {

        Component c = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

        if (column == 7 && value != null) {

            String status = value.toString();

            if (!isSelected) {
                switch (status) {
                    case "IN_QUEUE" -> {
                        c.setBackground(Color.darkGray);
                        c.setForeground(Color.WHITE);
                    }
                    case "CANCELLED" -> {
                        c.setBackground(new Color(231, 76, 60));
                        c.setForeground(Color.WHITE);
                    }
                    case "FINISHED" -> {
                        c.setBackground(new Color(46, 204, 113));
                        c.setForeground(Color.WHITE);
                    }
                    case "IN_PROGRESS"->{
                        c.setBackground(new Color(200, 220, 255)); // Light red
                        c.setForeground(new Color(41, 128, 185));
                        break;
                    }
                    default -> {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                }
            }
        }

        return c;
    }
}
