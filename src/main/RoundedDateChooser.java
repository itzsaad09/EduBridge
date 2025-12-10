/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

/**
 *
 * @author DELL
 */
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A wrapper class for JDateChooser that applies rounded corners to the 
 * internal components (JTextField and JButton) to match the custom theme.
 * FIX: Removed conflicting background painting in the custom border to ensure 
 * the date text is visible after selection.
 */
public class RoundedDateChooser extends JDateChooser {

    private final int arcSize = 15;
    private final Color defaultBorderColor = new Color(200, 200, 200);
    private final Color focusBorderColor = new Color(153, 255, 153); // Light Spring Green accent
    private final Color buttonColor = new Color(204, 153, 255); // Soft Lavender accent
    private final Color hoverButtonColor = new Color(180, 130, 240); 
    
    private JTextField dateEditorField;
    private JButton calendarButton;
    private String customDateFormatString = null; // Store custom format

    public RoundedDateChooser() {
        super();
        init();
    }
    
    // Constructor to match the JDateChooser API for setting format string
    public RoundedDateChooser(String dateFormatString) {
        super();
        this.customDateFormatString = dateFormatString;
        init();
    }

    private void init() {
        setOpaque(false);
        setBorder(new EmptyBorder(0, 0, 0, 0));
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setPreferredSize(new Dimension(getPreferredSize().width, 36));

        if (customDateFormatString != null) {
            setDateFormatString(customDateFormatString);
        }

        SwingUtilities.invokeLater(() -> {
            customizeInternalComponents(this);
            addFocusListener(new DateChooserFocusListener()); 
        });
    }
    
    /**
     * Recursively searches for the JTextField and JButton inside JDateChooser's hierarchy
     * and applies custom styling.
     */
    private void customizeInternalComponents(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof JTextField) {
                dateEditorField = (JTextField) component;
                setupDateField(dateEditorField);
            } else if (component instanceof JButton) {
                calendarButton = (JButton) component;
                setupCalendarButton(calendarButton);
            } else if (component instanceof Container) {
                customizeInternalComponents((Container) component);
            }
        }
    }
    
    /**
     * Sets up the text field component with custom drawing.
     */
    private void setupDateField(JTextField field) {
        // Use a custom painter for the text field to handle the left rounding and border
        field.setBorder(new CustomTextFieldBorder(defaultBorderColor, field));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Ensure background is explicitly white 
        field.setBackground(Color.WHITE); 
    }

    /**
     * Sets up the button component with custom drawing and hover effects.
     */
    private void setupCalendarButton(JButton button) {
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        
        // Use a custom painter for the button to handle the right rounding and coloring
        button.setUI(new CustomButtonUI(button));
    }
    
    /**
     * Custom Border for the JTextField inside JDateChooser.
     */
    private class CustomTextFieldBorder extends EmptyBorder {
        private final Color defaultColor;
        private final JTextField field;
        
        public CustomTextFieldBorder(Color color, JTextField field) {
            super(6, 10, 6, 0); // Top, Left, Bottom, Right padding
            this.defaultColor = color;
            this.field = field;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Determine if the *DateChooser wrapper* is focused
            Color borderColor = field.getParent().isFocusOwner() ? focusBorderColor : defaultColor;

            // --- FIX: COMMENTED OUT THE BACKGROUND FILL ---
            // If we draw a solid white background here, it covers the text.
            // We rely on field.setBackground(Color.WHITE) in setupDateField() instead.
            /*
            g2.setColor(Color.WHITE);
            // We draw the full rounded rect, but the button will paint over the right side
            g2.fill(new RoundRectangle2D.Double(x, y, width + 50, height, arcSize, arcSize));
            */
            // ----------------------------------------------
            
            // 2. Draw the rounded border
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(field.getParent().isFocusOwner() ? 2f : 1f));
            
            // Draw the border around the entire component area 
            g2.draw(new RoundRectangle2D.Double(0, 0, getParent().getWidth() - 1, getParent().getHeight() - 1, arcSize, arcSize));
            
            g2.dispose();
        }
    }
    
    /**
     * Custom UI for the JDateChooser's internal JButton (calendar icon button).
     */
    private class CustomButtonUI extends javax.swing.plaf.basic.BasicButtonUI {
        private final JButton button;
        private Color currentColor = buttonColor;
        
        public CustomButtonUI(JButton button) {
            this.button = button;
            
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    currentColor = hoverButtonColor;
                    button.repaint();
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    currentColor = buttonColor;
                    button.repaint();
                }
            });
        }
        
        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = c.getWidth();
            int h = c.getHeight();
            
            // Shape rounded on the right edge, straight on the left
            RoundRectangle2D fillShape = new RoundRectangle2D.Double(
                -arcSize, 0, 
                w + arcSize, h, 
                arcSize, arcSize
            );
            
            // Paint the button background
            g2.setColor(currentColor);
            g2.fill(fillShape);
            
            // Draw the default icon or text (handled by super.paint)
            super.paint(g2, c);
            g2.dispose();
        }
    }

    /**
     * Listener to force repaint on the *entire* JDateChooser when focus changes.
     */
    private class DateChooserFocusListener extends FocusAdapter {
        @Override
        public void focusGained(java.awt.event.FocusEvent evt) {
            RoundedDateChooser.this.repaint();
        }
        @Override
        public void focusLost(java.awt.event.FocusEvent evt) {
            RoundedDateChooser.this.repaint();
        }
    }
    
    /**
     * Override paint to draw the subtle drop shadow around the whole component.
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int w = getWidth();
        int h = getHeight();
        
        // Drop Shadow
        g2.setColor(new Color(0, 0, 0, 20)); 
        RoundRectangle2D shadowRect = new RoundRectangle2D.Double(1, 2, w - 3, h - 3, arcSize, arcSize);
        g2.fill(shadowRect);
        
        // This makes sure the internal components (JTextField/Button) are painted
        super.paintComponent(g2); 
        g2.dispose();
    }
}
