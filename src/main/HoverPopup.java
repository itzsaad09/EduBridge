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
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HoverPopup extends JWindow {

    // --- COLOR SCHEME DEFINITION ---
    // Primary Accent Color (151, 137, 219) - Used for the border
    private final Color PRIMARY_ACCENT = new Color(151, 137, 219);
    // Default Background (Light/Neutral)
    private final Color DEFAULT_BG = Color.WHITE; 
    // Hover Background (A lighter tint of the accent color for a smooth visual effect)
    private final Color HOVER_BG = new Color(220, 220, 245); 
    private final Color TEXT_COLOR = Color.BLACK;

    private JLabel messageLabel;

    public HoverPopup(Frame owner, String message) {
        super(owner);
        
        // --- UI Setup ---
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
        
        // Apply the accent color to the border
        panel.setBorder(BorderFactory.createLineBorder(PRIMARY_ACCENT, 2)); 
        
        // Message Label (Set up to handle HTML/multi-line content)
        messageLabel = new JLabel("<html>" + message + "</html>");
        messageLabel.setFont(new Font("Bodoni MT", Font.PLAIN, 14));
        messageLabel.setForeground(TEXT_COLOR);
        panel.add(messageLabel);

        // --- Mouse Hover Listener ---
        panel.setBackground(DEFAULT_BG); // Set initial color

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(HOVER_BG); // Change color on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(DEFAULT_BG); // Revert color
            }
        });
        
        this.setContentPane(panel);
        this.pack(); // Size the window based on the components
    }
    
    /**
     * Method to show the popup at a specific screen location.
     */
    public void showAtLocation(int x, int y) {
        this.setLocation(x, y);
        this.setVisible(true);
    }
    
    /**
     * Dynamically updates the message in the popup and repacks the window.
     */
    public void updateMessage(String newMessage) {
        messageLabel.setText(newMessage);
        this.pack(); // Re-pack to adjust size for the new content
    }
}
