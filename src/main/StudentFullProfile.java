/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

/**
 *
 * @author DELL
 */
import java.awt.*;
import java.sql.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class StudentFullProfile extends javax.swing.JFrame {
    private String studentId;
    private Connection connect;
    private JPanel mainContent;

    public StudentFullProfile(String id) {
        this.studentId = id;
        this.connect = Database.getConnection();
        // Add Logo in Title Bar
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/edubridgeicon.png"));
            Image image = icon.getImage();
            this.setIconImage(image);
        } catch (Exception e) {
            System.err.println("Error loading icon: " + e.getMessage());
        }
        setupUI();
        loadAllData();
    }

    private void setupUI() {
        setTitle("Comprehensive Student Profile - ID: " + studentId);
        setSize(923, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Main content panel with vertical BoxLayout
        mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(Color.WHITE);
        mainContent.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Adding ScrollPane to allow vertical movement
        JScrollPane mainScroll = new JScrollPane(mainContent);
        mainScroll.getVerticalScrollBar().setUnitIncrement(18);
        mainScroll.setBorder(null);
        add(mainScroll);
    }

    private void loadAllData() {
        // 1. Basic Information Section (Top)
        addSectionHeader("STUDENT PERSONAL INFORMATION");
        loadBasicInfo();

        // 2. Enrollment Section (Based on enrollment table)
        addTableSection("ACADEMIC ENROLLMENTS", 
            "SELECT coursecode, session, year, enrollment_date FROM enrollment WHERE student_id = ?", 
            new String[]{"Course Code", "Session", "Year", "Enrolled Date"});


        // 3. Fee Section (Based on fee table)
        addTableSection("FINANCIAL STATUS", 
            "SELECT session, year, total_fee, status FROM fee WHERE student_id = ?", 
            new String[]{"Session", "Year", "Total Fee", "Payment Status"});

        // 4. Results Section (Based on results table)
        addTableSection("EXAMINATION RESULTS", 
            "SELECT course_code, sessional, final, total, IF(is_published=1, 'Published', 'Pending') as Status FROM results WHERE student_id = ?", 
            new String[]{"Course", "Sessional", "Final", "Total", "Status"});

        // 5. Attendance Section (Based on attendance_json table)
        addTableSection("ATTENDANCE RECORDS", 
            "SELECT coursecode, log FROM attendance_json WHERE student_id = ?", 
            new String[]{"Course Code", "Attendance Log (JSON)"});
    }

    private void loadBasicInfo() {
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 15, 12));
        infoPanel.setBackground(new Color(250, 250, 250));
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Querying student table for personal details
        String query = "SELECT fName, lName, dob, cnic, phoneno, gender, department, program, email, status FROM student WHERE ID = ?";
        
        try (PreparedStatement pst = connect.prepareStatement(query)) {
            pst.setString(1, studentId);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                addInfoRow(infoPanel, "Full Name:", rs.getString("fName") + " " + rs.getString("lName"));
                addInfoRow(infoPanel, "Date of Birth:", rs.getString("dob"));
                addInfoRow(infoPanel, "CNIC:", rs.getString("cnic"));
                addInfoRow(infoPanel, "Email:", rs.getString("email"));
                addInfoRow(infoPanel, "Phone Number:", rs.getString("phoneno"));
                addInfoRow(infoPanel, "Department:", rs.getString("department"));
                addInfoRow(infoPanel, "Program:", rs.getString("program"));
                addInfoRow(infoPanel, "Account Status:", rs.getString("status"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching basic info: " + e.getMessage());
        }

        mainContent.add(infoPanel);
        mainContent.add(Box.createRigidArea(new Dimension(0, 30)));
    }

    private void addTableSection(String title, String query, String[] columnNames) {
        addSectionHeader(title);

        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.setEnabled(false);

        try (PreparedStatement pst = connect.prepareStatement(query)) {
            pst.setString(1, studentId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                for (int i = 1; i <= columnNames.length; i++) {
                    row.add(rs.getObject(i));
                }
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JScrollPane pane = new JScrollPane(table);
        // Set height based on data or cap it at 180px
        int height = Math.min(180, (model.getRowCount() * 30) + 40); 
        pane.setPreferredSize(new Dimension(850, height));
        pane.setMaximumSize(new Dimension(1600, height));
        pane.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        mainContent.add(pane);
        mainContent.add(Box.createRigidArea(new Dimension(0, 30)));
    }

    private void addSectionHeader(String title) {
        JLabel header = new JLabel(title);
        header.setFont(new Font("Bodoni MT", Font.BOLD, 22));
        header.setForeground(new Color(100, 80, 200));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainContent.add(header);
    }

    private void addInfoRow(JPanel panel, String label, String value) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        JLabel val = new JLabel(value != null ? value : "Not Available");
        val.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(lbl);
        panel.add(val);
    }
}