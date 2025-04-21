package enums;

/**
 * Represents the user roles within the BTO application system.
 * This enum defines the different types of users and their access levels
 * within the application.
 */
public enum Role {
    /**
     * Represents an applicant who can apply for BTO flats.
     */
    APPLICANT,

    /**
     * Represents an HDB officer who can process applications and respond to enquiries.
     */
    HDBOFFICER,

    /**
     * Represents an HDB manager with administrative privileges to manage officers
     * and oversee the BTO application system.
     */
    HDBMANAGER,
}