package com.studentmanagementsystem.student_management_system;

public class Grade {
    private String studentName;
    private String courseName;
    private String grade;
    private int id;

    public Grade(String studentName, String courseName, String grade , int id) {
        this.studentName = studentName;
        this.courseName = courseName;
        this.grade = grade;
        this.id = id;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
