package model;


import enums.MaritalStatus;
import enums.Role;
import java.io.Serial;
import java.io.Serializable;


public abstract class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;  // Optional but recommended for Serializable classes
    protected String nric;
    protected String name;
    protected Role role;
    protected MaritalStatus maritalStatus;
    protected String password;
    protected int age;

    public User(String nric, String name, Role role, String password, MaritalStatus maritalStatus, int age) {
        this.nric = nric;
        this.name = name;
        this.role = role;
        this.password = password;
        this.maritalStatus = maritalStatus;
        this.age = age;
    }

    // Getters
    public String getNRIC() {
        return nric;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
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
    public void setNRIC(String nric) {
        this.nric = nric;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(Role role) {
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
