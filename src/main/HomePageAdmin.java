/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package main;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author DELL
 */
public class HomePageAdmin extends javax.swing.JFrame {
    Connection connect = null;
    ResultSet result = null;
    PreparedStatement pst = null;
    ResultSetMetaData RSMD;
    DefaultTableModel DTM;
    
    boolean temp = false;
    String checkStatus = "Active",email,cnic;
    
    JTextField AdateTextField, UdateTextField, AdateTextField2, UdateTextField2;
    
    private final ImageIcon VIEW_ICON = new ImageIcon(getClass().getResource("/main/resources/icons8-view-24.png"));
    private final ImageIcon DELETE_ICON = new ImageIcon(getClass().getResource("/main/resources/icons8-delete-24.png")); 
    
    // Store View Time Table Data
    private String cName, instructorName, day, sTime, eTime, roomNo;

    // Top of file with other variables
    HashMap<String, String> courseMap = new HashMap<>();
    HashMap<String, String> instructorMap = new HashMap<>();
    public HomePageAdmin() {
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
        // For Add Student
        this.AdateTextField = (JTextField) ADateOfBirthField.getDateEditor().getUiComponent();
        AdateTextField.setText("DD-MM-YYYY");
        AdateTextField.setForeground(new Color(153,153,153));
        // For Add Instructor
        this.AdateTextField2 = (JTextField) ADateOfBirthField2.getDateEditor().getUiComponent();
        AdateTextField2.setText("DD-MM-YYYY");
        AdateTextField2.setForeground(new Color(153,153,153));
        // For Update Student
        this.UdateTextField = (JTextField) UDateOfBirthField.getDateEditor().getUiComponent();
        UdateTextField.setText("DD-MM-YYYY");
        UdateTextField.setForeground(new Color(153,153,153));
        // For Update Instructor
        this.UdateTextField2 = (JTextField) UDateOfBirthField2.getDateEditor().getUiComponent();
        UdateTextField2.setText("DD-MM-YYYY");
        UdateTextField2.setForeground(new Color(153,153,153));
        // Show Table
        show_Table("student", ViewStudentsTable);
    }
    
    public HomePageAdmin(String email) {
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
        this.email = email;
        // For Add Tab
        this.AdateTextField = (JTextField) ADateOfBirthField.getDateEditor().getUiComponent();
        AdateTextField.setText("DD-MM-YYYY");
        AdateTextField.setForeground(new Color(153,153,153));
        // For Update Tab
        this.UdateTextField = (JTextField) UDateOfBirthField.getDateEditor().getUiComponent();
        UdateTextField.setText("DD-MM-YYYY");
        UdateTextField.setForeground(new Color(153,153,153));
        // Show Table
        show_Table("student", ViewStudentsTable);
    }
    
    // Check Email Already Exist or Not
    public boolean checkEmail (String email) {
        boolean checkEmail = false;
        String studentQuery = "SELECT * FROM `student` WHERE `email`=?";
        String instructorQuery = "SELECT * FROM `instructor` WHERE `email`=?";
        try {
            pst = connect.prepareStatement(studentQuery);
            pst.setString(1, AEmailField.getText());
            result = pst.executeQuery();
            if (result.next()) {
                checkEmail = true;
            } else {
                if(result != null) result.close();
                if(pst != null) pst.close();
                
                pst = connect.prepareStatement(instructorQuery);
                pst.setString(1, AEmailField2.getText());
                result = pst.executeQuery();
                if(result.next()){
                    checkEmail = true;
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,ex);
        }
        return checkEmail;
    }
    
    // Check CNIC Already Exist or Not
    public boolean checkCNIC (String cnic) {
        boolean checkCNIC = false;
        String studentQuery = "SELECT * FROM `student` WHERE `cnic`=?";
        String instructorQuery = "SELECT * FROM `instructor` WHERE `cnic`=?";
        try {
            pst = connect.prepareStatement(studentQuery);
            pst.setString(1, ACNICField.getText());
            result = pst.executeQuery();
            if (result.next()) {
                checkCNIC = true;
            } else {
                if(result != null) result.close();
                if(pst != null) pst.close();
                
                pst = connect.prepareStatement(instructorQuery);
                pst.setString(1, ACNICField2.getText());
                result = pst.executeQuery();
                if(result.next()){
                    checkCNIC = true;
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,ex);
        }
        return checkCNIC;
    }
    
    // Check Instructor ID already exists
    public boolean checkInstructorID(String ID){
        boolean checkID = false;
        String query = "SELECT * FROM `instructor` WHERE `ID`=?";
        try {
            pst = connect.prepareStatement(query);
            pst.setString(1, ID);
            result = pst.executeQuery();
            if (result.next()) {
                checkID = true;
                generateRandomID();
            } 
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,ex);
        }
        return checkID;
    }
        
    
    // Check CourseCode Already Exists
    public boolean checkCourseCode (String ccode) {
        boolean checkCCODE = false;
        String query = "SELECT * FROM `course` WHERE `coursecode`=?";
        try {
            pst = connect.prepareStatement(query);
            pst.setString(1, ACourseCodeField.getText());
            result = pst.executeQuery();
            if (result.next()) {
                checkCCODE = true;
            } 
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,ex);
        }
        return checkCCODE;
    }
    
    // Show Table
    private void show_Table(String tableName, JTable targetTable) {
        // Icons
        if(tableName.equals("student")){
            setIconRenderer(4, VIEW_ICON, tableName);
            setIconRenderer(5, DELETE_ICON, tableName);
        } else if(tableName.equals("instructor")){
            setIconRenderer(5, VIEW_ICON, tableName);
            setIconRenderer(6, DELETE_ICON, tableName);
        } else if(tableName.equals("course")){
            setIconRenderer(4, VIEW_ICON, tableName);
            setIconRenderer(5, DELETE_ICON, tableName);
        }     
        String query = "SELECT * FROM `"+ tableName +"` ";
        try {
            // Database Connection
            connect = Database.getConnection();
            pst = connect.prepareStatement(query);
            result = pst.executeQuery();
            RSMD = result.getMetaData();
            int CC = RSMD.getColumnCount();
            DTM = (DefaultTableModel)targetTable.getModel();
            
            DTM.setRowCount(0);
            while(result.next()) {
                Vector v2 = new Vector();
                for(int i=1; i<=CC; i++) {
                    if(tableName.equals("student")){
                        v2.add(result.getString("Id"));
                        v2.add(result.getString("fName"));
                        v2.add(result.getString("lName"));
                        v2.add(result.getString("status"));
                    } else if(tableName.equals("instructor")){
                        v2.add(result.getString("Id"));
                        v2.add(result.getString("fName"));
                        v2.add(result.getString("lName"));
                        v2.add(result.getString("status"));
                        v2.add(result.getString("department"));
                        v2.add("");
                    } else if (tableName.equals("course")){
                        v2.add(result.getString("coursecode"));
                        v2.add(result.getString("coursename"));
                        v2.add(result.getString("credithrs"));
                        v2.add(result.getString("seats"));
                    } else if (tableName.equals("timetable")){
                        v2.add(result.getString("cName"));
                        v2.add(result.getString("instructorName"));
                        v2.add(result.getString("day"));
                        v2.add(result.getString("sTime"));
                        v2.add(result.getString("eTime"));
                        v2.add(result.getString("roomNo"));
                    }    
                }
                DTM.addRow(v2);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,ex);
        }
    }
    
    // Set Icon
    private void setIconRenderer(int columnIndex, ImageIcon icon, String tableName) {
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
        if(tableName.equals("student") || tableName.equals("course")){
            ViewStudentsTable.getColumnModel().getColumn(columnIndex).setCellRenderer(iconRenderer);
            ViewCoursesTable.getColumnModel().getColumn(columnIndex).setCellRenderer(iconRenderer);
        }else if(tableName.equals("instructor")){
            ViewInstructorsTable.getColumnModel().getColumn(columnIndex).setCellRenderer(iconRenderer);
        }
    }
    
