package model;

import enums.MaritalStatus;
import enums.Role;

public class HDBManager extends User {
    private final Role role = Role.HDBMANAGER;
    private Project projectInCharge;
    public HDBManager(String nric,
                     String name,
                     String password,
                     MaritalStatus maritalStatus,
                     int age,
                     Project project) {
        super(nric, name, Role.HDBMANAGER, password, maritalStatus, age); // Fixed role to HDBMANAGER
        this.projectInCharge = project;
    }

    // Getter for role
    public Role getRole() {
        return role;
    }

    // Getter for projectInCharge
    public Project getProjectInCharge() {
        return projectInCharge;
    }

    // Setter for projectInCharge
    public void setProjectInCharge(Project projectInCharge) {
        this.projectInCharge = projectInCharge;
    }

    public Project getProject() {
        return projectInCharge;
    }
}
