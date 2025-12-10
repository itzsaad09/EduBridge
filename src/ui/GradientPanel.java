package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

/**
 * A custom JPanel that draws a soft, blurred gradient background
 * and a subtle rounded border, with an optional background image.
 */
public class GradientPanel extends JPanel {

    private Image backgroundImage; // Field to hold the background image

    public GradientPanel() {
        // Use FlowLayout as a simple manager, but you can change this
        super(new BorderLayout()); 
        
        // --- Load the Image ---
        try {
            // !!! IMPORTANT: Replace "your_background_image.jpg" with your actual file name
            // and ensure the path is correct for your project structure.
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/vadim-sherbakov-d6ebY-faOO0-unsplash.jpg"));
            backgroundImage = icon.getImage();
        } catch (Exception e) {
            // Handle error if image fails to load (e.g., file not found)
            System.err.println("Error loading background image: " + e.getMessage());
        }
        
        // Ensure the base background is set
        setBackground(new Color(245, 250, 255)); 
        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Paint the default background color first
        super.paintComponent(g); 
        
        Graphics2D g2d = (Graphics2D) g.create();

        // 1. Enable high-quality rendering (anti-aliasing)
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        int w = getWidth();
        int h = getHeight();

        // --- 2. Draw the Background Image ---
        if (backgroundImage != null) {
            // Draw the image scaled to fit the entire panel (0, 0, width, height)
            g2d.drawImage(backgroundImage, 0, 0, w, h, this);
        }

        // Define colors for the soft gradients
        Color lightGreen = new Color(153, 255, 153, 200); 
        Color lightPurple = new Color(204, 153, 255, 200); 
        Color transparentWhite = new Color(255, 255, 255, 0); 

        // --- 3. Draw the Soft Gradient Spots (Overlaying the image) ---

        // Spot 1: Light Green 
        Point2D center1 = new Point2D.Float(w * 0.25f, h * 0.35f);
        float radius1 = Math.min(w, h) * 0.6f; 
        float[] dist1 = {0.0f, 1.0f}; 
        Color[] colors1 = {lightGreen, transparentWhite}; 
        RadialGradientPaint rgp1 = new RadialGradientPaint(center1, radius1, dist1, colors1);
        g2d.setPaint(rgp1);
        g2d.fillOval((int) (center1.getX() - radius1), (int) (center1.getY() - radius1), (int) (radius1 * 2), (int) (radius1 * 2));

        // Spot 2: Light Purple 
        Point2D center2 = new Point2D.Float(w * 0.75f, h * 0.6f);
        float radius2 = Math.min(w, h) * 0.5f; 
        float[] dist2 = {0.0f, 1.0f};
        Color[] colors2 = {lightPurple, transparentWhite}; 
        RadialGradientPaint rgp2 = new RadialGradientPaint(center2, radius2, dist2, colors2);
        g2d.setPaint(rgp2);
        g2d.fillOval((int) (center2.getX() - radius2), (int) (center2.getY() - radius2), (int) (radius2 * 2), (int) (radius2 * 2));

        // --- 4. Draw the Subtle Rounded Border ---
        RoundRectangle2D.Double roundedRect = new RoundRectangle2D.Double(
            5, 5, 
            w - 10, h - 10, 
            25, 25 
        );

        g2d.setColor(new Color(200, 200, 200, 150)); 
        g2d.setStroke(new BasicStroke(2)); 
        g2d.draw(roundedRect);
        
        // Dispose of the Graphics2D context
        g2d.dispose();
    }
}