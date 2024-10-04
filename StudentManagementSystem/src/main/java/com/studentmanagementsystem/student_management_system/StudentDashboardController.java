package com.studentmanagementsystem.student_management_system;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.io.IOException;
import java.sql.*;

public class StudentDashboardController {

    @FXML
    private TextField gradesSearchField;
    @FXML
    private TableColumn<Course, String> courseNameColumn;
    @FXML
    private TableColumn<Course, Integer> courseCreditsColumn;
    @FXML
    private TableColumn<Course, Void> courseremove;
    @FXML
    private TableView<Course> coursesTableView;

    @FXML
    private TableView<GradesStudent> gradesTableView;
    @FXML
    private TableColumn<GradesStudent, String> gradeCourseColumn;
    @FXML
    private TableColumn<GradesStudent, Integer> gradeCourseHours;
    @FXML
    private TableColumn<GradesStudent, String> gradeScoreColumn;
    @FXML
    private Label welcomeLabel, nameLabel, userNameLabel, majorLabel;
    @FXML
    private Button logout, enrollButton;

    private static final String URL = "jdbc:mysql://localhost:3306/system";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    String Name, Username, Major;
    private ObservableList<Course> coursesList = FXCollections.observableArrayList();
    private ObservableList<GradesStudent> gradesList = FXCollections.observableArrayList();

    public void setStudentInfo(String name, String username, String major) {
        this.Name = name;
        this.Username = username;
        this.Major = major;

        nameLabel.setText("Name: " + name);
        userNameLabel.setText("Username: " + username);
        majorLabel.setText("Major: " + major);
        welcomeLabel.setText("Welcome " + name + "!");

        // Set cell value factories for the enrolled courses table
        // Set cell value factories for the enrolled courses table
        courseCreditsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCreditHours()).asObject());
        courseNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourseName()));
        courseremove.setCellFactory(new Callback<TableColumn<Course, Void>, TableCell<Course, Void>>() {
            @Override
            public TableCell<Course, Void> call(TableColumn<Course, Void> param) {
                return new TableCell<Course, Void>() {
                    private final Button removeButton = new Button("Remove");

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(removeButton);
                            removeButton.setOnAction(event -> {
                                // Logic to remove the course
                                Course course = getTableView().getItems().get(getIndex());
                                removeCourse(course.getCourseName());
                            });
                        }
                    }
                };
            }
        });


        gradeCourseColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourseName()));
        gradeCourseHours.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCredits()).asObject()); // Adjust this line
        gradeScoreColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGrade()));


        loadEnrolledCourses(username);
        loadFinishedCourses(username);
    }
    private void removeCourse(String name){
        String sql = "DELETE FROM StudentCourses WHERE Course_ID = ? AND Student_ID = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Assume you have a method to get the student ID by username
            int studentId = getStudentId(Username);
            ps.setInt(1, getCourseIdByName(name));
            ps.setInt(2, studentId);
            ps.executeUpdate();
            loadEnrolledCourses(Name);
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorDialog("Error removing course from database.");
        }
    }

    private int getCourseIdByName(String courseName) {
        String sql = "SELECT Course_ID FROM Courses WHERE Course_Name = ?";
        int courseId = -1; // Default to -1 if not found

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, courseName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                courseId = rs.getInt("Course_ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorDialog("Error retrieving course ID from database.");
        }

        return courseId;
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null); // No header text
        alert.setContentText(message);
        alert.showAndWait(); // Wait for the user to close the dialog
    }
    @FXML
    private void loadEnrolledCourses(String username) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            int id = getStudentId(username);

            String sql = "SELECT Course_ID FROM StudentCourses WHERE Student_ID = ? AND Grade = '' ";
            coursesList.clear();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    int courseId = rs.getInt("Course_ID");
                    String courseName = getCourseName(courseId);
                    int credits = getCourseCredits(courseId);
                    coursesList.add(new Course(courseId, courseName, credits));
                }

                coursesTableView.setItems(coursesList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getCourseCredits(int courseId) {
        String sql = "SELECT Credit_Hours FROM Courses WHERE Course_ID = ?";
        int credits = 0;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                credits = rs.getInt("Credit_Hours");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return credits;
    }

    private void loadFinishedCourses(String username) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            int id = getStudentId(username);

            String sql = "SELECT Course_ID, Grade FROM StudentCourses WHERE Student_ID = ? AND Grade != ''";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();

                gradesList.clear();
                while (rs.next()) {
                    int courseId = rs.getInt("Course_ID");
                    String grade = rs.getString("Grade");
                    String courseName = getCourseName(courseId);
                    int credits = getCourseCredits(courseId);
                    System.out.println(grade);
                    gradesList.add(new GradesStudent(courseName, credits, grade));
                }

                gradesTableView.setItems(gradesList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getStudentId(String username) {
        int studentId = -1;
        String sql = "SELECT ID FROM Accounts WHERE Username = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                studentId = rs.getInt("ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return studentId;
    }

    private String getCourseName(int id) {
        String sql = "SELECT Course_Name FROM Courses WHERE Course_ID = ?";
        String courseName = "";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                courseName = rs.getString("Course_Name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courseName;
    }

    public void enroll(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXML/enrol-view.fxml"));
        Parent root = fxmlLoader.load();
        Studentenrollcontroller controller = fxmlLoader.getController();
        controller.setStudentInfo(Name, Username, Major);
        Stage stage = (Stage) enrollButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void logout(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("FXML/hello-view.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Student Management System");
        stage.show();
    }

    public void grade(MouseEvent mouseEvent) {
        String searchText = gradesSearchField.getText();

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            int id = getStudentId(Username);

            String sql = "SELECT Course_ID, Grade FROM StudentCourses WHERE Student_ID = ? AND Grade IS NOT NULL";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();

                gradesList.clear();
                while (rs.next()) {
                    int courseId = rs.getInt("Course_ID");
                    String grade = rs.getString("Grade");
                    String courseName = getCourseName(courseId);
                    int credits = getCourseCredits(courseId);
                    if (courseName.contains(searchText)) {
                        gradesList.add(new GradesStudent(courseName, credits, grade));
                    }
                }

                gradesTableView.setItems(gradesList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void course(MouseEvent mouseEvent) {
        String searchText = gradesSearchField.getText();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            int id = getStudentId(Username);

            String sql = "SELECT Course_ID FROM StudentCourses WHERE Student_ID = ?";
            coursesList.clear();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    int courseId = rs.getInt("Course_ID");
                    String courseName = getCourseName(courseId);
                    int credits = getCourseCredits(courseId);
                    if (courseName.contains(searchText)) {
                        coursesList.add(new Course(courseId, courseName, credits));
                    }
                }

                coursesTableView.setItems(coursesList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
