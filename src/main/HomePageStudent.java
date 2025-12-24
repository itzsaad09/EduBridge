/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package main;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.ImageIcon;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author DELL
 */
public class HomePageStudent extends javax.swing.JFrame {
    Connection connect = null;
    ResultSet result = null;
    PreparedStatement pst = null;
    ResultSetMetaData RSMD;
    DefaultTableModel DTM;
    
    String id;
    private HoverPopup infoPopup;
    private final ImageIcon ENROLL_ICON = new ImageIcon(getClass().getResource("/main/resources/enroll (2).png"));
    private final ImageIcon DELETE_ICON = new ImageIcon(getClass().getResource("/main/resources/icons8-delete-24.png"));
    public HomePageStudent() {
        initComponents();
        // Set Title
        this.setTitle("EduBridge University");
        // Database Connection
        connect = Database.getConnection();
        // Add Logo in Title Bar
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/edubridgeicon.png"));
            Image image = icon.getImage();
            this.setIconImage(image);
        } catch (Exception e) {
            System.err.println("Error loading icon: " + e.getMessage());
        }
        // Active Profile Button
        setActiveTab(Profile);
        updateDateTime();
        startDateTimeUpdater();
        loadAndDisplayImage();
        loadStudentDetails();
    }
    
    public HomePageStudent(String id){
        initComponents();
        // Set Title
        this.setTitle("EduBridge University");
        // Database Connection
        connect = Database.getConnection();
        // Add Logo in Title Bar
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/edubridgeicon.png"));
            Image image = icon.getImage();
            this.setIconImage(image);
        } catch (Exception e) {
            System.err.println("Error loading icon: " + e.getMessage());
        }
        this.id = id;
        String query = "SELECT `password` FROM `student` WHERE `ID` = ?";
        try {
            pst = connect.prepareStatement(query);
            pst.setString(1, id);
            
            result = pst.executeQuery();
            if(result.next()){
                String password = result.getString("password");
                if(password.equals("Student123")){
                    new ChangePassword(id, "student").setVisible(true);
                }
            }
                
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,ex);
        }
        // Active Profile Button
        setActiveTab(Profile);
        updateDateTime();
        startDateTimeUpdater();
        loadAndDisplayImage();
        loadStudentDetails();
    }
    
    // Active Tab Styling
    private void setActiveTab(javax.swing.JButton selectedBtn) {
        Color activeColor = new Color(100, 80, 200);
        Color defaultColor = new Color(151, 137, 219);
        Color activeTextColor = Color.WHITE;
        Color defaultTextColor = new Color(60, 60, 60);

        javax.swing.JButton[] sideButtons = {
            Profile, EnrollCourse, EnrolledCourses, 
            FeeSummary, MyAttendance, MyResults,
            MyNotifications, LogOut
        };

        for (javax.swing.JButton btn : sideButtons) {
            if (btn == selectedBtn) {
                btn.setBackground(activeColor);
                btn.setForeground(activeTextColor);
                btn.setFont(new java.awt.Font("Bodoni MT", java.awt.Font.BOLD, 19)); 
            } else {
                btn.setBackground(defaultColor);
                btn.setForeground(defaultTextColor);
                btn.setFont(new java.awt.Font("Bodoni MT", java.awt.Font.BOLD, 18));
            }
            
            btn.revalidate();
            btn.repaint();
        }
    }
    
    private void updateDateTime() {
        // Format for time and date (e.g., 04:25:30 PM, Dec 09, 2025)
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss a, MMM dd, yyyy");
        java.util.Date date = new java.util.Date();
        // Set the formatted string to the JLabel
        DateTime.setText(formatter.format(date));
    }
    
    private void startDateTimeUpdater() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateDateTime();
            }
        }, 0, 1000);
    }
    
    private void showProfilePopup(javax.swing.JLabel sourceButton, java.awt.event.MouseEvent evt) {
        String instructorName = "";
        String instructorEmail = "";
        String instructorDepartment = "";
        
        String query = "SELECT `fName`, `lName`, `email`, `department` FROM `student` WHERE `ID` = ?";
        try {
            pst = connect.prepareStatement(query);
            pst.setString(1, this.id);
            result = pst.executeQuery();
            
            if (result.next()) {
                instructorName = result.getString("fName") + " " + result.getString("lName");
                instructorEmail = result.getString("email");
                instructorDepartment = result.getString("department");
            } else {
                instructorName = "Data Not Found";
                instructorEmail = "N/A";
                instructorDepartment = "N/A";
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error fetching profile data: " + ex.getMessage());
            instructorName = "Database Error";
        }
        
        String message = "<html><b>Full Name:</b> " + instructorName + 
                         "<br><b>Email:</b> " + instructorEmail + 
                         "<br><b>Department:</b> " + instructorDepartment + 
                         "</html>";
        
        
        if (infoPopup == null) {
            infoPopup = new HoverPopup(HomePageStudent.this, message);
        } else {
            infoPopup.updateMessage(message); 
        }

        Point buttonLocation = sourceButton.getLocationOnScreen();
        
        int popupX = buttonLocation.x + sourceButton.getWidth() - infoPopup.getWidth();
        
        int popupY = buttonLocation.y + sourceButton.getHeight();
        
        infoPopup.showAtLocation(popupX, popupY);
    }

    private void saveImageToDatabase(byte[] imageBytes) throws SQLException {
        String query = "UPDATE `student` SET `profile` = ? WHERE `ID` = ?";
        
        try (PreparedStatement updatePst = connect.prepareStatement(query)) {
            updatePst.setBytes(1, imageBytes);
            updatePst.setString(2, this.id);
            updatePst.executeUpdate();
        }
    }

    private void loadAndDisplayImage() {
        String query = "SELECT `profile` FROM `student` WHERE `ID` = ?";
        
        try (PreparedStatement selectPst = connect.prepareStatement(query)) {
            selectPst.setString(1, this.id);
            
            try (ResultSet rs = selectPst.executeQuery()) {
                if (rs.next()) {
                    byte[] imageBytes = rs.getBytes("profile");
                    if (imageBytes != null) {
                        displayImageFromBytes(imageBytes);
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error loading image from database: " + ex.getMessage());
        }
    }

    private void displayImageFromBytes(byte[] imageBytes) {
        ImageIcon imageIcon = new ImageIcon(imageBytes);
        Image image = imageIcon.getImage();
        
        int width = ProfilePic.getWidth() > 0 ? ProfilePic.getWidth() : 134; 
        int height = ProfilePic.getHeight() > 0 ? ProfilePic.getHeight() : 172; 
        
        Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        
        ProfilePic.setIcon(new ImageIcon(scaledImage));
    }
    
    private void loadStudentDetails() {
        String query = "SELECT `fName`, `lName`, `program`, `phoneNo`, `cnic`, `dob` FROM `student` WHERE `ID` = ?";
        
        try {
            pst = connect.prepareStatement(query);
            pst.setString(1, this.id);
            
            result = pst.executeQuery();
            
            if (result.next()) {
                String fullName = result.getString("fName") + " " + result.getString("lName");
                String program = result.getString("program");
                String phoneNo = result.getString("phoneNo");
                String cnic = result.getString("cnic");
                String dob = result.getString("dob"); 
                
                NameField.setText(fullName);
                ProgramField.setText(program);
                PhoneNoField.setText(phoneNo);
                CNICField.setText(cnic);
                DOBField.setText(dob);
                
            } else {
                NameField.setText("Student Not Found");
                JOptionPane.showMessageDialog(this, "Error: Student ID " + this.id + " not found in database.", "Data Error", JOptionPane.ERROR_MESSAGE);
            }
                
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
        } 
    }
    
    // Show Table
    private void show_Table(String tableName, JTable targetTable) {
        // Icons
        if(tableName.equals("course")){
            setIconRenderer(4, ENROLL_ICON);
        } else if(tableName.equals("enrollment")){
            setIconRenderer(4, DELETE_ICON);
        }
        String courseQuery = "SELECT c.coursecode, c.coursename, c.credithrs, t.instructorName "
                           + "FROM `course` c "
                           + "LEFT JOIN `timetable` t ON c.coursename = t.cName "
                           + "GROUP BY c.coursecode, c.coursename, c.credithrs, t.instructorName";
        if(tableName.equals("course")){
            try {
                pst = connect.prepareStatement(courseQuery);
                result = pst.executeQuery();
                RSMD = result.getMetaData();
                int CC = RSMD.getColumnCount();
                DTM = (DefaultTableModel)targetTable.getModel();
                DTM.setRowCount(0);
                while(result.next()) {
                    Vector v2 = new Vector();
                    for(int i=1; i<=CC; i++) {
                        v2.add(result.getString("coursecode"));
                        v2.add(result.getString("coursename"));
                        v2.add(result.getString("credithrs"));
                        v2.add(result.getString("instructorName")); 
                    }
                    DTM.addRow(v2);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,ex);
            }
        } else if(tableName.equals("enrollment")){
            String enrollmentQuery = "SELECT `coursecode`, `enrollment_date` FROM `enrollment` WHERE `student_id` = ?";

            String courseDetailQuery = "SELECT `coursename`, `credithrs` FROM `course` WHERE `coursecode` = ?";

            try {
                pst = connect.prepareStatement(enrollmentQuery);
                pst.setString(1, this.id);
                result = pst.executeQuery();

                DTM = (DefaultTableModel) targetTable.getModel();
                DTM.setRowCount(0);

                while (result.next()) {
                    String combinedCodes = result.getString("coursecode");
                    String enrollDate = result.getString("enrollment_date");

                    String[] codes = combinedCodes.split(",\\s*");

                    for (String code : codes) {
                        pst = connect.prepareStatement(courseDetailQuery);
                        pst.setString(1, code);
                        result = pst.executeQuery();

                        if (result.next()) {
                            Vector row = new Vector();
                            row.add(code);
                            row.add(result.getString("coursename"));
                            row.add(result.getString("credithrs"));
                            row.add(enrollDate);
                            row.add(DELETE_ICON);
                            DTM.addRow(row);
                        }
                    }
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,ex);
            }
        }
    }
    
    // Set Icon
    private void setIconRenderer(int columnIndex, ImageIcon icon) {
        DefaultTableCellRenderer iconRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable ViewTable, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(icon);
                label.setHorizontalAlignment(JLabel.CENTER);
                if (isSelected) {
                    label.setBackground(ViewTable.getSelectionBackground());
                    label.setOpaque(true);
                }
                return label;
            }
        };
        EnrollTable.getColumnModel().getColumn(columnIndex).setCellRenderer(iconRenderer);
        EnrolledTable.getColumnModel().getColumn(columnIndex).setCellRenderer(iconRenderer);
    }
    
    // Enroll Course
    private void enrollCourse(String courseCode) {
        int currentYear = java.time.Year.now().getValue();
        String session = "N/A";
        int creditHrs = 0;
        int availableSeats = 0;

        try {
            // Get session, credit hrs, seats from course
            pst = connect.prepareStatement("SELECT `session`, `credithrs`, `seats` FROM `course` WHERE `coursecode` = ?");
            pst.setString(1, courseCode);
            result = pst.executeQuery();
            if (result.next()) {
                session = result.getString("session");
                creditHrs = result.getInt("credithrs");
                availableSeats = result.getInt("seats");
            }

            // Check Seats Availability
            if (availableSeats <= 0) {
                new CustomMessageDialog(this, "Failed", "No seats available in " + courseCode, CustomMessageDialog.ERROR).setVisible(true);
                return;
            }

            // Check Already Available Row
            String checkRowQuery = "SELECT `coursecode` FROM `enrollment` WHERE `student_id` = ? AND `session` = ? AND `year` = ?";
            pst = connect.prepareStatement(checkRowQuery);
            pst.setString(1, this.id);
            pst.setString(2, session);
            pst.setInt(3, currentYear);
            result = pst.executeQuery();

            boolean isNewRow = !result.isBeforeFirst(); 
            String updatedCourseString = "";

            if (result.next()) {
                String existingCourses = result.getString("coursecode");
                if (existingCourses.contains(courseCode)) {
                    new CustomMessageDialog(this, "Failed", "Already Enrolled", CustomMessageDialog.ERROR).setVisible(true);
                    return;
                }
                updatedCourseString = existingCourses + ", " + courseCode;
            } else {
                updatedCourseString = courseCode;
            }

            connect.setAutoCommit(false); 

            pst = connect.prepareStatement("UPDATE `course` SET `seats` = `seats` - 1 WHERE `coursecode` = ?");
            pst.setString(1, courseCode);
            pst.executeUpdate();

            if (isNewRow) {
                pst = connect.prepareStatement("INSERT INTO `enrollment`(`student_id`, `coursecode`, `session`, `year`, `enrollment_date`) VALUES (?, ?, ?, ?, NOW())");
                pst.setString(1, this.id);
                pst.setString(2, updatedCourseString);
                pst.setString(3, session);
                pst.setInt(4, currentYear);
            } else {
                pst = connect.prepareStatement("UPDATE `enrollment` SET `coursecode` = ? WHERE `student_id` = ? AND `session` = ? AND `year` = ?");
                pst.setString(1, updatedCourseString);
                pst.setString(2, this.id);
                pst.setString(3, session);
                pst.setInt(4, currentYear);
            }
            pst.executeUpdate();

            String[] allCodes = updatedCourseString.split(",\\s*");
            int totalCredits = 0;
            for (String code : allCodes) {
                PreparedStatement cpst = connect.prepareStatement("SELECT `credithrs` FROM `course` WHERE `coursecode` = ?");
                cpst.setString(1, code);
                ResultSet rs = cpst.executeQuery();
                if (rs.next()) totalCredits += rs.getInt("credithrs");
            }
            double totalFeeAmount = totalCredits * 6000;

            pst = connect.prepareStatement("INSERT INTO `fee` (`student_id`, `session`, `year`, `total_fee`) VALUES (?, ?, ?, ?) "
                                        + "ON DUPLICATE KEY UPDATE `total_fee` = ?");
            pst.setString(1, this.id);
            pst.setString(2, session);
            pst.setInt(3, currentYear);
            pst.setDouble(4, totalFeeAmount);
            pst.setDouble(5, totalFeeAmount);
            pst.executeUpdate();

            connect.commit();
            connect.setAutoCommit(true);

            new CustomMessageDialog(this, "Success", "Enrolled! Fee Updated: " + totalFeeAmount, CustomMessageDialog.SUCCESS).setVisible(true);

        } catch (SQLException ex) {
            try { connect.rollback(); } catch (SQLException e) { /* ignore */ }
            JOptionPane.showMessageDialog(null, "Database Error: " + ex.getMessage());
        }
    }
    
    // Drop Course
    private void deleteEnrollment(String courseCode) {
        String message = "Are you sure you want to drop course " + courseCode + "?";
        CustomConfirmDialog dialog = new CustomConfirmDialog(this, "Confirm Course Drop", message);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                // 1. Fetch current enrollment details
                String fetchQuery = "SELECT enrollment_id, coursecode, session, year FROM `enrollment` WHERE `student_id` = ? ORDER BY enrollment_date DESC LIMIT 1";
                pst = connect.prepareStatement(fetchQuery);
                pst.setString(1, this.id);
                result = pst.executeQuery();

                if (result.next()) {
                    int enrollmentId = result.getInt("enrollment_id");
                    String currentCourses = result.getString("coursecode");
                    String session = result.getString("session");
                    int year = result.getInt("year");

                    // 2. remove the course
                    java.util.List<String> codesList = new java.util.ArrayList<>(java.util.Arrays.asList(currentCourses.split(",\\s*")));
                    
                    if (codesList.remove(courseCode)) {
                        // Start Transaction
                        connect.setAutoCommit(false);

                        // Return the seat to the course table
                        pst = connect.prepareStatement("UPDATE `course` SET `seats` = `seats` + 1 WHERE `coursecode` = ?");
                        pst.setString(1, courseCode);
                        pst.executeUpdate();

                        // Update Enrollment Table
                        double totalFeeAmount = 0;
                        if (codesList.isEmpty()) {
                            // Delete row if no courses left
                            pst = connect.prepareStatement("DELETE FROM `enrollment` WHERE `enrollment_id` = ?");
                            pst.setInt(1, enrollmentId);
                            pst.executeUpdate();
                            
                            // Delete fee record as well
                            pst = connect.prepareStatement("DELETE FROM `fee` WHERE `student_id` = ? AND `session` = ? AND `year` = ?");
                            pst.setString(1, this.id);
                            pst.setString(2, session);
                            pst.setInt(3, year);
                            pst.executeUpdate();
                        } else {
                            // Update with remaining courses
                            String updatedCourses = String.join(", ", codesList);
                            pst = connect.prepareStatement("UPDATE `enrollment` SET `coursecode` = ? WHERE `enrollment_id` = ?");
                            pst.setString(1, updatedCourses);
                            pst.setInt(2, enrollmentId);
                            pst.executeUpdate();

                            // Recalculate Fees for remaining courses
                            int totalCredits = 0;
                            for (String code : codesList) {
                                PreparedStatement cpst = connect.prepareStatement("SELECT `credithrs` FROM `course` WHERE `coursecode` = ?");
                                cpst.setString(1, code);
                                ResultSet rs = cpst.executeQuery();
                                if (rs.next()) totalCredits += rs.getInt("credithrs");
                            }
                            totalFeeAmount = totalCredits * 6000;

                            // Update Fee Table
                            pst = connect.prepareStatement("UPDATE `fee` SET `total_fee` = ? WHERE `student_id` = ? AND `session` = ? AND `year` = ?");
                            pst.setDouble(1, totalFeeAmount);
                            pst.setString(2, this.id);
                            pst.setString(3, session);
                            pst.setInt(4, year);
                            pst.executeUpdate();
                        }

                        connect.commit();
                        connect.setAutoCommit(true);

                        String successMsg = "Course dropped. " + (codesList.isEmpty() ? "All fees cleared." : "New Fee: " + totalFeeAmount);
                        new CustomMessageDialog(this, "Success", successMsg, CustomMessageDialog.SUCCESS).setVisible(true);
                        show_Table("enrollment", EnrolledTable);
                    }
                }
            } catch (SQLException ex) {
                try { connect.rollback(); } catch (SQLException e) { }
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    // Fee
    private void showFee() {
        try {
            String query = "SELECT `year`, `session`, `total_fee`, `status` FROM `fee` WHERE `student_id` = ?";
            pst = connect.prepareStatement(query);
            pst.setString(1, this.id);
            result = pst.executeQuery();
            
            DTM = (DefaultTableModel) FeeTable.getModel();
            DTM.setRowCount(0);

            while (result.next()) {
                Vector v = new Vector();
                v.add(result.getString("year"));
                v.add(result.getString("session"));
                v.add(result.getString("total_fee"));
                v.add(result.getString("status"));
                DTM.addRow(v);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching fee data: " + ex.getMessage());
        }
    }

    // Download Fee PDF
    private void downloadFeeVoucherPDF(String year, String session, String amount, String status) {
        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        fileChooser.setDialogTitle("Save Fee Voucher");
        fileChooser.setSelectedFile(new java.io.File("FeeVoucher_" + session + "_" + year + ".pdf"));
        
        if (fileChooser.showSaveDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
            try {
                com.itextpdf.text.pdf.PdfWriter writer = com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(fileChooser.getSelectedFile()));
                document.open();

                // 1. UNIVERSITY LOGO
                java.net.URL logoUrl = getClass().getResource("/images/edubridge.png");
                if (logoUrl != null) {
                    com.itextpdf.text.Image logo = com.itextpdf.text.Image.getInstance(logoUrl);
                    logo.scaleToFit(80, 80);
                    logo.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                    document.add(logo);
                }

                // 2. FONTS
                com.itextpdf.text.Font boldFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD);
                com.itextpdf.text.Font smallBold = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD);
                com.itextpdf.text.Font normalFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.NORMAL);
                com.itextpdf.text.Font redBold = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.BOLD, com.itextpdf.text.BaseColor.RED);

                // 3. HEADERS
                com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph("EduBridge University", boldFont);
                title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                document.add(title);
                
                com.itextpdf.text.Paragraph subTitle = new com.itextpdf.text.Paragraph("Official Fee Challan", smallBold);
                subTitle.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                document.add(subTitle);
                document.add(new com.itextpdf.text.Paragraph(" "));

                // 4. CALCULATE DUE DATE (Current Date + 7 Days)
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMMM dd, yyyy");
                java.util.Calendar cal = java.util.Calendar.getInstance();
                String issueDate = sdf.format(cal.getTime());
                cal.add(java.util.Calendar.DAY_OF_MONTH, 7);
                String dueDate = sdf.format(cal.getTime());

                // 5. STUDENT & VOUCHER DETAILS
                com.itextpdf.text.pdf.PdfPTable detailsTable = new com.itextpdf.text.pdf.PdfPTable(2);
                detailsTable.setWidthPercentage(100);
                detailsTable.getDefaultCell().setBorder(com.itextpdf.text.Rectangle.NO_BORDER);

                detailsTable.addCell(new com.itextpdf.text.Phrase("Student ID: " + this.id, normalFont));
                detailsTable.addCell(new com.itextpdf.text.Phrase("Issue Date: " + issueDate, normalFont));
                detailsTable.addCell(new com.itextpdf.text.Phrase("Name: " + NameField.getText(), normalFont));
                detailsTable.addCell(new com.itextpdf.text.Phrase("Due Date: " + dueDate, redBold)); // Highlighted due date
                detailsTable.addCell(new com.itextpdf.text.Phrase("Session: " + session + " " + year, normalFont));
                detailsTable.addCell(new com.itextpdf.text.Phrase("Status: " + status, smallBold));
                
                document.add(detailsTable);
                document.add(new com.itextpdf.text.Paragraph(" "));

                // 6. FEE TABLE
                com.itextpdf.text.pdf.PdfPTable feeTable = new com.itextpdf.text.pdf.PdfPTable(2);
                feeTable.setWidthPercentage(100);
                feeTable.addCell(new com.itextpdf.text.Phrase("Description", smallBold));
                feeTable.addCell(new com.itextpdf.text.Phrase("Amount (PKR)", smallBold));
                
                feeTable.addCell("Tuition Fee (Enrolled Courses)");
                feeTable.addCell(amount);
                
                feeTable.addCell(new com.itextpdf.text.Phrase("TOTAL PAYABLE", smallBold));
                feeTable.addCell(new com.itextpdf.text.Phrase(amount, smallBold));
                document.add(feeTable);

                // 7. FOOTER (BANK SECTION)
                com.itextpdf.text.pdf.PdfPTable footerTable = new com.itextpdf.text.pdf.PdfPTable(1);
                footerTable.setTotalWidth(200);
                com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell();
                cell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
                cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);

                com.itextpdf.text.Paragraph line = new com.itextpdf.text.Paragraph("\n\n__________________________", normalFont);
                line.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                cell.addElement(line);
                
                com.itextpdf.text.Paragraph bankPara = new com.itextpdf.text.Paragraph("Bank Cashier Signature", smallBold);
                bankPara.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                cell.addElement(bankPara);

                footerTable.addCell(cell);
                footerTable.writeSelectedRows(0, -1, 370, 100, writer.getDirectContent());

                document.close();
                CustomMessageDialog messageDialog = new CustomMessageDialog(this, "Success", "Fee Voucher generated.", CustomMessageDialog.SUCCESS);
                messageDialog.setVisible(true);

            } catch (Exception e) {
                javax.swing.JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    // Attendance
    private void showAttendance() {
        DefaultTableModel model = (DefaultTableModel) AttendanceTable.getModel();
        model.setRowCount(0);

        try {
            String enrollQuery = "SELECT `coursecode` FROM `enrollment` WHERE `student_id` = ?";
            pst = connect.prepareStatement(enrollQuery);
            pst.setString(1, this.id);
            result = pst.executeQuery();

            if (result.next()) {
                String combinedCodes = result.getString("coursecode");
                String[] codes = combinedCodes.split(",\\s*");

                for (String code : codes) {
                    String attendanceQuery = "SELECT `log` FROM `attendance_json` WHERE `student_id` = ? AND `coursecode` = ?";
                    pst = connect.prepareStatement(attendanceQuery);
                    pst.setString(1, this.id);
                    pst.setString(2, code);
                    result = pst.executeQuery();

                    int presentCount = 0;
                    int absentCount = 0;

                    if (result.next()) {
                        String jsonLog = result.getString("log");
                        if (jsonLog != null) {
                            // Simple counting by checking occurrences in the JSON-like string
                            presentCount = (jsonLog.length() - jsonLog.replace("Present", "").length()) / "Present".length();
                            absentCount = (jsonLog.length() - jsonLog.replace("Absent", "").length()) / "Absent".length();
                        }
                    }

                    String nameQuery = "SELECT `coursename` FROM `course` WHERE `coursecode` = ?";
                    pst = connect.prepareStatement(nameQuery);
                    pst.setString(1, code);
                    result = pst.executeQuery();
                    String courseName = result.next() ? result.getString("coursename") : code;

                    double total = presentCount + absentCount;
                    String percentage = (total > 0) ? String.format("%.1f%%", (presentCount / total) * 100) : "0%";
                    
                    model.addRow(new Object[]{courseName, presentCount, absentCount, percentage});
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading attendance: " + ex.getMessage());
        }
    }

    // Detailed Attendance
    private void showDetailedAttendance(String courseCode) {
        try {
            String query = "SELECT log FROM attendance_json WHERE student_id = ? AND coursecode = ?";
            pst = connect.prepareStatement(query);
            pst.setString(1, this.id);
            pst.setString(2, courseCode);
            result = pst.executeQuery();

            if (result.next()) {
                String jsonLog = result.getString("log");
                if (jsonLog == null || jsonLog.equals("{}")) {
                    new CustomMessageDialog(this, "Attendance", "No logs found.", CustomMessageDialog.ERROR).setVisible(true);
                    return;
                }

                String cleanLog = jsonLog.replace("{", "").replace("}", "").replace("\"", "");
                String[] entries = cleanLog.split(", ");
                
                StringBuilder report = new StringBuilder("<html><div style='text-align: center;'>");
                report.append("<b>Attendance History for ").append(courseCode).append("</b><br><br>");
                for (String entry : entries) {
                    report.append(entry.replace(":", " &rarr; ")).append("<br>");
                }
                report.append("</div></html>");

                CustomMessageDialog detailDialog = new CustomMessageDialog(this, "Attendance Details", report.toString(), -1);
                detailDialog.setVisible(true);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // Result
    private void showResults() {
        DefaultTableModel model = (DefaultTableModel) ResultTable.getModel();
        model.setRowCount(0);

        // Query to get GPA and CGPA data
        String query = "SELECT e.year, e.session, " +
                "GROUP_CONCAT(r.total) as all_totals, " +
                "GROUP_CONCAT(c.credithrs) as all_credits " +
                "FROM results r " +
                "JOIN course c ON r.course_code = c.coursecode " +
                "JOIN enrollment e ON r.student_id = e.student_id AND e.coursecode LIKE CONCAT('%', r.course_code, '%') " +
                "WHERE r.student_id = ? " +
                "GROUP BY e.year, e.session " +
                "HAVING MIN(r.is_published) = 1 " +
                "ORDER BY e.year DESC, e.session DESC";

        try {
            pst = connect.prepareStatement(query);
            pst.setString(1, this.id);
            result = pst.executeQuery();

            double totalGP = 0;
            int totalCr = 0;

            while (result.next()) {
                String[] totals = result.getString("all_totals").split(",");
                String[] credits = result.getString("all_credits").split(",");
                
                double semGP = 0;
                int semCr = 0;

                for (int i = 0; i < totals.length; i++) {
                    int score = Integer.parseInt(totals[i]);
                    int cr = Integer.parseInt(credits[i]);
                    double gp = calculateGP(score);
                    semGP += (gp * cr);
                    semCr += cr;
                }

                double gpa = (semCr > 0) ? semGP / semCr : 0;
                totalGP += semGP;
                totalCr += semCr;
                double cgpa = (totalCr > 0) ? totalGP / totalCr : 0;

                model.addRow(new Object[]{
                    result.getString("year"),
                    result.getString("session"),
                    String.format("%.2f", gpa),
                    String.format("%.2f", cgpa)
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // GPA Calculation Helper
    private double calculateGP(int marks) {
        if (marks >= 85) return 4.0;
        if (marks >= 80) return 3.7;
        if (marks >= 75) return 3.3;
        if (marks >= 70) return 3.0;
        if (marks >= 65) return 2.7;
        if (marks >= 60) return 2.3;
        if (marks >= 50) return 2.0;
        return 0.0;
    }

    private void showDetailedResults(String year, String session) {
        try {
            String query = "SELECT c.coursename, r.total " +
                        "FROM results r " +
                        "JOIN course c ON r.course_code = c.coursecode " +
                        "JOIN enrollment e ON r.student_id = e.student_id AND e.coursecode LIKE CONCAT('%', r.course_code, '%') " +
                        "WHERE r.student_id = ? AND e.year = ? AND e.session = ?";
            
            pst = connect.prepareStatement(query);
            pst.setString(1, this.id);
            pst.setString(2, year);
            pst.setString(3, session);
            result = pst.executeQuery();

            StringBuilder report = new StringBuilder("<html><div style='text-align: center;'>");
            report.append("<b>Transcript: ").append(session).append(" ").append(year).append("</b><br><br>");

            boolean hasData = false;
            while (result.next()) {
                hasData = true;
                String course = result.getString("coursename");
                double total = result.getDouble("total");
                report.append(course).append(" &rarr; <b>").append(total).append("</b><br>");
            }

            if (!hasData) {
                report.append("No records found for this semester.");
            }
            
            report.append("</div></html>");

            CustomMessageDialog detailDialog = new CustomMessageDialog(this, "Academic Record", report.toString(), -1);
            detailDialog.setVisible(true);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void downloadTranscriptPDF(String year, String session) {
        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        fileChooser.setDialogTitle("Save Transcript");
        fileChooser.setSelectedFile(new java.io.File("Transcript_" + session + "_" + year + ".pdf"));
        
        if (fileChooser.showSaveDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
            try {
                com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(fileChooser.getSelectedFile()));
                // Update this line at the start of your try block
                com.itextpdf.text.pdf.PdfWriter writer = com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(fileChooser.getSelectedFile()));
                document.open();

                // 1. UNIVERSITY LOGO
                try {
                    java.net.URL logoUrl = getClass().getResource("/images/edubridge.png");
                    if (logoUrl != null) {
                        com.itextpdf.text.Image logo = com.itextpdf.text.Image.getInstance(logoUrl);
                        logo.scaleToFit(80, 80); 
                        logo.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                        document.add(logo);
                    }
                } catch (Exception e) {
                    System.err.println("Logo error: " + e.getMessage());
                }

                // 2. FONTS AND HEADERS
                com.itextpdf.text.Font boldFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD);
                com.itextpdf.text.Font smallBold = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD);
                com.itextpdf.text.Font normalFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.NORMAL);
                
                com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph("EduBridge University", boldFont);
                title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                document.add(title);
                
                com.itextpdf.text.Paragraph subTitle = new com.itextpdf.text.Paragraph("Official Academic Transcript", smallBold);
                subTitle.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                document.add(subTitle);
                
                document.add(new com.itextpdf.text.Paragraph(" ")); 
                document.add(new com.itextpdf.text.Paragraph("Student ID: " + this.id, normalFont));
                document.add(new com.itextpdf.text.Paragraph("Semester: " + session + " " + year, normalFont));
                document.add(new com.itextpdf.text.Paragraph(" ")); 

                // 3. RESULT TABLE
                com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(4);
                table.setWidthPercentage(100);
                table.addCell(new com.itextpdf.text.Phrase("Course Name", smallBold));
                table.addCell(new com.itextpdf.text.Phrase("Sessional", smallBold));
                table.addCell(new com.itextpdf.text.Phrase("Final", smallBold));
                table.addCell(new com.itextpdf.text.Phrase("Total", smallBold));

                String currentSemQuery = "SELECT c.coursename, r.sessional, r.final, r.total, c.credithrs " +
                                        "FROM results r " +
                                        "JOIN course c ON r.course_code = c.coursecode " +
                                        "JOIN enrollment e ON r.student_id = e.student_id AND e.coursecode LIKE CONCAT('%', r.course_code, '%') " +
                                        "WHERE r.student_id = ? AND e.year = ? AND e.session = ?";
                
                pst = connect.prepareStatement(currentSemQuery);
                pst.setString(1, this.id);
                pst.setString(2, year);
                pst.setString(3, session);
                result = pst.executeQuery();

                double semGP = 0;
                int semCr = 0;

                while (result.next()) {
                    table.addCell(result.getString("coursename"));
                    table.addCell(String.valueOf(result.getDouble("sessional")));
                    table.addCell(String.valueOf(result.getDouble("final")));
                    table.addCell(String.valueOf(result.getDouble("total")));

                    int totalMarks = (int) result.getDouble("total");
                    int credits = result.getInt("credithrs");
                    semGP += (calculateGP(totalMarks) * credits);
                    semCr += credits;
                }
                document.add(table);
                document.add(new com.itextpdf.text.Paragraph(" "));

                // 4. CGPA CALCULATION
                String allSemQuery = "SELECT r.total, c.credithrs FROM results r JOIN course c ON r.course_code = c.coursecode WHERE r.student_id = ?";
                pst = connect.prepareStatement(allSemQuery);
                pst.setString(1, this.id);
                java.sql.ResultSet rsAll = pst.executeQuery();

                double totalGP = 0;
                int totalCr = 0;

                while (rsAll.next()) {
                    int totalMarks = (int) rsAll.getDouble("total");
                    int credits = rsAll.getInt("credithrs");
                    totalGP += (calculateGP(totalMarks) * credits);
                    totalCr += credits;
                }

                double gpa = (semCr > 0) ? semGP / semCr : 0;
                double cgpa = (totalCr > 0) ? totalGP / totalCr : 0;

                document.add(new com.itextpdf.text.Paragraph("Semester GPA: " + String.format("%.2f", gpa), smallBold));
                document.add(new com.itextpdf.text.Paragraph("Cumulative CGPA: " + String.format("%.2f", cgpa), smallBold));
                
                // 5. SIGNATURE AND DATE SECTION (PINNED TO BOTTOM)
                com.itextpdf.text.pdf.PdfPTable footerTable = new com.itextpdf.text.pdf.PdfPTable(1);
                footerTable.setTotalWidth(200); // Width of the signature area
                
                // Create the signature cell
                com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell();
                cell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
                cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);

                // Add the line
                com.itextpdf.text.Paragraph line = new com.itextpdf.text.Paragraph("__________________________", normalFont);
                line.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                cell.addElement(line);
                
                // Add Registrar Title
                com.itextpdf.text.Paragraph registrarPara = new com.itextpdf.text.Paragraph("University Registrar", smallBold);
                registrarPara.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                cell.addElement(registrarPara);
                
                // Add Date
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMMM dd, yyyy");
                com.itextpdf.text.Paragraph datePara = new com.itextpdf.text.Paragraph("Date: " + sdf.format(new java.util.Date()), normalFont);
                datePara.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                cell.addElement(datePara);

                footerTable.addCell(cell);

                footerTable.writeSelectedRows(0, -1, 350, 100, writer.getDirectContent());

                document.close();
                CustomMessageDialog messageDialog = new CustomMessageDialog(this, "Success", "Transcript Saved Successfully", CustomMessageDialog.SUCCESS);
                messageDialog.setVisible(true);

            } catch (Exception e) {
                javax.swing.JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    private void loadNotifications() {
        DefaultTableModel model = (DefaultTableModel) NotificationTable.getModel();
        model.setRowCount(0);

        String query = "SELECT title, message, date_sent FROM notification ORDER BY date_sent DESC";
        try {
            pst = connect.prepareStatement(query);
            result = pst.executeQuery();

            while (result.next()) {
                Vector v = new Vector();
                v.add(result.getString("title"));
                v.add(result.getString("message"));
                v.add(result.getString("date_sent"));
                model.addRow(v);
            }
        } catch (SQLException ex) {
            System.err.println("Database Error: " + ex.getMessage());
        }
    }
    
    private void showNotificationDetailWindow(String title, String message, String date) {
        // 1. Setup the Dialog
        javax.swing.JDialog detailWindow = new javax.swing.JDialog(this, "Notification Detail", true);
        detailWindow.setSize(500, 450);
        detailWindow.setLocationRelativeTo(this);
        detailWindow.setLayout(new java.awt.BorderLayout(10, 10));

        // 2. Create components
        javax.swing.JPanel mainPanel = new javax.swing.JPanel();
        mainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setLayout(new java.awt.BorderLayout(10, 10));

        // Title and Date Header
        javax.swing.JLabel titleLabel = new javax.swing.JLabel("<html><body style='width: 300px'><b>Title: </b>" + title + "</body></html>");
        titleLabel.setFont(new java.awt.Font("Bodoni MT", java.awt.Font.BOLD, 16));

        javax.swing.JLabel dateLabel = new javax.swing.JLabel("Sent: " + date);
        dateLabel.setFont(new java.awt.Font("Bodoni MT", java.awt.Font.ITALIC, 12));

        javax.swing.JPanel headerPanel = new javax.swing.JPanel(new java.awt.GridLayout(0, 1));
        headerPanel.add(titleLabel);
        headerPanel.add(dateLabel);

        // Message Body
        javax.swing.JTextArea msgArea = new javax.swing.JTextArea(message);
        msgArea.setEditable(false);
        msgArea.setLineWrap(true);
        msgArea.setWrapStyleWord(true);
        msgArea.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
        msgArea.setBackground(new java.awt.Color(245, 245, 245));

        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(msgArea);
        scrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Message Content"));

        // 3. Assemble and Show
        mainPanel.add(headerPanel, java.awt.BorderLayout.NORTH);
        mainPanel.add(scrollPane, java.awt.BorderLayout.CENTER);

        detailWindow.add(mainPanel);
        detailWindow.setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        HomePageStudentPanel = new ui.GradientPanel();
        SidePanel = new ui.GlassmorphismPanel();
        DateTime = new javax.swing.JLabel();
        Profile = new RoundedButton();
        EnrollCourse = new RoundedButton();
        EnrolledCourses = new RoundedButton();
        FeeSummary = new RoundedButton();
        MyAttendance = new RoundedButton();
        MyResults = new RoundedButton();
        MyNotifications = new RoundedButton();
        LogOut = new RoundedButton();
        MainPagePanel = new javax.swing.JPanel();
        ProfilePanel = new ui.GlassmorphismPanel();
        ProfileButton = new javax.swing.JLabel();
        ProfilePic = new javax.swing.JLabel();
        Name = new javax.swing.JLabel();
        NameField = new javax.swing.JLabel();
        Program = new javax.swing.JLabel();
        ProgramField = new javax.swing.JLabel();
        PhoneNo = new javax.swing.JLabel();
        PhoneNoField = new javax.swing.JLabel();
        CNIC = new javax.swing.JLabel();
        CNICField = new javax.swing.JLabel();
        DOB = new javax.swing.JLabel();
        DOBField = new javax.swing.JLabel();
        Separator = new javax.swing.JSeparator();
        EnrollPanel = new javax.swing.JPanel();
        ProfileButton1 = new javax.swing.JLabel();
        EnrollScrollPane = new javax.swing.JScrollPane();
        EnrollTable = new RoundedTable();
        EnrolledPanel = new javax.swing.JPanel();
        ProfileButton2 = new javax.swing.JLabel();
        EnrolledScrollPane = new javax.swing.JScrollPane();
        EnrolledTable = new RoundedTable();
        FeePanel = new javax.swing.JPanel();
        ProfileButton3 = new javax.swing.JLabel();
        FeeScrollPane = new javax.swing.JScrollPane();
        FeeTable = new RoundedTable();
        AttendancePanel = new javax.swing.JPanel();
        ProfileButton4 = new javax.swing.JLabel();
        AttendanceScrollPane = new javax.swing.JScrollPane();
        AttendanceTable = new RoundedTable();
        ResultPanel = new javax.swing.JPanel();
        ProfileButton5 = new javax.swing.JLabel();
        ResultScrollPane = new javax.swing.JScrollPane();
        ResultTable = new RoundedTable();
        NotificationPanel = new javax.swing.JPanel();
        ProfileButton6 = new javax.swing.JLabel();
        NotificationScrollPane = new javax.swing.JScrollPane();
        NotificationTable = new RoundedTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        HomePageStudentPanel.setOpaque(false);
        HomePageStudentPanel.setPreferredSize(new java.awt.Dimension(923, 600));

        SidePanel.setOpaque(false);
        SidePanel.setPreferredSize(new java.awt.Dimension(200, 600));

        DateTime.setFont(new java.awt.Font("Bodoni MT", 1, 12)); // NOI18N
        DateTime.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        DateTime.setText("04:25:30 PM, Dec 09, 2025");

        Profile.setBackground(new java.awt.Color(151, 137, 219));
        Profile.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        Profile.setText("Profile");
        Profile.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        Profile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ProfileMouseClicked(evt);
            }
        });
        Profile.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ProfileKeyPressed(evt);
            }
        });

        EnrollCourse.setBackground(new java.awt.Color(151, 137, 219));
        EnrollCourse.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        EnrollCourse.setText("Enroll Course");
        EnrollCourse.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        EnrollCourse.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                EnrollCourseMouseClicked(evt);
            }
        });
        EnrollCourse.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                EnrollCourseKeyPressed(evt);
            }
        });

        EnrolledCourses.setBackground(new java.awt.Color(151, 137, 219));
        EnrolledCourses.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        EnrolledCourses.setText("Enrolled Course");
        EnrolledCourses.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        EnrolledCourses.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                EnrolledCoursesMouseClicked(evt);
            }
        });
        EnrolledCourses.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                EnrolledCoursesKeyPressed(evt);
            }
        });

        FeeSummary.setBackground(new java.awt.Color(151, 137, 219));
        FeeSummary.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        FeeSummary.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        FeeSummary.setLabel("Fee Summary");
        FeeSummary.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                FeeSummaryMouseClicked(evt);
            }
        });
        FeeSummary.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                FeeSummaryKeyPressed(evt);
            }
        });

        MyAttendance.setBackground(new java.awt.Color(151, 137, 219));
        MyAttendance.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        MyAttendance.setText("My Attendance");
        MyAttendance.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        MyAttendance.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                MyAttendanceMouseClicked(evt);
            }
        });
        MyAttendance.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                MyAttendanceKeyPressed(evt);
            }
        });

        MyResults.setBackground(new java.awt.Color(151, 137, 219));
        MyResults.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        MyResults.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        MyResults.setLabel("My Results");
        MyResults.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                MyResultsMouseClicked(evt);
            }
        });
        MyResults.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                MyResultsKeyPressed(evt);
            }
        });

        MyNotifications.setBackground(new java.awt.Color(151, 137, 219));
        MyNotifications.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        MyNotifications.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        MyNotifications.setLabel("Notifications");
        MyNotifications.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                MyNotificationsMouseClicked(evt);
            }
        });
        MyNotifications.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                MyNotificationsKeyPressed(evt);
            }
        });

        LogOut.setBackground(new java.awt.Color(151, 137, 219));
        LogOut.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        LogOut.setText("LogOut");
        LogOut.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        LogOut.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                LogOutMouseClicked(evt);
            }
        });
        LogOut.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                LogOutKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout SidePanelLayout = new javax.swing.GroupLayout(SidePanel);
        SidePanel.setLayout(SidePanelLayout);
        SidePanelLayout.setHorizontalGroup(
            SidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SidePanelLayout.createSequentialGroup()
                .addGroup(SidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(SidePanelLayout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(DateTime))
                    .addGroup(SidePanelLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(SidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(EnrollCourse, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Profile, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(FeeSummary, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(MyAttendance, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(MyResults, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(MyNotifications, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(LogOut, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(EnrolledCourses, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        SidePanelLayout.setVerticalGroup(
            SidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SidePanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(DateTime)
                .addGap(33, 33, 33)
                .addComponent(Profile)
                .addGap(33, 33, 33)
                .addComponent(EnrollCourse)
                .addGap(33, 33, 33)
                .addComponent(EnrolledCourses)
                .addGap(33, 33, 33)
                .addComponent(FeeSummary)
                .addGap(33, 33, 33)
                .addComponent(MyAttendance)
                .addGap(33, 33, 33)
                .addComponent(MyResults)
                .addGap(33, 33, 33)
                .addComponent(MyNotifications)
                .addGap(33, 33, 33)
                .addComponent(LogOut)
                .addGap(20, 20, 20))
        );

        MainPagePanel.setOpaque(false);
        MainPagePanel.setLayout(new java.awt.CardLayout());

        ProfilePanel.setOpaque(false);

        ProfileButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ProfileButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/main/resources/boy.png"))); // NOI18N
        ProfileButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ProfileButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ProfileButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                ProfileButtonMouseExited(evt);
            }
        });

        ProfilePic.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        ProfilePic.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ProfilePic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/main/resources/uplaod.png"))); // NOI18N
        ProfilePic.setText("Upload");
        ProfilePic.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        ProfilePic.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ProfilePic.setPreferredSize(new java.awt.Dimension(134, 172));
        ProfilePic.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ProfilePicMouseClicked(evt);
            }
        });
        ProfilePic.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ProfilePicKeyPressed(evt);
            }
        });

        Name.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        Name.setText("Name:");

        NameField.setFont(new java.awt.Font("Bodoni MT", 0, 18)); // NOI18N
        NameField.setText("Name Field");

        Program.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        Program.setText("Program:");

        ProgramField.setFont(new java.awt.Font("Bodoni MT", 0, 18)); // NOI18N
        ProgramField.setText("Program Field");

        PhoneNo.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        PhoneNo.setText("Phone No:");

        PhoneNoField.setFont(new java.awt.Font("Bodoni MT", 0, 18)); // NOI18N
        PhoneNoField.setText("PhoneNo Field");

        CNIC.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        CNIC.setText("CNIC:");

        CNICField.setFont(new java.awt.Font("Bodoni MT", 0, 18)); // NOI18N
        CNICField.setText("CNIC Field");

        DOB.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        DOB.setText("DOB:");

        DOBField.setFont(new java.awt.Font("Bodoni MT", 0, 18)); // NOI18N
        DOBField.setText("DOB Field");

        javax.swing.GroupLayout ProfilePanelLayout = new javax.swing.GroupLayout(ProfilePanel);
        ProfilePanel.setLayout(ProfilePanelLayout);
        ProfilePanelLayout.setHorizontalGroup(
            ProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ProfilePanelLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(ProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ProfilePanelLayout.createSequentialGroup()
                        .addComponent(Separator, javax.swing.GroupLayout.PREFERRED_SIZE, 635, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(ProfilePanelLayout.createSequentialGroup()
                        .addComponent(ProfilePic, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(ProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(ProfilePanelLayout.createSequentialGroup()
                                .addComponent(Name, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(NameField, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 190, Short.MAX_VALUE)
                                .addComponent(ProfileButton, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(ProfilePanelLayout.createSequentialGroup()
                                .addGroup(ProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(PhoneNo)
                                    .addGroup(ProfilePanelLayout.createSequentialGroup()
                                        .addComponent(CNIC, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(CNICField, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(ProfilePanelLayout.createSequentialGroup()
                                        .addComponent(DOB, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(DOBField, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 232, Short.MAX_VALUE))))))
            .addGroup(ProfilePanelLayout.createSequentialGroup()
                .addGap(187, 187, 187)
                .addComponent(Program, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PhoneNoField, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ProgramField, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        ProfilePanelLayout.setVerticalGroup(
            ProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ProfilePanelLayout.createSequentialGroup()
                .addGroup(ProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ProfileButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(ProfilePanelLayout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(ProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(ProfilePanelLayout.createSequentialGroup()
                                .addGroup(ProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(Name, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(NameField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(ProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(Program, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ProgramField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(ProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(PhoneNo)
                                    .addComponent(PhoneNoField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(ProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(CNIC, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(CNICField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(ProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(DOB, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(DOBField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(ProfilePic, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(65, 65, 65)
                .addComponent(Separator, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(316, Short.MAX_VALUE))
        );

        MainPagePanel.add(ProfilePanel, "Card1");

        EnrollPanel.setOpaque(false);

        ProfileButton1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ProfileButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/main/resources/boy.png"))); // NOI18N
        ProfileButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ProfileButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ProfileButton1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                ProfileButton1MouseExited(evt);
            }
        });

        EnrollTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Course Code", "Course Name", "Credit Hours", "Instructor", "Enroll"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        EnrollTable.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        EnrollTable.setRowHeight(50);
        EnrollTable.setRowSelectionAllowed(false);
        EnrollTable.setShowGrid(true);
        EnrollTable.getTableHeader().setResizingAllowed(false);
        EnrollTable.getTableHeader().setReorderingAllowed(false);
        EnrollTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                EnrollTableMouseClicked(evt);
            }
        });
        EnrollScrollPane.setViewportView(EnrollTable);

        javax.swing.GroupLayout EnrollPanelLayout = new javax.swing.GroupLayout(EnrollPanel);
        EnrollPanel.setLayout(EnrollPanelLayout);
        EnrollPanelLayout.setHorizontalGroup(
            EnrollPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EnrollPanelLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(EnrollScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 625, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(40, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, EnrollPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(ProfileButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        EnrollPanelLayout.setVerticalGroup(
            EnrollPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EnrollPanelLayout.createSequentialGroup()
                .addComponent(ProfileButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(EnrollScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                .addContainerGap())
        );

        MainPagePanel.add(EnrollPanel, "Card2");

        EnrolledPanel.setOpaque(false);

        ProfileButton2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ProfileButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/main/resources/boy.png"))); // NOI18N
        ProfileButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ProfileButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ProfileButton2MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                ProfileButton2MouseExited(evt);
            }
        });

        EnrolledTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Course Code", "Course Name", "Credit Hours", "Enrollment Date", "Drop Course"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        EnrolledTable.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        EnrolledTable.setRowHeight(50);
        EnrolledTable.setRowSelectionAllowed(false);
        EnrolledTable.setShowGrid(true);
        EnrolledTable.getTableHeader().setResizingAllowed(false);
        EnrolledTable.getTableHeader().setReorderingAllowed(false);
        EnrolledTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                EnrolledTableMouseClicked(evt);
            }
        });
        EnrolledScrollPane.setViewportView(EnrolledTable);

        javax.swing.GroupLayout EnrolledPanelLayout = new javax.swing.GroupLayout(EnrolledPanel);
        EnrolledPanel.setLayout(EnrolledPanelLayout);
        EnrolledPanelLayout.setHorizontalGroup(
            EnrolledPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EnrolledPanelLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(EnrolledScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 625, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(40, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, EnrolledPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(ProfileButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        EnrolledPanelLayout.setVerticalGroup(
            EnrolledPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EnrolledPanelLayout.createSequentialGroup()
                .addComponent(ProfileButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(EnrolledScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                .addContainerGap())
        );

        MainPagePanel.add(EnrolledPanel, "Card3");

        FeePanel.setOpaque(false);

        ProfileButton3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ProfileButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/main/resources/boy.png"))); // NOI18N
        ProfileButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ProfileButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ProfileButton3MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                ProfileButton3MouseExited(evt);
            }
        });

        FeeTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Year", "Session", "Total Fee", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        FeeTable.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        FeeTable.setRowHeight(50);
        FeeTable.setRowSelectionAllowed(false);
        FeeTable.setShowGrid(true);
        FeeTable.getTableHeader().setResizingAllowed(false);
        FeeTable.getTableHeader().setReorderingAllowed(false);
        FeeTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                FeeTableMouseClicked(evt);
            }
        });
        FeeScrollPane.setViewportView(FeeTable);

        javax.swing.GroupLayout FeePanelLayout = new javax.swing.GroupLayout(FeePanel);
        FeePanel.setLayout(FeePanelLayout);
        FeePanelLayout.setHorizontalGroup(
            FeePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, FeePanelLayout.createSequentialGroup()
                .addGap(0, 663, Short.MAX_VALUE)
                .addComponent(ProfileButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(FeePanelLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(FeeScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 625, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        FeePanelLayout.setVerticalGroup(
            FeePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FeePanelLayout.createSequentialGroup()
                .addComponent(ProfileButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(FeeScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE)
                .addContainerGap())
        );

        MainPagePanel.add(FeePanel, "Card4");

        AttendancePanel.setOpaque(false);

        ProfileButton4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ProfileButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/main/resources/boy.png"))); // NOI18N
        ProfileButton4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ProfileButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ProfileButton4MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                ProfileButton4MouseExited(evt);
            }
        });

        AttendanceTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Course Name", "Present", "Absent", "Percentage"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        AttendanceTable.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        AttendanceTable.setRowHeight(50);
        AttendanceTable.setRowSelectionAllowed(false);
        AttendanceTable.setShowGrid(true);
        AttendanceTable.getTableHeader().setResizingAllowed(false);
        AttendanceTable.getTableHeader().setReorderingAllowed(false);
        AttendanceTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                AttendanceTableMouseClicked(evt);
            }
        });
        AttendanceScrollPane.setViewportView(AttendanceTable);

        javax.swing.GroupLayout AttendancePanelLayout = new javax.swing.GroupLayout(AttendancePanel);
        AttendancePanel.setLayout(AttendancePanelLayout);
        AttendancePanelLayout.setHorizontalGroup(
            AttendancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AttendancePanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(ProfileButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AttendancePanelLayout.createSequentialGroup()
                .addContainerGap(40, Short.MAX_VALUE)
                .addComponent(AttendanceScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 625, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40))
        );
        AttendancePanelLayout.setVerticalGroup(
            AttendancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AttendancePanelLayout.createSequentialGroup()
                .addComponent(ProfileButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AttendanceScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 536, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        MainPagePanel.add(AttendancePanel, "Card5");

        ResultPanel.setOpaque(false);

        ProfileButton5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ProfileButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/main/resources/boy.png"))); // NOI18N
        ProfileButton5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ProfileButton5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ProfileButton5MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                ProfileButton5MouseExited(evt);
            }
        });

        ResultTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Year", "Session", "GPA", "CGPA"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ResultTable.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ResultTable.setRowHeight(50);
        ResultTable.setRowSelectionAllowed(false);
        ResultTable.setShowGrid(true);
        ResultTable.getTableHeader().setResizingAllowed(false);
        ResultTable.getTableHeader().setReorderingAllowed(false);
        ResultTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ResultTableMouseClicked(evt);
            }
        });
        ResultScrollPane.setViewportView(ResultTable);

        javax.swing.GroupLayout ResultPanelLayout = new javax.swing.GroupLayout(ResultPanel);
        ResultPanel.setLayout(ResultPanelLayout);
        ResultPanelLayout.setHorizontalGroup(
            ResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ResultPanelLayout.createSequentialGroup()
                .addGap(0, 663, Short.MAX_VALUE)
                .addComponent(ProfileButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ResultPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ResultScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 625, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40))
        );
        ResultPanelLayout.setVerticalGroup(
            ResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ResultPanelLayout.createSequentialGroup()
                .addComponent(ProfileButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ResultScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 536, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        MainPagePanel.add(ResultPanel, "Card6");

        NotificationPanel.setOpaque(false);

        ProfileButton6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ProfileButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/main/resources/boy.png"))); // NOI18N
        ProfileButton6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ProfileButton6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ProfileButton6MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                ProfileButton6MouseExited(evt);
            }
        });

        NotificationTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Title", "Message", "Date"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        NotificationTable.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        NotificationTable.setRowHeight(50);
        NotificationTable.setRowSelectionAllowed(false);
        NotificationTable.setShowGrid(true);
        NotificationTable.getTableHeader().setResizingAllowed(false);
        NotificationTable.getTableHeader().setReorderingAllowed(false);
        NotificationTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                NotificationTableMouseClicked(evt);
            }
        });
        NotificationScrollPane.setViewportView(NotificationTable);

        javax.swing.GroupLayout NotificationPanelLayout = new javax.swing.GroupLayout(NotificationPanel);
        NotificationPanel.setLayout(NotificationPanelLayout);
        NotificationPanelLayout.setHorizontalGroup(
            NotificationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, NotificationPanelLayout.createSequentialGroup()
                .addGap(0, 663, Short.MAX_VALUE)
                .addComponent(ProfileButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(NotificationPanelLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(NotificationScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 625, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        NotificationPanelLayout.setVerticalGroup(
            NotificationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NotificationPanelLayout.createSequentialGroup()
                .addComponent(ProfileButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(NotificationScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 536, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        MainPagePanel.add(NotificationPanel, "Card7");

        javax.swing.GroupLayout HomePageStudentPanelLayout = new javax.swing.GroupLayout(HomePageStudentPanel);
        HomePageStudentPanel.setLayout(HomePageStudentPanelLayout);
        HomePageStudentPanelLayout.setHorizontalGroup(
            HomePageStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HomePageStudentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(SidePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(MainPagePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 705, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        HomePageStudentPanelLayout.setVerticalGroup(
            HomePageStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HomePageStudentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(HomePageStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(HomePageStudentPanelLayout.createSequentialGroup()
                        .addComponent(SidePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 588, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(MainPagePanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(HomePageStudentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(HomePageStudentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void ProfileMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ProfileMouseClicked
        // TODO add your handling code here:
        CardLayout c1 = (CardLayout)(MainPagePanel.getLayout());
        c1.show(MainPagePanel,"Card1");
        setActiveTab(Profile);
    }//GEN-LAST:event_ProfileMouseClicked

    private void ProfileKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ProfileKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER) {
            ProfileMouseClicked(null);
        }
    }//GEN-LAST:event_ProfileKeyPressed

    private void EnrollCourseMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EnrollCourseMouseClicked
        // TODO add your handling code here:
        CardLayout c1 = (CardLayout)(MainPagePanel.getLayout());
        c1.show(MainPagePanel,"Card2");
        setActiveTab(EnrollCourse);
        show_Table("course", EnrollTable);
    }//GEN-LAST:event_EnrollCourseMouseClicked

    private void EnrollCourseKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_EnrollCourseKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER) {
            EnrollCourseMouseClicked(null);
        }
    }//GEN-LAST:event_EnrollCourseKeyPressed

    private void EnrolledCoursesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EnrolledCoursesMouseClicked
        // TODO add your handling code here:
        CardLayout c1 = (CardLayout)(MainPagePanel.getLayout());
        c1.show(MainPagePanel,"Card3");
        setActiveTab(EnrolledCourses);
        show_Table("enrollment", EnrolledTable);
    }//GEN-LAST:event_EnrolledCoursesMouseClicked

    private void EnrolledCoursesKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_EnrolledCoursesKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER) {
            EnrolledCoursesMouseClicked(null);
        }
    }//GEN-LAST:event_EnrolledCoursesKeyPressed

    private void FeeSummaryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_FeeSummaryMouseClicked
        // TODO add your handling code here:
        CardLayout c1 = (CardLayout)(MainPagePanel.getLayout());
        c1.show(MainPagePanel,"Card4");
        setActiveTab(FeeSummary);
        showFee();
    }//GEN-LAST:event_FeeSummaryMouseClicked

    private void FeeSummaryKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_FeeSummaryKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER) {
            FeeSummaryMouseClicked(null);
        }
    }//GEN-LAST:event_FeeSummaryKeyPressed

    private void MyAttendanceMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MyAttendanceMouseClicked
        // TODO add your handling code here:
        CardLayout c1 = (CardLayout)(MainPagePanel.getLayout());
        c1.show(MainPagePanel,"Card5");
        setActiveTab(MyAttendance);
        showAttendance();
    }//GEN-LAST:event_MyAttendanceMouseClicked

    private void MyAttendanceKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_MyAttendanceKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER) {
            MyAttendanceMouseClicked(null);
        }
    }//GEN-LAST:event_MyAttendanceKeyPressed

    private void MyResultsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MyResultsMouseClicked
        // TODO add your handling code here:
        CardLayout c1 = (CardLayout)(MainPagePanel.getLayout());
        c1.show(MainPagePanel,"Card6");
        setActiveTab(MyResults);
        showResults();
    }//GEN-LAST:event_MyResultsMouseClicked

    private void MyResultsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_MyResultsKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER) {
            MyResultsMouseClicked(null);
        }
    }//GEN-LAST:event_MyResultsKeyPressed

    private void MyNotificationsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MyNotificationsMouseClicked
        // TODO add your handling code here:
        CardLayout c1 = (CardLayout)(MainPagePanel.getLayout());
        c1.show(MainPagePanel,"Card7");
        setActiveTab(MyNotifications);
        loadNotifications();
    }//GEN-LAST:event_MyNotificationsMouseClicked

    private void MyNotificationsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_MyNotificationsKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER) {
            MyNotificationsMouseClicked(null);
        }
    }//GEN-LAST:event_MyNotificationsKeyPressed

    private void LogOutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LogOutMouseClicked
        // TODO add your handling code here:
        new Timer().schedule(new TimerTask() {
            public void run() {
                new LogIn().setVisible(true);
            }
        }, 1000);
        this.dispose();
    }//GEN-LAST:event_LogOutMouseClicked

    private void LogOutKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_LogOutKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER) {
            LogOutMouseClicked(null);
        }
    }//GEN-LAST:event_LogOutKeyPressed

    private void ProfileButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ProfileButtonMouseEntered
        // TODO add your handling code here:
        showProfilePopup(ProfileButton, evt);
    }//GEN-LAST:event_ProfileButtonMouseEntered

    private void ProfileButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ProfileButtonMouseExited
        // TODO add your handling code here:
        if (infoPopup != null) {
            infoPopup.setVisible(false);
        }
    }//GEN-LAST:event_ProfileButtonMouseExited

    private void ProfilePicMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ProfilePicMouseClicked
        // TODO add your handling code here:
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Profile Picture");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png"));
        
        int result = fileChooser.showOpenDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                FileInputStream fis = new FileInputStream(selectedFile);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buf = new byte[4096];
                int readNum;
                while ((readNum = fis.read(buf)) != -1) {
                    bos.write(buf, 0, readNum);
                }
                byte[] imageBytes = bos.toByteArray();
                
                saveImageToDatabase(imageBytes);
                
                displayImageFromBytes(imageBytes);
                
                DataUpdated dataupdated = new DataUpdated();
                dataupdated.setVisible(true);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        dataupdated.dispose();
                    }
                }, 1000);
                
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error processing image file: " + ex.getMessage());
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database Error during image upload: " + ex.getMessage());
            }
        }
    }//GEN-LAST:event_ProfilePicMouseClicked

    private void ProfilePicKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ProfilePicKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            ProfilePicMouseClicked(null);
        }
    }//GEN-LAST:event_ProfilePicKeyPressed

    private void ProfileButton1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ProfileButton1MouseEntered
        // TODO add your handling code here:
        showProfilePopup(ProfileButton1, evt);
    }//GEN-LAST:event_ProfileButton1MouseEntered

    private void ProfileButton1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ProfileButton1MouseExited
        // TODO add your handling code here:
        if (infoPopup != null) {
            infoPopup.setVisible(false);
        }
    }//GEN-LAST:event_ProfileButton1MouseExited

    private void EnrollTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EnrollTableMouseClicked
        // TODO add your handling code here:
        int row = EnrollTable.rowAtPoint(evt.getPoint());
        int col = EnrollTable.columnAtPoint(evt.getPoint());
        if(row >= 0 && col == 4){
            String courseCode = (String)EnrollTable.getModel().getValueAt(row, 0);
            enrollCourse(courseCode);
        }
    }//GEN-LAST:event_EnrollTableMouseClicked

    private void ProfileButton2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ProfileButton2MouseEntered
        // TODO add your handling code here:
        showProfilePopup(ProfileButton2, evt);
    }//GEN-LAST:event_ProfileButton2MouseEntered

    private void ProfileButton2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ProfileButton2MouseExited
        // TODO add your handling code here:
        if (infoPopup != null) {
            infoPopup.setVisible(false);
        }
    }//GEN-LAST:event_ProfileButton2MouseExited

    private void EnrolledTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EnrolledTableMouseClicked
        // TODO add your handling code here:
        int row = EnrollTable.rowAtPoint(evt.getPoint());
        int col = EnrollTable.columnAtPoint(evt.getPoint());
        if(row >= 0 && col == 4){
            String courseCode = (String)EnrolledTable.getModel().getValueAt(row, 0);
            deleteEnrollment(courseCode);
        }
    }//GEN-LAST:event_EnrolledTableMouseClicked

    private void ProfileButton3MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ProfileButton3MouseEntered
        // TODO add your handling code here:
        showProfilePopup(ProfileButton3, evt);
    }//GEN-LAST:event_ProfileButton3MouseEntered

    private void ProfileButton3MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ProfileButton3MouseExited
        // TODO add your handling code here:
        if (infoPopup != null) {
            infoPopup.setVisible(false);
        }
    }//GEN-LAST:event_ProfileButton3MouseExited

    private void FeeTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_FeeTableMouseClicked
        // TODO add your handling code here:
        int row = FeeTable.getSelectedRow();
        if (row != -1) {
            String year = FeeTable.getValueAt(row, 0).toString();
            String session = FeeTable.getValueAt(row, 1).toString();
            String amount = FeeTable.getValueAt(row, 2).toString();
            String status = FeeTable.getValueAt(row, 3).toString();
            
            boolean confirmed = false;
            String message = "Download Fee Voucher for " + session + " " + year + "?";
            CustomConfirmDialog customDialog = new CustomConfirmDialog(this, "Download Transcript", message);
            customDialog.setVisible(true);
            confirmed = customDialog.isConfirmed();
            if (confirmed == true) {
                downloadFeeVoucherPDF(year, session, amount, status);
            }
        }
    }//GEN-LAST:event_FeeTableMouseClicked

    private void ProfileButton4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ProfileButton4MouseEntered
        // TODO add your handling code here:
        showProfilePopup(ProfileButton4, evt);
    }//GEN-LAST:event_ProfileButton4MouseEntered

    private void ProfileButton4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ProfileButton4MouseExited
        // TODO add your handling code here:
        if (infoPopup != null) {
            infoPopup.setVisible(false);
        }
    }//GEN-LAST:event_ProfileButton4MouseExited

    private void AttendanceTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AttendanceTableMouseClicked
        // TODO add your handling code here:
        int row = AttendanceTable.getSelectedRow();
        if (row != -1) {
            String courseName = AttendanceTable.getValueAt(row, 0).toString();
            
            try {
                PreparedStatement ps = connect.prepareStatement("SELECT coursecode FROM course WHERE coursename = ?");
                ps.setString(1, courseName);
                ResultSet rs = ps.executeQuery();
                if(rs.next()) {
                    showDetailedAttendance(rs.getString("coursecode"));
                }
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }//GEN-LAST:event_AttendanceTableMouseClicked

    private void ProfileButton5MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ProfileButton5MouseEntered
        // TODO add your handling code here:
        showProfilePopup(ProfileButton5, evt);
    }//GEN-LAST:event_ProfileButton5MouseEntered

    private void ProfileButton5MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ProfileButton5MouseExited
        // TODO add your handling code here:
        if (infoPopup != null) {
            infoPopup.setVisible(false);
        }
    }//GEN-LAST:event_ProfileButton5MouseExited

    private void ResultTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ResultTableMouseClicked
        // TODO add your handling code here:
        int row = ResultTable.getSelectedRow();
        if (row != -1) {
            String year = ResultTable.getValueAt(row, 0).toString();
            String session = ResultTable.getValueAt(row, 1).toString();
            boolean confirmed = false;
            String message = "Would you like to download the transcript as PDF?";
            CustomConfirmDialog customDialog = new CustomConfirmDialog(this, "Download Transcript", message);
            customDialog.setVisible(true);
            confirmed = customDialog.isConfirmed();
            if (confirmed == true) {
                downloadTranscriptPDF(year, session);
            } else {
                showDetailedResults(year, session);
            }
        }
    }//GEN-LAST:event_ResultTableMouseClicked

    private void ProfileButton6MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ProfileButton6MouseEntered
        // TODO add your handling code here:
        showProfilePopup(ProfileButton6, evt);
    }//GEN-LAST:event_ProfileButton6MouseEntered

    private void ProfileButton6MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ProfileButton6MouseExited
        // TODO add your handling code here:
        if (infoPopup != null) {
            infoPopup.setVisible(false);
        }
    }//GEN-LAST:event_ProfileButton6MouseExited

    private void NotificationTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_NotificationTableMouseClicked
        // TODO add your handling code here:
        int row = NotificationTable.getSelectedRow();
        if (row != -1) {
            // Get data from the selected row
            String title = NotificationTable.getValueAt(row, 0).toString();
            String message = NotificationTable.getValueAt(row, 1).toString();
            String date = NotificationTable.getValueAt(row, 2).toString();

            // Create a separate window (Dialog) to show the details
            showNotificationDetailWindow(title, message, date);
        }
    }//GEN-LAST:event_NotificationTableMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(HomePageStudent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HomePageStudent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HomePageStudent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HomePageStudent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HomePageStudent().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel AttendancePanel;
    private javax.swing.JScrollPane AttendanceScrollPane;
    private javax.swing.JTable AttendanceTable;
    private javax.swing.JLabel CNIC;
    private javax.swing.JLabel CNICField;
    private javax.swing.JLabel DOB;
    private javax.swing.JLabel DOBField;
    private javax.swing.JLabel DateTime;
    private javax.swing.JButton EnrollCourse;
    private javax.swing.JPanel EnrollPanel;
    private javax.swing.JScrollPane EnrollScrollPane;
    private javax.swing.JTable EnrollTable;
    private javax.swing.JButton EnrolledCourses;
    private javax.swing.JPanel EnrolledPanel;
    private javax.swing.JScrollPane EnrolledScrollPane;
    private javax.swing.JTable EnrolledTable;
    private javax.swing.JPanel FeePanel;
    private javax.swing.JScrollPane FeeScrollPane;
    private javax.swing.JButton FeeSummary;
    private javax.swing.JTable FeeTable;
    private javax.swing.JPanel HomePageStudentPanel;
    private javax.swing.JButton LogOut;
    private javax.swing.JPanel MainPagePanel;
    private javax.swing.JButton MyAttendance;
    private javax.swing.JButton MyNotifications;
    private javax.swing.JButton MyResults;
    private javax.swing.JLabel Name;
    private javax.swing.JLabel NameField;
    private javax.swing.JPanel NotificationPanel;
    private javax.swing.JScrollPane NotificationScrollPane;
    private javax.swing.JTable NotificationTable;
    private javax.swing.JLabel PhoneNo;
    private javax.swing.JLabel PhoneNoField;
    private javax.swing.JButton Profile;
    private javax.swing.JLabel ProfileButton;
    private javax.swing.JLabel ProfileButton1;
    private javax.swing.JLabel ProfileButton2;
    private javax.swing.JLabel ProfileButton3;
    private javax.swing.JLabel ProfileButton4;
    private javax.swing.JLabel ProfileButton5;
    private javax.swing.JLabel ProfileButton6;
    private javax.swing.JPanel ProfilePanel;
    private javax.swing.JLabel ProfilePic;
    private javax.swing.JLabel Program;
    private javax.swing.JLabel ProgramField;
    private javax.swing.JPanel ResultPanel;
    private javax.swing.JScrollPane ResultScrollPane;
    private javax.swing.JTable ResultTable;
    private javax.swing.JSeparator Separator;
    private javax.swing.JPanel SidePanel;
    // End of variables declaration//GEN-END:variables
}
