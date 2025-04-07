package model;


import enums.MaritalStatus;

import java.io.Serial;
import java.io.Serializable;


public abstract class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;  // Optional but recommended for Serializable classes
    protected String userID;
    protected String name;
    protected String role;
    protected String password;
    protected int age;
    protected MaritalStatus maritalStatus;

    public User(String userID, String name, String role, String password, int age, MaritalStatus maritalStatus) {
        this.userID = userID;
        this.name = name;
        this.role = role;
        this.password = password;
        this.age = age;
        this.maritalStatus = maritalStatus;
    }

    // Getters
    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getPassword() {
        return password;
    }

    public int getAge() {
        return age;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    // Setters
    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }
}
