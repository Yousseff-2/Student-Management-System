package com.studentmanagementsystem.student_management_system;

import java.util.Date;

public class Enrollment {
    private String studentName;
    private String courseName;
    private int StudentCourse_ID;
    public Enrollment(String studentName, String courseName , int StudentCourse_ID) {
        this.studentName = studentName;
        this.courseName = courseName;
        this.StudentCourse_ID = StudentCourse_ID;
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

    public int getStudentCourse_ID() {
        return StudentCourse_ID;
    }

    public void setStudentCourse_ID(int studentCourse_ID) {
        StudentCourse_ID = studentCourse_ID;
    }
}
