package model;

import enums.MaritalStatus;import enums.ApplicantAppStatus;
import enums.WithdrawalStatus;
import enums.Role;

public class Applicant extends User {
    private final Role role = Role.APPLICANT;


    // Constructor for creating an Applicant initially without applicationID and enquiryID
    public Applicant(String nric,
                     String name,
                     String password,
                     int age,
                     MaritalStatus maritalStatus) {
        super(nric, name, Role.APPLICANT, password, maritalStatus, age);
    }

    // Constructor for creating an Applicant who is also an Officer
    public Applicant(String nric,
                     String name,
                     Role role,
                     String password,
                     MaritalStatus maritalStatus,
                     int age) {
        super(nric, name, role, password, maritalStatus, age);
    }
    public Role getRole() {
        return role;
    }
}