    // Generate Random ID For Instructor
    private String generateRandomID(){
        Random random = new Random();
        char fixedLetter = 'F';
        int randomNum = random.nextInt(10000);
        String generatedID = String.format("%04d", randomNum);
        return fixedLetter + generatedID;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        HomePageAdminPanel = new ui.GradientPanel();
        SidePanel = new ui.GlassmorphismPanel();
        ManageStudent = new RoundedButton();
        ManageInstructor = new RoundedButton();
        ManageCourse = new RoundedButton();
        TimeTable = new RoundedButton();
        PublishResult = new RoundedButton();
        LogOut = new RoundedButton();
        MainPagePanel = new javax.swing.JPanel();
        StudentPanel = new javax.swing.JPanel();
        StudentTabs = new javax.swing.JTabbedPane();
        AddStudent = new javax.swing.JPanel();
        AFirstName = new javax.swing.JLabel();
        AFirstNameField = new RoundedTextField(20);
        ALastName = new javax.swing.JLabel();
        ALastNameField = new RoundedTextField(20);
        APhoneNo = new javax.swing.JLabel();
        APhoneNoField = new RoundedTextField(20);
        AGender = new javax.swing.JLabel();
        AGenderCombo = new RoundedComboBox<>();
        ACNIC = new javax.swing.JLabel();
        ACNICField = new RoundedTextField(20);
        ADateOfBirth = new javax.swing.JLabel();
        ADateOfBirthField = new RoundedDateChooser();
        ADepartment = new javax.swing.JLabel();
        ADepartmentCombo = new RoundedComboBox<>()
        ;
        AProgram = new javax.swing.JLabel();
        AProgramField = new RoundedTextField(20);
        AAddress = new javax.swing.JLabel();
        AAddressField = new RoundedTextField(20);
        AEmail = new javax.swing.JLabel();
        AEmailField = new RoundedTextField(20);
        ASubmitBtn = new RoundedButton();
        UpdateStudent = new javax.swing.JPanel();
        UId = new javax.swing.JLabel();
        UIdField = new RoundedTextField(20);
        UFirstName = new javax.swing.JLabel();
        UFirstNameField = new RoundedTextField(20);
        ULastName = new javax.swing.JLabel();
        ULastNameField = new RoundedTextField(20);
        UPhoneNo = new javax.swing.JLabel();
        UPhoneNoField = new RoundedTextField(20);
        UGender = new javax.swing.JLabel();
        UGenderCombo = new RoundedComboBox<>();
        UCNIC = new javax.swing.JLabel();
        UCNICField = new RoundedTextField(20);
        UDateOfBirth = new javax.swing.JLabel();
        UDateOfBirthField = new RoundedDateChooser();
        UDepartment = new javax.swing.JLabel();
        UDepartmentCombo = new RoundedComboBox<>()
        ;
        UProgram = new javax.swing.JLabel();
        UProgramField = new RoundedTextField(20);
        UAddress = new javax.swing.JLabel();
        UAddressField = new RoundedTextField(20);
        UEmail = new javax.swing.JLabel();
        UEmailField = new RoundedTextField(20);
        UPassword = new javax.swing.JLabel();
        UPasswordField = new RoundedPasswordField(20);
        UStatus = new javax.swing.JLabel();
        UActive = new javax.swing.JRadioButton();
        UInactive = new javax.swing.JRadioButton();
        USubmitBtn = new RoundedButton();
        ViewStudents = new javax.swing.JPanel();
        ViewStudentsScrollPane = new javax.swing.JScrollPane();
        ViewStudentsTable = new RoundedTable();
        InstructorPanel = new javax.swing.JPanel();
        InstructorTabs = new javax.swing.JTabbedPane();
        AddInstructor = new javax.swing.JPanel();
        AFirstName2 = new javax.swing.JLabel();
        AFirstNameField2 = new RoundedTextField(20);
        ALastName2 = new javax.swing.JLabel();
        ALastNameField2 = new RoundedTextField(20);
        APhoneNo2 = new javax.swing.JLabel();
        APhoneNoField2 = new RoundedTextField(20);
        AGender2 = new javax.swing.JLabel();
        AGenderCombo2 = new RoundedComboBox<>();
        ACNIC2 = new javax.swing.JLabel();
        ACNICField2 = new RoundedTextField(20);
        ADateOfBirth2 = new javax.swing.JLabel();
        ADateOfBirthField2 = new RoundedDateChooser();
        ADepartment2 = new javax.swing.JLabel();
        ADepartmentCombo2 = new RoundedComboBox<>()
        ;
        AAddress2 = new javax.swing.JLabel();
        AAddressField2 = new RoundedTextField(20);
        AEmail2 = new javax.swing.JLabel();
        AEmailField2 = new RoundedTextField(20);
        ASubmitBtn2 = new RoundedButton();
        UpdateInstructor = new javax.swing.JPanel();
        UId2 = new javax.swing.JLabel();
        UIdField2 = new RoundedTextField(20);
        UFirstName2 = new javax.swing.JLabel();
        UFirstNameField2 = new RoundedTextField(20);
        ULastName2 = new javax.swing.JLabel();
        ULastNameField2 = new RoundedTextField(20);
        UPhoneNo2 = new javax.swing.JLabel();
        UPhoneNoField2 = new RoundedTextField(20);
        UGender2 = new javax.swing.JLabel();
        UGenderCombo2 = new RoundedComboBox<>();
        UCNIC2 = new javax.swing.JLabel();
        UCNICField2 = new RoundedTextField(20);
        UDateOfBirth2 = new javax.swing.JLabel();
        UDateOfBirthField2 = new RoundedDateChooser();
        UDepartment2 = new javax.swing.JLabel();
        UDepartmentCombo2 = new RoundedComboBox<>()
        ;
        UAddress2 = new javax.swing.JLabel();
        UAddressField2 = new RoundedTextField(20);
        UEmail2 = new javax.swing.JLabel();
        UEmailField2 = new RoundedTextField(20);
        UPassword2 = new javax.swing.JLabel();
        UPasswordField2 = new RoundedPasswordField(20);
        UStatus2 = new javax.swing.JLabel();
        UActive2 = new javax.swing.JRadioButton();
        UInactive2 = new javax.swing.JRadioButton();
        USubmitBtn2 = new RoundedButton();
        ViewInstructors = new javax.swing.JPanel();
        ViewInstructorsScrollPane = new javax.swing.JScrollPane();
        ViewInstructorsTable = new RoundedTable();
        CoursePanel = new javax.swing.JPanel();
        CourseTabs = new javax.swing.JTabbedPane();
        AddCourse = new javax.swing.JPanel();
        ACourseCode = new javax.swing.JLabel();
        ACourseCodeField = new RoundedTextField(20);
        ACourseName = new javax.swing.JLabel();
        ACourseNameField = new RoundedTextField(20);
        ACreditHrs = new javax.swing.JLabel();
        ACreditHrsField = new RoundedTextField(20);
        ASession = new javax.swing.JLabel();
        ASessionCombo = new RoundedComboBox<>();
        AAcademicYear = new javax.swing.JLabel();
        AAcademicYearField = new RoundedTextField(20);
        ACDepartment = new javax.swing.JLabel();
        ACDepartmentCombo = new RoundedComboBox<>()
        ;
        ASeats = new javax.swing.JLabel();
        ASeatsField = new RoundedTextField(20);
        ACSubmitBtn = new RoundedButton();
        UpdateCourse = new javax.swing.JPanel();
        UCourseCode = new javax.swing.JLabel();
        UCourseCodeField = new RoundedTextField(20);
        UCourseName = new javax.swing.JLabel();
        UCourseNameField = new RoundedTextField(20);
        UCreditHrs = new javax.swing.JLabel();
        UCreditHrsField = new RoundedTextField(20);
        USession = new javax.swing.JLabel();
        USessionCombo = new RoundedComboBox<>();
        UAcademicYear = new javax.swing.JLabel();
        UAcademicYearField = new RoundedTextField(20);
        UCDepartment = new javax.swing.JLabel();
        UCDepartmentCombo = new RoundedComboBox<>()
        ;
        USeats = new javax.swing.JLabel();
        USeatsField = new RoundedTextField(20);
        UCSubmitBtn = new RoundedButton();
        ViewCourses = new javax.swing.JPanel();
        ViewCoursesScrollPane = new javax.swing.JScrollPane();
        ViewCoursesTable = new RoundedTable();
        TimeTablePanel = new javax.swing.JPanel();
        Day = new javax.swing.JLabel();
        DayCombo = new RoundedComboBox<>();
        StartTime = new javax.swing.JLabel();
        HoursCombo = new RoundedComboBox<>();
        MinutesCombo = new RoundedComboBox<>();
        EndTime = new javax.swing.JLabel();
        HoursCombo1 = new RoundedComboBox<>();
        MinutesCombo1 = new RoundedComboBox<>();
        Course = new javax.swing.JLabel();
        CourseCombo = new RoundedComboBox<>();
        Room = new javax.swing.JLabel();
        RoomCombo = new RoundedComboBox<>();
        Instructor = new javax.swing.JLabel();
        InstructorCombo = new RoundedComboBox<>();
        Add = new RoundedButton();
        Update = new RoundedButton();
        Delete = new RoundedButton();
        TimeTableScrollPane = new javax.swing.JScrollPane();
        ViewTimeTable = new RoundedTable();
        ResultPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        HomePageAdminPanel.setPreferredSize(new java.awt.Dimension(923, 600));

        SidePanel.setOpaque(false);
        SidePanel.setPreferredSize(new java.awt.Dimension(200, 600));

        ManageStudent.setBackground(new java.awt.Color(151, 137, 219));
        ManageStudent.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        ManageStudent.setText("Student");
        ManageStudent.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ManageStudent.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ManageStudentMouseClicked(evt);
            }
        });
        ManageStudent.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ManageStudentKeyPressed(evt);
            }
        });

        ManageInstructor.setBackground(new java.awt.Color(151, 137, 219));
        ManageInstructor.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        ManageInstructor.setText("Instructor");
        ManageInstructor.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ManageInstructor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ManageInstructorMouseClicked(evt);
            }
        });
        ManageInstructor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ManageInstructorKeyPressed(evt);
            }
        });

        ManageCourse.setBackground(new java.awt.Color(151, 137, 219));
        ManageCourse.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        ManageCourse.setText("Course");
        ManageCourse.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ManageCourse.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ManageCourseMouseClicked(evt);
            }
        });
        ManageCourse.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ManageCourseKeyPressed(evt);
            }
        });

        TimeTable.setBackground(new java.awt.Color(151, 137, 219));
        TimeTable.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        TimeTable.setText("Time Table");
        TimeTable.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        TimeTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TimeTableMouseClicked(evt);
            }
        });
        TimeTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TimeTableKeyPressed(evt);
            }
        });

        PublishResult.setBackground(new java.awt.Color(151, 137, 219));
        PublishResult.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        PublishResult.setText("Publish Result");
        PublishResult.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        PublishResult.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                PublishResultMouseClicked(evt);
            }
        });
        PublishResult.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PublishResultKeyPressed(evt);
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
                    .addComponent(ManageStudent, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ManageCourse, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ManageInstructor, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PublishResult, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LogOut, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TimeTable, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        SidePanelLayout.setVerticalGroup(
            SidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SidePanelLayout.createSequentialGroup()
                .addGap(70, 70, 70)
                .addComponent(ManageStudent)
                .addGap(50, 50, 50)
                .addComponent(ManageInstructor)
                .addGap(50, 50, 50)
                .addComponent(ManageCourse)
                .addGap(50, 50, 50)
                .addComponent(TimeTable)
                .addGap(50, 50, 50)
                .addComponent(PublishResult)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 116, Short.MAX_VALUE)
                .addComponent(LogOut)
                .addGap(22, 22, 22))
        );

        MainPagePanel.setOpaque(false);
        MainPagePanel.setPreferredSize(new java.awt.Dimension(100, 600));
        MainPagePanel.setLayout(new java.awt.CardLayout());

        StudentPanel.setOpaque(false);

        StudentTabs.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        StudentTabs.setFont(new java.awt.Font("Bodoni MT", 1, 14)); // NOI18N

        AddStudent.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        AddStudent.setOpaque(false);

        AFirstName.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        AFirstName.setText("First Name");

        AFirstNameField.setForeground(new java.awt.Color(153, 153, 153));
        AFirstNameField.setText("John");
        AFirstNameField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                AFirstNameFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                AFirstNameFieldFocusLost(evt);
            }
        });

        ALastName.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        ALastName.setText("Last Name");

        ALastNameField.setForeground(new java.awt.Color(153, 153, 153));
        ALastNameField.setText("Doe");
        ALastNameField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ALastNameFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                ALastNameFieldFocusLost(evt);
            }
        });

        APhoneNo.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        APhoneNo.setText("Phone No");

        APhoneNoField.setForeground(new java.awt.Color(153, 153, 153));
        APhoneNoField.setText("0333-1234567");
        APhoneNoField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                APhoneNoFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                APhoneNoFieldFocusLost(evt);
            }
        });

        AGender.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        AGender.setText("Gender");

        AGenderCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Gender", "Male", "Female", "Other" }));

        ACNIC.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        ACNIC.setText("CNIC");

        ACNICField.setForeground(new java.awt.Color(153, 153, 153));
        ACNICField.setText("00000-0000000-0");
        ACNICField.setPreferredSize(new java.awt.Dimension(64, 22));
        ACNICField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ACNICFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                ACNICFieldFocusLost(evt);
            }
        });

        ADateOfBirth.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        ADateOfBirth.setText("Date of Birth");

        ADateOfBirthField.setDateFormatString("dd-MM-yyyy");
        ADateOfBirthField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ADateOfBirthFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                ADateOfBirthFieldFocusLost(evt);
            }
        });

        ADepartment.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        ADepartment.setText("Department");

        ADepartmentCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Department", "Department of Computer Science", "Department of English", "Department of Mathematics", "Department of Physics" }));

        AProgram.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        AProgram.setText("Program");

        AProgramField.setForeground(new java.awt.Color(153, 153, 153));
        AProgramField.setText("Program Name");
        AProgramField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                AProgramFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                AProgramFieldFocusLost(evt);
            }
        });

        AAddress.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        AAddress.setText("Address");

        AEmail.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        AEmail.setText("Email");

        AEmailField.setForeground(new java.awt.Color(153, 153, 153));
        AEmailField.setText("user@domain.com");
        AEmailField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                AEmailFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                AEmailFieldFocusLost(evt);
            }
        });

        ASubmitBtn.setBackground(new java.awt.Color(151, 137, 219));
        ASubmitBtn.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        ASubmitBtn.setText("Submit");
        ASubmitBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ASubmitBtnMouseClicked(evt);
            }
        });
        ASubmitBtn.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ASubmitBtnKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout AddStudentLayout = new javax.swing.GroupLayout(AddStudent);
        AddStudent.setLayout(AddStudentLayout);
        AddStudentLayout.setHorizontalGroup(
            AddStudentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AddStudentLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(AddStudentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(AAddressField)
                    .addComponent(AEmailField)
                    .addGroup(AddStudentLayout.createSequentialGroup()
                        .addComponent(AEmail)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(AddStudentLayout.createSequentialGroup()
                        .addGroup(AddStudentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(ACNIC, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ACNICField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(AFirstNameField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(AddStudentLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(AddStudentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(AddStudentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(AAddress)
                                        .addComponent(ADepartment)
                                        .addComponent(ADepartmentCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(AddStudentLayout.createSequentialGroup()
                                        .addComponent(AFirstName)
                                        .addGap(279, 279, 279))
                                    .addGroup(AddStudentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(AddStudentLayout.createSequentialGroup()
                                            .addComponent(ALastName)
                                            .addGap(71, 71, 71))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AddStudentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(APhoneNoField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(APhoneNo))
                                        .addComponent(ALastNameField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(63, 63, 63)
                        .addGroup(AddStudentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AddStudentLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(AddStudentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AddStudentLayout.createSequentialGroup()
                                        .addComponent(AProgram)
                                        .addGap(147, 147, 147))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AddStudentLayout.createSequentialGroup()
                                        .addComponent(ADateOfBirth)
                                        .addGap(114, 114, 114))))
                            .addGroup(AddStudentLayout.createSequentialGroup()
                                .addGroup(AddStudentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(AGenderCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(AGender)
                                    .addComponent(ADateOfBirthField, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(AProgramField, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addGap(40, 40, 40))
            .addGroup(AddStudentLayout.createSequentialGroup()
                .addGap(289, 289, 289)
                .addComponent(ASubmitBtn)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        AddStudentLayout.setVerticalGroup(
            AddStudentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AddStudentLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(AddStudentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(AddStudentLayout.createSequentialGroup()
                        .addGroup(AddStudentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(AddStudentLayout.createSequentialGroup()
                                .addComponent(AFirstName)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(AFirstNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(ACNIC)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ACNICField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(ADepartment)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, AddStudentLayout.createSequentialGroup()
                                .addComponent(ALastName)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ALastNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(APhoneNo)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(APhoneNoField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addComponent(ADepartmentCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(AddStudentLayout.createSequentialGroup()
                        .addComponent(ADateOfBirth)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ADateOfBirthField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(AGender)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AGenderCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(AProgram)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AProgramField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(AAddress)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AAddressField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(AEmail)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AEmailField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(ASubmitBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        StudentTabs.addTab("Add Student", new javax.swing.ImageIcon(getClass().getResource("/main/resources/icons8-add-16.png")), AddStudent); // NOI18N

        UpdateStudent.setOpaque(false);

        UId.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        UId.setText("ID");

        UIdField.setForeground(new java.awt.Color(153, 153, 153));
        UIdField.setText("Enter ID to Search");
        UIdField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                UIdFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                UIdFieldFocusLost(evt);
            }
        });
        UIdField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                UIdFieldKeyPressed(evt);
            }
        });

        UFirstName.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        UFirstName.setText("First Name");

        UFirstNameField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                UFirstNameFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                UFirstNameFieldFocusLost(evt);
            }
        });

        ULastName.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        ULastName.setText("Last Name");

        ULastNameField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ULastNameFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                ULastNameFieldFocusLost(evt);
            }
        });

        UPhoneNo.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        UPhoneNo.setText("Phone No");

        UPhoneNoField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                UPhoneNoFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                UPhoneNoFieldFocusLost(evt);
            }
        });

        UGender.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        UGender.setText("Gender");

        UGenderCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Gender", "Male", "Female", "Other" }));

        UCNIC.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        UCNIC.setText("CNIC");

        UCNICField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                UCNICFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                UCNICFieldFocusLost(evt);
            }
        });

        UDateOfBirth.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        UDateOfBirth.setText("Date of Birth");

        UDateOfBirthField.setDateFormatString("dd-MM-yyyy");
        UDateOfBirthField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                UDateOfBirthFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                UDateOfBirthFieldFocusLost(evt);
            }
        });

        UDepartment.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        UDepartment.setText("Department");

        UDepartmentCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Department", "Department of Computer Science", "Department of English", "Department of Mathematics", "Department of Physics" }));

        UProgram.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        UProgram.setText("Program");

        UProgramField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                UProgramFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                UProgramFieldFocusLost(evt);
            }
        });

        UAddress.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        UAddress.setText("Address");

        UEmail.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        UEmail.setText("Email");

        UEmailField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                UEmailFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                UEmailFieldFocusLost(evt);
            }
        });

        UPassword.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        UPassword.setText("Password");

        UStatus.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        UStatus.setText("Status");

        UActive.setFont(new java.awt.Font("Bodoni MT", 1, 14)); // NOI18N
        UActive.setText("Active");
        UActive.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                UActiveMouseClicked(evt);
            }
        });
        UActive.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                UActiveKeyPressed(evt);
            }
        });

        UInactive.setFont(new java.awt.Font("Bodoni MT", 1, 14)); // NOI18N
        UInactive.setText("In-Active");
        UInactive.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                UInactiveMouseClicked(evt);
            }
        });
        UInactive.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                UInactiveKeyPressed(evt);
            }
        });

        USubmitBtn.setBackground(new java.awt.Color(151, 137, 219));
        USubmitBtn.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        USubmitBtn.setText("Submit");
        USubmitBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                USubmitBtnMouseClicked(evt);
            }
        });
        USubmitBtn.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                USubmitBtnKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout UpdateStudentLayout = new javax.swing.GroupLayout(UpdateStudent);
        UpdateStudent.setLayout(UpdateStudentLayout);
        UpdateStudentLayout.setHorizontalGroup(
            UpdateStudentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UpdateStudentLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(UpdateStudentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(UAddressField)
                    .addComponent(UEmailField)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, UpdateStudentLayout.createSequentialGroup()
                        .addComponent(UStatus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(UActive)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(UInactive)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(USubmitBtn))
                    .addComponent(UPasswordField)
                    .addGroup(UpdateStudentLayout.createSequentialGroup()
                        .addGroup(UpdateStudentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(UIdField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(UId))
                        .addGap(12, 12, 12)
                        .addGroup(UpdateStudentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(UFirstNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(UFirstName))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                        .addGroup(UpdateStudentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(UpdateStudentLayout.createSequentialGroup()
                                .addComponent(ULastName)
                                .addGap(83, 83, 83)
                                .addComponent(UDateOfBirth))
                            .addGroup(UpdateStudentLayout.createSequentialGroup()
                                .addComponent(ULastNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(UDateOfBirthField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(UpdateStudentLayout.createSequentialGroup()
                        .addGroup(UpdateStudentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(UDepartment)
                            .addComponent(UEmail)
                            .addComponent(UPassword)
                            .addComponent(UAddress))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(UpdateStudentLayout.createSequentialGroup()
                        .addGroup(UpdateStudentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(UCNIC)
                            .addComponent(UCNICField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(UpdateStudentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(UPhoneNoField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(UPhoneNo))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(UpdateStudentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(UGender)
                            .addComponent(UGenderCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(UpdateStudentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(UProgram)
                            .addComponent(UProgramField)))
                    .addComponent(UDepartmentCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(40, 40, 40))
        );
        UpdateStudentLayout.setVerticalGroup(
            UpdateStudentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UpdateStudentLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(UpdateStudentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(UpdateStudentLayout.createSequentialGroup()
                        .addGroup(UpdateStudentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(UFirstName)
                            .addComponent(ULastName)
                            .addComponent(UDateOfBirth)
                            .addComponent(UId))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(UpdateStudentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(UDateOfBirthField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(UpdateStudentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(UIdField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(UFirstNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(ULastNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(UpdateStudentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(UPhoneNo)
                            .addComponent(UGender)
                            .addComponent(UCNIC))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(UpdateStudentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(UPhoneNoField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(UGenderCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(UCNICField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(UpdateStudentLayout.createSequentialGroup()
                        .addComponent(UProgram)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(UProgramField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(UDepartment)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(UDepartmentCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(UAddress)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(UAddressField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(UEmail)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(UEmailField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(UPassword)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(UPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(UpdateStudentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(USubmitBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(UStatus)
                    .addComponent(UActive)
                    .addComponent(UInactive))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        StudentTabs.addTab("Update Student", new javax.swing.ImageIcon(getClass().getResource("/main/resources/icons8-update-16.png")), UpdateStudent); // NOI18N

        ViewStudents.setOpaque(false);

        ViewStudentsTable.setFont(new java.awt.Font("Bodoni MT", 0, 14)); // NOI18N
        ViewStudentsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "First Name", "Last Name", "Status", "View", "Delete"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ViewStudentsTable.setRowHeight(50);
        ViewStudentsTable.setRowSelectionAllowed(false);
        ViewStudentsTable.setShowGrid(false);
        ViewStudentsTable.setShowHorizontalLines(true);
        ViewStudentsTable.setShowVerticalLines(true);
        ViewStudentsTable.getTableHeader().setResizingAllowed(false);
        ViewStudentsTable.getTableHeader().setReorderingAllowed(false);
        ViewStudentsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ViewStudentsTableMouseClicked(evt);
            }
        });
        ViewStudentsScrollPane.setViewportView(ViewStudentsTable);

        javax.swing.GroupLayout ViewStudentsLayout = new javax.swing.GroupLayout(ViewStudents);
        ViewStudents.setLayout(ViewStudentsLayout);
        ViewStudentsLayout.setHorizontalGroup(
            ViewStudentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ViewStudentsLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(ViewStudentsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 617, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(50, Short.MAX_VALUE))
        );
        ViewStudentsLayout.setVerticalGroup(
            ViewStudentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ViewStudentsLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(ViewStudentsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 515, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        StudentTabs.addTab("View Students", new javax.swing.ImageIcon(getClass().getResource("/main/resources/icons8-view-16.png")), ViewStudents); // NOI18N

        javax.swing.GroupLayout StudentPanelLayout = new javax.swing.GroupLayout(StudentPanel);
        StudentPanel.setLayout(StudentPanelLayout);
        StudentPanelLayout.setHorizontalGroup(
            StudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(StudentTabs)
        );
        StudentPanelLayout.setVerticalGroup(
            StudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(StudentTabs)
        );

        MainPagePanel.add(StudentPanel, "Card1");

        InstructorPanel.setOpaque(false);

        InstructorTabs.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        InstructorTabs.setFont(new java.awt.Font("Bodoni MT", 1, 14)); // NOI18N

        AddInstructor.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        AddInstructor.setOpaque(false);

        AFirstName2.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        AFirstName2.setText("First Name");

        AFirstNameField2.setForeground(new java.awt.Color(153, 153, 153));
        AFirstNameField2.setText("John");
        AFirstNameField2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                AFirstNameField2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                AFirstNameField2FocusLost(evt);
            }
        });

        ALastName2.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        ALastName2.setText("Last Name");

        ALastNameField2.setForeground(new java.awt.Color(153, 153, 153));
        ALastNameField2.setText("Doe");
        ALastNameField2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ALastNameField2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                ALastNameField2FocusLost(evt);
            }
        });

        APhoneNo2.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        APhoneNo2.setText("Phone No");

        APhoneNoField2.setForeground(new java.awt.Color(153, 153, 153));
        APhoneNoField2.setText("0333-1234567");
        APhoneNoField2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                APhoneNoField2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                APhoneNoField2FocusLost(evt);
            }
        });

        AGender2.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        AGender2.setText("Gender");

        AGenderCombo2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Gender", "Male", "Female", "Other" }));

        ACNIC2.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        ACNIC2.setText("CNIC");

        ACNICField2.setForeground(new java.awt.Color(153, 153, 153));
        ACNICField2.setText("00000-0000000-0");
        ACNICField2.setPreferredSize(new java.awt.Dimension(64, 22));
        ACNICField2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ACNICField2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                ACNICField2FocusLost(evt);
            }
        });

        ADateOfBirth2.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        ADateOfBirth2.setText("Date of Birth");

        ADateOfBirthField2.setDateFormatString("dd-MM-yyyy");
        ADateOfBirthField2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ADateOfBirthField2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                ADateOfBirthField2FocusLost(evt);
            }
        });

        ADepartment2.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        ADepartment2.setText("Department");

        ADepartmentCombo2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Department", "Department of Computer Science", "Department of English", "Department of Mathematics", "Department of Physics" }));

        AAddress2.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        AAddress2.setText("Address");

        AEmail2.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        AEmail2.setText("Email");

        AEmailField2.setForeground(new java.awt.Color(153, 153, 153));
        AEmailField2.setText("user@domain.com");
        AEmailField2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                AEmailField2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                AEmailField2FocusLost(evt);
            }
        });

        ASubmitBtn2.setBackground(new java.awt.Color(151, 137, 219));
        ASubmitBtn2.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        ASubmitBtn2.setText("Submit");
        ASubmitBtn2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ASubmitBtn2MouseClicked(evt);
            }
        });
        ASubmitBtn2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ASubmitBtn2KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout AddInstructorLayout = new javax.swing.GroupLayout(AddInstructor);
        AddInstructor.setLayout(AddInstructorLayout);
        AddInstructorLayout.setHorizontalGroup(
            AddInstructorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AddInstructorLayout.createSequentialGroup()
                .addGap(289, 289, 289)
                .addComponent(ASubmitBtn2)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AddInstructorLayout.createSequentialGroup()
                .addGroup(AddInstructorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(AddInstructorLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ADepartmentCombo2, javax.swing.GroupLayout.PREFERRED_SIZE, 637, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, AddInstructorLayout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addGroup(AddInstructorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(AAddressField2)
                            .addComponent(AEmailField2)
                            .addGroup(AddInstructorLayout.createSequentialGroup()
                                .addComponent(AEmail2)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(AddInstructorLayout.createSequentialGroup()
                                .addGroup(AddInstructorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(ACNIC2, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(ACNICField2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(AFirstNameField2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(AddInstructorLayout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addGroup(AddInstructorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(AddInstructorLayout.createSequentialGroup()
                                                .addGroup(AddInstructorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(AAddress2)
                                                    .addComponent(ADepartment2))
                                                .addGap(272, 272, 272))
                                            .addGroup(AddInstructorLayout.createSequentialGroup()
                                                .addComponent(AFirstName2)
                                                .addGap(279, 279, 279))
                                            .addGroup(AddInstructorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(AddInstructorLayout.createSequentialGroup()
                                                    .addComponent(ALastName2)
                                                    .addGap(71, 71, 71))
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AddInstructorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(APhoneNoField2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(APhoneNo2))
                                                .addComponent(ALastNameField2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                .addGap(63, 63, 63)
                                .addGroup(AddInstructorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AddInstructorLayout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(ADateOfBirth2)
                                        .addGap(114, 114, 114))
                                    .addGroup(AddInstructorLayout.createSequentialGroup()
                                        .addGroup(AddInstructorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(AGenderCombo2, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(AGender2)
                                            .addComponent(ADateOfBirthField2, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(0, 0, Short.MAX_VALUE)))))))
                .addGap(40, 40, 40))
        );
        AddInstructorLayout.setVerticalGroup(
            AddInstructorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AddInstructorLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(AddInstructorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AddInstructorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(AddInstructorLayout.createSequentialGroup()
                            .addComponent(AFirstName2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(AFirstNameField2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(ACNIC2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(ACNICField2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(ADepartment2))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, AddInstructorLayout.createSequentialGroup()
                            .addComponent(ALastName2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(ALastNameField2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(APhoneNo2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(APhoneNoField2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AddInstructorLayout.createSequentialGroup()
                        .addComponent(ADateOfBirth2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ADateOfBirthField2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(AGender2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AGenderCombo2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ADepartmentCombo2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(AAddress2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AAddressField2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(AEmail2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AEmailField2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(ASubmitBtn2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        InstructorTabs.addTab("Add Instructor", new javax.swing.ImageIcon(getClass().getResource("/main/resources/icons8-add-16.png")), AddInstructor); // NOI18N

        UpdateInstructor.setOpaque(false);

        UId2.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        UId2.setText("ID");

        UIdField2.setForeground(new java.awt.Color(153, 153, 153));
        UIdField2.setText("Enter ID to Search");
        UIdField2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                UIdField2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                UIdField2FocusLost(evt);
            }
        });
        UIdField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                UIdField2KeyPressed(evt);
            }
        });

        UFirstName2.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        UFirstName2.setText("First Name");

        UFirstNameField2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                UFirstNameField2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                UFirstNameField2FocusLost(evt);
            }
        });

        ULastName2.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        ULastName2.setText("Last Name");

        ULastNameField2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ULastNameField2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                ULastNameField2FocusLost(evt);
            }
        });

        UPhoneNo2.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        UPhoneNo2.setText("Phone No");

        UPhoneNoField2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                UPhoneNoField2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                UPhoneNoField2FocusLost(evt);
            }
        });

        UGender2.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        UGender2.setText("Gender");

        UGenderCombo2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Gender", "Male", "Female", "Other" }));

        UCNIC2.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        UCNIC2.setText("CNIC");

        UCNICField2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                UCNICField2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                UCNICField2FocusLost(evt);
            }
        });

        UDateOfBirth2.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        UDateOfBirth2.setText("Date of Birth");

        UDateOfBirthField2.setDateFormatString("dd-MM-yyyy");
        UDateOfBirthField2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                UDateOfBirthField2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                UDateOfBirthField2FocusLost(evt);
            }
        });

        UDepartment2.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        UDepartment2.setText("Department");

        UDepartmentCombo2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Department", "Department of Computer Science", "Department of English", "Department of Mathematics", "Department of Physics" }));

        UAddress2.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        UAddress2.setText("Address");

        UEmail2.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        UEmail2.setText("Email");

        UEmailField2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                UEmailField2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                UEmailField2FocusLost(evt);
            }
        });

        UPassword2.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        UPassword2.setText("Password");

        UStatus2.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        UStatus2.setText("Status");

        UActive2.setFont(new java.awt.Font("Bodoni MT", 1, 14)); // NOI18N
        UActive2.setText("Active");
        UActive2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                UActive2MouseClicked(evt);
            }
        });
        UActive2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                UActive2KeyPressed(evt);
            }
        });

        UInactive2.setFont(new java.awt.Font("Bodoni MT", 1, 14)); // NOI18N
        UInactive2.setText("In-Active");
        UInactive2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                UInactive2MouseClicked(evt);
            }
        });
        UInactive2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                UInactive2KeyPressed(evt);
            }
        });

        USubmitBtn2.setBackground(new java.awt.Color(151, 137, 219));
        USubmitBtn2.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        USubmitBtn2.setText("Submit");
        USubmitBtn2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                USubmitBtn2MouseClicked(evt);
            }
        });
        USubmitBtn2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                USubmitBtn2KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout UpdateInstructorLayout = new javax.swing.GroupLayout(UpdateInstructor);
        UpdateInstructor.setLayout(UpdateInstructorLayout);
        UpdateInstructorLayout.setHorizontalGroup(
            UpdateInstructorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UpdateInstructorLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(UpdateInstructorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(UAddressField2)
                    .addComponent(UEmailField2)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, UpdateInstructorLayout.createSequentialGroup()
                        .addComponent(UStatus2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(UActive2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(UInactive2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(USubmitBtn2))
                    .addComponent(UPasswordField2)
                    .addGroup(UpdateInstructorLayout.createSequentialGroup()
                        .addGroup(UpdateInstructorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(UIdField2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(UId2))
                        .addGap(12, 12, 12)
                        .addGroup(UpdateInstructorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(UFirstNameField2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(UFirstName2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                        .addGroup(UpdateInstructorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(UpdateInstructorLayout.createSequentialGroup()
                                .addComponent(ULastName2)
                                .addGap(83, 83, 83)
                                .addComponent(UDateOfBirth2))
                            .addGroup(UpdateInstructorLayout.createSequentialGroup()
                                .addComponent(ULastNameField2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(UDateOfBirthField2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(UDepartmentCombo2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(UpdateInstructorLayout.createSequentialGroup()
                        .addGroup(UpdateInstructorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(UDepartment2)
                            .addComponent(UEmail2)
                            .addComponent(UPassword2)
                            .addComponent(UAddress2))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(UpdateInstructorLayout.createSequentialGroup()
                        .addGroup(UpdateInstructorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(UCNIC2)
                            .addComponent(UCNICField2, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(UpdateInstructorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(UPhoneNoField2, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(UPhoneNo2))
                        .addGap(63, 63, 63)
                        .addGroup(UpdateInstructorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(UGender2)
                            .addComponent(UGenderCombo2, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(40, 40, 40))
        );
        UpdateInstructorLayout.setVerticalGroup(
            UpdateInstructorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UpdateInstructorLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(UpdateInstructorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(UFirstName2)
                    .addComponent(ULastName2)
                    .addComponent(UDateOfBirth2)
                    .addComponent(UId2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(UpdateInstructorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(UDateOfBirthField2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(UpdateInstructorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(UIdField2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(UFirstNameField2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ULastNameField2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(UpdateInstructorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(UPhoneNo2)
                    .addComponent(UGender2)
                    .addComponent(UCNIC2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(UpdateInstructorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(UPhoneNoField2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(UGenderCombo2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(UCNICField2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(UDepartment2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(UDepartmentCombo2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(UAddress2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(UAddressField2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(UEmail2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(UEmailField2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(UPassword2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(UPasswordField2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(UpdateInstructorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(USubmitBtn2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(UStatus2)
                    .addComponent(UActive2)
                    .addComponent(UInactive2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        InstructorTabs.addTab("Update Instructor", new javax.swing.ImageIcon(getClass().getResource("/main/resources/icons8-update-16.png")), UpdateInstructor); // NOI18N

        ViewInstructors.setOpaque(false);

        ViewInstructorsTable.setFont(new java.awt.Font("Bodoni MT", 0, 14)); // NOI18N
        ViewInstructorsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "First Name", "Last Name", "Status", "Department", "View", "Delete"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ViewInstructorsTable.setRowHeight(50);
        ViewInstructorsTable.setRowSelectionAllowed(false);
        ViewInstructorsTable.setShowGrid(false);
        ViewInstructorsTable.setShowHorizontalLines(true);
        ViewInstructorsTable.setShowVerticalLines(true);
        ViewInstructorsTable.getTableHeader().setResizingAllowed(false);
        ViewInstructorsTable.getTableHeader().setReorderingAllowed(false);
        ViewInstructorsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ViewInstructorsTableMouseClicked(evt);
            }
        });
        ViewInstructorsScrollPane.setViewportView(ViewInstructorsTable);

        javax.swing.GroupLayout ViewInstructorsLayout = new javax.swing.GroupLayout(ViewInstructors);
        ViewInstructors.setLayout(ViewInstructorsLayout);
        ViewInstructorsLayout.setHorizontalGroup(
            ViewInstructorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ViewInstructorsLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(ViewInstructorsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 617, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(50, Short.MAX_VALUE))
        );
        ViewInstructorsLayout.setVerticalGroup(
            ViewInstructorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ViewInstructorsLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(ViewInstructorsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 515, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        InstructorTabs.addTab("View Instructors", new javax.swing.ImageIcon(getClass().getResource("/main/resources/icons8-view-16.png")), ViewInstructors); // NOI18N

        javax.swing.GroupLayout InstructorPanelLayout = new javax.swing.GroupLayout(InstructorPanel);
        InstructorPanel.setLayout(InstructorPanelLayout);
        InstructorPanelLayout.setHorizontalGroup(
            InstructorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(InstructorTabs)
        );
        InstructorPanelLayout.setVerticalGroup(
            InstructorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(InstructorTabs)
        );

        MainPagePanel.add(InstructorPanel, "Card2");

        CoursePanel.setOpaque(false);

        CourseTabs.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        CourseTabs.setFont(new java.awt.Font("Bodoni MT", 1, 14)); // NOI18N

        AddCourse.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        AddCourse.setOpaque(false);

        ACourseCode.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        ACourseCode.setText("Course Code");

        ACourseName.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        ACourseName.setText("Course Name");

        ACreditHrs.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        ACreditHrs.setText("Credit Hours");

        ASession.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        ASession.setText("Session");

        ASessionCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Session", "Fall", "Spring", "Summer" }));

        AAcademicYear.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        AAcademicYear.setText("Academic Year");

        ACDepartment.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        ACDepartment.setText("Department");

        ACDepartmentCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Department", "Department of Computer Science", "Department of English", "Department of Mathematics", "Department of Physics" }));

        ASeats.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        ASeats.setText("Seats");

        ACSubmitBtn.setBackground(new java.awt.Color(151, 137, 219));
        ACSubmitBtn.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        ACSubmitBtn.setText("Submit");
        ACSubmitBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ACSubmitBtnMouseClicked(evt);
            }
        });
        ACSubmitBtn.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ACSubmitBtnKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout AddCourseLayout = new javax.swing.GroupLayout(AddCourse);
        AddCourse.setLayout(AddCourseLayout);
        AddCourseLayout.setHorizontalGroup(
            AddCourseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AddCourseLayout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addGroup(AddCourseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ACDepartment)
                    .addComponent(ASeats)
                    .addGroup(AddCourseLayout.createSequentialGroup()
                        .addGroup(AddCourseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ASession)
                            .addGroup(AddCourseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AddCourseLayout.createSequentialGroup()
                                    .addComponent(ACourseCode)
                                    .addGap(60, 60, 60))
                                .addComponent(ACourseCodeField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(AddCourseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AddCourseLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(AddCourseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(ACourseName)
                                    .addComponent(ACourseNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(73, 73, 73)
                                .addGroup(AddCourseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(ACreditHrsField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ACreditHrs)))
                            .addGroup(AddCourseLayout.createSequentialGroup()
                                .addGap(297, 297, 297)
                                .addComponent(AAcademicYear)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AddCourseLayout.createSequentialGroup()
                        .addComponent(ASessionCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(AAcademicYearField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(ASeatsField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 597, Short.MAX_VALUE)
                    .addComponent(ACDepartmentCombo, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AddCourseLayout.createSequentialGroup()
                .addGap(317, 317, 317)
                .addComponent(ACSubmitBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(317, 317, 317))
        );
        AddCourseLayout.setVerticalGroup(
            AddCourseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AddCourseLayout.createSequentialGroup()
                .addGroup(AddCourseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(AddCourseLayout.createSequentialGroup()
                        .addContainerGap(36, Short.MAX_VALUE)
                        .addGroup(AddCourseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(AddCourseLayout.createSequentialGroup()
                                .addComponent(ACreditHrs)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ACreditHrsField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(AddCourseLayout.createSequentialGroup()
                                .addComponent(ACourseCode)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ACourseCodeField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(AddCourseLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(ACourseName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ACourseNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(45, 45, 45)
                .addGroup(AddCourseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ASession)
                    .addComponent(AAcademicYear))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(AddCourseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ASessionCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AAcademicYearField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addComponent(ACDepartment)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ACDepartmentCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(ASeats)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ASeatsField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(ACSubmitBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                .addGap(43, 43, 43))
        );

        CourseTabs.addTab("Add Course", new javax.swing.ImageIcon(getClass().getResource("/main/resources/icons8-add-16.png")), AddCourse); // NOI18N

        UpdateCourse.setOpaque(false);

        UCourseCode.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        UCourseCode.setText("Course Code");

        UCourseCodeField.setForeground(new java.awt.Color(153, 153, 153));
        UCourseCodeField.setText("Enter Code to Search");
        UCourseCodeField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                UCourseCodeFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                UCourseCodeFieldFocusLost(evt);
            }
        });
        UCourseCodeField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                UCourseCodeFieldKeyPressed(evt);
            }
        });

        UCourseName.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        UCourseName.setText("Course Name");

        UCreditHrs.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        UCreditHrs.setText("Credit Hours");

        USession.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        USession.setText("Session");

        USessionCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Session", "Fall", "Spring", "Summer" }));

        UAcademicYear.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        UAcademicYear.setText("Academic Year");

        UCDepartment.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        UCDepartment.setText("Department");

        UCDepartmentCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Department", "Department of Computer Science", "Department of English", "Department of Mathematics", "Department of Physics" }));

        USeats.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        USeats.setText("Seats");

        UCSubmitBtn.setBackground(new java.awt.Color(151, 137, 219));
        UCSubmitBtn.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        UCSubmitBtn.setText("Submit");
        UCSubmitBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                UCSubmitBtnMouseClicked(evt);
            }
        });
        UCSubmitBtn.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                UCSubmitBtnKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout UpdateCourseLayout = new javax.swing.GroupLayout(UpdateCourse);
        UpdateCourse.setLayout(UpdateCourseLayout);
        UpdateCourseLayout.setHorizontalGroup(
            UpdateCourseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UpdateCourseLayout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addGroup(UpdateCourseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(UCDepartment)
                    .addComponent(USeats)
                    .addComponent(UCDepartmentCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 597, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(USeatsField)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, UpdateCourseLayout.createSequentialGroup()
                        .addGroup(UpdateCourseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(UpdateCourseLayout.createSequentialGroup()
                                .addGroup(UpdateCourseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(UpdateCourseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, UpdateCourseLayout.createSequentialGroup()
                                            .addComponent(UCourseCode)
                                            .addGap(60, 60, 60))
                                        .addComponent(UCourseCodeField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(USession))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(UpdateCourseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(UCourseNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(UCourseName))
                                .addGap(74, 74, 74))
                            .addGroup(UpdateCourseLayout.createSequentialGroup()
                                .addComponent(USessionCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(221, 221, 221)))
                        .addGroup(UpdateCourseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(UAcademicYearField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(UCreditHrsField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(UCreditHrs)
                            .addComponent(UAcademicYear))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, UpdateCourseLayout.createSequentialGroup()
                .addGap(317, 317, 317)
                .addComponent(UCSubmitBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(317, 317, 317))
        );
        UpdateCourseLayout.setVerticalGroup(
            UpdateCourseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UpdateCourseLayout.createSequentialGroup()
                .addGroup(UpdateCourseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(UpdateCourseLayout.createSequentialGroup()
                        .addGroup(UpdateCourseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(UpdateCourseLayout.createSequentialGroup()
                                .addContainerGap(36, Short.MAX_VALUE)
                                .addComponent(UCourseCode)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(UCourseCodeField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(UpdateCourseLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(UCourseName)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(UCourseNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(45, 45, 45)
                        .addComponent(USession)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(USessionCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40)
                        .addComponent(UCDepartment))
                    .addGroup(UpdateCourseLayout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(UCreditHrs)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(UCreditHrsField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(45, 45, 45)
                        .addComponent(UAcademicYear)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(UAcademicYearField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(UCDepartmentCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(USeats)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(USeatsField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(UCSubmitBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                .addGap(43, 43, 43))
        );

        CourseTabs.addTab("Update Course", new javax.swing.ImageIcon(getClass().getResource("/main/resources/icons8-update-16.png")), UpdateCourse); // NOI18N

        ViewCourses.setOpaque(false);

        ViewCoursesTable.setFont(new java.awt.Font("Bodoni MT", 0, 14)); // NOI18N
        ViewCoursesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Course Code", "Course Name", "Credit Hours", "Seats", "View", "Delete"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
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
        ViewCoursesTable.setShowGrid(false);
        ViewCoursesTable.setShowHorizontalLines(true);
        ViewCoursesTable.setShowVerticalLines(true);
        ViewCoursesTable.getTableHeader().setResizingAllowed(false);
        ViewCoursesTable.getTableHeader().setReorderingAllowed(false);
        ViewCoursesTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ViewCoursesTableMouseClicked(evt);
            }
        });
        ViewCoursesScrollPane.setViewportView(ViewCoursesTable);

        javax.swing.GroupLayout ViewCoursesLayout = new javax.swing.GroupLayout(ViewCourses);
        ViewCourses.setLayout(ViewCoursesLayout);
        ViewCoursesLayout.setHorizontalGroup(
            ViewCoursesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ViewCoursesLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(ViewCoursesScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 617, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(50, Short.MAX_VALUE))
        );
        ViewCoursesLayout.setVerticalGroup(
            ViewCoursesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ViewCoursesLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(ViewCoursesScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 515, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        CourseTabs.addTab("View Courses", new javax.swing.ImageIcon(getClass().getResource("/main/resources/icons8-view-16.png")), ViewCourses); // NOI18N

        javax.swing.GroupLayout CoursePanelLayout = new javax.swing.GroupLayout(CoursePanel);
        CoursePanel.setLayout(CoursePanelLayout);
        CoursePanelLayout.setHorizontalGroup(
            CoursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(CourseTabs)
        );
        CoursePanelLayout.setVerticalGroup(
            CoursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(CourseTabs)
        );

        MainPagePanel.add(CoursePanel, "Card3");

        TimeTablePanel.setOpaque(false);

        Day.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        Day.setText("Day");

        DayCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Day", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" }));

        StartTime.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        StartTime.setText("Start Time");

        HoursCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Hour", "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", " " }));

        MinutesCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Minute", "00", "15", "30", "45" }));

        EndTime.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        EndTime.setText("End Time");

        HoursCombo1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Hour", "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", " " }));

        MinutesCombo1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Minute", "00", "15", "30", "45" }));

        Course.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        Course.setText("Course");

        CourseCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Course" }));

        Room.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        Room.setText("Room");

        RoomCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Room", "B301", "B302", "B303", "B304", "B305" }));

        Instructor.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        Instructor.setText("Instructor");

        InstructorCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Instructor" }));

        Add.setBackground(new java.awt.Color(151, 137, 219));
        Add.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        Add.setText("Add");
        Add.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                AddMouseClicked(evt);
            }
        });
        Add.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                AddKeyPressed(evt);
            }
        });

        Update.setBackground(new java.awt.Color(151, 137, 219));
        Update.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        Update.setText("Update");
        Update.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                UpdateMouseClicked(evt);
            }
        });
        Update.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                UpdateKeyPressed(evt);
            }
        });

        Delete.setBackground(new java.awt.Color(151, 137, 219));
        Delete.setFont(new java.awt.Font("Bodoni MT", 1, 18)); // NOI18N
        Delete.setText("Delete");
        Delete.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                DeleteMouseClicked(evt);
            }
        });
        Delete.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DeleteKeyPressed(evt);
            }
        });

        ViewTimeTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Course", "Instructor", "Day", "Start Time", "End Time", "Room"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ViewTimeTable.setShowHorizontalLines(true);
        ViewTimeTable.setShowVerticalLines(true);
        ViewTimeTable.getTableHeader().setResizingAllowed(false);
        ViewTimeTable.getTableHeader().setReorderingAllowed(false);
        ViewTimeTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ViewTimeTableMouseClicked(evt);
            }
        });
        TimeTableScrollPane.setViewportView(ViewTimeTable);

        javax.swing.GroupLayout TimeTablePanelLayout = new javax.swing.GroupLayout(TimeTablePanel);
        TimeTablePanel.setLayout(TimeTablePanelLayout);
        TimeTablePanelLayout.setHorizontalGroup(
            TimeTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TimeTablePanelLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(TimeTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(TimeTablePanelLayout.createSequentialGroup()
                        .addGroup(TimeTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(TimeTablePanelLayout.createSequentialGroup()
                                .addGroup(TimeTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(Day)
                                    .addComponent(DayCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(TimeTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(StartTime)
                                    .addGroup(TimeTablePanelLayout.createSequentialGroup()
                                        .addComponent(HoursCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(TimeTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(Room)
                                            .addComponent(MinutesCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addComponent(EndTime))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(TimeTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Course)
                            .addComponent(CourseCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Instructor)))
                    .addGroup(TimeTablePanelLayout.createSequentialGroup()
                        .addGroup(TimeTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(TimeTablePanelLayout.createSequentialGroup()
                                .addComponent(HoursCombo1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(MinutesCombo1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(RoomCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(TimeTablePanelLayout.createSequentialGroup()
                                .addComponent(Add, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(Update, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(101, 101, 101)))
                        .addGroup(TimeTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(InstructorCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Delete, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(TimeTableScrollPane))
                .addContainerGap(37, Short.MAX_VALUE))
        );
        TimeTablePanelLayout.setVerticalGroup(
            TimeTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TimeTablePanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(TimeTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(TimeTablePanelLayout.createSequentialGroup()
                        .addGroup(TimeTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(StartTime)
                            .addComponent(Course))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(TimeTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(HoursCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(MinutesCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CourseCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(TimeTablePanelLayout.createSequentialGroup()
                        .addComponent(Day)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DayCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(TimeTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(EndTime)
                    .addComponent(Room)
                    .addComponent(Instructor))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(TimeTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(HoursCombo1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(MinutesCombo1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(RoomCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(InstructorCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(TimeTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Add, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Update, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Delete, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(TimeTableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        MainPagePanel.add(TimeTablePanel, "Card4");

        javax.swing.GroupLayout ResultPanelLayout = new javax.swing.GroupLayout(ResultPanel);
        ResultPanel.setLayout(ResultPanelLayout);
        ResultPanelLayout.setHorizontalGroup(
            ResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 705, Short.MAX_VALUE)
        );
        ResultPanelLayout.setVerticalGroup(
            ResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        MainPagePanel.add(ResultPanel, "Card5");

        javax.swing.GroupLayout HomePageAdminPanelLayout = new javax.swing.GroupLayout(HomePageAdminPanel);
        HomePageAdminPanel.setLayout(HomePageAdminPanelLayout);
        HomePageAdminPanelLayout.setHorizontalGroup(
            HomePageAdminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HomePageAdminPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(SidePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(MainPagePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 705, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        HomePageAdminPanelLayout.setVerticalGroup(
            HomePageAdminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HomePageAdminPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(HomePageAdminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(HomePageAdminPanelLayout.createSequentialGroup()
                        .addComponent(SidePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 588, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(MainPagePanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 588, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(HomePageAdminPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(HomePageAdminPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void ManageStudentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ManageStudentMouseClicked
        // TODO add your handling code here:
        CardLayout c1 = (CardLayout)(MainPagePanel.getLayout());
        c1.show(MainPagePanel,"Card1");
        show_Table("student", ViewStudentsTable);
    }//GEN-LAST:event_ManageStudentMouseClicked

    private void ManageStudentKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ManageStudentKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER) {
            ManageStudentMouseClicked(null);
        }
    }//GEN-LAST:event_ManageStudentKeyPressed

    private void ManageInstructorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ManageInstructorMouseClicked
        // TODO add your handling code here:
        CardLayout c1 = (CardLayout)(MainPagePanel.getLayout());
        c1.show(MainPagePanel,"Card2");
        show_Table("instructor", ViewInstructorsTable);
    }//GEN-LAST:event_ManageInstructorMouseClicked

    private void ManageInstructorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ManageInstructorKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER) {
            ManageInstructorMouseClicked(null);
        }
    }//GEN-LAST:event_ManageInstructorKeyPressed

    private void ManageCourseMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ManageCourseMouseClicked
        // TODO add your handling code here:
        CardLayout c1 = (CardLayout)(MainPagePanel.getLayout());
        c1.show(MainPagePanel,"Card3");
        show_Table("course", ViewCoursesTable);
    }//GEN-LAST:event_ManageCourseMouseClicked

    private void ManageCourseKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ManageCourseKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER) {
            ManageCourseMouseClicked(null);
        }
    }//GEN-LAST:event_ManageCourseKeyPressed

    private void TimeTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TimeTableMouseClicked
        // TODO add your handling code here:
        CardLayout c1 = (CardLayout)(MainPagePanel.getLayout());
        c1.show(MainPagePanel,"Card4");
        show_Table("timetable", ViewTimeTable);
        
        // 1. Populate Course Combo and Map
        CourseCombo.removeAllItems();
        CourseCombo.addItem("Select Course");
        courseMap.clear();
        String query_course = "SELECT `coursecode`, `coursename` FROM `course`";
        try {
            pst = connect.prepareStatement(query_course);
            result = pst.executeQuery();
            while (result.next()) {
                String code = result.getString("coursecode");
                String name = result.getString("coursename");
                courseMap.put(name, code);
                CourseCombo.addItem(name);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }

        // 2. Populate Instructor Combo and Map
        InstructorCombo.removeAllItems();
        InstructorCombo.addItem("Select Instructor");
        instructorMap.clear();
        String query_instructor = "SELECT `ID`, CONCAT(`fName`, ' ', `lName`) AS `fullName` FROM `instructor`";
        try {
            pst = connect.prepareStatement(query_instructor);
            result = pst.executeQuery();
            while(result.next()){
                String id = result.getString("ID");
                String name = result.getString("fullName");
                instructorMap.put(name, id);
                InstructorCombo.addItem(name);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }//GEN-LAST:event_TimeTableMouseClicked

    private void TimeTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TimeTableKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER) {
            TimeTableMouseClicked(null);
        }
    }//GEN-LAST:event_TimeTableKeyPressed

    private void PublishResultMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PublishResultMouseClicked
        // TODO add your handling code here:
        CardLayout c1 = (CardLayout)(MainPagePanel.getLayout());
        c1.show(MainPagePanel,"Card5");
    }//GEN-LAST:event_PublishResultMouseClicked

    private void PublishResultKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PublishResultKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER) {
            PublishResultMouseClicked(null);
        }
    }//GEN-LAST:event_PublishResultKeyPressed

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

    private void AFirstNameFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_AFirstNameFieldFocusGained
        // TODO add your handling code here:
        if(AFirstNameField.getText().equals("John")){
            AFirstNameField.setText("");
            AFirstNameField.setForeground(new Color(0,0,0));
        }
    }//GEN-LAST:event_AFirstNameFieldFocusGained

    private void AFirstNameFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_AFirstNameFieldFocusLost
        // TODO add your handling code here:
        if(AFirstNameField.getText().equals("")) {
            AFirstNameField.setText("John");
            AFirstNameField.setForeground(new Color(153,153,153));
        } else {
            String PATTERN = "^[a-zA-Z]{1,20}$";
            Pattern pattern = Pattern.compile(PATTERN);
            Matcher match = pattern.matcher(AFirstNameField.getText());
            if (!match.matches()) {
                AFirstName.setForeground(Color.RED);
                temp = false;
            } else {
                AFirstName.setForeground(Color.BLACK);
                temp = true;
            }
        }
    }//GEN-LAST:event_AFirstNameFieldFocusLost

    private void ALastNameFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ALastNameFieldFocusGained
        // TODO add your handling code here:
        if(ALastNameField.getText().equals("Doe")){
            ALastNameField.setText("");
            ALastNameField.setForeground(new Color(0,0,0));
        }
        
    }//GEN-LAST:event_ALastNameFieldFocusGained

    private void ALastNameFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ALastNameFieldFocusLost
        // TODO add your handling code here:
        if(ALastNameField.getText().equals("")) {
            ALastNameField.setText("Doe");
            ALastNameField.setForeground(new Color(153,153,153));
        } else {
            String PATTERN = "^[a-zA-Z]{1,20}$";
            Pattern pattern = Pattern.compile(PATTERN);
            Matcher match = pattern.matcher(ALastNameField.getText());
            if (!match.matches()) {
                ALastName.setForeground(Color.RED);
                temp = false;
            } else {
                ALastName.setForeground(Color.BLACK);
                temp = true;
            }
        }
    }//GEN-LAST:event_ALastNameFieldFocusLost

    private void ADateOfBirthFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ADateOfBirthFieldFocusGained
        // TODO add your handling code here:
        if(AdateTextField.getText().equals("DD-MM-YYYY")){
            AdateTextField.setText("");
            ADateOfBirthField.setForeground(new Color(0,0,0));
        }
    }//GEN-LAST:event_ADateOfBirthFieldFocusGained

    private void ADateOfBirthFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ADateOfBirthFieldFocusLost
        // TODO add your handling code here:
        if(AdateTextField.getText().equals("")) {
            AdateTextField.setText("DD-MM-YYYY");
            ADateOfBirthField.setForeground(new Color(153,153,153));
        }
    }//GEN-LAST:event_ADateOfBirthFieldFocusLost

    private void ACNICFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ACNICFieldFocusGained
        // TODO add your handling code here:
        if (ACNICField.getText().trim().equals("00000-0000000-0")) {
            ACNICField.setText("");
            ACNICField.setForeground(new Color(0,0,0));
        }
    }//GEN-LAST:event_ACNICFieldFocusGained

    private void ACNICFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ACNICFieldFocusLost
        // TODO add your handling code here:
        if (ACNICField.getText().trim().equals("")) {
            ACNICField.setText("00000-0000000-0");
            ACNICField.setForeground(new Color(153,153,153));
        } else {
            String PATTERN = "^[0-9]{5}[-][0-9]{7}[-][0-9]{1}$";
            Pattern pattern = Pattern.compile(PATTERN);
            Matcher match = pattern.matcher(ACNICField.getText());
            if (!match.matches()) {
                ACNIC.setForeground(Color.RED);
                temp = false;
            } else {
                ACNIC.setForeground(Color.BLACK);
                temp = true;
            }
        }
    }//GEN-LAST:event_ACNICFieldFocusLost

    private void APhoneNoFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_APhoneNoFieldFocusGained
        // TODO add your handling code here:
        if (APhoneNoField.getText().trim().equals("0333-1234567")) {
            APhoneNoField.setText("");
            APhoneNoField.setForeground(new Color(0,0,0));
        }
    }//GEN-LAST:event_APhoneNoFieldFocusGained

    private void APhoneNoFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_APhoneNoFieldFocusLost
        // TODO add your handling code here:
        if (APhoneNoField.getText().trim().equals("")) {
            APhoneNoField.setText("0333-1234567");
            APhoneNoField.setForeground(new Color(153,153,153));
        } else {
            String PATTERN = "^[0-9]{4}[-][0-9]{7}$";
            Pattern pattern = Pattern.compile(PATTERN);
            Matcher match = pattern.matcher(APhoneNoField.getText());
            if (!match.matches()) {
                APhoneNo.setForeground(Color.RED);
                temp = false;
            } else {
                APhoneNo.setForeground(Color.BLACK);
                temp = true;
            }
        }
    }//GEN-LAST:event_APhoneNoFieldFocusLost

    private void AProgramFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_AProgramFieldFocusGained
        // TODO add your handling code here:
        if (AProgramField.getText().trim().equals("Program Name")) {
            AProgramField.setText("");
            AProgramField.setForeground(new Color(0,0,0));
        }
    }//GEN-LAST:event_AProgramFieldFocusGained

    private void AProgramFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_AProgramFieldFocusLost
        // TODO add your handling code here:
        if (AProgramField.getText().trim().equals("")) {
            AProgramField.setText("Program Name");
            AProgramField.setForeground(new Color(153,153,153));
        }
    }//GEN-LAST:event_AProgramFieldFocusLost

    private void AEmailFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_AEmailFieldFocusGained
        // TODO add your handling code here:
        if (AEmailField.getText().trim().equals("user@domain.com")) {
            AEmailField.setText("");
            AEmailField.setForeground(new Color(0,0,0));
        }
    }//GEN-LAST:event_AEmailFieldFocusGained

    private void AEmailFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_AEmailFieldFocusLost
        // TODO add your handling code here:
        if (AEmailField.getText().trim().equals("")) {
            AEmailField.setText("user@domain.com");
            AEmailField.setForeground(new Color(153,153,153));
        } else {
            String PATTERN = "^(.+)@(.+)$";
            Pattern pattern = Pattern.compile(PATTERN);
            Matcher match = pattern.matcher(AEmailField.getText());
            if (!match.matches()) {
                AEmail.setForeground(Color.RED);
                temp = false;
            } else {
                AEmail.setForeground(Color.BLACK);
                temp = true;
            }
        }
    }//GEN-LAST:event_AEmailFieldFocusLost

    private void ASubmitBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ASubmitBtnMouseClicked
        // TODO add your handling code here:
        String fName = AFirstNameField.getText();
        String lName = ALastNameField.getText();
        String dob = AdateTextField.getText();
        cnic = ACNICField.getText();
        String phoneno = APhoneNoField.getText();
        String gender = (String)AGenderCombo.getSelectedItem();
        String department = (String)ADepartmentCombo.getSelectedItem();
        String program = AProgramField.getText();
        String address = AAddressField.getText();
        email = AEmailField.getText();

        if(fName.equals("John") || fName.isBlank()){
            AFirstName.setForeground(Color.RED);
        } else {
            AFirstName.setForeground(Color.BLACK);
            temp = true;
        }
        if(lName.equals("Doe") || lName.isBlank()){
            ALastName.setForeground(Color.RED);
        } else {
            ALastName.setForeground(Color.BLACK);
            temp = true;
        }
        if(dob.equals("DD-MM-YYYY") || fName.isBlank()){
            ADateOfBirth.setForeground(Color.RED);
        } else {
            ADateOfBirth.setForeground(Color.BLACK);
            temp = true;
        }
        if(cnic.equals("00000-0000000-0") || cnic.isBlank()){
            ACNIC.setForeground(Color.RED);
        } else {
            ACNIC.setForeground(Color.BLACK);
            temp = true;
        }
        if(phoneno.equals("0333-1234567") || phoneno.isBlank()){
            APhoneNo.setForeground(Color.RED);
        } else {
            APhoneNo.setForeground(Color.BLACK);
            temp = true;
        }
        if(gender.equals("Select Gender")){
            AGender.setForeground(Color.RED);
        } else {
            AGender.setForeground(Color.BLACK);
            temp = true;
        }
        if(department.equals("Select Department")){
            ADepartment.setForeground(Color.RED);
        } else {
            ADepartment.setForeground(Color.BLACK);
            temp = true;
        }
        if(program.equals("Program Name") || program.isBlank()){
            AProgram.setForeground(Color.RED);
        } else {
            AProgram.setForeground(Color.BLACK);
            temp = true;
        }
        if(address.isBlank()){
            AAddress.setForeground(Color.RED);
        } else {
            AAddress.setForeground(Color.BLACK);
            temp = true;
        }
        if(email.equals("user@domain.com") || email.isBlank()){
            AEmail.setForeground(Color.RED);
        } else {
            AEmail.setForeground(Color.BLACK);
            temp = true;
        }
        if(checkEmail(email) || checkCNIC(cnic)){
            UserAlreadyExists useralreadyexists = new UserAlreadyExists();
            useralreadyexists.setVisible(true);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    useralreadyexists.dispose();
                }
            }, 1000);
        } else {
            if(temp == true){
                String query = "INSERT INTO `student`(`fName`, `lName`, `dob`, `cnic`, `phoneno`, `gender`, `department`, `program`, `address`, `email`) VALUES (?,?,?,?,?,?,?,?,?,?)";
                try {
                    pst = connect.prepareStatement(query);

                    pst.setString(1, AFirstNameField.getText());
                    pst.setString(2, ALastNameField.getText());
                    pst.setString(3, AdateTextField.getText());
                    pst.setString(4, ACNICField.getText());
                    pst.setString(5, APhoneNoField.getText());
                    pst.setString(6, (String)AGenderCombo.getSelectedItem());
                    pst.setString(7, (String)ADepartmentCombo.getSelectedItem());
                    pst.setString(8, AProgramField.getText());
                    pst.setString(9, AAddressField.getText());
                    pst.setString(10, AEmailField.getText());

                    pst.execute();
                    DataAdded dataadded = new DataAdded();
                    dataadded.setVisible(true);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            dataadded.dispose();
                        }
                    }, 1000);
                    show_Table("student", ViewStudentsTable);
                    AFirstNameField.setText("");
                    ALastNameField.setText("");
                    AdateTextField.setText("");
                    ACNICField.setText("");
                    APhoneNoField.setText("");
                    AGenderCombo.setSelectedItem("Select Gender");
                    ADepartmentCombo.setSelectedItem("Select Department");
                    AProgramField.setText("");
                    AAddressField.setText("");
                    AEmailField.setText("");

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,ex);
                }
            }
        }
    }//GEN-LAST:event_ASubmitBtnMouseClicked

    private void ASubmitBtnKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ASubmitBtnKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            ASubmitBtnMouseClicked(null);
        }
    }//GEN-LAST:event_ASubmitBtnKeyPressed

    private void UFirstNameFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_UFirstNameFieldFocusGained
        // TODO add your handling code here:
        UFirstNameField.setForeground(new Color(0,0,0));
    }//GEN-LAST:event_UFirstNameFieldFocusGained

    private void UFirstNameFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_UFirstNameFieldFocusLost
        // TODO add your handling code here:
        if(UFirstNameField.getText().equals("")) {
            UFirstNameField.setText("John");
            UFirstNameField.setForeground(new Color(153,153,153));
        } else {
            String PATTERN = "^[a-zA-Z]{1,20}$";
            Pattern pattern = Pattern.compile(PATTERN);
            Matcher match = pattern.matcher(UFirstNameField.getText());
            if (!match.matches()) {
                UFirstName.setForeground(Color.RED);
                temp = false;
            } else {
                UFirstName.setForeground(Color.BLACK);
                temp = true;
            }
        }
    }//GEN-LAST:event_UFirstNameFieldFocusLost

    private void ULastNameFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ULastNameFieldFocusGained
        // TODO add your handling code here:
        ULastNameField.setForeground(new Color(0,0,0));
    }//GEN-LAST:event_ULastNameFieldFocusGained

    private void ULastNameFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ULastNameFieldFocusLost
        // TODO add your handling code here:
        if(ULastNameField.getText().equals("")) {
            ULastNameField.setText("Doe");
            ULastNameField.setForeground(new Color(153,153,153));
        } else {
            String PATTERN = "^[a-zA-Z]{1,20}$";
            Pattern pattern = Pattern.compile(PATTERN);
            Matcher match = pattern.matcher(ULastNameField.getText());
            if (!match.matches()) {
                ULastName.setForeground(Color.RED);
                temp = false;
            } else {
                ULastName.setForeground(Color.BLACK);
                temp = true;
            }
        }
    }//GEN-LAST:event_ULastNameFieldFocusLost

    private void UPhoneNoFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_UPhoneNoFieldFocusGained
        // TODO add your handling code here:
        UPhoneNoField.setForeground(new Color(0,0,0));
    }//GEN-LAST:event_UPhoneNoFieldFocusGained

    private void UPhoneNoFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_UPhoneNoFieldFocusLost
        // TODO add your handling code here:
        if (UPhoneNoField.getText().trim().equals("")) {
            UPhoneNoField.setText("0333-1234567");
            UPhoneNoField.setForeground(new Color(153,153,153));
        } else {
            String PATTERN = "^[0-9]{4}[-][0-9]{7}$";
            Pattern pattern = Pattern.compile(PATTERN);
            Matcher match = pattern.matcher(UPhoneNoField.getText());
            if (!match.matches()) {
                UPhoneNo.setForeground(Color.RED);
                temp = false;
            } else {
                UPhoneNo.setForeground(Color.BLACK);
                temp = true;
            }
        }
    }//GEN-LAST:event_UPhoneNoFieldFocusLost

    private void UDateOfBirthFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_UDateOfBirthFieldFocusGained
        // TODO add your handling code here:
        UDateOfBirthField.setForeground(new Color(0,0,0));
    }//GEN-LAST:event_UDateOfBirthFieldFocusGained

    private void UDateOfBirthFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_UDateOfBirthFieldFocusLost
        // TODO add your handling code here:
        if(UdateTextField.getText().equals("")) {
            UdateTextField.setText("DD-MM-YYYY");
            UDateOfBirthField.setForeground(new Color(153,153,153));
        }
    }//GEN-LAST:event_UDateOfBirthFieldFocusLost

    private void UProgramFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_UProgramFieldFocusGained
        // TODO add your handling code here:
        UProgramField.setForeground(new Color(0,0,0));
    }//GEN-LAST:event_UProgramFieldFocusGained

    private void UProgramFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_UProgramFieldFocusLost
        // TODO add your handling code here:
        if (UProgramField.getText().trim().equals("")) {
            UProgramField.setText("Program Name");
            UProgramField.setForeground(new Color(153,153,153));
        }
    }//GEN-LAST:event_UProgramFieldFocusLost

    private void UEmailFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_UEmailFieldFocusGained
        // TODO add your handling code here:
        UEmailField.setForeground(new Color(0,0,0));
    }//GEN-LAST:event_UEmailFieldFocusGained

    private void UEmailFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_UEmailFieldFocusLost
        // TODO add your handling code here:
        if (UEmailField.getText().trim().equals("")) {
            UEmailField.setText("user@domain.com");
            UEmailField.setForeground(new Color(153,153,153));
        } else {
            String PATTERN = "^(.+)@(.+)$";
            Pattern pattern = Pattern.compile(PATTERN);
            Matcher match = pattern.matcher(UEmailField.getText());
            if (!match.matches()) {
                UEmail.setForeground(Color.RED);
                temp = false;
            } else {
                UEmail.setForeground(Color.BLACK);
                temp = true;
            }
        }
    }//GEN-LAST:event_UEmailFieldFocusLost

    private void UIdFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_UIdFieldFocusGained
        // TODO add your handling code here:
        if (UIdField.getText().trim().equals("Enter ID to Search")) {
            UIdField.setText("");
            UIdField.setForeground(new Color(0,0,0));
        }
    }//GEN-LAST:event_UIdFieldFocusGained

    private void UIdFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_UIdFieldFocusLost
        // TODO add your handling code here:
        if (UIdField.getText().trim().equals("")) {
            UIdField.setText("Enter ID to Search");
            UIdField.setForeground(new Color(153,153,153));
        }
    }//GEN-LAST:event_UIdFieldFocusLost

    private void UIdFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_UIdFieldKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER) {
            String ID = UIdField.getText();
            try {
                String query = "SELECT * FROM `student` WHERE `ID` = ?";
                pst = connect.prepareStatement(query);
                pst.setString(1, ID);
                result = pst.executeQuery();
                if (result.next()) {
                    UFirstNameField.setText(result.getString(2));
                    ULastNameField.setText(result.getString(3));
                    UdateTextField.setText(result.getString(4));
                    UCNICField.setText(result.getString(5));
                    UPhoneNoField.setText(result.getString(6));
                    UGenderCombo.setSelectedItem(result.getString(7));
                    UDepartmentCombo.setSelectedItem(result.getString(8));
                    UProgramField.setText(result.getString(9));
                    UAddressField.setText(result.getString(10));
                    UEmailField.setText(result.getString(11));
                    UPasswordField.setText(result.getString(12));
                    if(result.getString(13).equals("Active")) {
                        UActive.setSelected(true);
                        UInactive.setSelected(false);
                    } else if(result.getString(13).equals("In-Active")) {
                        UActive.setSelected(false);
                        UInactive.setSelected(true);
                    }
                } else {
                    IDNotFound idnotfound = new IDNotFound();
                    idnotfound.setVisible(true);
                    new Timer().schedule(new TimerTask() {
                        public void run() {
                            idnotfound.dispose();
                        }
                    }, 1000);
                    
                    UIdField.setText("Enter ID to Search");
                    UFirstNameField.setText("");
                    ULastNameField.setText("");
                    UdateTextField.setText("DD-MM-YYYY");
                    UCNICField.setText("");
                    UPhoneNoField.setText("");
                    UGenderCombo.setSelectedItem("Select Gender");
                    UActive.setSelected(false);
                    UInactive.setSelected(false);
                    UDepartmentCombo.setSelectedItem("Select Department");
                    UProgramField.setText("");
                    UAddressField.setText("");
                    UEmailField.setText("");
                    UPasswordField.setText("");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,ex);
            }
        }
    }//GEN-LAST:event_UIdFieldKeyPressed

    private void UActiveMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_UActiveMouseClicked
        // TODO add your handling code here:
        checkStatus = "Active";
        UActive.setSelected(true);
        UInactive.setSelected(false);
    }//GEN-LAST:event_UActiveMouseClicked

    private void UActiveKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_UActiveKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            checkStatus = "Active";
            UActive.setSelected(true);
            UInactive.setSelected(false);
        }
    }//GEN-LAST:event_UActiveKeyPressed

    private void UInactiveMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_UInactiveMouseClicked
        // TODO add your handling code here:
        checkStatus = "In-Active";
        UActive.setSelected(false);
        UInactive.setSelected(true);
    }//GEN-LAST:event_UInactiveMouseClicked

    private void UInactiveKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_UInactiveKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            checkStatus = "Active";
            UActive.setSelected(false);
            UInactive.setSelected(true);
        }
    }//GEN-LAST:event_UInactiveKeyPressed

    private void USubmitBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_USubmitBtnMouseClicked
        // TODO add your handling code here:
        String id = UIdField.getText();
        String fName = UFirstNameField.getText();
        String lName = ULastNameField.getText();
        String dob = UdateTextField.getText();
        cnic = UCNICField.getText();
        String phoneno = UPhoneNoField.getText();
        String gender = (String)UGenderCombo.getSelectedItem();
        String department = (String)UDepartmentCombo.getSelectedItem();
        String program = UProgramField.getText();
        String address = UAddressField.getText();
        email = UEmailField.getText();
        String password = UPasswordField.getText();
        String value13 = checkStatus;

        if(fName.equals("John") || fName.isBlank()){
            UFirstName.setForeground(Color.RED);
        } else {
            UFirstName.setForeground(Color.BLACK);
            temp = true;
        }
        if(lName.equals("Doe") || lName.isBlank()){
            ULastName.setForeground(Color.RED);
        } else {
            ULastName.setForeground(Color.BLACK);
            temp = true;
        }
        if(dob.equals("DD-MM-YYYY") || fName.isBlank()){
            UDateOfBirth.setForeground(Color.RED);
        } else {
            UDateOfBirth.setForeground(Color.BLACK);
            temp = true;
        }
        if(cnic.equals("00000-0000000-0") || cnic.isBlank()){
            UCNIC.setForeground(Color.RED);
        } else {
            UCNIC.setForeground(Color.BLACK);
            temp = true;
        }
        if(phoneno.equals("0333-1234567") || phoneno.isBlank()){
            UPhoneNo.setForeground(Color.RED);
        } else {
            UPhoneNo.setForeground(Color.BLACK);
            temp = true;
        }
        if(gender.equals("Select Gender")){
            UGender.setForeground(Color.RED);
        } else {
            UGender.setForeground(Color.BLACK);
            temp = true;
        }
        if(department.equals("Select Department")){
            UDepartment.setForeground(Color.RED);
        } else {
            UDepartment.setForeground(Color.BLACK);
            temp = true;
        }
        if(program.equals("Program Name") || program.isBlank()){
            UProgram.setForeground(Color.RED);
        } else {
            UProgram.setForeground(Color.BLACK);
            temp = true;
        }
        if(address.isBlank()){
            UAddress.setForeground(Color.RED);
        } else {
            UAddress.setForeground(Color.BLACK);
            temp = true;
        }
        if(email.equals("user@domain.com") || email.isBlank()){
            UEmail.setForeground(Color.RED);
        } else {
            UEmail.setForeground(Color.BLACK);
            temp = true;
        }
        if(password.isBlank()){
            UPassword.setForeground(Color.RED);
        } else {
            UPassword.setForeground(Color.BLACK);
            temp = true;
        }
        if(checkEmail(email) || checkCNIC(cnic)){
            UserAlreadyExists useralreadyexists = new UserAlreadyExists();
            useralreadyexists.setVisible(true);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    useralreadyexists.dispose();
                }
            }, 1000);
        } else {
            if(temp == true){
                String query = "UPDATE `student` SET `ID`='"+id+"',`fName`='"+fName+"',`lName`='"+lName+"',`dob`='"+dob+"',`cnic`='"+cnic+"',`phoneno`='"+phoneno+"',`gender`='"+gender+"',`department`='"+department+"',`program`='"+program+"',`address`='"+address+"',`email`='"+email+"',`password`='"+password+"',`status`='"+value13+"' WHERE `ID`='"+id+"'";
                    try {
                        pst = connect.prepareStatement(query);
                        pst.execute();

                        DataUpdated dataupdated = new DataUpdated();
                        dataupdated.setVisible(true);
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                dataupdated.dispose();
                            }
                        }, 1000);
                        show_Table("student", ViewStudentsTable);

                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null,ex);
                    }
            } else {
                Failed failed = new Failed();
                failed.setVisible(true);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        failed.dispose();
                    }
                }, 1000);
            }
        }
    }//GEN-LAST:event_USubmitBtnMouseClicked

    private void USubmitBtnKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_USubmitBtnKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            USubmitBtnMouseClicked(null);
        }
    }//GEN-LAST:event_USubmitBtnKeyPressed

    private void UCNICFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_UCNICFieldFocusLost
        // TODO add your handling code here:
        if (UCNICField.getText().trim().equals("")) {
            UCNICField.setText("00000-0000000-0");
            UCNICField.setForeground(new Color(153,153,153));
        } else {
            String PATTERN = "^[0-9]{5}[-][0-9]{7}[-][0-9]{1}$";
            Pattern pattern = Pattern.compile(PATTERN);
            Matcher match = pattern.matcher(UCNICField.getText());
            if (!match.matches()) {
                UCNIC.setForeground(Color.RED);
                temp = false;
            } else {
                UCNIC.setForeground(Color.BLACK);
                temp = true;
            }
        }
    }//GEN-LAST:event_UCNICFieldFocusLost

    private void UCNICFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_UCNICFieldFocusGained
        // TODO add your handling code here:
        UCNICField.setForeground(new Color(0,0,0));
    }//GEN-LAST:event_UCNICFieldFocusGained

    private void ViewStudentsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ViewStudentsTableMouseClicked
        // TODO add your handling code here:
        boolean confirmed = false;
        int row = ViewStudentsTable.rowAtPoint(evt.getPoint());
        int col = ViewStudentsTable.columnAtPoint(evt.getPoint());
        if(row >= 0 && col >= 0){
            Object cellValue = ViewStudentsTable.getModel().getValueAt(row, 0);
            if(col == 4){
                ViewStudent viewstudent = new ViewStudent();
                viewstudent.setVisible(true);
            } else if(col == 5){
                String message = "Are you sure want to delete?";
                CustomConfirmDialog customDialog = new CustomConfirmDialog(this, "Confirm Student Deletion", message);
                customDialog.setVisible(true);
                confirmed = customDialog.isConfirmed();
                if(confirmed == true){
                    String query = "DELETE FROM `student` WHERE `ID`=?";
                    try {
                        pst = connect.prepareStatement(query);

                        pst.setString(1, (String)cellValue);

                        pst.execute();
                        DataDeleted datadeleted = new DataDeleted();
                        datadeleted.setVisible(true);
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                datadeleted.dispose();
                            }
                        }, 1000);
                        show_Table("student", ViewStudentsTable);

                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null,ex);
                    }
                }
            }
        }
    }//GEN-LAST:event_ViewStudentsTableMouseClicked

    private void AFirstNameField2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_AFirstNameField2FocusGained
        // TODO add your handling code here:
        if(AFirstNameField2.getText().equals("John")){
            AFirstNameField2.setText("");
            AFirstNameField2.setForeground(new Color(0,0,0));
        }
    }//GEN-LAST:event_AFirstNameField2FocusGained

    private void AFirstNameField2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_AFirstNameField2FocusLost
        // TODO add your handling code here:
        if(AFirstNameField2.getText().equals("")) {
            AFirstNameField2.setText("John");
            AFirstNameField2.setForeground(new Color(153,153,153));
        } else {
            String PATTERN = "^[a-zA-Z]{1,20}$";
            Pattern pattern = Pattern.compile(PATTERN);
            Matcher match = pattern.matcher(AFirstNameField2.getText());
            if (!match.matches()) {
                AFirstName2.setForeground(Color.RED);
                temp = false;
            } else {
                AFirstName2.setForeground(Color.BLACK);
                temp = true;
            }
        }
    }//GEN-LAST:event_AFirstNameField2FocusLost

    private void ALastNameField2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ALastNameField2FocusGained
        // TODO add your handling code here:
        if(ALastNameField2.getText().equals("Doe")){
            ALastNameField2.setText("");
            ALastNameField2.setForeground(new Color(0,0,0));
        }
    }//GEN-LAST:event_ALastNameField2FocusGained

    private void ALastNameField2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ALastNameField2FocusLost
        // TODO add your handling code here:
        if(ALastNameField2.getText().equals("")) {
            ALastNameField2.setText("Doe");
            ALastNameField2.setForeground(new Color(153,153,153));
        } else {
            String PATTERN = "^[a-zA-Z]{1,20}$";
            Pattern pattern = Pattern.compile(PATTERN);
            Matcher match = pattern.matcher(ALastNameField2.getText());
            if (!match.matches()) {
                ALastName2.setForeground(Color.RED);
                temp = false;
            } else {
                ALastName2.setForeground(Color.BLACK);
                temp = true;
            }
        }
    }//GEN-LAST:event_ALastNameField2FocusLost

    private void APhoneNoField2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_APhoneNoField2FocusGained
        // TODO add your handling code here:
        if (APhoneNoField2.getText().trim().equals("0333-1234567")) {
            APhoneNoField2.setText("");
            APhoneNoField2.setForeground(new Color(0,0,0));
        }
    }//GEN-LAST:event_APhoneNoField2FocusGained

    private void APhoneNoField2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_APhoneNoField2FocusLost
        // TODO add your handling code here:
        if (APhoneNoField2.getText().trim().equals("")) {
            APhoneNoField2.setText("0333-1234567");
            APhoneNoField2.setForeground(new Color(153,153,153));
        } else {
            String PATTERN = "^[0-9]{4}[-][0-9]{7}$";
            Pattern pattern = Pattern.compile(PATTERN);
            Matcher match = pattern.matcher(APhoneNoField2.getText());
            if (!match.matches()) {
                APhoneNo2.setForeground(Color.RED);
                temp = false;
            } else {
                APhoneNo2.setForeground(Color.BLACK);
                temp = true;
            }
        }
    }//GEN-LAST:event_APhoneNoField2FocusLost

    private void ACNICField2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ACNICField2FocusGained
        // TODO add your handling code here:
        if (ACNICField2.getText().trim().equals("00000-0000000-0")) {
            ACNICField2.setText("");
            ACNICField2.setForeground(new Color(0,0,0));
        }
    }//GEN-LAST:event_ACNICField2FocusGained

    private void ACNICField2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ACNICField2FocusLost
        // TODO add your handling code here:
        if (ACNICField2.getText().trim().equals("")) {
            ACNICField2.setText("00000-0000000-0");
            ACNICField2.setForeground(new Color(153,153,153));
        } else {
            String PATTERN = "^[0-9]{5}[-][0-9]{7}[-][0-9]{1}$";
            Pattern pattern = Pattern.compile(PATTERN);
            Matcher match = pattern.matcher(ACNICField2.getText());
            if (!match.matches()) {
                ACNIC2.setForeground(Color.RED);
                temp = false;
            } else {
                ACNIC2.setForeground(Color.BLACK);
                temp = true;
            }
        }
    }//GEN-LAST:event_ACNICField2FocusLost

    private void ADateOfBirthField2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ADateOfBirthField2FocusGained
        // TODO add your handling code here:
        if(AdateTextField2.getText().equals("DD-MM-YYYY")){
            AdateTextField2.setText("");
            ADateOfBirthField2.setForeground(new Color(0,0,0));
        }
    }//GEN-LAST:event_ADateOfBirthField2FocusGained

    private void ADateOfBirthField2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ADateOfBirthField2FocusLost
        // TODO add your handling code here:
        if(AdateTextField2.getText().equals("")) {
            AdateTextField2.setText("DD-MM-YYYY");
            ADateOfBirthField2.setForeground(new Color(153,153,153));
        }
    }//GEN-LAST:event_ADateOfBirthField2FocusLost

    private void AEmailField2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_AEmailField2FocusGained
        // TODO add your handling code here:
        if (AEmailField2.getText().trim().equals("user@domain.com")) {
            AEmailField2.setText("");
            AEmailField2.setForeground(new Color(0,0,0));
        }
    }//GEN-LAST:event_AEmailField2FocusGained

    private void AEmailField2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_AEmailField2FocusLost
        // TODO add your handling code here:
        if (AEmailField2.getText().trim().equals("")) {
            AEmailField2.setText("user@domain.com");
            AEmailField2.setForeground(new Color(153,153,153));
        } else {
            String PATTERN = "^(.+)@(.+)$";
            Pattern pattern = Pattern.compile(PATTERN);
            Matcher match = pattern.matcher(AEmailField2.getText());
            if (!match.matches()) {
                AEmail2.setForeground(Color.RED);
                temp = false;
            } else {
                AEmail2.setForeground(Color.BLACK);
                temp = true;
            }
        }
    }//GEN-LAST:event_AEmailField2FocusLost

    private void ASubmitBtn2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ASubmitBtn2MouseClicked
        // TODO add your handling code here:
        String ID = generateRandomID();
        String fName = AFirstNameField2.getText();
        String lName = ALastNameField2.getText();
        String dob = AdateTextField2.getText();
        cnic = ACNICField2.getText();
        String phoneno = APhoneNoField2.getText();
        String gender = (String)AGenderCombo2.getSelectedItem();
        String department = (String)ADepartmentCombo2.getSelectedItem();
        String address = AAddressField2.getText();
        email = AEmailField2.getText();

        if(fName.equals("John") || fName.isBlank()){
            AFirstName2.setForeground(Color.RED);
        } else {
            AFirstName2.setForeground(Color.BLACK);
            temp = true;
        }
        if(lName.equals("Doe") || lName.isBlank()){
            ALastName2.setForeground(Color.RED);
        } else {
            ALastName2.setForeground(Color.BLACK);
            temp = true;
        }
        if(dob.equals("DD-MM-YYYY") || fName.isBlank()){
            ADateOfBirth2.setForeground(Color.RED);
        } else {
            ADateOfBirth2.setForeground(Color.BLACK);
            temp = true;
        }
        if(cnic.equals("00000-0000000-0") || cnic.isBlank()){
            ACNIC2.setForeground(Color.RED);
        } else {
            ACNIC2.setForeground(Color.BLACK);
            temp = true;
        }
        if(phoneno.equals("0333-1234567") || phoneno.isBlank()){
            APhoneNo2.setForeground(Color.RED);
        } else {
            APhoneNo2.setForeground(Color.BLACK);
            temp = true;
        }
        if(gender.equals("Select Gender")){
            AGender2.setForeground(Color.RED);
        } else {
            AGender2.setForeground(Color.BLACK);
            temp = true;
        }
        if(department.equals("Select Department")){
            ADepartment2.setForeground(Color.RED);
        } else {
            ADepartment2.setForeground(Color.BLACK);
            temp = true;
        }
        if(address.isBlank()){
            AAddress2.setForeground(Color.RED);
        } else {
            AAddress2.setForeground(Color.BLACK);
            temp = true;
        }
        if(email.equals("user@domain.com") || email.isBlank()){
            AEmail2.setForeground(Color.RED);
        } else {
            AEmail2.setForeground(Color.BLACK);
            temp = true;
        }
        if(checkEmail(email) || checkCNIC(cnic)){
            UserAlreadyExists useralreadyexists = new UserAlreadyExists();
            useralreadyexists.setVisible(true);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    useralreadyexists.dispose();
                }
            }, 1000);
        } else if(checkInstructorID(ID)){
            UserAlreadyExists useralreadyexists = new UserAlreadyExists();
            useralreadyexists.setVisible(true);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    useralreadyexists.dispose();
                }
            }, 1000);
        }else {
            if(temp == true){
                String query = "INSERT INTO `instructor`(`ID`, `fName`, `lName`, `dob`, `cnic`, `phoneno`, `gender`, `department`, `address`, `email`) VALUES (?,?,?,?,?,?,?,?,?,?)";
                try {
                    pst = connect.prepareStatement(query);
                    pst.setString(1, ID);
                    pst.setString(2, AFirstNameField2.getText());
                    pst.setString(3, ALastNameField2.getText());
                    pst.setString(4, AdateTextField2.getText());
                    pst.setString(5, ACNICField2.getText());
                    pst.setString(6, APhoneNoField2.getText());
                    pst.setString(7, (String)AGenderCombo2.getSelectedItem());
                    pst.setString(8, (String)ADepartmentCombo2.getSelectedItem());
                    pst.setString(9, AAddressField2.getText());
                    pst.setString(10, AEmailField2.getText());

                    pst.execute();
                    DataAdded dataadded = new DataAdded();
                    dataadded.setVisible(true);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            dataadded.dispose();
                        }
                    }, 1000);
                    show_Table("instructor", ViewInstructorsTable);
                    AFirstNameField2.setText("");
                    ALastNameField2.setText("");
                    AdateTextField2.setText("");
                    ACNICField2.setText("");
                    APhoneNoField2.setText("");
                    AGenderCombo2.setSelectedItem("Select Gender");
                    ADepartmentCombo2.setSelectedItem("Select Department");
                    AAddressField2.setText("");
                    AEmailField2.setText("");

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,ex);
                }
            }
        }
    }//GEN-LAST:event_ASubmitBtn2MouseClicked

    private void ASubmitBtn2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ASubmitBtn2KeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            ASubmitBtn2MouseClicked(null);
        }
    }//GEN-LAST:event_ASubmitBtn2KeyPressed

    private void UIdField2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_UIdField2FocusGained
        // TODO add your handling code here:
        if (UIdField2.getText().trim().equals("Enter ID to Search")) {
            UIdField2.setText("");
            UIdField2.setForeground(new Color(0,0,0));
        }
    }//GEN-LAST:event_UIdField2FocusGained

    private void UIdField2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_UIdField2FocusLost
        // TODO add your handling code here:
        if (UIdField2.getText().trim().equals("")) {
            UIdField2.setText("Enter ID to Search");
            UIdField2.setForeground(new Color(153,153,153));
        }
    }//GEN-LAST:event_UIdField2FocusLost

    private void UIdField2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_UIdField2KeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER) {
            String ID = UIdField2.getText();
            try {
                String query = "SELECT * FROM `instructor` WHERE `ID` = ?";
                pst = connect.prepareStatement(query);
                pst.setString(1, ID);
                result = pst.executeQuery();
                if (result.next()) {
                    UFirstNameField2.setText(result.getString(2));
                    ULastNameField2.setText(result.getString(3));
                    UdateTextField2.setText(result.getString(4));
                    UCNICField2.setText(result.getString(5));
                    UPhoneNoField2.setText(result.getString(6));
                    UGenderCombo2.setSelectedItem(result.getString(7));
                    UDepartmentCombo2.setSelectedItem(result.getString(8));
                    UAddressField2.setText(result.getString(9));
                    UEmailField2.setText(result.getString(10));
                    UPasswordField2.setText(result.getString(11));
                    if(result.getString(12).equals("Active")) {
                        UActive2.setSelected(true);
                        UInactive2.setSelected(false);
                    } else if(result.getString(12).equals("In-Active")) {
                        UActive2.setSelected(false);
                        UInactive2.setSelected(true);
                    }
                } else {
                    IDNotFound idnotfound = new IDNotFound();
                    idnotfound.setVisible(true);
                    new Timer().schedule(new TimerTask() {
                        public void run() {
                            idnotfound.dispose();
                        }
                    }, 1000);
                    
                    UIdField2.setText("Enter ID to Search");
                    UFirstNameField2.setText("");
                    ULastNameField2.setText("");
                    UdateTextField2.setText("DD-MM-YYYY");
                    UCNICField2.setText("");
                    UPhoneNoField2.setText("");
                    UGenderCombo2.setSelectedItem("Select Gender");
                    UActive2.setSelected(false);
                    UInactive2.setSelected(false);
                    UDepartmentCombo2.setSelectedItem("Select Department");
                    UAddressField2.setText("");
                    UEmailField2.setText("");
                    UPasswordField2.setText("");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,ex);
            }
        }
    }//GEN-LAST:event_UIdField2KeyPressed

    private void UFirstNameField2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_UFirstNameField2FocusGained
        // TODO add your handling code here:
        UFirstNameField2.setForeground(new Color(0,0,0));
    }//GEN-LAST:event_UFirstNameField2FocusGained

    private void UFirstNameField2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_UFirstNameField2FocusLost
        // TODO add your handling code here:
        if(UFirstNameField2.getText().equals("")) {
            UFirstNameField2.setText("John");
            UFirstNameField2.setForeground(new Color(153,153,153));
        } else {
            String PATTERN = "^[a-zA-Z]{1,20}$";
            Pattern pattern = Pattern.compile(PATTERN);
            Matcher match = pattern.matcher(UFirstNameField2.getText());
            if (!match.matches()) {
                UFirstName.setForeground(Color.RED);
                temp = false;
            } else {
                UFirstName.setForeground(Color.BLACK);
                temp = true;
            }
        }
    }//GEN-LAST:event_UFirstNameField2FocusLost

    private void ULastNameField2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ULastNameField2FocusGained
        // TODO add your handling code here:
        ULastNameField2.setForeground(new Color(0,0,0));
    }//GEN-LAST:event_ULastNameField2FocusGained

    private void ULastNameField2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ULastNameField2FocusLost
        // TODO add your handling code here:
        if (ULastNameField2.getText().equals("")) {
            ULastNameField2.setText("Doe");
            ULastNameField2.setForeground(new Color(153,153,153));
        } else {
            String PATTERN = "^[a-zA-Z]{1,20}$";
            Pattern pattern = Pattern.compile(PATTERN);
            Matcher match = pattern.matcher(ULastNameField2.getText());
            if (!match.matches()) {
                ULastName.setForeground(Color.RED);
                temp = false;
            } else {
                ULastName.setForeground(Color.BLACK);
                temp = true;
            }
        }
    }//GEN-LAST:event_ULastNameField2FocusLost

    private void UPhoneNoField2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_UPhoneNoField2FocusGained
        // TODO add your handling code here:
        UPhoneNoField2.setForeground(new Color(0,0,0));
    }//GEN-LAST:event_UPhoneNoField2FocusGained

    private void UPhoneNoField2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_UPhoneNoField2FocusLost
        // TODO add your handling code here:
        if (UPhoneNoField2.getText().trim().equals("")) {
            UPhoneNoField2.setText("0333-1234567");
            UPhoneNoField2.setForeground(new Color(153,153,153));
        } else {
            String PATTERN = "^[0-9]{4}[-][0-9]{7}$";
            Pattern pattern = Pattern.compile(PATTERN);
            Matcher match = pattern.matcher(UPhoneNoField2.getText());
            if (!match.matches()) {
                UPhoneNo.setForeground(Color.RED);
                temp = false;
            } else {
                UPhoneNo.setForeground(Color.BLACK);
                temp = true;
            }
        }
    }//GEN-LAST:event_UPhoneNoField2FocusLost

    private void UCNICField2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_UCNICField2FocusGained
        // TODO add your handling code here:
        UCNICField2.setForeground(new Color(0,0,0));
    }//GEN-LAST:event_UCNICField2FocusGained

    private void UCNICField2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_UCNICField2FocusLost
        // TODO add your handling code here:
        if (UCNICField2.getText().trim().equals("")) {
            UCNICField2.setText("00000-0000000-0");
            UCNICField2.setForeground(new Color(153,153,153));
        } else {
            String PATTERN = "^[0-9]{5}[-][0-9]{7}[-][0-9]{1}$";
            Pattern pattern = Pattern.compile(PATTERN);
            Matcher match = pattern.matcher(UCNICField2.getText());
            if (!match.matches()) {
                UCNIC.setForeground(Color.RED);
                temp = false;
            } else {
                UCNIC.setForeground(Color.BLACK);
                temp = true;
            }
        }
    }//GEN-LAST:event_UCNICField2FocusLost

    private void UDateOfBirthField2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_UDateOfBirthField2FocusGained
        // TODO add your handling code here:
        UDateOfBirthField2.setForeground(new Color(0,0,0));
    }//GEN-LAST:event_UDateOfBirthField2FocusGained

    private void UDateOfBirthField2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_UDateOfBirthField2FocusLost
        // TODO add your handling code here:
        if(UdateTextField2.getText().equals("")) {
            UdateTextField2.setText("DD-MM-YYYY");
            UDateOfBirthField2.setForeground(new Color(153,153,153));
        }
    }//GEN-LAST:event_UDateOfBirthField2FocusLost

    private void UEmailField2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_UEmailField2FocusGained
        // TODO add your handling code here:
        UEmailField2.setForeground(new Color(0,0,0));
    }//GEN-LAST:event_UEmailField2FocusGained

    private void UEmailField2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_UEmailField2FocusLost
        // TODO add your handling code here:
        if (UEmailField2.getText().trim().equals("")) {
            UEmailField2.setText("user@domain.com");
            UEmailField2.setForeground(new Color(153,153,153));
        }
    }//GEN-LAST:event_UEmailField2FocusLost

    private void UActive2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_UActive2MouseClicked
        // TODO add your handling code here:
        checkStatus = "Active";
        UActive2.setSelected(true);
        UInactive2.setSelected(false);
    }//GEN-LAST:event_UActive2MouseClicked

    private void UActive2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_UActive2KeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            checkStatus = "Active";
            UActive2.setSelected(true);
            UInactive2.setSelected(false);
        }
    }//GEN-LAST:event_UActive2KeyPressed

    private void UInactive2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_UInactive2MouseClicked
        // TODO add your handling code here:
        checkStatus = "In-Active";
        UActive2.setSelected(false);
        UInactive2.setSelected(true);
    }//GEN-LAST:event_UInactive2MouseClicked

    private void UInactive2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_UInactive2KeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            checkStatus = "In-Active";
            UActive2.setSelected(false);
            UInactive2.setSelected(true);
        }
    }//GEN-LAST:event_UInactive2KeyPressed

    private void USubmitBtn2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_USubmitBtn2MouseClicked
        // TODO add your handling code here:
        String id = UIdField2.getText();
        String fName = UFirstNameField2.getText();
        String lName = ULastNameField2.getText();
        String dob = UdateTextField2.getText();
        cnic = UCNICField2.getText();
        String phoneno = UPhoneNoField2.getText();
        String gender = (String)UGenderCombo2.getSelectedItem();
        String department = (String)UDepartmentCombo2.getSelectedItem();
        String address = UAddressField2.getText();
        email = UEmailField2.getText();
        String password = UPasswordField2.getText();
        String value13 = checkStatus;

        if(fName.equals("John") || fName.isBlank()){
            UFirstName.setForeground(Color.RED);
        } else {
            UFirstName.setForeground(Color.BLACK);
            temp = true;
        }
        if(lName.equals("Doe") || lName.isBlank()){
            ULastName.setForeground(Color.RED);
        } else {
            ULastName.setForeground(Color.BLACK);
            temp = true;
        }
        if(dob.equals("DD-MM-YYYY") || fName.isBlank()){
            UDateOfBirth.setForeground(Color.RED);
        } else {
            UDateOfBirth.setForeground(Color.BLACK);
            temp = true;
        }
        if(cnic.equals("00000-0000000-0") || cnic.isBlank()){
            UCNIC.setForeground(Color.RED);
        } else {
            UCNIC.setForeground(Color.BLACK);
            temp = true;
        }
        if(phoneno.equals("0333-1234567") || phoneno.isBlank()){
            UPhoneNo.setForeground(Color.RED);
        } else {
            UPhoneNo.setForeground(Color.BLACK);
            temp = true;
        }
        if(gender.equals("Select Gender")){
            UGender.setForeground(Color.RED);
        } else {
            UGender.setForeground(Color.BLACK);
            temp = true;
        }
        if(department.equals("Select Department")){
            UDepartment.setForeground(Color.RED);
        } else {
            UDepartment.setForeground(Color.BLACK);
            temp = true;
        }
        if(address.isBlank()){
            UAddress.setForeground(Color.RED);
        } else {
            UAddress.setForeground(Color.BLACK);
            temp = true;
        }
        if(email.equals("user@domain.com") || email.isBlank()){
            UEmail.setForeground(Color.RED);
        } else {
            UEmail.setForeground(Color.BLACK);
            temp = true;
        }
        if(password.isBlank()){
            UPassword.setForeground(Color.RED);
        } else {
            UPassword.setForeground(Color.BLACK);
            temp = true;
        }
        if(checkEmail(email) || checkCNIC(cnic)){
            UserAlreadyExists useralreadyexists = new UserAlreadyExists();
            useralreadyexists.setVisible(true);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    useralreadyexists.dispose();
                }
            }, 1000);
        } else {
            if(temp == true){
                String query = "UPDATE `instructor` SET `ID`='"+id+"',`fName`='"+fName+"',`lName`='"+lName+"',`dob`='"+dob+"',`cnic`='"+cnic+"',`phoneno`='"+phoneno+"',`gender`='"+gender+"',`department`='"+department+"',`address`='"+address+"',`email`='"+email+"',`password`='"+password+"',`status`='"+value13+"' WHERE `ID`='"+id+"'";
                    try {
                        pst = connect.prepareStatement(query);
                        pst.execute();

                        DataUpdated dataupdated = new DataUpdated();
                        dataupdated.setVisible(true);
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                dataupdated.dispose();
                            }
                        }, 1000);
                        show_Table("instructor", ViewInstructorsTable);

                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null,ex);
                    }
            } else {
                Failed failed = new Failed();
                failed.setVisible(true);
                new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                failed.dispose();
                            }
                        }, 1000);
            }
        }
    }//GEN-LAST:event_USubmitBtn2MouseClicked

    private void USubmitBtn2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_USubmitBtn2KeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            USubmitBtn2MouseClicked(null);
        }
    }//GEN-LAST:event_USubmitBtn2KeyPressed

    private void ViewInstructorsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ViewInstructorsTableMouseClicked
        // TODO add your handling code here:
        boolean confirmed = false;
        int row = ViewInstructorsTable.rowAtPoint(evt.getPoint());
        int col = ViewInstructorsTable.columnAtPoint(evt.getPoint());
        if(row >= 0 && col >= 0){
            Object cellValue = ViewInstructorsTable.getModel().getValueAt(row, 0);
            if(col == 5){
                ViewStudent viewstudent = new ViewStudent();
                viewstudent.setVisible(true);
            } else if(col == 6){
                String message = "Are you sure want to delete?";
                CustomConfirmDialog customDialog = new CustomConfirmDialog(this, "Confirm Instructor Deletion", message);
                customDialog.setVisible(true);
                confirmed = customDialog.isConfirmed();
                if(confirmed == true){
                    String query = "DELETE FROM `instructor` WHERE `ID`=?";
                    try {
                        pst = connect.prepareStatement(query);

                        pst.setString(1, (String)cellValue);

                        pst.execute();
                        DataDeleted datadeleted = new DataDeleted();
                        datadeleted.setVisible(true);
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                datadeleted.dispose();
                            }
                        }, 1000);
                        show_Table("instructor", ViewInstructorsTable);

                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null,ex);
                    }
                }
            }
        }
    }//GEN-LAST:event_ViewInstructorsTableMouseClicked

    private void ACSubmitBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ACSubmitBtnMouseClicked
        // TODO add your handling code here:
        String cCode = ACourseCodeField.getText();
        String courseName = ACourseNameField.getText();
        String cCreditHr = ACreditHrsField.getText();
        String cSession = (String)ASessionCombo.getSelectedItem();
        String cAcademicYear = AAcademicYearField.getText();
        String cDepartment = (String)ACDepartmentCombo.getSelectedItem();
        String cSeats = ASeatsField.getText();

        if(cCode.isBlank()){
            ACourseCode.setForeground(Color.RED);
        } else {
            ACourseCode.setForeground(Color.BLACK);
            temp = true;
        }
        if(courseName.isBlank()){
            ACourseName.setForeground(Color.RED);
        } else {
            ACourseName.setForeground(Color.BLACK);
            temp = true;
        }
        if(cCreditHr.isBlank()){
            ACreditHrs.setForeground(Color.RED);
        } else {
            ACreditHrs.setForeground(Color.BLACK);
            temp = true;
        }
        if(cSession.equals("Select Session")){
            ASession.setForeground(Color.RED);
        } else {
            ASession.setForeground(Color.BLACK);
            temp = true;
        }
        if(cAcademicYear.isBlank()){
            AAcademicYear.setForeground(Color.RED);
        } else {
            AAcademicYear.setForeground(Color.BLACK);
            temp = true;
        }
        if(cDepartment.equals("Select Department")){
            ACDepartment.setForeground(Color.RED);
        } else {
            ACDepartment.setForeground(Color.BLACK);
            temp = true;
        }
        if(cSeats.isBlank()){
            ASeats.setForeground(Color.RED);
        } else {
            ASeats.setForeground(Color.BLACK);
            temp = true;
        }
        if(checkCourseCode(cCode)){
            UserAlreadyExists useralreadyexists = new UserAlreadyExists();
            useralreadyexists.setVisible(true);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    useralreadyexists.dispose();
                }
            }, 1000);
        } else {
            if(temp == true){
                String query = "INSERT INTO `course`(`coursecode`, `coursename`, `credithrs`, `session`, `academicyear`, `department`, `seats`) VALUES (?,?,?,?,?,?,?)";
                try {
                    pst = connect.prepareStatement(query);
                    pst.setString(1, cCode);
                    pst.setString(2, courseName);
                    pst.setString(3, cCreditHr);
                    pst.setString(4, cSession);
                    pst.setString(5, cAcademicYear);
                    pst.setString(6, cDepartment);
                    pst.setString(7, cSeats);

                    pst.execute();
                    DataAdded dataadded = new DataAdded();
                    dataadded.setVisible(true);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            dataadded.dispose();
                        }
                    }, 1000);
                    show_Table("course", ViewCoursesTable);
                    ACourseCodeField.setText("");
                    ACourseNameField.setText("");
                    ACreditHrsField.setText("");
                    ASessionCombo.setSelectedItem("Select Session");
                    AAcademicYearField.setText("");
                    ACDepartmentCombo.setSelectedItem("Select Department");
                    ASeatsField.setText("");

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,ex);
                }
            }
        }
    }//GEN-LAST:event_ACSubmitBtnMouseClicked

    private void ACSubmitBtnKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ACSubmitBtnKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            ACSubmitBtnMouseClicked(null);
        }
    }//GEN-LAST:event_ACSubmitBtnKeyPressed

    private void UCourseCodeFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_UCourseCodeFieldFocusGained
        // TODO add your handling code here:
        if (UCourseCodeField.getText().trim().equals("Enter Code to Search")) {
            UCourseCodeField.setText("");
            UCourseCodeField.setForeground(new Color(0,0,0));
        }
    }//GEN-LAST:event_UCourseCodeFieldFocusGained

    private void UCourseCodeFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_UCourseCodeFieldFocusLost
        // TODO add your handling code here:
        if (UCourseCodeField.getText().trim().equals("")) {
            UCourseCodeField.setText("Enter Code to Search");
            UCourseCodeField.setForeground(new Color(153,153,153));
        }
    }//GEN-LAST:event_UCourseCodeFieldFocusLost

    private void UCourseCodeFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_UCourseCodeFieldKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER) {
            String Code = UCourseCodeField.getText();
            try {
                String query = "SELECT * FROM `course` WHERE `coursecode` = ?";
                pst = connect.prepareStatement(query);
                pst.setString(1, Code);
                result = pst.executeQuery();
                if (result.next()) {
                    UCourseNameField.setText(result.getString(2));
                    UCreditHrsField.setText(result.getString(3));
                    USessionCombo.setSelectedItem(result.getString(4));
                    UAcademicYearField.setText(result.getString(5));
                    UCDepartmentCombo.setSelectedItem(result.getString(6));
                    USeatsField.setText(result.getString(7));
                } else {
                    IDNotFound idnotfound = new IDNotFound();
                    idnotfound.setVisible(true);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            idnotfound.dispose();
                        }
                    }, 1000);
                    
                    UCourseCodeField.setText("");
                    UCourseNameField.setText("");
                    UCreditHrsField.setText("");
                    USessionCombo.setSelectedItem("Select Session");
                    UAcademicYearField.setText("");
                    UCDepartmentCombo.setSelectedItem("Select Department");
                    USeatsField.setText("");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,ex);
            }
        }
    }//GEN-LAST:event_UCourseCodeFieldKeyPressed

    private void UCSubmitBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_UCSubmitBtnMouseClicked
        // TODO add your handling code here:
        String cCode = UCourseCodeField.getText();
        String courseName = UCourseNameField.getText();
        String cCreditHr = UCreditHrsField.getText();
        String cSession = (String)USessionCombo.getSelectedItem();
        String cAcademicYear = UAcademicYearField.getText();
        String cDepartment = (String)UCDepartmentCombo.getSelectedItem();
        String cSeats = USeatsField.getText();

        if(cCode.isBlank()){
            UCourseCode.setForeground(Color.RED);
        } else {
            UCourseCode.setForeground(Color.BLACK);
            temp = true;
        }
        if(courseName.isBlank()){
            UCourseName.setForeground(Color.RED);
        } else {
            UCourseName.setForeground(Color.BLACK);
            temp = true;
        }
        if(cCreditHr.isBlank()){
            UCreditHrs.setForeground(Color.RED);
        } else {
            UCreditHrs.setForeground(Color.BLACK);
            temp = true;
        }
        if(cSession.equals("Select Session")){
            USession.setForeground(Color.RED);
        } else {
            USession.setForeground(Color.BLACK);
            temp = true;
        }
        if(cAcademicYear.isBlank()){
            UAcademicYear.setForeground(Color.RED);
        } else {
            UAcademicYear.setForeground(Color.BLACK);
            temp = true;
        }
        if(cDepartment.equals("Select Department")){
            UCDepartment.setForeground(Color.RED);
        } else {
            UCDepartment.setForeground(Color.BLACK);
            temp = true;
        }
        if(cSeats.isBlank()){
            USeatsField.setForeground(Color.RED);
        } else {
            USeatsField.setForeground(Color.BLACK);
            temp = true;
        }
        if(temp == true){
            String query = "UPDATE `course` SET `coursecode`='"+cCode+"',`coursename`='"+courseName+"',`credithrs`='"+cCreditHr+"',`session`='"+cSession+"',`academicyear`='"+cAcademicYear+"',`department`='"+cDepartment+"',`seats`='"+cSeats+"' WHERE `coursecode`='"+cCode+"'";
            try {
                pst = connect.prepareStatement(query);
                pst.execute();

                DataUpdated dataupdated = new DataUpdated();
                dataupdated.setVisible(true);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        dataupdated.dispose();
                    }
                }, 1000);
                show_Table("course", ViewCoursesTable);

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,ex);
            }
        } else {
            Failed failed = new Failed();
            failed.setVisible(true);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    failed.dispose();
                }
            }, 1000);
        }
    }//GEN-LAST:event_UCSubmitBtnMouseClicked

    private void UCSubmitBtnKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_UCSubmitBtnKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER) {
            UCSubmitBtnMouseClicked(null);
        }
    }//GEN-LAST:event_UCSubmitBtnKeyPressed

    private void ViewCoursesTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ViewCoursesTableMouseClicked
        // TODO add your handling code here:
        boolean confirmed = false;
        int row = ViewCoursesTable.rowAtPoint(evt.getPoint());
        int col = ViewCoursesTable.columnAtPoint(evt.getPoint());
        if(row >= 0 && col >= 0){
            Object cellValue = ViewCoursesTable.getModel().getValueAt(row, 0);
            if(col == 4){
                ViewStudent viewstudent = new ViewStudent();
                viewstudent.setVisible(true);
            } else if(col == 5){
                String message = "Are you sure want to delete?";
                CustomConfirmDialog customDialog = new CustomConfirmDialog(this, "Confirm Course Deletion", message);
                customDialog.setVisible(true);
                confirmed = customDialog.isConfirmed();
                if(confirmed == true){
                    String query = "DELETE FROM `course` WHERE `coursecode`=?";
                    try {
                        pst = connect.prepareStatement(query);

                        pst.setString(1, (String)cellValue);

                        pst.execute();
                        DataDeleted datadeleted = new DataDeleted();
                        datadeleted.setVisible(true);
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                datadeleted.dispose();
                            }
                        }, 1000);
                        show_Table("course", ViewCoursesTable);

                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null,ex);
                    }
                }
            }
        }
    }//GEN-LAST:event_ViewCoursesTableMouseClicked

    private void ViewTimeTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ViewTimeTableMouseClicked
        // TODO add your handling code here:
        int row = ViewTimeTable.rowAtPoint(evt.getPoint());
        int col = ViewTimeTable.columnAtPoint(evt.getPoint());
        if(row >= 0 && col >= 0){
            TableModel model = ViewTimeTable.getModel();
            try {
                cName = model.getValueAt(row,0).toString();
                instructorName = model.getValueAt(row,1).toString();
                day = model.getValueAt(row,2).toString();
                sTime = model.getValueAt(row,3).toString();
                eTime = model.getValueAt(row,4).toString();
                roomNo = model.getValueAt(row,5).toString();
                
                String[] sparts = sTime.split(":");
                String sHour = sparts[0];
                String sMinutes = sparts[1];
                
                String[] eparts = eTime.split(":");
                String eHour = eparts[0];
                String eMinutes = eparts[1];
                
                DayCombo.setSelectedItem(day);
                HoursCombo.setSelectedItem(sHour);
                MinutesCombo.setSelectedItem(sMinutes);
                HoursCombo1.setSelectedItem(eHour);
                MinutesCombo1.setSelectedItem(eMinutes);
                CourseCombo.setSelectedItem(cName);
                InstructorCombo.setSelectedItem(instructorName);
                RoomCombo.setSelectedItem(roomNo);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,ex);
            }
        }
    }//GEN-LAST:event_ViewTimeTableMouseClicked

    private void AddMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AddMouseClicked
        // TODO add your handling code here:
        boolean overlap = true;
        String sHour = (String)HoursCombo.getSelectedItem();
        String sMinutes = (String)MinutesCombo.getSelectedItem();
        String eHour = (String)HoursCombo1.getSelectedItem();
        String eMinutes = (String)MinutesCombo1.getSelectedItem();
        
        day = (String)DayCombo.getSelectedItem();
        sTime = sHour + ':' + sMinutes;
        eTime = eHour + ':' + eMinutes;
        
        // Get Names from UI
        cName = (String)CourseCombo.getSelectedItem();
        instructorName = (String)InstructorCombo.getSelectedItem();
        roomNo = (String)RoomCombo.getSelectedItem();
        
        // Retrieve IDs from Maps
        String courseCode = courseMap.get(cName);
        String instructorID = instructorMap.get(instructorName);
        
        String[] sparts = sTime.split(":");
        String[] eparts = eTime.split(":");

        // Validation logic
        if(day.equals("Select Day") || sparts[0].equals("Select Hour") || eparts[0].equals("Select Hour") || 
        cName.equals("Select Course") || instructorName.equals("Select Instructor") || roomNo.equals("Select Room")){
            JOptionPane.showMessageDialog(null, "Please fill all fields");
            return;
        }

        // Overlap Check
        String checkQuery = "SELECT COUNT(*) FROM `timetable` WHERE `day` = ? AND ((`instructorName` = ?) OR (`roomNo` = ?)) AND (`sTime` < ? AND `eTime` > ?)";
        try {
            pst = connect.prepareStatement(checkQuery);
            pst.setString(1, day);
            pst.setString(2, instructorName);
            pst.setString(3, roomNo);
            pst.setString(4, eTime);
            pst.setString(5, sTime);
            result = pst.executeQuery();
            if(result.next() && result.getInt(1) > 0){
                AlreadyBooked alreadybooked = new AlreadyBooked();
                alreadybooked.setVisible(true);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        alreadybooked.dispose();
                    }
                }, 1000);
                return;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }

        String insertQuery = "INSERT INTO `timetable`(`day`, `sTime`, `eTime`,`courseCode`, `cName`, `instructorID`, `instructorName`, `roomNo`) VALUES (?,?,?,?,?,?,?,?)";
        try {
            pst = connect.prepareStatement(insertQuery);
            pst.setString(1, day);
            pst.setString(2, sTime);
            pst.setString(3, eTime);
            pst.setString(4, courseCode);
            pst.setString(5, cName);
            pst.setString(6, instructorID);
            pst.setString(7, instructorName);
            pst.setString(8, roomNo);

            pst.execute();
            DataAdded dataadded = new DataAdded();
            dataadded.setVisible(true);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    dataadded.dispose();
                }
            }, 1000);
            show_Table("timetable", ViewTimeTable);
            
            DayCombo.setSelectedIndex(0);
            HoursCombo.setSelectedIndex(0);
            MinutesCombo.setSelectedIndex(0);
            HoursCombo1.setSelectedIndex(0);
            MinutesCombo1.setSelectedIndex(0);
            CourseCombo.setSelectedIndex(0);
            InstructorCombo.setSelectedIndex(0);
            RoomCombo.setSelectedIndex(0);

        } catch (SQLException ex) {
            Failed failed = new Failed();
            failed.setVisible(true);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    failed.dispose();
                }
            }, 1000);
        }
    }//GEN-LAST:event_AddMouseClicked

    private void AddKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_AddKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER) {
            AddMouseClicked(null);
        }
    }//GEN-LAST:event_AddKeyPressed

    private void UpdateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_UpdateMouseClicked
        // TODO add your handling code here:
        String sHour = (String)HoursCombo.getSelectedItem();
        String sMinutes = (String)MinutesCombo.getSelectedItem();
        String eHour = (String)HoursCombo1.getSelectedItem();
        String eMinutes = (String)MinutesCombo1.getSelectedItem();
        
        String tempDay = (String)DayCombo.getSelectedItem();
        String tempSTime = sHour + ':' + sMinutes;
        String tempETime = eHour + ':' + eMinutes;
        String tempCName = (String)CourseCombo.getSelectedItem();
        String tempInstructorName = (String)InstructorCombo.getSelectedItem();
        String tempRoomNo = (String)RoomCombo.getSelectedItem();
        
        String tempCourseCode = courseMap.get(tempCName);
        String tempInstructorID = instructorMap.get(tempInstructorName);
        
        boolean noChanges = tempDay.equals(day) && tempSTime.equals(sTime) && 
                            tempETime.equals(eTime) && tempInstructorName.equals(instructorName) && 
                            tempRoomNo.equals(roomNo);

        if(tempDay.equals("Select Day") || sHour.equals("Select Hour") || eHour.equals("Select Hour") || 
        tempCName.equals("Select Course") || tempInstructorName.equals("Select Instructor")) {
            JOptionPane.showMessageDialog(null, "Please fill all required fields.");
            return;
        }

        // Overlap Check
        if(!noChanges){
            String query = "SELECT COUNT(*) FROM `timetable` WHERE `day` = ? AND (`instructorName` = ? OR `roomNo` = ?) " +
                        "AND (`sTime` < ? AND `eTime` > ?) AND NOT (`cName` = ? AND `day` = ? AND `sTime` = ?)";
            try {
                pst = connect.prepareStatement(query);
                pst.setString(1, tempDay);
                pst.setString(2, tempInstructorName);
                pst.setString(3, tempRoomNo);
                pst.setString(4, tempETime);
                pst.setString(5, tempSTime);
                // Exclude current record
                pst.setString(6, cName); 
                pst.setString(7, day);
                pst.setString(8, sTime);
                
                result = pst.executeQuery();
                if(result.next() && result.getInt(1) > 0){
                    AlreadyBooked alreadybooked = new AlreadyBooked();
                    alreadybooked.setVisible(true);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            alreadybooked.dispose();
                        }
                    }, 1000);
                    return;
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
        }
        
        // Update the database using the retrieved Course Code and Instructor ID
        String updateQuery = "UPDATE `timetable` SET `day` = ?, `sTime` = ?, `eTime` = ?, `courseCode` = ?, `cName` = ?, " +
                            "`instructorID` = ?, `instructorName` = ?, `roomNo` = ? " +
                            "WHERE `day` = ? AND `sTime` = ? AND `cName` = ?";
        try {
            pst = connect.prepareStatement(updateQuery);
            pst.setString(1, tempDay);
            pst.setString(2, tempSTime);
            pst.setString(3, tempETime);
            pst.setString(4, tempCourseCode);
            pst.setString(5, tempCName);
            pst.setString(6, tempInstructorID);
            pst.setString(7, tempInstructorName);
            pst.setString(8, tempRoomNo);
            
            pst.setString(9, day);
            pst.setString(10, sTime);
            pst.setString(11, cName);

            pst.execute();
            DataUpdated dataupdated = new DataUpdated();
            dataupdated.setVisible(true);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    dataupdated.dispose();
                }
            }, 1000);
            show_Table("timetable", ViewTimeTable);
            
            DayCombo.setSelectedIndex(0);
            HoursCombo.setSelectedIndex(0);
            MinutesCombo.setSelectedIndex(0);
            HoursCombo1.setSelectedIndex(0);
            MinutesCombo1.setSelectedIndex(0);
            CourseCombo.setSelectedIndex(0);
            InstructorCombo.setSelectedIndex(0);
            RoomCombo.setSelectedIndex(0);
            
        } catch (SQLException ex) {
            Failed failed = new Failed();
            failed.setVisible(true);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    failed.dispose();
                }
            }, 1000);
        }
    }//GEN-LAST:event_UpdateMouseClicked

    private void UpdateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_UpdateKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER) {
            UpdateMouseClicked(null);
        }
    }//GEN-LAST:event_UpdateKeyPressed

    private void DeleteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_DeleteMouseClicked
        // TODO add your handling code here:
        String sHour = (String)HoursCombo.getSelectedItem();
        String sMinutes = (String)MinutesCombo.getSelectedItem();
        String eHour = (String)HoursCombo1.getSelectedItem();
        String eMinutes = (String)MinutesCombo1.getSelectedItem();
        day = (String)DayCombo.getSelectedItem();
        sTime = sHour + ':' + sMinutes;
        eTime = eHour + ':' + eMinutes;
        cName = (String)CourseCombo.getSelectedItem();
        instructorName = (String)InstructorCombo.getSelectedItem();
        roomNo = (String)RoomCombo.getSelectedItem();
        
        String[] sparts = sTime.split(":");
        String[] eparts = eTime.split(":");

        if(day.equals("Select Day")){
            Day.setForeground(Color.RED);
            temp = false;
        } else {
            Day.setForeground(Color.BLACK);
            temp = true;
        }
        if(sparts[0].equals("Select Hour") || sparts[1].equals("Select Minute")){
            StartTime.setForeground(Color.RED);
            temp = false;
        } else {
            StartTime.setForeground(Color.BLACK);
            temp = true;
        }
        if(eparts[0].equals("Select Hour") || eparts[1].equals("Select Minute")){
            EndTime.setForeground(Color.RED);
            temp = false;
        } else {
            EndTime.setForeground(Color.BLACK);
            temp = true;
        }
        if(cName.equals("Select Course")){
            Course.setForeground(Color.RED);
            temp = false;
        } else {
            Course.setForeground(Color.BLACK);
            temp = true;
        }
        if(instructorName.equals("Select Instructor")){
            Instructor.setForeground(Color.RED);
            temp = false;
        } else {
            Instructor.setForeground(Color.BLACK);
            temp = true;
        }
        if(roomNo.equals("Select Room")){
            Room.setForeground(Color.RED);
            temp = false;
        } else {
            Room.setForeground(Color.BLACK);
            temp = true;
        }
        boolean confirmed = false;
        if (temp == true){
            String message = "Are you sure you want to delete the time table entry for Course: " + cName + " on " + day + " from " + sTime + " to " + eTime + "?";
            CustomConfirmDialog customDialog = new CustomConfirmDialog(this, "Confirm Deletion", message);
            customDialog.setVisible(true);
            confirmed = customDialog.isConfirmed();
        }

        if (confirmed == true) {
            String query = "DELETE FROM `timetable` WHERE `day` = ? AND `sTime` = ? AND `eTime` = ? AND `cName` = ? AND `instructorName` = ? AND `roomNo` = ?";
            try {
                pst = connect.prepareStatement(query);
                pst.setString(1, day);
                pst.setString(2, sTime);
                pst.setString(3, eTime);
                pst.setString(4, cName);
                pst.setString(5, instructorName);
                pst.setString(6, roomNo);

                int rowsDeleted = pst.executeUpdate();

                if (rowsDeleted > 0) {
                    DataDeleted datadeleted = new DataDeleted();
                    datadeleted.setVisible(true);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            datadeleted.dispose();
                        }
                    }, 1000);
                    show_Table("timetable", ViewTimeTable);
                    DayCombo.setSelectedIndex(0);
                    HoursCombo.setSelectedIndex(0);
                    MinutesCombo.setSelectedIndex(0);
                    HoursCombo1.setSelectedIndex(0);
                    MinutesCombo1.setSelectedIndex(0);
                    CourseCombo.setSelectedIndex(0);
                    InstructorCombo.setSelectedIndex(0);
                    RoomCombo.setSelectedIndex(0);
                }
            } catch (SQLException ex) {
                Failed failed = new Failed();
                failed.setVisible(true);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        failed.dispose();
                    }
                }, 1000);
                JOptionPane.showMessageDialog(null, "Database Error: " + ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_DeleteMouseClicked

    private void DeleteKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DeleteKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER) {
            DeleteMouseClicked(null);
        }
    }//GEN-LAST:event_DeleteKeyPressed

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
            java.util.logging.Logger.getLogger(HomePageAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HomePageAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HomePageAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HomePageAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HomePageAdmin().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel AAcademicYear;
    private javax.swing.JTextField AAcademicYearField;
    private javax.swing.JLabel AAddress;
    private javax.swing.JLabel AAddress2;
    private javax.swing.JTextField AAddressField;
    private javax.swing.JTextField AAddressField2;
    private javax.swing.JLabel ACDepartment;
    private javax.swing.JComboBox<String> ACDepartmentCombo;
    private javax.swing.JLabel ACNIC;
    private javax.swing.JLabel ACNIC2;
    private javax.swing.JTextField ACNICField;
    private javax.swing.JTextField ACNICField2;
    private javax.swing.JButton ACSubmitBtn;
    private javax.swing.JLabel ACourseCode;
    private javax.swing.JTextField ACourseCodeField;
    private javax.swing.JLabel ACourseName;
    private javax.swing.JTextField ACourseNameField;
    private javax.swing.JLabel ACreditHrs;
    private javax.swing.JTextField ACreditHrsField;
    private javax.swing.JLabel ADateOfBirth;
    private javax.swing.JLabel ADateOfBirth2;
    private com.toedter.calendar.JDateChooser ADateOfBirthField;
    private com.toedter.calendar.JDateChooser ADateOfBirthField2;
    private javax.swing.JLabel ADepartment;
    private javax.swing.JLabel ADepartment2;
    private javax.swing.JComboBox<String> ADepartmentCombo;
    private javax.swing.JComboBox<String> ADepartmentCombo2;
    private javax.swing.JLabel AEmail;
    private javax.swing.JLabel AEmail2;
    private javax.swing.JTextField AEmailField;
    private javax.swing.JTextField AEmailField2;
    private javax.swing.JLabel AFirstName;
    private javax.swing.JLabel AFirstName2;
    private javax.swing.JTextField AFirstNameField;
    private javax.swing.JTextField AFirstNameField2;
    private javax.swing.JLabel AGender;
    private javax.swing.JLabel AGender2;
    private javax.swing.JComboBox<String> AGenderCombo;
    private javax.swing.JComboBox<String> AGenderCombo2;
    private javax.swing.JLabel ALastName;
    private javax.swing.JLabel ALastName2;
    private javax.swing.JTextField ALastNameField;
    private javax.swing.JTextField ALastNameField2;
    private javax.swing.JLabel APhoneNo;
    private javax.swing.JLabel APhoneNo2;
    private javax.swing.JTextField APhoneNoField;
    private javax.swing.JTextField APhoneNoField2;
    private javax.swing.JLabel AProgram;
    private javax.swing.JTextField AProgramField;
    private javax.swing.JLabel ASeats;
    private javax.swing.JTextField ASeatsField;
    private javax.swing.JLabel ASession;
    private javax.swing.JComboBox<String> ASessionCombo;
    private javax.swing.JButton ASubmitBtn;
    private javax.swing.JButton ASubmitBtn2;
    private javax.swing.JButton Add;
    private javax.swing.JPanel AddCourse;
    private javax.swing.JPanel AddInstructor;
    private javax.swing.JPanel AddStudent;
    private javax.swing.JLabel Course;
    private javax.swing.JComboBox<String> CourseCombo;
    private javax.swing.JPanel CoursePanel;
    private javax.swing.JTabbedPane CourseTabs;
    private javax.swing.JLabel Day;
    private javax.swing.JComboBox<String> DayCombo;
    private javax.swing.JButton Delete;
    private javax.swing.JLabel EndTime;
    private javax.swing.JPanel HomePageAdminPanel;
    private javax.swing.JComboBox<String> HoursCombo;
    private javax.swing.JComboBox<String> HoursCombo1;
    private javax.swing.JLabel Instructor;
    private javax.swing.JComboBox<String> InstructorCombo;
    private javax.swing.JPanel InstructorPanel;
    private javax.swing.JTabbedPane InstructorTabs;
    private javax.swing.JButton LogOut;
    private javax.swing.JPanel MainPagePanel;
    private javax.swing.JButton ManageCourse;
    private javax.swing.JButton ManageInstructor;
    private javax.swing.JButton ManageStudent;
    private javax.swing.JComboBox<String> MinutesCombo;
    private javax.swing.JComboBox<String> MinutesCombo1;
    private javax.swing.JButton PublishResult;
    private javax.swing.JPanel ResultPanel;
    private javax.swing.JLabel Room;
    private javax.swing.JComboBox<String> RoomCombo;
    private javax.swing.JPanel SidePanel;
    private javax.swing.JLabel StartTime;
    private javax.swing.JPanel StudentPanel;
    private javax.swing.JTabbedPane StudentTabs;
    private javax.swing.JButton TimeTable;
    private javax.swing.JPanel TimeTablePanel;
    private javax.swing.JScrollPane TimeTableScrollPane;
    private javax.swing.JLabel UAcademicYear;
    private javax.swing.JTextField UAcademicYearField;
    private javax.swing.JRadioButton UActive;
    private javax.swing.JRadioButton UActive2;
    private javax.swing.JLabel UAddress;
    private javax.swing.JLabel UAddress2;
    private javax.swing.JTextField UAddressField;
    private javax.swing.JTextField UAddressField2;
    private javax.swing.JLabel UCDepartment;
    private javax.swing.JComboBox<String> UCDepartmentCombo;
    private javax.swing.JLabel UCNIC;
    private javax.swing.JLabel UCNIC2;
    private javax.swing.JTextField UCNICField;
    private javax.swing.JTextField UCNICField2;
    private javax.swing.JButton UCSubmitBtn;
    private javax.swing.JLabel UCourseCode;
    private javax.swing.JTextField UCourseCodeField;
    private javax.swing.JLabel UCourseName;
    private javax.swing.JTextField UCourseNameField;
    private javax.swing.JLabel UCreditHrs;
    private javax.swing.JTextField UCreditHrsField;
    private javax.swing.JLabel UDateOfBirth;
    private javax.swing.JLabel UDateOfBirth2;
    private com.toedter.calendar.JDateChooser UDateOfBirthField;
    private com.toedter.calendar.JDateChooser UDateOfBirthField2;
    private javax.swing.JLabel UDepartment;
    private javax.swing.JLabel UDepartment2;
    private javax.swing.JComboBox<String> UDepartmentCombo;
    private javax.swing.JComboBox<String> UDepartmentCombo2;
    private javax.swing.JLabel UEmail;
    private javax.swing.JLabel UEmail2;
    private javax.swing.JTextField UEmailField;
    private javax.swing.JTextField UEmailField2;
    private javax.swing.JLabel UFirstName;
    private javax.swing.JLabel UFirstName2;
    private javax.swing.JTextField UFirstNameField;
    private javax.swing.JTextField UFirstNameField2;
    private javax.swing.JLabel UGender;
    private javax.swing.JLabel UGender2;
    private javax.swing.JComboBox<String> UGenderCombo;
    private javax.swing.JComboBox<String> UGenderCombo2;
    private javax.swing.JLabel UId;
    private javax.swing.JLabel UId2;
    private javax.swing.JTextField UIdField;
    private javax.swing.JTextField UIdField2;
    private javax.swing.JRadioButton UInactive;
    private javax.swing.JRadioButton UInactive2;
    private javax.swing.JLabel ULastName;
    private javax.swing.JLabel ULastName2;
    private javax.swing.JTextField ULastNameField;
    private javax.swing.JTextField ULastNameField2;
    private javax.swing.JLabel UPassword;
    private javax.swing.JLabel UPassword2;
    private javax.swing.JPasswordField UPasswordField;
    private javax.swing.JPasswordField UPasswordField2;
    private javax.swing.JLabel UPhoneNo;
    private javax.swing.JLabel UPhoneNo2;
    private javax.swing.JTextField UPhoneNoField;
    private javax.swing.JTextField UPhoneNoField2;
    private javax.swing.JLabel UProgram;
    private javax.swing.JTextField UProgramField;
    private javax.swing.JLabel USeats;
    private javax.swing.JTextField USeatsField;
    private javax.swing.JLabel USession;
    private javax.swing.JComboBox<String> USessionCombo;
    private javax.swing.JLabel UStatus;
    private javax.swing.JLabel UStatus2;
    private javax.swing.JButton USubmitBtn;
    private javax.swing.JButton USubmitBtn2;
    private javax.swing.JButton Update;
    private javax.swing.JPanel UpdateCourse;
    private javax.swing.JPanel UpdateInstructor;
    private javax.swing.JPanel UpdateStudent;
    private javax.swing.JPanel ViewCourses;
    private javax.swing.JScrollPane ViewCoursesScrollPane;
    private javax.swing.JTable ViewCoursesTable;
    private javax.swing.JPanel ViewInstructors;
    private javax.swing.JScrollPane ViewInstructorsScrollPane;
    private javax.swing.JTable ViewInstructorsTable;
    private javax.swing.JPanel ViewStudents;
    private javax.swing.JScrollPane ViewStudentsScrollPane;
    private javax.swing.JTable ViewStudentsTable;
    private javax.swing.JTable ViewTimeTable;
    // End of variables declaration//GEN-END:variables
}
