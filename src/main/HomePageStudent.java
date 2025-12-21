/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package main;

import java.awt.CardLayout;
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
        updateDateTime();
        startDateTimeUpdater();
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
        updateDateTime();
        startDateTimeUpdater();
        loadAndDisplayImage();
        loadStudentDetails();
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
        String enrollmentQuery = "SELECT c.coursecode, c.coursename, c.credithrs, e.enrollment_date "
                               + "FROM `enrollment` e JOIN `course` c "
                               + "ON e.coursecode = c.coursecode "
                               + "WHERE e.student_id = ?";
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
            try {
                pst = connect.prepareStatement(enrollmentQuery);
                pst.setString(1, this.id);
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
                        v2.add(result.getString("enrollment_date"));
                    }
                    DTM.addRow(v2);
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
    private void enrollCourse(String courseCode){
        String checkQuery = "SELECT COUNT(*) FROM `enrollment` WHERE `student_id` = ? AND `coursecode` = ?";
        String query = "INSERT INTO `enrollment`(`student_id`, `coursecode`, `enrollment_date`) VALUES (?, ?, NOW())";
        try {
            pst = connect.prepareStatement(checkQuery);
            pst.setString(1, this.id);
            pst.setString(2, courseCode);
            
            result =  pst.executeQuery();
            if(result.next() && result.getInt(1) > 0){
                String failedMessage = "Already Enrolled";
                CustomMessageDialog messageDialog = new CustomMessageDialog(this, "Failed", failedMessage, CustomMessageDialog.ERROR);
                messageDialog.setVisible(true);
                return;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,ex);
        }
        
        // Insert Enrollment
        try {
            pst = connect.prepareStatement(query);
            pst.setString(1, this.id);
            pst.setString(2, courseCode);
            
            pst.execute();
            String successMessage = "Successfully Enrolled: " + courseCode;
            CustomMessageDialog messageDialog = new CustomMessageDialog(this, "Success", successMessage, CustomMessageDialog.SUCCESS);
            messageDialog.setVisible(true);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,ex);
        }
    }
    
    // Drop Course
    private void deleteEnrollment(String courseCode) {
        String message = "Are you sure you want to drop course " + courseCode + "?";

        CustomConfirmDialog dialog = new CustomConfirmDialog(this, "Confirm Course Drop", message);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            String deleteQuery = "DELETE FROM `enrollment` WHERE `student_id` = ? AND `coursecode` = ?";

            try {
                pst = connect.prepareStatement(deleteQuery);
                pst.setString(1, this.id);
                pst.setString(2, courseCode);

                int rowsAffected = pst.executeUpdate();

                if (rowsAffected > 0) {
                    String successMessage = "Successfully Dropped Course: " + courseCode;
                    CustomMessageDialog messageDialog = new CustomMessageDialog(this, "Success", successMessage, CustomMessageDialog.SUCCESS);
                    messageDialog.setVisible(true);
                    show_Table("enrollment", EnrolledTable);
                } else {
                    String failedMessage = "Enrollment not found";
                    CustomMessageDialog messageDialog = new CustomMessageDialog(this, "Failed", failedMessage, CustomMessageDialog.ERROR);
                    messageDialog.setVisible(true);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Database Error: " + ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
            }
        }
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

        EnrollCourse.setBackground(new java.awt.Color(151, 137, 219));
        EnrollCourse.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        EnrollCourse.setText("Enroll Course");
        EnrollCourse.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        EnrollCourse.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                EnrollCourseMouseClicked(evt);
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

        FeeSummary.setBackground(new java.awt.Color(151, 137, 219));
        FeeSummary.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        FeeSummary.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        FeeSummary.setLabel("Fee Summary");

        MyAttendance.setBackground(new java.awt.Color(151, 137, 219));
        MyAttendance.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        MyAttendance.setText("My Attendance");
        MyAttendance.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        MyResults.setBackground(new java.awt.Color(151, 137, 219));
        MyResults.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        MyResults.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        MyResults.setLabel("My Results");

        MyNotifications.setBackground(new java.awt.Color(151, 137, 219));
        MyNotifications.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        MyNotifications.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        MyNotifications.setLabel("Notifications");

        LogOut.setBackground(new java.awt.Color(151, 137, 219));
        LogOut.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        LogOut.setText("LogOut");
        LogOut.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

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

    private void ProfileMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ProfileMouseClicked
        // TODO add your handling code here:
        CardLayout c1 = (CardLayout)(MainPagePanel.getLayout());
        c1.show(MainPagePanel,"Card1");
    }//GEN-LAST:event_ProfileMouseClicked

    private void EnrollCourseMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EnrollCourseMouseClicked
        // TODO add your handling code here:
        CardLayout c1 = (CardLayout)(MainPagePanel.getLayout());
        c1.show(MainPagePanel,"Card2");
        show_Table("course", EnrollTable);
    }//GEN-LAST:event_EnrollCourseMouseClicked

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

    private void EnrolledCoursesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EnrolledCoursesMouseClicked
        // TODO add your handling code here:
        CardLayout c1 = (CardLayout)(MainPagePanel.getLayout());
        c1.show(MainPagePanel,"Card3");
        show_Table("enrollment", EnrolledTable);
    }//GEN-LAST:event_EnrolledCoursesMouseClicked

    private void EnrolledTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EnrolledTableMouseClicked
        // TODO add your handling code here:
        int row = EnrollTable.rowAtPoint(evt.getPoint());
        int col = EnrollTable.columnAtPoint(evt.getPoint());
        if(row >= 0 && col == 4){
            String courseCode = (String)EnrollTable.getModel().getValueAt(row, 0);
            deleteEnrollment(courseCode);
        }
    }//GEN-LAST:event_EnrolledTableMouseClicked

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
    private javax.swing.JButton FeeSummary;
    private javax.swing.JPanel HomePageStudentPanel;
    private javax.swing.JButton LogOut;
    private javax.swing.JPanel MainPagePanel;
    private javax.swing.JButton MyAttendance;
    private javax.swing.JButton MyNotifications;
    private javax.swing.JButton MyResults;
    private javax.swing.JLabel Name;
    private javax.swing.JLabel NameField;
    private javax.swing.JLabel PhoneNo;
    private javax.swing.JLabel PhoneNoField;
    private javax.swing.JButton Profile;
    private javax.swing.JLabel ProfileButton;
    private javax.swing.JLabel ProfileButton1;
    private javax.swing.JLabel ProfileButton2;
    private javax.swing.JPanel ProfilePanel;
    private javax.swing.JLabel ProfilePic;
    private javax.swing.JLabel Program;
    private javax.swing.JLabel ProgramField;
    private javax.swing.JSeparator Separator;
    private javax.swing.JPanel SidePanel;
    // End of variables declaration//GEN-END:variables
}
