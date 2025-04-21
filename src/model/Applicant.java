package model;

import enums.MaritalStatus;
import enums.ApplicantAppStatus;
import enums.WithdrawalStatus;
import enums.Role;

/**
 * Represents an applicant in the BTO housing system.
 * <p>
 * An Applicant is a specialized type of User who can apply for BTO housing.
 * This class extends the User class and includes applicant-specific attributes
 * and behaviors. All Applicants have the APPLICANT role by default, though they
 * can potentially have dual roles.
 * </p>
 */
public class Applicant extends User {
    /** The role of this user, always set to APPLICANT for this class */
    private final Role role = Role.APPLICANT;

    /**
     * Constructs a new Applicant with basic information.
     * <p>
     * This constructor is used for creating standard applicants who do not
     * have any additional roles in the system.
     * </p>
     *
     * @param nric          The National Registration Identity Card number of the applicant
     * @param name          The full name of the applicant
     * @param password      The password for the applicant's account
     * @param age           The age of the applicant
     * @param maritalStatus The marital status of the applicant
     */
    public Applicant(String nric,
                     String name,
                     String password,
                     int age,
                     MaritalStatus maritalStatus) {
        super(nric, name, Role.APPLICANT, password, maritalStatus, age);
    }

    /**
     * Constructs an Applicant who also has another role in the system.
     * <p>
     * This constructor allows for creating users who are both applicants
     * and officers or other roles in the BTO system.
     * </p>
     *
     * @param nric          The National Registration Identity Card number of the applicant
     * @param name          The full name of the applicant
     * @param role          The additional role of the applicant
     * @param password      The password for the applicant's account
     * @param maritalStatus The marital status of the applicant
     * @param age           The age of the applicant
     */
    public Applicant(String nric,
                     String name,
                     Role role,
                     String password,
                     MaritalStatus maritalStatus,
                     int age) {
        super(nric, name, role, password, maritalStatus, age);
    }

    /**
     * Gets the role of this applicant.
     *
     * @return The role, which is always APPLICANT for this class
     */
    public Role getRole() {
        return role;
    }
}