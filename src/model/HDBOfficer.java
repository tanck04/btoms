package model;

import enums.MaritalStatus;
import enums.Role;

public class HDBOfficer extends User{
    public HDBOfficer(String userID, String name, Role role, String password, int age, MaritalStatus maritalStatus) {
        super(userID, name, Role.HDBOFFICER, password, maritalStatus, age);
    }
}
