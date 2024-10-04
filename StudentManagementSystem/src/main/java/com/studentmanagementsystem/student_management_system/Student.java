package com.studentmanagementsystem.student_management_system;
public class Student {
    private final int id;
    private final String name;
    private final String username;
    private final String major;
    private final int age;

    public Student(int id, String name, String username, String major, int age) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.major = major;
        this.age = age;
    }

    // Getters for each field
    public int getId() { return id; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getMajor() { return major; }
    public int getAge() { return age; }
}
