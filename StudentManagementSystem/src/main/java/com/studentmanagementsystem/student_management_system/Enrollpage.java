package com.studentmanagementsystem.student_management_system;

public class Enrollpage {
    private String name;
    private int credits , id;

    Enrollpage(String name , int credits , int id){
        this.name = name;
        this.credits = credits;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
