/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package main;

import java.awt.CardLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import java.sql.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author DELL
 */
public class HomePageInstructor extends javax.swing.JFrame {
    Connection connect = null;
    ResultSet result = null;
    PreparedStatement pst = null;
    
    String id;
    private HoverPopup infoPopup;
    public HomePageInstructor() {
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
        showInstructorTimetable();
    }
    
    public HomePageInstructor(String id) {
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
        String query = "SELECT `password` FROM `instructor` WHERE `ID` = ?";
        try {
            pst = connect.prepareStatement(query);
            pst.setString(1, id);
            
            result = pst.executeQuery();
            if(result.next()){
                String password = result.getString("password");
                if(password.equals("Employee123")){
                    new ChangePassword(id, "instructor").setVisible(true);
                }
            }
                
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,ex);
        }
        updateDateTime();
        startDateTimeUpdater();
        showInstructorTimetable();
    }
    
    private void updateDateTime() {
        // Format for time and date (e.g., 04:25:30 PM, Dec 09, 2025)
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss a, MMM dd, yyyy");
        Date date = new Date();
        // Set the formatted string to the JLabel
        DateTime.setText(formatter.format(date));
    }
    
    private void startDateTimeUpdater() {
        Timer timer = new Timer(true); // 'true' means it's a daemon thread
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateDateTime();
            }
        }, 0, 1000); // Start immediately (0 delay), repeat every 1000ms (1 second)
    }

    private void showProfilePopup(javax.swing.JLabel sourceButton, java.awt.event.MouseEvent evt) {
        String instructorName = "";
        String instructorEmail = "";
        String instructorDepartment = "";
        
        String query = "SELECT `fName`, `lName`, `email`, `department` FROM `instructor` WHERE `ID` = ?";
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
            infoPopup = new HoverPopup(HomePageInstructor.this, message);
        } else {
            infoPopup.updateMessage(message); 
        }

        Point buttonLocation = sourceButton.getLocationOnScreen();
        
        int popupX = buttonLocation.x + sourceButton.getWidth() - infoPopup.getWidth();
        
        int popupY = buttonLocation.y + sourceButton.getHeight();
        
        infoPopup.showAtLocation(popupX, popupY);
    }
    
    private void showInstructorTimetable() {
    
        String instructorFullName = "";
        try {
            pst = connect.prepareStatement("SELECT CONCAT(`fName`, ' ', `lName`) AS `fullName` FROM `instructor` WHERE `ID` = ?");
            pst.setString(1, this.id);
            result = pst.executeQuery();
            if (result.next()) {
                instructorFullName = result.getString("fullName");
            } else {
                JOptionPane.showMessageDialog(this, "Instructor ID not found in the database.", "Data Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error fetching instructor name: " + ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DefaultTableModel DTM = (DefaultTableModel)ViewCoursesTable.getModel();
        DTM.setRowCount(0);

        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        String timetableQuery = "SELECT `day`, `sTime`, `eTime`, `cName`, `roomNo` FROM `timetable` WHERE `instructorName` = ? AND `day` = ? ORDER BY `sTime`";

        try {
            boolean dayHeaderAdded = false; 

            for (String currentDay : daysOfWeek) {
                pst = connect.prepareStatement(timetableQuery);
                pst.setString(1, instructorFullName);
                pst.setString(2, currentDay); 
                result = pst.executeQuery();

                dayHeaderAdded = false;

                if (result.isBeforeFirst()) { 
                    while (result.next()) {
                        String startTime = result.getString("sTime");
                        String endTime = result.getString("eTime");
                        String timeSlot = startTime + " - " + endTime;

                        String dayToDisplay = dayHeaderAdded ? "" : currentDay;

                        DTM.addRow(new Object[]{
                            dayToDisplay, 
                            result.getString("cName"),
                            timeSlot, 
                            result.getString("roomNo")
                        });

                        dayHeaderAdded = true; 
                    }
                }

                if (!dayHeaderAdded) {
                    DTM.addRow(new Object[]{
                        currentDay, 
                        "Free", 
                        "N/A",
                        "N/A"
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error fetching timetable: " + ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
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

        HomePageInstuctorPanel = new ui.GradientPanel();
        SidePanel = new ui.GlassmorphismPanel();
        DateTime = new javax.swing.JLabel();
        ViewCourses = new RoundedButton();
        MarkAttendance = new RoundedButton();
        UploadResult = new RoundedButton();
        LogOut = new RoundedButton();
        MainPagePanel = new javax.swing.JPanel();
        ViewCoursesPanel = new javax.swing.JPanel();
        ProfileButton = new javax.swing.JLabel();
        ViewCoursesScrollPane = new javax.swing.JScrollPane();
        ViewCoursesTable = new RoundedTable();
        AttendancePanel = new javax.swing.JPanel();
        ProfileButton1 = new javax.swing.JLabel();
        ResultsPanel = new javax.swing.JPanel();
        ProfileButton2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        HomePageInstuctorPanel.setPreferredSize(new java.awt.Dimension(923, 600));

        SidePanel.setOpaque(false);
        SidePanel.setPreferredSize(new java.awt.Dimension(200, 600));

        DateTime.setFont(new java.awt.Font("Bodoni MT", 1, 12)); // NOI18N
        DateTime.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        DateTime.setText("04:25:30 PM, Dec 09, 2025");

        ViewCourses.setBackground(new java.awt.Color(151, 137, 219));
        ViewCourses.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        ViewCourses.setText("View Courses");
        ViewCourses.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ViewCourses.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ViewCoursesMouseClicked(evt);
            }
        });
        ViewCourses.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ViewCoursesKeyPressed(evt);
            }
        });

        MarkAttendance.setBackground(new java.awt.Color(151, 137, 219));
        MarkAttendance.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        MarkAttendance.setText("Attendance");
        MarkAttendance.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        MarkAttendance.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                MarkAttendanceMouseClicked(evt);
            }
        });
        MarkAttendance.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                MarkAttendanceKeyPressed(evt);
            }
        });

        UploadResult.setBackground(new java.awt.Color(151, 137, 219));
        UploadResult.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        UploadResult.setText("Results");
        UploadResult.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        UploadResult.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                UploadResultMouseClicked(evt);
            }
        });
        UploadResult.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                UploadResultKeyPressed(evt);
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
                .addGap(30, 30, 30)
                .addGroup(SidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(DateTime)
                    .addComponent(ViewCourses, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(UploadResult, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(MarkAttendance, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LogOut, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        SidePanelLayout.setVerticalGroup(
            SidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SidePanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(DateTime)
                .addGap(50, 50, 50)
                .addComponent(ViewCourses)
                .addGap(50, 50, 50)
                .addComponent(MarkAttendance)
                .addGap(50, 50, 50)
                .addComponent(UploadResult)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 263, Short.MAX_VALUE)
                .addComponent(LogOut)
                .addGap(20, 20, 20))
        );

        MainPagePanel.setOpaque(false);
        MainPagePanel.setPreferredSize(new java.awt.Dimension(100, 600));
        MainPagePanel.setLayout(new java.awt.CardLayout());

        ViewCoursesPanel.setOpaque(false);
        ViewCoursesPanel.setPreferredSize(new java.awt.Dimension(720, 594));

        ProfileButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ProfileButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/main/resources/profile.png"))); // NOI18N
        ProfileButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ProfileButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ProfileButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                ProfileButtonMouseExited(evt);
            }
        });

        ViewCoursesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Day", "Course", "Time", "Room No"
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
        ViewCoursesTable.setRowHeight(50);
        ViewCoursesTable.setRowSelectionAllowed(false);
        ViewCoursesTable.setShowGrid(true);
        ViewCoursesTable.getTableHeader().setResizingAllowed(false);
        ViewCoursesTable.getTableHeader().setReorderingAllowed(false);
        ViewCoursesScrollPane.setViewportView(ViewCoursesTable);

        javax.swing.GroupLayout ViewCoursesPanelLayout = new javax.swing.GroupLayout(ViewCoursesPanel);
        ViewCoursesPanel.setLayout(ViewCoursesPanelLayout);
        ViewCoursesPanelLayout.setHorizontalGroup(
            ViewCoursesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ViewCoursesPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(ProfileButton, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(ViewCoursesPanelLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(ViewCoursesScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 625, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(40, Short.MAX_VALUE))
        );
        ViewCoursesPanelLayout.setVerticalGroup(
            ViewCoursesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ViewCoursesPanelLayout.createSequentialGroup()
                .addComponent(ProfileButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ViewCoursesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                .addContainerGap())
        );

        MainPagePanel.add(ViewCoursesPanel, "Card1");

        AttendancePanel.setOpaque(false);
        AttendancePanel.setPreferredSize(new java.awt.Dimension(720, 594));

        ProfileButton1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ProfileButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/main/resources/profile.png"))); // NOI18N
        ProfileButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ProfileButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ProfileButton1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                ProfileButton1MouseExited(evt);
            }
        });

        javax.swing.GroupLayout AttendancePanelLayout = new javax.swing.GroupLayout(AttendancePanel);
        AttendancePanel.setLayout(AttendancePanelLayout);
        AttendancePanelLayout.setHorizontalGroup(
            AttendancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AttendancePanelLayout.createSequentialGroup()
                .addGap(0, 663, Short.MAX_VALUE)
                .addComponent(ProfileButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        AttendancePanelLayout.setVerticalGroup(
            AttendancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AttendancePanelLayout.createSequentialGroup()
                .addComponent(ProfileButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 548, Short.MAX_VALUE))
        );

        MainPagePanel.add(AttendancePanel, "Card2");

        ResultsPanel.setOpaque(false);
        ResultsPanel.setPreferredSize(new java.awt.Dimension(720, 594));

        ProfileButton2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ProfileButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/main/resources/profile.png"))); // NOI18N
        ProfileButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ProfileButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ProfileButton2MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                ProfileButton2MouseExited(evt);
            }
        });

        javax.swing.GroupLayout ResultsPanelLayout = new javax.swing.GroupLayout(ResultsPanel);
        ResultsPanel.setLayout(ResultsPanelLayout);
        ResultsPanelLayout.setHorizontalGroup(
            ResultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ResultsPanelLayout.createSequentialGroup()
                .addGap(0, 663, Short.MAX_VALUE)
                .addComponent(ProfileButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        ResultsPanelLayout.setVerticalGroup(
            ResultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ResultsPanelLayout.createSequentialGroup()
                .addComponent(ProfileButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 548, Short.MAX_VALUE))
        );

        MainPagePanel.add(ResultsPanel, "Card3");

        javax.swing.GroupLayout HomePageInstuctorPanelLayout = new javax.swing.GroupLayout(HomePageInstuctorPanel);
        HomePageInstuctorPanel.setLayout(HomePageInstuctorPanelLayout);
        HomePageInstuctorPanelLayout.setHorizontalGroup(
            HomePageInstuctorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HomePageInstuctorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(SidePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(MainPagePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 705, Short.MAX_VALUE)
                .addContainerGap())
        );
        HomePageInstuctorPanelLayout.setVerticalGroup(
            HomePageInstuctorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HomePageInstuctorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(HomePageInstuctorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(MainPagePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
                    .addComponent(SidePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(HomePageInstuctorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(HomePageInstuctorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void ViewCoursesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ViewCoursesMouseClicked
        // TODO add your handling code here:
        CardLayout c1 = (CardLayout)(MainPagePanel.getLayout());
        c1.show(MainPagePanel,"Card1");
        showInstructorTimetable();
    }//GEN-LAST:event_ViewCoursesMouseClicked

    private void ViewCoursesKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ViewCoursesKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER) {
            ViewCoursesMouseClicked(null);
        }
    }//GEN-LAST:event_ViewCoursesKeyPressed

    private void MarkAttendanceMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MarkAttendanceMouseClicked
        // TODO add your handling code here:
        CardLayout c1 = (CardLayout)(MainPagePanel.getLayout());
        c1.show(MainPagePanel,"Card2");
    }//GEN-LAST:event_MarkAttendanceMouseClicked

    private void MarkAttendanceKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_MarkAttendanceKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER) {
            MarkAttendanceMouseClicked(null);
        }
    }//GEN-LAST:event_MarkAttendanceKeyPressed

    private void UploadResultMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_UploadResultMouseClicked
        // TODO add your handling code here:
        CardLayout c1 = (CardLayout)(MainPagePanel.getLayout());
        c1.show(MainPagePanel,"Card3");
    }//GEN-LAST:event_UploadResultMouseClicked

    private void UploadResultKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_UploadResultKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER) {
            UploadResultMouseClicked(null);
        }
    }//GEN-LAST:event_UploadResultKeyPressed

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
        LogOutMouseClicked(null);
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
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(HomePageInstructor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HomePageInstructor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HomePageInstructor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HomePageInstructor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HomePageInstructor().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel AttendancePanel;
    private javax.swing.JLabel DateTime;
    private javax.swing.JPanel HomePageInstuctorPanel;
    private javax.swing.JButton LogOut;
    private javax.swing.JPanel MainPagePanel;
    private javax.swing.JButton MarkAttendance;
    private javax.swing.JLabel ProfileButton;
    private javax.swing.JLabel ProfileButton1;
    private javax.swing.JLabel ProfileButton2;
    private javax.swing.JPanel ResultsPanel;
    private javax.swing.JPanel SidePanel;
    private javax.swing.JButton UploadResult;
    private javax.swing.JButton ViewCourses;
    private javax.swing.JPanel ViewCoursesPanel;
    private javax.swing.JScrollPane ViewCoursesScrollPane;
    private javax.swing.JTable ViewCoursesTable;
    // End of variables declaration//GEN-END:variables
}
