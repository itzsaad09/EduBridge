/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

/**
 *
 * @author DELL
 */
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class GlassmorphismPanel extends JPanel {
    
    // Define the base color and transparency for the glass effect
    private static final Color GLASS_BASE_COLOR = new Color(255, 255, 255, 100); // White, 40% opaque
    private static final Color BORDER_COLOR = new Color(255, 255, 255, 150); // White, 60% opaque
    private static final int CORNER_RADIUS = 25; // Rounded corners for the modern look

    public GlassmorphismPanel() {
        setOpaque(false);
        
        // Setup internal content layout
        setLayout(new GridBagLayout());
        
        // Add the title label
        JLabel titleLabel = new JLabel();
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(new Color(30, 30, 30));
        add(titleLabel);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        
        // 1. Enable Anti-aliasing for smooth edges
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        
        // Create the shape of the panel (a rounded rectangle)
        RoundRectangle2D roundedRect = new RoundRectangle2D.Float(
            0, 0, w - 1, h - 1, CORNER_RADIUS, CORNER_RADIUS
        );

        // 2. Draw the subtle drop shadow (simulating the raised effect)
        g2.setColor(new Color(0, 0, 0, 30)); // Black, 12% opaque
        g2.fill(new RoundRectangle2D.Float(
            2, 2, w - 1, h - 1, CORNER_RADIUS, CORNER_RADIUS
        ));
        
        // 3. Apply the semi-transparent glass color
        g2.setColor(GLASS_BASE_COLOR);
        g2.fill(roundedRect);

        // 4. Draw the "Frosted Light" Effect (a subtle gradient)
        GradientPaint frostedEffect = new GradientPaint(
            0, 0, new Color(255, 255, 255, 80), 
            w, h, new Color(255, 255, 255, 10)  
        );
        g2.setPaint(frostedEffect);
        g2.fill(roundedRect);
        
        // 5. Draw the translucent white border
        g2.setColor(BORDER_COLOR);
        g2.setStroke(new BasicStroke(2)); 
        g2.draw(roundedRect);

        g2.dispose();
    }
}
