package model;

import enums.MaritalStatus;
import enums.Role;
import java.io.Serial;
import java.io.Serializable;

/**
 * Abstract base class representing a user in the BTO housing system.
 * <p>
 * The User class serves as the foundation for all user types in the system,
 * providing common attributes and behaviors. It implements Serializable to
 * support object persistence. This class cannot be instantiated directly
 * and must be extended by concrete user classes.
 * </p>
 */
public abstract class User implements Serializable {
    /** Serial version UID for serialization compatibility */
    @Serial
    private static final long serialVersionUID = 1L;

    /** National Registration Identity Card number, serves as unique identifier */
    protected String nric;

    /** Full name of the user */
    protected String name;

    /** Role of the user in the system (e.g., APPLICANT, HDBOFFICER, HDBMANAGER) */
    protected Role role;

    /** Marital status of the user */
    protected MaritalStatus maritalStatus;

    /** Password for account authentication */
    protected String password;

    /** Age of the user in years */
    protected int age;

    /**
     * Constructs a new User with the specified attributes.
     *
     * @param nric          The National Registration Identity Card number
     * @param name          The full name of the user
     * @param role          The role of the user in the system
     * @param password      The password for account authentication
     * @param maritalStatus The marital status of the user
     * @param age           The age of the user in years
     */
    public User(String nric, String name, Role role, String password, MaritalStatus maritalStatus, int age) {
        this.nric = nric;
        this.name = name;
        this.role = role;
        this.password = password;
        this.maritalStatus = maritalStatus;
        this.age = age;
    }

    /**
     * Gets the National Registration Identity Card number.
     *
     * @return The NRIC number
     */
    public String getNRIC() {
        return nric;
    }

    /**
     * Gets the full name of the user.
     *
     * @return The user's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the role of the user in the system.
     *
     * @return The user's role
     */
    public Role getRole() {
        return role;
    }

    /**
     * Gets the password for account authentication.
     *
     * @return The user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the age of the user in years.
     *
     * @return The user's age
     */
    public int getAge() {
        return age;
    }

    /**
     * Gets the marital status of the user.
     *
     * @return The user's marital status
     */
    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    /**
     * Sets the National Registration Identity Card number.
     *
     * @param nric The new NRIC number to set
     */
    public void setNRIC(String nric) {
        this.nric = nric;
    }

    /**
     * Sets the full name of the user.
     *
     * @param name The new name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the role of the user in the system.
     *
     * @param role The new role to set
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Sets the password for account authentication.
     *
     * @param password The new password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Sets the age of the user in years.
     *
     * @param age The new age to set
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Sets the marital status of the user.
     *
     * @param maritalStatus The new marital status to set
     */
    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }
}