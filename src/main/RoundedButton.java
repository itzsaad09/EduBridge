/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

/**
 *
 * @author DELL
 */
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A custom JButton with rounded corners and a hover effect.
 */
public class RoundedButton extends JButton {

    private Shape shape;
    private final int arcWidth = 15;
    private final int arcHeight = 15;
    
    // Color states
    private final Color defaultColor = new Color(204, 153, 255); // Soft Lavender/Purple from background
    private final Color hoverColor = new Color(180, 130, 240); // Slightly darker for hover
    private Color currentColor = defaultColor;

    public RoundedButton(String text) {
        super(text);
        setOpaque(false); // Make the component non-opaque
        setContentAreaFilled(false); // Required for custom button painting
        setFocusPainted(false); // Remove the focus rectangle
        
        setForeground(Color.BLACK); // White text for contrast
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Add hover listener
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                currentColor = hoverColor;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                currentColor = defaultColor;
                repaint();
            }
        });
    }
    
    // Default constructor for NetBeans GUI Builder
    public RoundedButton() {
        this("");
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // --- 1. Paint the rounded background color ---
        g2.setColor(currentColor); 
        RoundRectangle2D rect = new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, arcWidth, arcHeight);
        g2.fill(rect);

        // Set color back to white for text drawing
        g2.setColor(getForeground());
        
        // Let the superclass draw the text on top
        super.paintComponent(g2); 
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        // No need for a separate border unless you want one. 
        // We'll skip this method to avoid painting the default button border.
    }

    @Override
    public boolean contains(int x, int y) {
        // Ensures the click area matches the rounded shape
        if (shape == null || !shape.getBounds().equals(getBounds())) {
            shape = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), arcWidth, arcHeight);
        }
        return shape.contains(x, y);
    }
}
