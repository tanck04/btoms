package model;

import enums.MaritalStatus;

public class HDBManager extends User{
    public HDBManager(String userID, String name, String role, String password, int age, MaritalStatus maritalStatus) {
        super(userID, name, role, password, age, maritalStatus);
    }
}
