package com.studentmanagementsystem.student_management_system;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.sql.*;

public class SignupController {
    private static final String URL = "jdbc:mysql://localhost:3306/system";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    @FXML
    private TextField nameField;
    @FXML
    private TextField ageField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<String> majorComboBox;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    public void submit(ActionEvent event) {
        String name = nameField.getText();
        String age = ageField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String major = majorComboBox.getValue();

        if (name.isEmpty() || age.isEmpty() || username.isEmpty() || password.isEmpty() || major == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setHeaderText("Please fill in all required fields.");
            alert.setContentText("All fields must be filled out, and major and role must be selected.");
            alert.showAndWait();
            return;
        }
        if(password.length() < 8){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setHeaderText("Password cannot be less than 8 characters.");
            alert.showAndWait();
            return;
        }
        if (!name.matches("[a-zA-Z\\s]+")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setHeaderText("Invalid Name");
            alert.setContentText("Name can only contain letters and spaces.");
            alert.showAndWait();
            return;
        }
        String sql = "INSERT INTO Accounts (Name, Age, Major, Username, Password, Role) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, name);
                stmt.setInt(2, Integer.parseInt(age));
                stmt.setString(3, major);
                stmt.setString(4, username);
                stmt.setString(5, password);
                stmt.setString(6, "Student");

                stmt.executeUpdate();
                System.out.println("Record inserted successfully!");
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Pending Approval");
                alert.setHeaderText("Your signup request is being processed");
                alert.setContentText("Please wait until an admin approves your account.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Username Error");
                alert.setHeaderText("Username already exists.");
                alert.setContentText("Please choose a different username.");
                alert.showAndWait();
            } else {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Database Error");
                alert.setHeaderText("An error occurred while connecting to the database.");
                alert.setContentText("Please try again later.");
                alert.showAndWait();
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid age input. Please enter a number.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setHeaderText("Invalid age input.");
            alert.setContentText("Please enter a valid number for age.");
            alert.showAndWait();
        }
    }

    @FXML
    public void cancel(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
