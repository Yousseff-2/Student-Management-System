package com.studentmanagementsystem.student_management_system;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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

public class InActiveStudentAdminController {
    public TableView<Student> studentTableView;
    public TableColumn<Student, Integer> idColumn;
    public TableColumn<Student, String> nameColumn;
    public TableColumn<Student, String> usernameColumn;
    public TableColumn<Student, String> majorColumn;
    public Label userNameLabel;
    public Label nameLabel;
    public Label welcomeLabel;
    public Button student;
    public Button inactiveStudent;
    public Button course;
    public Button enroll;
    public Button grade;
    public TextField searchField;

    private ObservableList<Student> studentList = FXCollections.observableArrayList();
    private String Name, Username;
    private static final String URL = "jdbc:mysql://localhost:3306/system";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public void setAdminInfo(String name, String username) {
        this.Name = name;
        this.Username = username;
        nameLabel.setText("Name: " + name);
        userNameLabel.setText("Username: " + username);
        welcomeLabel.setText("Welcome " + name + "!");
        loadStudent();
    }

    private void loadStudent() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT * FROM Accounts WHERE Role = 'Student' AND IsActive = 0";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                List<Student> students = new ArrayList<>();
                while (rs.next()) {
                    int id = rs.getInt("ID");
                    String name = rs.getString("Name");
                    String username = rs.getString("Username");
                    String major = rs.getString("Major");
                    int age = rs.getInt("Age");
                    students.add(new Student(id, name, username, major , age));
                }

