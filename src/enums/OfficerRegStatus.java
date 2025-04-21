package enums;

/**
 * Represents the possible registration statuses for housing officers in the BTO system.
 * This enum defines the different states an officer's registration application
 * can be in during the approval process.
 */
public enum OfficerRegStatus {
    /**
     * Registration has been submitted but not yet reviewed by an administrator.
     */
    PENDING,

    /**
     * Registration has been reviewed and approved by HDB Manager.
     * The officer can now access the intended project.
     */
    APPROVED,

    /**
     * Registration has been reviewed and rejected by an HDB Manager.
     * The officer cannot access the intended project.
     */
    REJECTED,
}