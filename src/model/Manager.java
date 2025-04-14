package model;

import enums.MaritalStatus;
import enums.Role;

public class Manager extends User {
    private final Role role = Role.HDBMANAGER;
    private Project projectInCharge;
    public Manager(String nric,
                   String name,
                   String password,
                   MaritalStatus maritalStatus,
                   int age) {
        super(nric, name, Role.HDBMANAGER, password, maritalStatus, age); // Fixed role to HDBMANAGER
    }

    // Getter for role
    public Role getRole() {
        return role;
    }

}
