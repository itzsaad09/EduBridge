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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

// Assuming RoundedButton is available in your main package or imported correctly
//import ui.RoundedButton;

public class CustomConfirmDialog extends JDialog {

    private boolean confirmed = false;
    
    // Custom colors matching the purple in your HomePageAdmin (151, 137, 219)
    private final Color PRIMARY_COLOR = new Color(151, 137, 219);
    private final Color BACKGROUND_COLOR = new Color(240, 240, 240); 
    private final Color TEXT_COLOR = Color.BLACK;

    public CustomConfirmDialog(java.awt.Frame parent, String title, String message) {
        super(parent, title, true); // true makes it modal

        // --- Basic Dialog Setup ---
        this.setSize(450, 200);
        this.setResizable(false);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        // --- Main Panel and Layout ---
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR); // Set the background color of the dialog content
        GridBagConstraints gbc = new GridBagConstraints();

        // --- Message Label ---
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Bodoni MT", Font.PLAIN, 16));
        messageLabel.setForeground(TEXT_COLOR);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span across two columns
        gbc.insets = new Insets(20, 10, 30, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(messageLabel, gbc);

        // --- Custom Buttons ---
        
        // Yes Button
        RoundedButton yesButton = new RoundedButton();
        yesButton.setText("Yes");
        yesButton.setBackground(PRIMARY_COLOR);
        yesButton.setFont(new Font("Bodoni MT", Font.BOLD, 14));
        yesButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        yesButton.addActionListener(e -> {
            confirmed = true;
            dispose(); // Close the dialog
        });

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1; 
        gbc.insets = new Insets(10, 10, 20, 20);
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(yesButton, gbc);
        
        // No Button
        RoundedButton noButton = new RoundedButton();
        noButton.setText("No, Cancel");
        noButton.setBackground(new Color(200, 200, 200)); // Lighter/neutral color
        noButton.setFont(new Font("Bodoni MT", Font.BOLD, 14));
        noButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        noButton.addActionListener(e -> {
            confirmed = false;
            dispose(); // Close the dialog
        });

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 20, 20, 10);
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(noButton, gbc);
        
        this.setContentPane(panel);
        this.pack(); // Adjusts window size to fit components
        this.setLocationRelativeTo(parent);
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
