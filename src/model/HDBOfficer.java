package model;

import enums.MaritalStatus;

public class HDBOfficer extends User{
    public HDBOfficer(String userID, String name, String role, String password, int age, MaritalStatus maritalStatus) {
        super(userID, name, role, password, age, maritalStatus);
    }
}
