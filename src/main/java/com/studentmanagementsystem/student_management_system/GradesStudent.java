package com.studentmanagementsystem.student_management_system;

public class GradesStudent {
    private int credits;
    private String grade;
    private String courseName;
    public GradesStudent(String courseName, int credits, String grade) {
        this.credits = credits;
        this.grade = grade;
        this.courseName = courseName;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}
