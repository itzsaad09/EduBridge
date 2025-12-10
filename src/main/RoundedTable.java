/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

/**
 *
 * @author DELL
 */
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 * Custom JTable that applies the theme's aesthetic, including custom cell rendering
 * and header styling.
 */
public class RoundedTable extends JTable {

    // Theme Colors
    private final Color headerBackground = new Color(204, 153, 255); // Soft Lavender
    private final Color headerForeground = Color.BLACK;
    private final Font headerFont = new Font("Segoe UI", Font.BOLD, 14);

    public RoundedTable() {
        super();
        setupTableProperties();
        customizeHeader();
    }
    
    // Constructor with a TableModel
    public RoundedTable(javax.swing.table.TableModel model) {
        super(model);
        setupTableProperties();
        customizeHeader();
    }

    private void setupTableProperties() {
        // Remove standard grid lines and use row striping from the renderer
        setShowGrid(false); 
        setIntercellSpacing(new java.awt.Dimension(0, 0)); 
        
        // General look and feel
        setRowHeight(35); // Increase row height for better padding/spacing
        setFillsViewportHeight(true); // Fills the entire viewport height
        setBackground(Color.WHITE); 
        setBorder(null); // Remove default table border

        // Set the custom cell renderer for all columns
        setDefaultRenderer(Object.class, new CustomTableCellRenderer());
    }

    private void customizeHeader() {
        JTableHeader header = getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setBackground(headerBackground);
        header.setForeground(headerForeground);
        header.setFont(headerFont);
        
        // Use a blank renderer to apply the background/foreground without borders
        header.setDefaultRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel headerLabel = new JLabel(value.toString());
                headerLabel.setOpaque(true);
                headerLabel.setBackground(headerBackground);
                headerLabel.setForeground(headerForeground);
                headerLabel.setFont(headerFont);
                headerLabel.setBorder(new javax.swing.border.EmptyBorder(10, 8, 10, 8)); // Padding
                headerLabel.setHorizontalAlignment(JLabel.CENTER);
                return headerLabel;
            }
        });
    }

    /**
     * Optional: Method to auto-fit column widths based on content.
     */
    public void autoSizeColumns() {
        final TableColumnModel columnModel = getColumnModel();
        for (int column = 0; column < getColumnCount(); column++) {
            int width = 15; // Minimum width
            for (int row = 0; row < getRowCount(); row++) {
                TableCellRenderer renderer = getCellRenderer(row, column);
                Component comp = prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width + 1, width);
            }
            // Add extra width for padding/margin
            width += 20; 
            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }
}
