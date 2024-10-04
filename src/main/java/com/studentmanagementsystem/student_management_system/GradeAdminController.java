package com.studentmanagementsystem.student_management_system;

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

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class GradeAdminController {

    @FXML
    public Label welcomeLabel;
    @FXML
    public Label nameLabel;
    @FXML
    public Label userNameLabel;
    @FXML
    public Button student;
    @FXML
    public Button course;
    @FXML
    public Button enroll;
    @FXML
    public Button grade;
    @FXML
    public Button inactiveStudent;
    public TextField searchField;
    @FXML
    private TableView<Grade> gradesTableView;
    @FXML
    private TableColumn<Grade, String> studentNameColumn;
    @FXML
    private TableColumn<Grade, String> courseNameColumn;
    @FXML
    private TableColumn<Grade, String> gradeColumn;
    ObservableList<Grade> gradesList = FXCollections.observableArrayList();

    private String Name, Username;
    private static final String URL = "jdbc:mysql://localhost:3306/system";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public void setAdminInfo(String name, String username) {
        nameLabel.setText("Name: " + name);
        userNameLabel.setText("Username: " + username);
        welcomeLabel.setText("Welcome " + name + "!");
        this.Name = name;
        this.Username = username;

        studentNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStudentName()));
        courseNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourseName()));
        gradeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGrade()));

        loadGrades();
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

    public void course(ActionEvent event) throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXML/course-view.fxml"));
        Parent root = fxmlLoader.load();
        CoursesController controller = fxmlLoader.getController();
        controller.setAdminInfo(Name, Username);
        Stage stage = (Stage) course.getScene().getWindow();
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
    
    private void loadGrades() {
        String sql = "SELECT * FROM StudentCourses WHERE Grade IS NOT NULL AND Grade != ''";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            gradesList.clear();
            while (rs.next()) {
                int studentid = rs.getInt("Student_ID");
                int courseid = rs.getInt("Course_ID");
                String grade = rs.getString("Grade");
                String studentName = getStudentName(studentid);
                String courseName = getCourseName(courseid);
                int id = rs.getInt("StudentCourse_ID");
                gradesList.add(new Grade(studentName, courseName, grade , id));
            }

            gradesTableView.setItems(gradesList);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error loading grades: " + e.getMessage());
        }
    }

    private String getStudentName(int studentId) {
        String sql = "SELECT Name FROM Accounts WHERE ID = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("Name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown Student";
    }

    private String getCourseName(int courseId) {
        String sql = "SELECT Course_Name FROM Courses WHERE Course_ID = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("Course_Name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown Course";
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

    public void editGrade(ActionEvent event) {
        Grade selectedGrade = gradesTableView.getSelectionModel().getSelectedItem();
        if (selectedGrade == null) {
            showAlert("Please select a grade to edit.");
            return;
        }

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Edit Grade");
        dialog.setHeaderText("Edit Grade for " + selectedGrade.getStudentName() + " in " + selectedGrade.getCourseName());

        ComboBox<String> gradeComboBox = new ComboBox<>();
        loadGrades(gradeComboBox);
        gradeComboBox.setValue(selectedGrade.getGrade());

        dialog.getDialogPane().setContent(gradeComboBox);

        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                return gradeComboBox.getValue();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(newGrade -> {
            if (newGrade != null && !newGrade.trim().isEmpty()) {
                String sql = "UPDATE StudentCourses SET Grade = ? WHERE StudentCourse_ID = ?";
                try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, newGrade);
                    pstmt.setInt(2, selectedGrade.getId());
                    pstmt.executeUpdate();
                    loadGrades();
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Error updating grade: " + e.getMessage());
                }
            }
        });
    }

    public void deleteGrade(ActionEvent event) {
        Grade selectedGrade = gradesTableView.getSelectionModel().getSelectedItem();

        if (selectedGrade == null) {
            showAlert("Please select a grade to delete.");
            return;
        }

        String sql = "UPDATE StudentCourses SET Grade = NULL WHERE StudentCourse_ID = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, selectedGrade.getId());
            pstmt.executeUpdate();
            showAlert("Grade deleted successfully.");
            loadGrades();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error deleting grade: " + e.getMessage());
        }
    }

    public void addGrade(ActionEvent event) {
        Dialog<Grade> dialog = new Dialog<>();
        dialog.setTitle("Add Grade");

        ComboBox<String> studentUsernameComboBox = new ComboBox<>();
        ComboBox<String> courseComboBox = new ComboBox<>();
        ComboBox<String> gradeComboBox = new ComboBox<>();

        loadStudentUsernames(studentUsernameComboBox);
        loadCourses(courseComboBox);
        loadGrades(gradeComboBox);

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        VBox dialogPane = new VBox(10);
        dialogPane.getChildren().addAll(
                new Label("Select Student Username:"),
                studentUsernameComboBox,
                new Label("Select Course:"),
                courseComboBox,
                new Label("Select Grade:"),
                gradeComboBox
        );

        dialog.getDialogPane().setContent(dialogPane);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String studentUsername = studentUsernameComboBox.getValue();
                String courseName = courseComboBox.getValue();
                String grade = gradeComboBox.getValue();
                return new Grade(studentUsername, courseName, grade, 0);
            }
            return null;
        });

        dialog.showAndWait().ifPresent(grade -> {
            String sql = "INSERT INTO StudentCourses (Student_ID, Course_ID, Grade) VALUES (?, ?, ?)";
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                int studentId = getStudentIdByUsername(grade.getStudentName()); // Using username
                pstmt.setInt(1, studentId);
                pstmt.setInt(2, getCourseId(grade.getCourseName()));
                pstmt.setString(3, grade.getGrade());
                pstmt.executeUpdate();
                loadGrades();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error adding grade: " + e.getMessage());
            }
        });
    }

    private void loadStudentUsernames(ComboBox<String> studentComboBox) {
        String sql = "SELECT Username FROM Accounts WHERE Role = 'Student'";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            studentComboBox.getItems().clear();

            while (rs.next()) {
                studentComboBox.getItems().add(rs.getString("Username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error loading student usernames: " + e.getMessage());
        }
    }

    private int getStudentIdByUsername(String username) {
        String sql = "SELECT ID FROM Accounts WHERE Username = ? AND Role = 'Student'";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error retrieving student ID: " + e.getMessage());
        }
        return -1;
    }

    private void loadStudents(ComboBox<String> studentComboBox) {
        String sql = "SELECT Name FROM Accounts WHERE Type = 'Student'";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                studentComboBox.getItems().add(rs.getString("Name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error loading students: " + e.getMessage());
        }
    }

    private void loadCourses(ComboBox<String> courseComboBox) {
        String sql = "SELECT Course_Name FROM Courses";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                courseComboBox.getItems().add(rs.getString("Course_Name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error loading courses: " + e.getMessage());
        }
    }

    private void loadGrades(ComboBox<String> gradeComboBox) {
        String[] validGrades = {"A", "A+", "A-", "B", "B+", "B-", "C", "C+", "C-", "D", "D+", "D-", "F"};
        gradeComboBox.getItems().addAll(validGrades);
    }

    private int getStudentId(String studentName) {
        String sql = "SELECT ID FROM Accounts WHERE Name = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private int getCourseId(String courseName) {
        String sql = "SELECT Course_ID FROM Courses WHERE Course_Name = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, courseName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("Course_ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void searchstudent(MouseEvent mouseEvent) {
        String searchText = searchField.getText();
        gradesList.clear();

        String sql = "SELECT * FROM StudentCourses WHERE Grade IS NOT NULL";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int studentid = rs.getInt("Student_ID");
                int courseid = rs.getInt("Course_ID");
                String grade = rs.getString("Grade");
                String studentName = getStudentName(studentid);
                String courseName = getCourseName(courseid);
                int id = rs.getInt("StudentCourse_ID");
                if(studentName.contains(searchText))gradesList.add(new Grade(studentName, courseName, grade , id));
            }

            gradesTableView.setItems(gradesList);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error loading grades: " + e.getMessage());
        }
    }
}
