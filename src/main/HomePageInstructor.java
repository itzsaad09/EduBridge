/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package main;

import com.toedter.calendar.IDateEvaluator;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import java.sql.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss a, MMM dd, yyyy");
        Date date = new Date();
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
        String instructorFullName = null;
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

    private void loadAttendanceForSelectedDate() {
        String selectedCourse = (String) CourseCombo.getSelectedItem();
        java.util.Date selectedDate = SelectDate.getDate();

        if (selectedCourse == null || selectedCourse.equals("Select Course") || selectedDate == null) {
            return;
        }

        DefaultTableModel model = (DefaultTableModel) AttendanceTable.getModel();
        String[] options = {"Select Attendance","Present", "Absent"};
        javax.swing.JComboBox<String> editorCombo = new RoundedComboBox<>(options);

        AttendanceTable.getColumnModel().getColumn(2).setCellEditor(new javax.swing.DefaultCellEditor(editorCombo));
        
        AttendanceTable.getColumnModel().getColumn(2).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                // Create a temporary combo box just for painting the cell
                javax.swing.JComboBox<String> renderCombo = new RoundedComboBox<>(options);
                renderCombo.setSelectedItem(value);
                
                // Match the table's selection colors
                if (isSelected) {
                    renderCombo.setBackground(table.getSelectionBackground());
                } else {
                    renderCombo.setBackground(table.getBackground());
                }
                return renderCombo;
            }
        });
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateKey = sdf.format(selectedDate);
        
        try {
            // Query remains the same
            String query = "SELECT s.fName, s.lName, aj.log " +
                        "FROM student s " +
                        "JOIN enrollment e ON s.ID = e.student_id " +
                        "LEFT JOIN attendance_json aj ON s.ID = aj.student_id AND e.coursecode = aj.coursecode " +
                        "WHERE e.coursecode = (SELECT coursecode FROM course WHERE coursename = ?)";
            
            pst = connect.prepareStatement(query);
            pst.setString(1, selectedCourse);
            result = pst.executeQuery();
            
            model.setRowCount(0);
            while (result.next()) {
                String fullName = result.getString("fName") + " " + result.getString("lName");
                String jsonLog = result.getString("log");
                
                // Default value if no record exists for this date
                String status = "Select Attendance"; 
                
                if (jsonLog != null && !jsonLog.isEmpty()) {
                    // Check if the specific date exists in the JSON string
                    if (jsonLog.contains("\"" + dateKey + "\": \"Present\"")) {
                        status = "Present";
                    } else if (jsonLog.contains("\"" + dateKey + "\": \"Absent\"")) {
                        status = "Absent";
                    }
                }
                
                // Populate table
                model.addRow(new Object[]{
                    fullName, 
                    calculateTotalAbsences(jsonLog), 
                    status
                });
            }
        } catch (SQLException ex) {
            System.err.println("Error loading attendance: " + ex.getMessage());
        }
    }

    // Helper to keep absence count accurate
    private int calculateTotalAbsences(String jsonLog) {
        if (jsonLog == null || jsonLog.isEmpty()) return 0;
        return (jsonLog.length() - jsonLog.replace("Absent", "").length()) / "Absent".length();
    }
    
    private void restrictDateAndMultipleDays(String session, java.util.List<Integer> allowedDays) {
        com.toedter.calendar.JDayChooser dayChooser = SelectDate.getJCalendar().getDayChooser();

        // Clear previous custom evaluators using reflection (safe for jcalendar-1.4)
        try {
            java.lang.reflect.Field field = com.toedter.calendar.JDayChooser.class.getDeclaredField("dateEvaluators");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<IDateEvaluator> evaluators = (List<IDateEvaluator>) field.get(dayChooser);
            // Remove all except the built-in MinMaxDateEvaluator (index 0)
            while (evaluators.size() > 1) {
                evaluators.remove(1);
            }
        } catch (Exception e) {
            System.err.println("Could not clear old evaluators: " + e.getMessage());
        }

        // Set semester range
        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        int currentYear = cal.get(Calendar.YEAR);
        Date minDate, maxDate;

        if (session.equalsIgnoreCase("Fall")) {
            cal.set(currentYear, Calendar.OCTOBER, 1);
            minDate = cal.getTime();
            cal.set(currentYear + 1, Calendar.JANUARY, 31);
            Date semesterEnd = cal.getTime();
            maxDate = today.before(semesterEnd) ? today : semesterEnd;
        } else { // Spring
            cal.set(currentYear, Calendar.MARCH, 1);
            minDate = cal.getTime();
            cal.set(currentYear, Calendar.JUNE, 30);
            Date semesterEnd = cal.getTime();
            maxDate = today.before(semesterEnd) ? today : semesterEnd;
        }

        SelectDate.setMinSelectableDate(minDate);
        SelectDate.setMaxSelectableDate(maxDate);
        SelectDate.setSelectableDateRange(minDate, maxDate);

        dayChooser.addDateEvaluator(new IDateEvaluator() {
            @Override
            public boolean isSpecial(Date date) { return false; }

            @Override
            public Color getSpecialForegroundColor() { return null; }

            @Override
            public Color getSpecialBackroundColor() { return null; }

            @Override
            public String getInvalidTooltip() { return "No lecture on this day"; }

            @Override
            public boolean isInvalid(Date date) {
                if (date == null) return true;
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                return !allowedDays.contains(dayOfWeek);
            }

            @Override
            public String getSpecialTooltip() {
                return null;
            }

            @Override
            public Color getInvalidForegroundColor() {
                return Color.RED;
            }

            @Override
            public Color getInvalidBackroundColor() {
                return null;
            }
        });

        dayChooser.repaint();
    }

    private int getDayInteger(String day) {
        if (day == null) return -1;
        
        switch (day.trim().toLowerCase()) {
            case "sunday":    return java.util.Calendar.SUNDAY;    // 1
            case "monday":    return java.util.Calendar.MONDAY;    // 2
            case "tuesday":   return java.util.Calendar.TUESDAY;   // 3
            case "wednesday": return java.util.Calendar.WEDNESDAY; // 4
            case "thursday":  return java.util.Calendar.THURSDAY;  // 5
            case "friday":    return java.util.Calendar.FRIDAY;    // 6
            case "saturday":  return java.util.Calendar.SATURDAY;  // 7
            default: 
                System.out.println("Warning: Could not map day string: '" + day + "'");
                return -1;
        }
    }
    
    private void updateStudentAttendance(String studentId, String courseCode, String dateKey, String status) throws SQLException {
        // Check if record exists
        String selectQuery = "SELECT log FROM attendance_json WHERE student_id = ? AND coursecode = ?";
        pst = connect.prepareStatement(selectQuery);
        pst.setString(1, studentId);
        pst.setString(2, courseCode);
        result = pst.executeQuery();

        String currentLog = "";
        boolean exists = false;

        if (result.next()) {
            currentLog = result.getString("log");
            if (currentLog == null) currentLog = "";
            exists = true;
        }

        // Simple JSON manipulation (string-based for compatibility)
        String newEntry = "\"" + dateKey + "\": \"" + status + "\"";
        String updatedLog;

        if (currentLog.isEmpty() || currentLog.equals("{}")) {
            updatedLog = "{" + newEntry + "}";
        } else if (currentLog.contains("\"" + dateKey + "\"")) {
            // Replace existing date status using regex
            updatedLog = currentLog.replaceAll("\"" + dateKey + "\": \"[^\"]*\"", newEntry);
        } else {
            // Append to existing JSON
            updatedLog = currentLog.substring(0, currentLog.length() - 1) + ", " + newEntry + "}";
        }

        if (exists) {
            String updateSQL = "UPDATE attendance_json SET log = ? WHERE student_id = ? AND coursecode = ?";
            PreparedStatement pstUpd = connect.prepareStatement(updateSQL);
            pstUpd.setString(1, updatedLog);
            pstUpd.setString(2, studentId);
            pstUpd.setString(3, courseCode);
            pstUpd.executeUpdate();
        } else {
            String insertSQL = "INSERT INTO attendance_json (student_id, coursecode, log) VALUES (?, ?, ?)";
            PreparedStatement pstIns = connect.prepareStatement(insertSQL);
            pstIns.setString(1, studentId);
            pstIns.setString(2, courseCode);
            pstIns.setString(3, updatedLog);
            pstIns.executeUpdate();
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
        Course = new javax.swing.JLabel();
        CourseCombo = new RoundedComboBox<>();
        Date = new javax.swing.JLabel();
        SelectDate = new RoundedDateChooser();
        AttendanceTableScrollPane = new javax.swing.JScrollPane();
        AttendanceTable = new RoundedTable();
        SaveBtn = new RoundedButton();
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addComponent(ViewCoursesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 534, Short.MAX_VALUE)
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

        Course.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        Course.setText("Course");

        CourseCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Course" }));
        CourseCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CourseComboActionPerformed(evt);
            }
        });

        Date.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        Date.setText("Date");

        SelectDate.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                SelectDatePropertyChange(evt);
            }
        });

        AttendanceTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Name", "Absence Count", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        AttendanceTable.setRowHeight(50);
        AttendanceTable.setRowSelectionAllowed(false);
        AttendanceTable.setShowHorizontalLines(true);
        AttendanceTable.setShowVerticalLines(true);
        AttendanceTable.getTableHeader().setResizingAllowed(false);
        AttendanceTable.getTableHeader().setReorderingAllowed(false);
        AttendanceTableScrollPane.setViewportView(AttendanceTable);

        SaveBtn.setBackground(new java.awt.Color(151, 137, 219));
        SaveBtn.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        SaveBtn.setText("Save");
        SaveBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                SaveBtnMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout AttendancePanelLayout = new javax.swing.GroupLayout(AttendancePanel);
        AttendancePanel.setLayout(AttendancePanelLayout);
        AttendancePanelLayout.setHorizontalGroup(
            AttendancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AttendancePanelLayout.createSequentialGroup()
                .addGap(0, 663, Short.MAX_VALUE)
                .addComponent(ProfileButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(AttendancePanelLayout.createSequentialGroup()
                .addGap(159, 159, 159)
                .addGroup(AttendancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CourseCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Course))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(AttendancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Date)
                    .addComponent(SelectDate, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(159, 159, 159))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AttendancePanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(SaveBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(314, 314, 314))
            .addGroup(AttendancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(AttendancePanelLayout.createSequentialGroup()
                    .addGap(43, 43, 43)
                    .addComponent(AttendanceTableScrollPane)
                    .addGap(44, 44, 44)))
        );
        AttendancePanelLayout.setVerticalGroup(
            AttendancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AttendancePanelLayout.createSequentialGroup()
                .addComponent(ProfileButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(AttendancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Course)
                    .addComponent(Date))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(AttendancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CourseCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SelectDate, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(424, 424, 424)
                .addComponent(SaveBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(26, 26, 26))
            .addGroup(AttendancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(AttendancePanelLayout.createSequentialGroup()
                    .addGap(124, 124, 124)
                    .addComponent(AttendanceTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE)
                    .addGap(78, 78, 78)))
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
                .addGap(0, 552, Short.MAX_VALUE))
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
                    .addComponent(MainPagePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 592, Short.MAX_VALUE)
                    .addComponent(SidePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 592, Short.MAX_VALUE))
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
        CourseCombo.removeAllItems();
        CourseCombo.addItem("Select Course");

        try {
            String instructorFullName = "";
            String nameQuery = "SELECT CONCAT(`fName`, ' ', `lName`) AS `fullName` FROM `instructor` WHERE `ID` = ?";
            pst = connect.prepareStatement(nameQuery);
            pst.setString(1, this.id);
            result = pst.executeQuery();

            if (result.next()) {
                instructorFullName = result.getString("fullName");
            } else {
                return;
            }

            String courseQuery = "SELECT DISTINCT `cName` FROM `timetable` WHERE `instructorName` = ?";
            pst = connect.prepareStatement(courseQuery);
            pst.setString(1, instructorFullName);
            result = pst.executeQuery();

            while (result.next()) {
                CourseCombo.addItem(result.getString("cName"));
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading courses: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (result != null) result.close();
                if (pst != null) pst.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
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

    private void CourseComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CourseComboActionPerformed
        // TODO add your handling code here:
        String selectedCourse = (String) CourseCombo.getSelectedItem();
        if (selectedCourse == null || selectedCourse.equals("Select Course")) {
            ((DefaultTableModel) AttendanceTable.getModel()).setRowCount(0);
            return;
        }

        try {
            String courseQuery = "SELECT `coursecode`, `session` FROM `course` WHERE `coursename` = ?";
            pst = connect.prepareStatement(courseQuery);
            pst.setString(1, selectedCourse);
            result = pst.executeQuery();

            if (result.next()) {
                String code = result.getString("coursecode");
                String session = result.getString("session");

                java.util.List<Integer> scheduledDays = new java.util.ArrayList<>();
                String dayQuery = "SELECT `day` FROM `timetable` WHERE `courseCode` = ?";
                pst = connect.prepareStatement(dayQuery);
                pst.setString(1, code);
                result = pst.executeQuery();

                while (result.next()) {
                    scheduledDays.add(getDayInteger(result.getString("day")));
                }
                
                restrictDateAndMultipleDays(session, scheduledDays);
                if (SelectDate.getDate() != null){
                    loadAttendanceForSelectedDate();
                } else {
                    ((DefaultTableModel) AttendanceTable.getModel()).setRowCount(0);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Database Error: " + ex.getMessage());
        }
    }//GEN-LAST:event_CourseComboActionPerformed

    private void SelectDatePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_SelectDatePropertyChange
        // TODO add your handling code here:
            if ("date".equals(evt.getPropertyName())) {
                String selectedCourse = (String) CourseCombo.getSelectedItem();
                if(selectedCourse != null && !selectedCourse.equals("Select Course") && SelectDate.getDate() != null){
                    loadAttendanceForSelectedDate();
                }
            }
    }//GEN-LAST:event_SelectDatePropertyChange

    private void SaveBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SaveBtnMouseClicked
        // TODO add your handling code here:
        String selectedCourse = (String) CourseCombo.getSelectedItem();
        java.util.Date selectedDate = SelectDate.getDate();

        if (selectedCourse == null || selectedCourse.equals("Select Course") || selectedDate == null) {
            JOptionPane.showMessageDialog(this, "Please select both a course and a date.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateKey = sdf.format(selectedDate);
        DefaultTableModel model = (DefaultTableModel) AttendanceTable.getModel();

        try {
            // 1. Get Course Code
            String codeQuery = "SELECT coursecode FROM course WHERE coursename = ?";
            pst = connect.prepareStatement(codeQuery);
            pst.setString(1, selectedCourse);
            result = pst.executeQuery();
            String courseCode = result.next() ? result.getString("coursecode") : null;

            if (courseCode == null) return;

            // 2. Iterate through table rows to update each student
            for (int i = 0; i < model.getRowCount(); i++) {
                String studentName = (String) model.getValueAt(i, 0);
                String status = (String) model.getValueAt(i, 2);

                if (status.equals("Select Attendance")) continue;

                // Get Student ID from Name (assuming unique names or you'd need ID in the table)
                String studentIdQuery = "SELECT ID FROM student WHERE CONCAT(fName, ' ', lName) = ?";
                PreparedStatement pstId = connect.prepareStatement(studentIdQuery);
                pstId.setString(1, studentName);
                ResultSet rsId = pstId.executeQuery();

                if (rsId.next()) {
                    String studentId = rsId.getString("ID");
                    updateStudentAttendance(studentId, courseCode, dateKey, status);
                }
            }
            JOptionPane.showMessageDialog(this, "Attendance saved successfully!");
            loadAttendanceForSelectedDate(); // Refresh to update absence counts

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saving attendance: " + ex.getMessage());
        }
    }//GEN-LAST:event_SaveBtnMouseClicked

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
    private javax.swing.JTable AttendanceTable;
    private javax.swing.JScrollPane AttendanceTableScrollPane;
    private javax.swing.JLabel Course;
    private javax.swing.JComboBox<String> CourseCombo;
    private javax.swing.JLabel Date;
    private javax.swing.JLabel DateTime;
    private javax.swing.JPanel HomePageInstuctorPanel;
    private javax.swing.JButton LogOut;
    private javax.swing.JPanel MainPagePanel;
    private javax.swing.JButton MarkAttendance;
    private javax.swing.JLabel ProfileButton;
    private javax.swing.JLabel ProfileButton1;
    private javax.swing.JLabel ProfileButton2;
    private javax.swing.JPanel ResultsPanel;
    private javax.swing.JButton SaveBtn;
    private com.toedter.calendar.JDateChooser SelectDate;
    private javax.swing.JPanel SidePanel;
    private javax.swing.JButton UploadResult;
    private javax.swing.JButton ViewCourses;
    private javax.swing.JPanel ViewCoursesPanel;
    private javax.swing.JScrollPane ViewCoursesScrollPane;
    private javax.swing.JTable ViewCoursesTable;
    // End of variables declaration//GEN-END:variables

}
