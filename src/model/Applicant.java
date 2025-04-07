package model;

import enums.MaritalStatus;

public class Applicant extends User{
    public Applicant(String userID, String name, String role, String password, int age, MaritalStatus maritalStatus) {
        super(userID, name, role, password, age, maritalStatus);
    }
}
