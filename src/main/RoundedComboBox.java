/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

/**
 *
 * @author DELL
 */
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.basic.ComboPopup;

/**
 * A highly customized JComboBox with rounded corners, a subtle drop shadow, and 
 * dynamic border styling.It addresses the issues of left-side rounding and scrollbar display.
 * @param <E>
 */
public class RoundedComboBox<E> extends JComboBox<E> {

    private final int arcWidth = 15;
    private final int arcHeight = 15;
    private final int editorPadding = 1;
    
    // Custom colors for styling
    private final Color defaultBorderColor = new Color(200, 200, 200);
    private final Color focusBorderColor = new Color(153, 255, 153); // Light Spring Green accent
    private final Color shadowColor = new Color(0, 0, 0, 20); // Semi-transparent black for shadow
    private final Color buttonColor = new Color(204, 153, 255); // Soft Lavender accent
    
    private boolean isFocused = false;

    public RoundedComboBox(E[] items) {
        super(items);
        init();
    }
    
    public RoundedComboBox() {
        super();
        init();
    }
    
    private void init() {
        setOpaque(false); 
        setUI(new RoundedComboBoxUI()); 
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setPreferredSize(new Dimension(getPreferredSize().width, 36));
        
        // Ensure that the list can't scroll by keeping the item count limited 
        // in combination with the custom popup creation.
        setMaximumRowCount(6); 
        
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                isFocused = true;
                repaint();
            }
            @Override
            public void focusLost(FocusEvent evt) {
                isFocused = false;
                repaint();
            }
        });
        
        setRenderer(new CustomListCellRenderer());
    }
    
    /**
     * Custom painting of the rounded background, drop shadow, and border.
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        int w = getWidth();
        int h = getHeight();
        
        // 0. Draw Shadow
        g2.setColor(shadowColor);
        RoundRectangle2D shadowRect = new RoundRectangle2D.Double(1, 2, w - 3, h - 3, arcWidth, arcHeight);
        g2.fill(shadowRect);
        
        RoundRectangle2D rect = new RoundRectangle2D.Double(0, 0, w - 1, h - 1, arcWidth, arcHeight);

        // 1. Paint the full rounded background (White)
        g2.setColor(Color.WHITE);
        g2.fill(rect);

        // 2. Paint the rounded border (Dynamic Color and Thickness)
        g2.setColor(isFocused ? focusBorderColor : defaultBorderColor);
        g2.setStroke(new BasicStroke(isFocused ? 2f : 1f)); 
        g2.draw(rect);

        // The default UI paintComponent will draw the editor and button on top.
        super.paintComponent(g2); 
        g2.dispose();
    }
    
    /**
     * Renders items in the popup list, using theme colors for selection.
     */
    private class CustomListCellRenderer extends DefaultListCellRenderer {
        private final Color selectionColor = new Color(153, 255, 153, 100); // Semi-transparent green
        private final Color selectionTextColor = Color.BLACK;
        private final EmptyBorder padding = new EmptyBorder(5, editorPadding, 5, editorPadding);
        
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            setBorder(padding);
            setFont(getFont().deriveFont(Font.PLAIN, 14));
            
            if (isSelected) {
                setBackground(selectionColor);
                setForeground(selectionTextColor);
            } else {
                setBackground(Color.WHITE); 
                setForeground(Color.BLACK);
            }
            return this;
        }
    }


    /**
     * Custom UI delegate to handle the appearance and layout of internal components.
     */
    private class RoundedComboBoxUI extends BasicComboBoxUI {

        private JButton arrowButton;
        private final Color hoverButtonColor = new Color(180, 130, 240); 
        private Color currentButtonColor = buttonColor;
        
        @Override
        public void installUI(JComponent c) {
            super.installUI(c);
            // Ensure the internal editor component has no default L&F border
            if (editor instanceof JComponent) {
                ((JComponent) editor).setBorder(null);
            }
        }
        
        /**
         * FIX: Calculates the editor bounds manually to ensure correct padding and 
         * to avoid the arrow button area.
         */
        protected Rectangle getEditorBounds() {
            int width = comboBox.getWidth();
            int height = comboBox.getHeight();
            
            // Get the button area bounds
            Rectangle buttonRect = arrowButton.getBounds();
            
            // Calculate the editor area:
            // x: Start after the left border/padding (editorPadding)
            int x = editorPadding; 
            // y: Start from the top border/padding (usually 1 or 2px offset)
            int y = 1; 
            
            // width: Span from the start x to the start of the arrow button
            int w = buttonRect.x - x;
            
            // height: Span almost the full height, minus top/bottom border/padding
            int h = height - 2; 
            
            return new Rectangle(x, y, w, h);
        }


        @Override
        protected JButton createArrowButton() {
            arrowButton = new JButton() {
                private Shape buttonShape;
                
                {
                    setOpaque(false);
                    setContentAreaFilled(false);
                    setBorderPainted(false);
                    setFocusPainted(false);
                    
                    // Mouse listeners for button hover effect
                    addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseEntered(MouseEvent e) {
                            currentButtonColor = hoverButtonColor;
                            repaint();
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            currentButtonColor = buttonColor;
                            repaint();
                        }
                    });
                }
                
                @Override
                public void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    int w = getWidth();
                    int h = getHeight();
                    
                    // Shape rounded on the right edge, straight on the left
                    RoundRectangle2D fillShape = new RoundRectangle2D.Double(
                        -arcWidth, 0, 
                        w + arcWidth, h, 
                        arcWidth, arcHeight
                    );
                    
                    // Clip the graphics context to the actual button area
                    g2.clip(new Rectangle(0, 0, w, h));
                    
                    // Paint the button background
                    g2.setColor(currentButtonColor);
                    g2.fill(fillShape);
                    
                    // Draw the down arrow icon (smaller)
                    g2.setClip(null); 
                    g2.setColor(Color.WHITE);
                    
                    int iconSize = h / 4; 
                    int x = w / 2 - iconSize / 2;
                    int y = h / 2 - (iconSize / 4); 
                    
                    int[] xPoints = {x, x + iconSize, x + iconSize / 2};
                    int[] yPoints = {y, y, y + iconSize / 2}; 
                    
                    g2.fillPolygon(xPoints, yPoints, 3);
                    
                    g2.dispose();
                }
            };
            return arrowButton;
        }

        /**
         * Overrides the editor component to ensure it is transparent and has padding for centering.
         */
        @Override
        protected ComboBoxEditor createEditor() {
            BasicComboBoxEditor editor = (BasicComboBoxEditor) super.createEditor();
            Component editorComponent = editor.getEditorComponent();
            
            if (editorComponent instanceof JTextField) {
                JTextField textField = (JTextField) editorComponent;
                
                // Set the default L&F border to null, which is handled in installUI now.
                // Use EmptyBorder only for vertical padding (horizontal is handled by getEditorBounds)
                textField.setBorder(new EmptyBorder(6, 0, 6, 0)); 
                
                textField.setOpaque(false); 
                textField.setForeground(Color.BLACK); 
            }
            return editor;
        }

        /**
         * Overrides the popup to prevent the scrollbar from being initialized.
         */
        @Override
        protected ComboPopup createPopup() {
            return new BasicComboPopup(comboBox) {
                @Override
                protected JScrollPane createScroller() {
                    // FIX #1: Return a JScrollPane that only holds the list (JList is set by the superclass)
                    // The trick is to prevent the scroll pane from being installed on the list in the first place.
                    return new JScrollPane(list) {
                        // Prevent the list from being wrapped in a viewport that forces scrolling
                        {
                            setBorder(null); // Remove scrollpane border
                            setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
                            setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                            list.setVisibleRowCount(comboBox.getMaximumRowCount()); // Use max rows
                        }
                    };
                }
                
                @Override
                public void show() {
                    // Remove the default popup border
                    setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); 
                    
                    // Set the list content itself to have clean white background and border
                    list.setBackground(Color.WHITE);
                    list.setBorder(BorderFactory.createLineBorder(defaultBorderColor, 1));
                    
                    // Ensure minimum width of the list matches the combobox
                    list.setPrototypeCellValue(null); 
                    list.setPreferredSize(new Dimension(comboBox.getSize().width, list.getPreferredSize().height));
                    
                    super.show();
                }
            };
        }
    }
}