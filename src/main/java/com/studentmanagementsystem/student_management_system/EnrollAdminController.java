package com.studentmanagementsystem.student_management_system;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnrollAdminController {

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

    @FXML
    public TableView<Enrollment> enrollmentTableView;
    @FXML
    public TableColumn<Enrollment, String> studentNameColumn;
    @FXML
    public TableColumn<Enrollment, String> courseNameColumn;
    private ObservableList<Enrollment> enrollmentList = FXCollections.observableArrayList();
    private static final String URL = "jdbc:mysql://localhost:3306/system";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    public TextField searchField;
    private String Name, Username;

    public void setAdminInfo(String name, String username) throws SQLException {
        nameLabel.setText("Name: " + name);
        userNameLabel.setText("Username: " + username);
        welcomeLabel.setText("Welcome " + name + "!");
        this.Name = name;
        this.Username = username;

        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));

        loadEnrollment();
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

    public void grade(ActionEvent event) throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXML/grade-view.fxml"));
        Parent root = fxmlLoader.load();
        GradeAdminController controller = fxmlLoader.getController();
        controller.setAdminInfo(Name, Username);
        Stage stage = (Stage) grade.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }


    public void loadEnrollment() throws SQLException {
        String sql = "SELECT * FROM StudentCourses WHERE Grade IS NULL OR Grade = ''";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            enrollmentList.clear();
            while (rs.next()) {
                int student_id = rs.getInt("Student_ID");
                int course_id = rs.getInt("Course_ID");
                String student_name = getStudentName(student_id);
                String course_name = getCourseName(course_id);
                int StudentCourse_ID = rs.getInt("StudentCourse_ID");
                enrollmentList.add(new Enrollment(student_name, course_name, StudentCourse_ID));
            }

            enrollmentTableView.setItems(enrollmentList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    private String getStudentName(int id) {
        String sql = "SELECT Name FROM Accounts WHERE ID = ?";
        String studentName = "";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                studentName = rs.getString("Name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return studentName;
    }

    public void editenroll(ActionEvent event) {
        Enrollment selectedEnrollment = enrollmentTableView.getSelectionModel().getSelectedItem();
        if (selectedEnrollment != null) {
            int studentCourseId = selectedEnrollment.getStudentCourse_ID();

            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Edit Grade");

            CheckBox aGrade = new CheckBox("A");
            CheckBox aPlusGrade = new CheckBox("A+");
            CheckBox aMinusGrade = new CheckBox("A-");
            CheckBox bGrade = new CheckBox("B");
            CheckBox bPlusGrade = new CheckBox("B+");
            CheckBox bMinusGrade = new CheckBox("B-");
            CheckBox cGrade = new CheckBox("C");
            CheckBox cPlusGrade = new CheckBox("C+");
            CheckBox cMinusGrade = new CheckBox("C-");
            CheckBox dGrade = new CheckBox("D");
            CheckBox dPlusGrade = new CheckBox("D+");
            CheckBox dMinusGrade = new CheckBox("D-");
            CheckBox fGrade = new CheckBox("F");

            List<CheckBox> gradeCheckboxes = List.of(
                    aGrade, aPlusGrade, aMinusGrade, bGrade, bPlusGrade, bMinusGrade,
                    cGrade, cPlusGrade, cMinusGrade, dGrade, dPlusGrade, dMinusGrade, fGrade
            );

            for (CheckBox checkBox : gradeCheckboxes) {
                checkBox.setOnAction(e -> {
                    if (checkBox.isSelected()) {
                        gradeCheckboxes.forEach(cb -> {
                            if (cb != checkBox) cb.setSelected(false);
                        });
                    }
                });
            }

            VBox dialogPane = new VBox(10);
            dialogPane.getChildren().addAll(
                    new Label("Select New Grade:"),
                    aGrade, aPlusGrade, aMinusGrade, bGrade, bPlusGrade, bMinusGrade,
                    cGrade, cPlusGrade, cMinusGrade, dGrade, dPlusGrade, dMinusGrade, fGrade
            );

            ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);
            dialog.getDialogPane().setContent(dialogPane);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == updateButtonType) {
                    for (CheckBox checkBox : gradeCheckboxes) {
                        if (checkBox.isSelected()) {
                            return checkBox.getText();
                        }
                    }
                }
                return null;
            });

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(newGrade -> {
                String sql = "UPDATE StudentCourses SET Grade = ? WHERE StudentCourse_ID = ?";
                try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, newGrade);
                    ps.setInt(2, studentCourseId);
                    ps.executeUpdate();
                    loadEnrollment();
                    showAlert("Grade updated successfully.");
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Error updating grade: " + e.getMessage());
                }
            });
        } else {
            showAlert("Please select an enrollment to edit.");
        }
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public void deleteenroll(ActionEvent event) {
        Enrollment selectedEnrollment = enrollmentTableView.getSelectionModel().getSelectedItem();

        if (selectedEnrollment != null) {
            int enrollmentId = selectedEnrollment.getStudentCourse_ID();

            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this enrollment?", ButtonType.YES, ButtonType.NO);
            confirmationAlert.setTitle("Confirm Deletion");
            confirmationAlert.setHeaderText(null);
            Optional<ButtonType> result = confirmationAlert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.YES) {
                String sql = "DELETE FROM StudentCourses WHERE StudentCourse_ID = ?";

                try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, enrollmentId);
                    ps.executeUpdate();

                    loadEnrollment();
                    showAlert("Enrollment deleted successfully.");
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Error occurred while deleting enrollment.");
                }
            }
        } else {
            showAlert("Please select an enrollment to delete.");
        }
    }

    public void addenroll(ActionEvent event) {
        Dialog<Enrollment> dialog = new Dialog<>();
        dialog.setTitle("Add Enrollment");

        ComboBox<String> studentUsernameComboBox = new ComboBox<>();
        ComboBox<String> courseComboBox = new ComboBox<>();

        loadStudentUsernames(studentUsernameComboBox);
        loadCourses(courseComboBox);

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        VBox dialogPane = new VBox(10);
        dialogPane.getChildren().addAll(
                new Label("Select Student (Username):"),
                studentUsernameComboBox,
                new Label("Select Course:"),
                courseComboBox
        );

        dialog.getDialogPane().setContent(dialogPane);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new Enrollment(studentUsernameComboBox.getValue(), courseComboBox.getValue(), -1);
            }
            return null;
        });

        Optional<Enrollment> result = dialog.showAndWait();
        result.ifPresent(enrollment -> {
            if (enrollment != null &&
                    enrollment.getStudentName() != null && !enrollment.getStudentName().isEmpty() &&
                    enrollment.getCourseName() != null && !enrollment.getCourseName().isEmpty()) {

                int studentId = getStudentIdByUsername(enrollment.getStudentName());
                int courseId = getCourseId(enrollment.getCourseName());

                if (studentId == -1 || courseId == -1) {
                    showAlert("Invalid student or course selection.");
                    return;
                }

                if (hasEnrollmentCourse(enrollment.getStudentName(), enrollment.getCourseName())) {
                    showAlert("The student has already enrolled in this course.");
                } else if (!hasPassedCourse(enrollment.getStudentName(), enrollment.getCourseName())) {
                    String sql = "INSERT INTO StudentCourses (Student_ID, Course_ID) VALUES (?, ?)";
                    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                         PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                        ps.setInt(1, studentId);
                        ps.setInt(2, courseId);

                        ps.executeUpdate();
                        ResultSet generatedKeys = ps.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            int generatedId = generatedKeys.getInt(1);
                            enrollment.setStudentCourse_ID(generatedId);
                        }
                        loadEnrollment();
                        showAlert("Enrollment added successfully.");
                    } catch (SQLException e) {
                        e.printStackTrace();
                        showAlert("Error adding enrollment: " + e.getMessage());
                    }
                } else {
                    showAlert("The student has already passed this course.");
                }
            } else {
                showAlert("Please fill all fields correctly.");
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


    private boolean hasEnrollmentCourse(String studentName , String courseName) {
        String sql = "SELECT COUNT(*) FROM StudentCourses WHERE Student_ID = ? AND Course_ID = ? AND Grade IS NULL";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, getStudentId(studentName));
            ps.setInt(2, getCourseId(courseName));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void loadStudents(ComboBox<String> comboBox) {
        String sql = "SELECT Name FROM Students";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                comboBox.getItems().add(rs.getString("Name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadCourses(ComboBox<String> comboBox) {
        String sql = "SELECT Course_Name FROM Courses";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                comboBox.getItems().add(rs.getString("Course_Name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean hasPassedCourse(String studentName, String courseName) {
        String sql = "SELECT COUNT(*) FROM StudentCourses WHERE Student_ID = ? AND Course_ID = ? AND Grade IS NOT NULL";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, getStudentId(studentName));
            ps.setInt(2, getCourseId(courseName));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private int getStudentId(String studentName) {
        String sql = "SELECT Student_ID FROM Students WHERE Name = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("Student_ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private int getCourseId(String courseName) {
        String sql = "SELECT Course_ID FROM Courses WHERE Course_Name = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("Course_ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @FXML
    public void logout(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("FXML/hello-view.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Student Management System");
        stage.show();
    }

    public void searchstudent(MouseEvent mouseEvent) {
        String searchText = searchField.getText();
        enrollmentList.clear();

        String sql = "SELECT * FROM StudentCourses WHERE Grade IS NULL";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int student_id = rs.getInt("Student_ID");
                int course_id = rs.getInt("Course_ID");
                String student_name = getStudentName(student_id);
                String course_name = getCourseName(course_id);
                int StudentCourse_ID = rs.getInt("StudentCourse_ID");
                if(student_name.contains(searchText))enrollmentList.add(new Enrollment(student_name, course_name, StudentCourse_ID));
            }

            enrollmentTableView.setItems(enrollmentList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
