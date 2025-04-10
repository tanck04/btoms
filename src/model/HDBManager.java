package model;

import enums.MaritalStatus;
import enums.Role;
public class HDBManager extends User{
    public HDBManager(String userID, String name, Role role, String password, int age, MaritalStatus maritalStatus) {
        super(userID, name, Role.HDBMANAGER, password, age, maritalStatus);
    }
}
