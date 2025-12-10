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
import java.awt.Frame;
import javax.swing.Timer; 

// Assuming RoundedButton is available in your main package or imported correctly
// import ui.RoundedButton;

public class CustomMessageDialog extends JDialog {

    // --- Message Type Constants ---
    public static final int SUCCESS = 0;
    public static final int ERROR = 1;
    
    // --- Custom Colors ---
    private final Color PRIMARY_PURPLE = new Color(151, 137, 219); // Success color
    private final Color ERROR_RED = new Color(220, 50, 50);        // Error color
    private final Color BACKGROUND_COLOR = new Color(240, 240, 240); 
    private final Color SUCCESS_TEXT_COLOR = Color.BLACK;

    public CustomMessageDialog(Frame parent, String title, String message, int messageType) {
        super(parent, title, true); // true makes it modal

        // Determine colors based on messageType
        Color buttonColor;
        Color textColor;
        
        if (messageType == ERROR) {
            buttonColor = ERROR_RED;
            textColor = ERROR_RED; // Set the text color to red for errors
        } else {
            buttonColor = PRIMARY_PURPLE;
            textColor = SUCCESS_TEXT_COLOR;
        }

        // --- Basic Dialog Setup ---
        this.setSize(400, 180); 
        this.setResizable(false);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        // --- Main Panel and Layout ---
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR); 
        GridBagConstraints gbc = new GridBagConstraints();

        // --- Message Label ---
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Bodoni MT", Font.PLAIN, 16));
        messageLabel.setForeground(textColor); // Use determined text color
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 10, 20, 10); 
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(messageLabel, gbc);

        // --- OK Button ---
        RoundedButton okButton = new RoundedButton();
        okButton.setText("OK");
        okButton.setBackground(buttonColor); // Use determined button color
        okButton.setFont(new Font("Bodoni MT", Font.BOLD, 14));
        okButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        okButton.addActionListener(e -> {
            dispose(); 
        });

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 10, 20, 10);
        gbc.anchor = GridBagConstraints.CENTER; 
        panel.add(okButton, gbc);
        
        this.setContentPane(panel);
        this.pack(); 
        this.setLocationRelativeTo(parent);
        
        // --- Auto-Dispose Timer Logic (1000ms) ---
        Timer timer = new Timer(1500, e -> {
            ((Timer)e.getSource()).stop();
            dispose();
        });
        timer.setRepeats(false);
        timer.start();
    }
}