                ObservableList<Student> observableStudents = FXCollections.observableArrayList(students);
                studentTableView.setItems(observableStudents);
            }
        } catch (SQLException e) {
            showErrorDialog("Error loading students: " + e.getMessage());
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

    public void editStudent(ActionEvent event) {
        Student selectedStudent = (Student) studentTableView.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            TextField nameField = new TextField(selectedStudent.getName());
            TextField usernameField = new TextField(selectedStudent.getUsername());
            TextField majorField = new TextField(selectedStudent.getMajor());
            TextField ageField = new TextField(String.valueOf(selectedStudent.getAge()));
            String[] isActiveOptions = {"Active", "Inactive"};
            ComboBox<String> isActiveBox = new ComboBox<>(FXCollections.observableArrayList(isActiveOptions));
            isActiveBox.setValue("Active");

            VBox dialogPane = new VBox(10);
            dialogPane.getChildren().addAll(
                    new Label("Name:"), nameField,
                    new Label("Username:"), usernameField,
                    new Label("Major:"), majorField,
                    new Label("Age:"), ageField,
                    new Label("Status:"), isActiveBox
            );

            ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Edit Student");
            dialog.getDialogPane().setContent(dialogPane);
            dialog.getDialogPane().getButtonTypes().addAll(okButton, cancelButton);

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == okButton) {
                String newName = nameField.getText().trim();
                String newUsername = usernameField.getText().trim();
                String newMajor = majorField.getText().trim();
                String newAge = ageField.getText().trim();
                int isActive = isActiveBox.getValue().equals("Active") ? 1 : 0;

                if (!newName.matches("[a-zA-Z ]+")) {
                    showErrorDialog("Name must contain only letters!");
                } else if (newName.isEmpty() || newUsername.isEmpty() || newMajor.isEmpty() || newAge.isEmpty()) {
                    showErrorDialog("All fields must be filled in!");
                } else if (!newMajor.equals("CS") && !newMajor.equals("IS") && !newMajor.equals("IT") && !newMajor.equals("General")) {
                    showErrorDialog("Major must be one of the following: CS, IS, IT, General");
                } else {
                    try {
                        int age = Integer.parseInt(newAge);

                        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                            String checkUsernameSql = "SELECT COUNT(*) FROM Accounts WHERE Username = ? AND ID != ?";
                            try (PreparedStatement checkUsernameStmt = conn.prepareStatement(checkUsernameSql)) {
                                checkUsernameStmt.setString(1, newUsername);
                                checkUsernameStmt.setInt(2, selectedStudent.getId());
                                ResultSet rs = checkUsernameStmt.executeQuery();
                                rs.next();
                                int count = rs.getInt(1);

                                if (count > 0) {
                                    showErrorDialog("Username is already taken!");
                                } else {
                                    String sql = "UPDATE Accounts SET Name = ?, Username = ?, Major = ?, Age = ?, IsActive = ? WHERE ID = ?";
                                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                                        ps.setString(1, newName);
                                        ps.setString(2, newUsername);
                                        ps.setString(3, newMajor);
                                        ps.setInt(4, age);
                                        ps.setInt(5, isActive);
                                        ps.setInt(6, selectedStudent.getId());

                                        ps.executeUpdate();
                                        loadStudent();
                                    }
                                }
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                    } catch (NumberFormatException e) {
                        showErrorDialog("Age must be a valid number!");
                    }
                }
            }
        } else {
            showErrorDialog("Please select a student to edit.");
        }
    }

    public void deleteStudent(ActionEvent event) {
        Student selectedStudent = (Student) studentTableView.getSelectionModel().getSelectedItem();

        if (selectedStudent != null) {
            String studentName = selectedStudent.getName();
            int studentId = selectedStudent.getId();

            int option = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to delete student " + studentName + "?",
                    "Delete Confirmation",
                    JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                    String sql = "DELETE FROM Accounts WHERE ID = ?";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setInt(1, studentId);

                        int rowsAffected = ps.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("Student deleted successfully.");

                            loadStudent();
                        } else {
                            System.out.println("No student was deleted.");
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select a student to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }



    public void addStudent(ActionEvent event) {
        JTextField nameField = new JTextField();
        JTextField usernameField = new JTextField();
        JTextField ageField = new JTextField();

        JCheckBox csCheckBox = new JCheckBox("CS");
        JCheckBox isCheckBox = new JCheckBox("IS");
        JCheckBox itCheckBox = new JCheckBox("IT");
        JCheckBox generalCheckBox = new JCheckBox("General");

        ButtonGroup majorGroup = new ButtonGroup();
        majorGroup.add(csCheckBox);
        majorGroup.add(isCheckBox);
        majorGroup.add(itCheckBox);
        majorGroup.add(generalCheckBox);

        Object[] fields = {
                "Name:", nameField,
                "Username:", usernameField,
                "Age:", ageField,
                "Major:", csCheckBox, isCheckBox, itCheckBox, generalCheckBox
        };

        int option = JOptionPane.showConfirmDialog(null, fields, "Add New Student", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String username = usernameField.getText().trim();
            String ageStr = ageField.getText().trim();

            String major = "";
            if (csCheckBox.isSelected()) {
                major = "CS";
            } else if (isCheckBox.isSelected()) {
                major = "IS";
            } else if (itCheckBox.isSelected()) {
                major = "IT";
            } else if (generalCheckBox.isSelected()) {
                major = "General";
            }

            if (name.isEmpty() || username.isEmpty() || major.isEmpty() || ageStr.isEmpty()) {
                showErrorDialog("All fields must be filled in!");
            } else if (!name.matches("[a-zA-Z ]+")) {
                showErrorDialog("Name must contain only letters!");
            } else {
                try {
                    int age = Integer.parseInt(ageStr);
                    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                        String checkUsernameSql = "SELECT COUNT(*) FROM Accounts WHERE Username = ?";
                        try (PreparedStatement checkUsernameStmt = conn.prepareStatement(checkUsernameSql)) {
                            checkUsernameStmt.setString(1, username);
                            ResultSet rs = checkUsernameStmt.executeQuery();
                            rs.next();
                            int count = rs.getInt(1);

                            if (count > 0) {
                                showErrorDialog("Username is already taken!");
                            } else {
                                String sql = "INSERT INTO Accounts (Name, Username, Major, Age, Role, IsActive, Password) VALUES (?, ?, ?, ?, 'Student', 0, ?)";
                                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                                    ps.setString(1, name);
                                    ps.setString(2, username);
                                    ps.setString(3, major);
                                    ps.setInt(4, age);
                                    ps.setString(5, "12345678");
                                    ps.executeUpdate();
                                    System.out.println("New student added successfully.");

                                    loadStudent();
                                }
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                } catch (NumberFormatException e) {
                    showErrorDialog("Age must be a valid number!");
                }
            }
        }
    }


    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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

    public void searchStudent(MouseEvent keyEvent) {
        String searchText = searchField.getText();
        studentList.clear();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT * FROM Accounts WHERE Role = 'Student' AND IsActive = 0";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                ObservableList<Student> studentList = FXCollections.observableArrayList();
                studentList.clear();
                while (rs.next()) {
                    int id = rs.getInt("ID");
                    String name = rs.getString("Name");
                    String username = rs.getString("Username");
                    String major = rs.getString("Major");
                    int age = rs.getInt("Age");
                    if(name.contains(searchText))studentList.add(new Student(id, name, username, major, age));
                }
                studentTableView.setItems(studentList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
