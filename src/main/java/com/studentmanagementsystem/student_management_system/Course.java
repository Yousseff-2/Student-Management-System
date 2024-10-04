package com.studentmanagementsystem.student_management_system;

public class Course {
    private  int courseId;
    private String courseName;
    private  int credits;

    public Course(int courseId, String courseName, int credits) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.credits = credits;
    }

    public int getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getCreditHours() {
        return credits;
    }
}
