package com.studentmanagementsystem.student_management_system;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class Studentenrollcontroller {

    @FXML
    private TextField searchField;
    @FXML
    private TableView<Enrollpage> availableCoursesTableView;
    @FXML
    private TableColumn<Enrollpage, String> courseNameColumn;
    @FXML
    private TableColumn<Enrollpage, Integer> courseCreditsColumn;
    @FXML
    private TableColumn<Enrollpage, Integer> courseIDColumn; // Corrected type
    @FXML
    private TableColumn<Enrollpage, Void> enrollButtonColumn; // Added @FXML
    @FXML
    private Label welcomeLabel, nameLabel, userNameLabel, majorLabel;
    @FXML
    private Button backButton;

    private ObservableList<Enrollpage> coursesList = FXCollections.observableArrayList();

    private static final String URL = "jdbc:mysql://localhost:3306/system";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public void setStudentInfo(String name, String username, String major) {
        nameLabel.setText("Name: " + name);
        userNameLabel.setText("Username: " + username);
        majorLabel.setText("Major: " + major);
        welcomeLabel.setText("Welcome " + name + "!");

        courseNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        courseCreditsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCredits()).asObject());
        courseIDColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());

        enrollButtonColumn.setCellFactory(col -> new TableCell<Enrollpage, Void>() {
            private final Button enrollButton = new Button("Enroll");

            {
                enrollButton.setOnAction(event -> {
                    Enrollpage course = getTableView().getItems().get(getIndex());
                    enrollInCourse(course);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(enrollButton);
                }
            }
        });

        loadAvailableCourses();
    }

    private void loadAvailableCourses() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT Course_ID, Course_Name, Credit_Hours FROM Courses " +
                    "WHERE Course_ID NOT IN ( " +
                    "    SELECT Course_ID " +
                    "    FROM StudentCourses " +
                    "    INNER JOIN Accounts ON Student_ID = Accounts.ID " +
                    "    WHERE Accounts.username = ? " +
                    ") OR Course_ID IN ( " +
                    "    SELECT Course_ID " +
                    "    FROM StudentCourses " +
                    "    INNER JOIN Accounts ON Student_ID = Accounts.ID " +
                    "    WHERE Accounts.username = ? AND Grade = 'F' " +
                    ");";

            PreparedStatement ps = conn.prepareStatement(sql);
            String username = userNameLabel.getText().replace("Username: ", "").trim();
            ps.setString(1, username);
            ps.setString(2, username);
            ResultSet rs = ps.executeQuery();
            coursesList.clear();

            while (rs.next()) {
                coursesList.add(new Enrollpage(
                        rs.getString("Course_Name"),
                        rs.getInt("Credit_Hours"),
                        rs.getInt("Course_ID")));
            }
            availableCoursesTableView.setItems(coursesList);

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Failed to load available courses.");
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Database Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void search(MouseEvent mouseEvent) {
        String query = searchField.getText().toLowerCase(); // Case insensitive search
        coursesList.clear();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT Course_ID, Course_Name, Credit_Hours FROM Courses " +
                    "WHERE Course_ID NOT IN ( " +
                    "    SELECT Course_ID " +
                    "    FROM StudentCourses " +
                    "    INNER JOIN Accounts ON Student_ID = Accounts.ID " +
                    "    WHERE Accounts.username = ? " +
                    ") OR Course_ID IN ( " +
                    "    SELECT Course_ID " +
                    "    FROM StudentCourses " +
                    "    INNER JOIN Accounts ON Student_ID = Accounts.ID " +
                    "    WHERE Accounts.username = ? AND Grade = 'F' " +
                    ");";

            PreparedStatement ps = conn.prepareStatement(sql);
            String username = userNameLabel.getText().replace("Username: ", "").trim();
            ps.setString(1, username);
            ps.setString(2, username);
            ResultSet rs = ps.executeQuery();
            coursesList.clear();
            while (rs.next()) {
                String courseName = rs.getString("Course_Name");
                if (courseName.toLowerCase().contains(query)) {
                    coursesList.add(new Enrollpage(
                            courseName,
                            rs.getInt("Credit_Hours"),
                            rs.getInt("Course_ID")));
                }
            }
            availableCoursesTableView.setItems(coursesList);
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Failed to load available courses.");
        }
    }

    public void handleBackAction(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXML/student-view.fxml"));
        Parent root = fxmlLoader.load();
        StudentDashboardController controller = fxmlLoader.getController();
        controller.setStudentInfo(nameLabel.getText().replace("Name: ", "").trim(),
                userNameLabel.getText().replace("Username: ", "").trim(),
                majorLabel.getText().replace("Major: ", "").trim());
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void enrollInCourse(Enrollpage course) {
        int studentId = getCurrentStudentId();

        String sql = "INSERT INTO StudentCourses (Student_ID, Course_ID, Grade) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, course.getId());
            ps.setString(3, "");
            String sql1 = "SELECT COUNT(*) FROM StudentCourses WHERE Student_ID = ? AND Grade IS NULL";
            PreparedStatement preparedStatement = conn.prepareStatement(sql1);
            preparedStatement.setInt(1, studentId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if(count == 6){
                    showErrorAlert("You cannot enroll in more than 6 courses.");
                    return;
                }
            }
            ps.executeUpdate();

            showInfoAlert("Enrollment successful!", "You have been enrolled in " + course.getName() + ".");
            loadAvailableCourses();

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Failed to enroll in the course.");
        }
    }

    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private int getCurrentStudentId() {
        int studentId = -1;
        String username = userNameLabel.getText().replace("Username: ", "").trim();

        String sql = "SELECT ID FROM Accounts WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                studentId = rs.getInt("ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Failed to retrieve student ID.");
        }

        return studentId;
    }

}
