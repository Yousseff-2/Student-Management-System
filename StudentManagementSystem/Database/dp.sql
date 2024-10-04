DROP DATABASE IF EXISTS system;

CREATE DATABASE system;

USE system;
CREATE TABLE Accounts (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(255) NOT NULL,
    Age INT NOT NULL,
    Major Enum('CS' , 'IT' , 'IS' , 'Admin' , 'General') NOT NULL,
    Username VARCHAR(255) NOT NULL UNIQUE,
    Password VARCHAR(255) NOT NULL,
    Created_At TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    Role ENUM('Student', 'Admin') NOT NULL,
    IsActive BOOLEAN DEFAULT FALSE
);
CREATE TABLE Courses (
    Course_ID INT AUTO_INCREMENT PRIMARY KEY,
    Course_Name VARCHAR(255) NOT NULL,
    Credit_Hours INT NOT NULL
);

CREATE TABLE StudentCourses (
      StudentCourse_ID INT AUTO_INCREMENT PRIMARY KEY,
      Student_ID INT NOT NULL,
      Course_ID INT NOT NULL,
      Grade VARCHAR(10),
      FOREIGN KEY (Student_ID) REFERENCES Accounts(ID),
      FOREIGN KEY (Course_ID) REFERENCES Courses(Course_ID)
);
