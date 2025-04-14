package model;

import enums.MaritalStatus;
import enums.Role;

public class HDBOfficer extends User{
    public HDBOfficer(String nric, String name, String password, int age, MaritalStatus maritalStatus) {
        super(nric, name, Role.HDBOFFICER, password, maritalStatus, age);
    }
}
