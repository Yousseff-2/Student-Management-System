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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CoursesController {
    public TextField searchField;
    @FXML private TableView<Course> coursesTableView;
    @FXML private TableColumn<Course, Integer> courseIdColumn;
    @FXML private TableColumn<Course, String> courseNameColumn;
    @FXML private TableColumn<Course, Integer> courseCreditsColumn;
    @FXML private TableColumn<Course, String> courseInstructorColumn;

    @FXML private Label welcomeLabel;
    @FXML private Label nameLabel;
    @FXML private Label userNameLabel;

    @FXML private Button student;
    @FXML private Button inactiveStudent;
    @FXML private Button course;
    @FXML private Button enroll;
    @FXML private Button grade;
    private ObservableList<Course> courseList = FXCollections.observableArrayList();
    private static final String URL = "jdbc:mysql://localhost:3306/system";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    String Name , Username;
    public void setAdminInfo(String name, String username) {
        nameLabel.setText("Name: " + name);
        userNameLabel.setText("Username: " + username);
        welcomeLabel.setText("Welcome " + name + "!");
        Name = name;
        Username = username;
        courseIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCourseId()).asObject());
        courseNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourseName()));
        courseCreditsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCreditHours()).asObject());
        loadCourses();
    }

    private void loadCourses() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT * FROM Courses";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                List<Course> courses = new ArrayList<>();
                while (rs.next()) {
                    int courseId = rs.getInt("Course_ID");
                    String courseName = rs.getString("Course_Name");
                    int credits = rs.getInt("Credit_Hours");
                    courses.add(new Course(courseId, courseName, credits));
                }

                ObservableList<Course> observableCourses = FXCollections.observableArrayList(courses);
                coursesTableView.setItems(observableCourses);
            }
        } catch (SQLException e) {
            showErrorDialog("Error loading courses: " + e.getMessage());
        }
    }

    public void active(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXML/adminstudent.fxml"));
        Parent root = fxmlLoader.load();
        ActiveStudentAdminController controller = fxmlLoader.getController();
        controller.setAdminInfo(Name, Username);
        Stage stage = (Stage) student.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void inactive(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXML/Inactive-view.fxml"));
        Parent root = fxmlLoader.load();
        InActiveStudentAdminController controller = fxmlLoader.getController();
        controller.setAdminInfo(Name, Username);
        Stage stage = (Stage) inactiveStudent.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void enroll(ActionEvent event) throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXML/enroll-adminview.fxml"));
        Parent root = fxmlLoader.load();
        EnrollAdminController controller = fxmlLoader.getController();
        controller.setAdminInfo(Name, Username);
        Stage stage = (Stage) enroll.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void grade(ActionEvent event) throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXML/grade-view.fxml"));
        Parent root = fxmlLoader.load();
        GradeAdminController controller = fxmlLoader.getController();
        controller.setAdminInfo(Name, Username);
        Stage stage = (Stage) grade.getScene().getWindow();
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

    public void addCourse(ActionEvent event) {
        TextField courseNameField = new TextField();
        TextField creditHoursField = new TextField();

        VBox dialogPane = new VBox(10);
        dialogPane.getChildren().addAll(
                new Label("Course Name:"), courseNameField,
                new Label("Credit Hours:"), creditHoursField
        );

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New Course");
        dialog.getDialogPane().setContent(dialogPane);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, cancelButton);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == okButton) {
            String courseName = courseNameField.getText().trim();
            String creditHoursStr = creditHoursField.getText().trim();

            if (courseName.isEmpty() || creditHoursStr.isEmpty()) {
                showErrorDialog("All fields must be filled in!");
            } else {
                try {
                    int creditHours = Integer.parseInt(creditHoursStr);
                    if (creditHours <= 0) {
                        showErrorDialog("Credit Hours must be greater than zero!");
                        return;
                    }

                    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                        String checkSql = "SELECT COUNT(*) FROM Courses WHERE Course_Name = ?";
                        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                            checkStmt.setString(1, courseName);
                            ResultSet rs = checkStmt.executeQuery();
                            rs.next();
                            if (rs.getInt(1) > 0) {
                                showErrorDialog("Course name already exists!");
                                return;
                            }
                        }

                        String sql = "INSERT INTO Courses (Course_Name, Credit_Hours) VALUES (?, ?)";
                        try (PreparedStatement ps = conn.prepareStatement(sql)) {
                            ps.setString(1, courseName);
                            ps.setInt(2, creditHours);
                            ps.executeUpdate();
                            loadCourses();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } catch (NumberFormatException e) {
                    showErrorDialog("Credit Hours must be a valid number!");
                }
            }
        }
    }


    public void editCourse(ActionEvent event) {
        Course selectedCourse = (Course) coursesTableView.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            TextField courseNameField = new TextField(selectedCourse.getCourseName());
            TextField creditHoursField = new TextField(String.valueOf(selectedCourse.getCreditHours()));

            VBox dialogPane = new VBox(10);
            dialogPane.getChildren().addAll(
                    new Label("Course Name:"), courseNameField,
                    new Label("Credit Hours:"), creditHoursField
            );

            ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Edit Course");
            dialog.getDialogPane().setContent(dialogPane);
            dialog.getDialogPane().getButtonTypes().addAll(okButton, cancelButton);

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == okButton) {
                String newCourseName = courseNameField.getText().trim();
                String newCreditHoursStr = creditHoursField.getText().trim();

                if (newCourseName.isEmpty() || newCreditHoursStr.isEmpty()) {
                    showErrorDialog("All fields must be filled in!");
                } else {
                    try {
                        int newCreditHours = Integer.parseInt(newCreditHoursStr);
                        if (newCreditHours <= 0) {
                            showErrorDialog("Credit Hours must be greater than zero!");
                            return;
                        }

                        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                            String checkSql = "SELECT COUNT(*) FROM Courses WHERE Course_Name = ? AND Course_ID != ?";
                            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                                checkStmt.setString(1, newCourseName);
                                checkStmt.setInt(2, selectedCourse.getCourseId());
                                ResultSet rs = checkStmt.executeQuery();
                                rs.next();
                                if (rs.getInt(1) > 0) {
                                    showErrorDialog("Course name already exists!");
                                    return;
                                }
                            }

                            String sql = "UPDATE Courses SET Course_Name = ?, Credit_Hours = ? WHERE Course_ID = ?";
                            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                                ps.setString(1, newCourseName);
                                ps.setInt(2, newCreditHours);
                                ps.setInt(3, selectedCourse.getCourseId());
                                ps.executeUpdate();
                                loadCourses(); // Reload the courses after updating
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } catch (NumberFormatException e) {
                        showErrorDialog("Credit Hours must be a valid number!");
                    }
                }
            }
        } else {
            showErrorDialog("Please select a course to edit.");
        }
    }



    public void deleteCourse(ActionEvent event) {
        Course selectedCourse = (Course) coursesTableView.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            String courseName = selectedCourse.getCourseName();
            int courseId = selectedCourse.getCourseId();

            int option = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to delete course " + courseName + "?",
                    "Delete Confirmation",
                    JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                    String sql = "DELETE FROM Courses WHERE Course_ID = ?";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setInt(1, courseId);
                        int rowsAffected = ps.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("Course deleted successfully.");
                            loadCourses();
                        } else {
                            System.out.println("No course was deleted.");
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            showErrorDialog("Please select a course to delete.");
        }
    }


    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void searchCourse(MouseEvent keyEvent) {
        String searchText = searchField.getText().toLowerCase(); // Get the search text and convert to lower case
        ObservableList<Course> filteredList = FXCollections.observableArrayList(); // Create a new list for filtered courses

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT * FROM Courses WHERE LOWER(Course_Name) LIKE ?"; // Filter by course name
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, "%" + searchText + "%"); // Set the parameter for the LIKE query
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    int courseId = rs.getInt("Course_ID");
                    String courseName = rs.getString("Course_Name");
                    int credits = rs.getInt("Credit_Hours");
                    filteredList.add(new Course(courseId, courseName, credits)); // Add to the filtered list
                }
            }
        } catch (SQLException e) {
            showErrorDialog("Error searching courses: " + e.getMessage());
        }

        coursesTableView.setItems(filteredList); // Update the table view with the filtered list
    }

}
