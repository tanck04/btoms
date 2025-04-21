package model;

import enums.MaritalStatus;
import enums.Role;

/**
 * Represents a manager in the BTO housing system.
 * <p>
 * A Manager is a specialized type of User who oversees BTO housing projects.
 * This class extends the User class and includes manager-specific attributes
 * and behaviors. All Managers have the HDBMANAGER role by default.
 * </p>
 */
public class Manager extends User {
    /** The role of this user, always set to HDBMANAGER for this class */
    private final Role role = Role.HDBMANAGER;

    /** The project this manager is currently in charge of */
    private Project projectInCharge;

    /**
     * Constructs a new Manager with basic information.
     * <p>
     * This constructor creates a manager with the HDBMANAGER role
     * and initializes the basic user attributes.
     * </p>
     *
     * @param nric          The National Registration Identity Card number of the manager
     * @param name          The full name of the manager
     * @param password      The password for the manager's account
     * @param maritalStatus The marital status of the manager
     * @param age           The age of the manager
     */
    public Manager(String nric,
                   String name,
                   String password,
                   MaritalStatus maritalStatus,
                   int age) {
        super(nric, name, Role.HDBMANAGER, password, maritalStatus, age); // Fixed role to HDBMANAGER
    }

    /**
     * Gets the role of this manager.
     *
     * @return The role, which is always HDBMANAGER for this class
     */
    public Role getRole() {
        return role;
    }
}