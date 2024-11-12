# Student Management System

## Overview
The **Student Management System** is a Java-based desktop application designed to simplify university management tasks. It allows students to manage their courses and grades, while administrators can manage student accounts, courses, and grades. The system has separate dashboards for students and administrators, each with its respective features.

## Features
### Login Page
- Users can log in using their credentials.
- Students can create a new account, but their account will remain inactive until an admin approves it.
- Students and admins choose their role during login (either "Student" or "Admin").
  
### Student Dashboard
- **Course Management**: Students can view their enrolled courses.
- **Grades**: Students can view their grades for completed courses.
- **Course Enrollment**: Students can register for new courses if they are eligible.

### Admin Dashboard
- **Student Management**: Admins can view all students, search by name, and perform actions such as adding, removing, or activating/deactivating student accounts.
- **Course Management**: Admins can add, delete, and view courses.
- **Grade Management**: Admins can assign grades to students and manage course enrollments.
- **Student Course Enrollment**: Admins can view the courses that students are enrolled in.
  
## Technologies Used
- **Java**: Main programming language.
- **JavaFX**: For building the graphical user interface (GUI).
- **JDBC**: For managing database interactions.
- **CSS**: For styling JavaFX components.
- **FXML**: For defining the UI structure.
- **Maven**: For dependency and build management.

## Prerequisites
- **Java Development Kit (JDK)** 11 or higher (Java 21 is configured in the project).
- **JavaFX SDK** (if not included with JDK).
- **Database**: MySQL.
 
## Dependencies
Maven handles required dependencies. Key dependencies are:
- **JavaFX**: For UI elements like `javafx-controls` and `javafx-fxml`.
- **JDBC Driver**: For database connectivity (MySQL Connector).

Dependencies are listed in `pom.xml`.

## Database Schema

The system uses MySQL for database management. The schema for the system is as follows:

### SQL Script for Database Setup:
```sql
DROP DATABASE IF EXISTS system;

CREATE DATABASE system;

USE system;

-- Create Accounts table for students and admins
CREATE TABLE Accounts (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(255) NOT NULL,
    Age INT NOT NULL,
    Major Enum('CS', 'IT', 'IS', 'Admin', 'General') NOT NULL,
    Username VARCHAR(255) NOT NULL UNIQUE,
    Password VARCHAR(255) NOT NULL,
    Created_At TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    Role ENUM('Student', 'Admin') NOT NULL,
    IsActive BOOLEAN DEFAULT FALSE
);

-- Create Courses table
CREATE TABLE Courses (
    Course_ID INT AUTO_INCREMENT PRIMARY KEY,
    Course_Name VARCHAR(255) NOT NULL,
    Credit_Hours INT NOT NULL
);

-- Create StudentCourses table for course enrollments and grades
CREATE TABLE StudentCourses (
    StudentCourse_ID INT AUTO_INCREMENT PRIMARY KEY,
    Student_ID INT NOT NULL,
    Course_ID INT NOT NULL,
    Grade VARCHAR(10),
    FOREIGN KEY (Student_ID) REFERENCES Accounts(ID),
    FOREIGN KEY (Course_ID) REFERENCES Courses(Course_ID)
);
