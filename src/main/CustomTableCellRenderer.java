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
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;

/**
 * Custom renderer for JTable cells to apply padding, font, and row striping.
 */
public class CustomTableCellRenderer extends DefaultTableCellRenderer {

    // Theme Colors
    private final Color primaryColor = new Color(204, 153, 255); // Soft Lavender/Purple (from RoundedButton)
    private final Color secondaryColor = new Color(245, 245, 245); // Light Gray for alternating rows
    private final Color selectionColor = new Color(153, 255, 153, 150); // Semi-transparent green for selection
    private final Color defaultTextColor = Color.BLACK;

    // Padding for cells
    private final EmptyBorder cellPadding = new EmptyBorder(10, 8, 10, 8); 

    public CustomTableCellRenderer() {
        // Center the text by default, but this can be overridden.
        setHorizontalAlignment(JLabel.CENTER); 
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        // Set the default JLabel properties
        super.getTableCellRendererComponent(table, value, isSelected, false, row, column);

        // Apply Padding and Font
        setBorder(cellPadding);
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setForeground(defaultTextColor);

        // --- Color Logic ---
        if (isSelected) {
            // Use a theme-matching selection color
            setBackground(selectionColor);
        } else {
            // Row Striping: Alternating colors for non-selected rows
            if (row % 2 == 0) {
                setBackground(Color.WHITE); // Even rows
            } else {
                setBackground(secondaryColor); // Odd rows (light gray)
            }
        }

        return this;
    }
}
