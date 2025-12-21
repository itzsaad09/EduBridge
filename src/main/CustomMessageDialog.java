package main;

import java.awt.*;
import javax.swing.*;
import javax.swing.Timer; 

public class CustomMessageDialog extends JDialog {

    public static final int SUCCESS = 0;
    public static final int ERROR = 1;
    public static final int PERSISTENT = -1; // Type to disable timer
    
    private final Color PRIMARY_PURPLE = new Color(151, 137, 219);
    private final Color ERROR_RED = new Color(220, 50, 50);
    private final Color BACKGROUND_COLOR = new Color(240, 240, 240); 

    public CustomMessageDialog(Frame parent, String title, String message, int messageType) {
        super(parent, title, true); 

        Color buttonColor = (messageType == ERROR) ? ERROR_RED : PRIMARY_PURPLE;
        Color textColor = (messageType == ERROR) ? ERROR_RED : Color.BLACK;

        this.setResizable(false);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- Message area with Scroll Support ---
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Bodoni MT", Font.PLAIN, 16));
        messageLabel.setForeground(textColor);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Wrap label in a scroll pane for long attendance logs
        JScrollPane scrollPane = new JScrollPane(messageLabel);
        scrollPane.setPreferredSize(new Dimension(250, 150)); // Set maximum viewing area
        scrollPane.setBorder(null); // Remove border for cleaner look
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // --- OK Button ---
        RoundedButton okButton = new RoundedButton();
        okButton.setText("OK");
        okButton.setBackground(buttonColor);
        okButton.setFont(new Font("Bodoni MT", Font.BOLD, 14));
        okButton.setPreferredSize(new Dimension(100, 35));
        okButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(okButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        this.setContentPane(mainPanel);
        this.pack(); 
        this.setLocationRelativeTo(parent);
        
        // --- Timer Logic ---
        // Timer is disabled if messageType is -1
        if (messageType != PERSISTENT) {
            Timer timer = new Timer(1500, e -> {
                ((Timer)e.getSource()).stop();
                dispose();
            });
            timer.setRepeats(false);
            timer.start();
        }
    }
}