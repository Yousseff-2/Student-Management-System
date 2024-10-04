package com.studentmanagementsystem.student_management_system;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.sql.*;

public class HelloController {

    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private CheckBox admin;
    @FXML
    private CheckBox student;

    @FXML
    private Button login;
    private static final String URL = "jdbc:mysql://localhost:3306/system";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    @FXML
    private void login(ActionEvent event) {
        String user = username.getText();
        String pass = password.getText();
        boolean adminCheck = admin.isSelected();
        boolean studentCheck = student.isSelected();

        if (adminCheck && studentCheck) {
            JOptionPane.showMessageDialog(null, "You cannot select both Admin and Student roles.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (studentCheck) {
            authenticateUser(user, pass, "Student");
        } else if (adminCheck) {
            authenticateUser(user, pass, "Admin");
        } else {
            JOptionPane.showMessageDialog(null, "Please select either Admin or Student role.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void authenticateUser(String user, String pass, String role) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Accounts WHERE Username = ? AND Password = ? AND Role = ?")) {

            stmt.setString(1, user);
            stmt.setString(2, pass);
            stmt.setString(3, role);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    if (rs.getBoolean("IsActive")) {
                        if (role.equals(rs.getString("Role")) && role.equals("Student")) {
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXML/student-view.fxml"));
                            Parent root = fxmlLoader.load();
                            StudentDashboardController controller = fxmlLoader.getController();
                            controller.setStudentInfo(rs.getString("Name"), rs.getString("Username"), rs.getString("Major"));
                            Stage stage = (Stage) login.getScene().getWindow();
                            stage.setScene(new Scene(root));
                            stage.show();
                        }
                        else if (role.equals(rs.getString("Role")) && role.equals("Admin")) {
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXML/admin-view.fxml"));
                            Parent root = fxmlLoader.load();
                            AdminDashboardController controller = fxmlLoader.getController();
                            controller.setAdminInfo(rs.getString("Name"), rs.getString("Username"));
                            Stage stage = (Stage) login.getScene().getWindow();
                            stage.setScene(new Scene(root));
                            stage.show();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Your account is still inactive.", "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid username or password", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }


    public void signup(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("FXML/sign-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Student Management System");
        stage.show();
    }
}
