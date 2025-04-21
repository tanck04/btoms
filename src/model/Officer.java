package model;

import enums.MaritalStatus;
import enums.Role;

/**
 * Represents an HDB officer in the BTO housing system.
 * <p>
 * An Officer is a specialized type of Applicant who processes BTO housing applications.
 * This class extends the Applicant class and inherits its functionality while
 * maintaining the HDBOFFICER role. Officers can both apply for housing (as Applicants)
 * and process applications from other users.
 * </p>
 */
public class Officer extends Applicant {
    /**
     * Constructs a new Officer with basic information.
     * <p>
     * This constructor creates an officer with the HDBOFFICER role
     * and initializes the basic user attributes. Since Officer extends
     * Applicant, officers have dual functionality in the system.
     * </p>
     *
     * @param nric          The National Registration Identity Card number of the officer
     * @param name          The full name of the officer
     * @param password      The password for the officer's account
     * @param age           The age of the officer
     * @param maritalStatus The marital status of the officer
     */
    public Officer(String nric, String name, String password, int age, MaritalStatus maritalStatus) {
        super(nric, name, Role.HDBOFFICER, password, maritalStatus, age);
    }
}