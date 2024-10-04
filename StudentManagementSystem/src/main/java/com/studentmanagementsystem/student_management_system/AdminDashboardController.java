package com.studentmanagementsystem.student_management_system;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class AdminDashboardController {

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
    public ListView<String> studentListView;
    @FXML
    public Button inactiveStudent;

    private String Name, Username;

    public void setAdminInfo(String name, String username) {
        nameLabel.setText("Name: " + name);
        userNameLabel.setText("Username: " + username);
        welcomeLabel.setText("Welcome " + name + "!");
        this.Name = name;
        this.Username = username;
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
}
