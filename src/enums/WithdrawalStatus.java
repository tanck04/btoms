package enums;

/**
 * Represents the status of withdrawal applications in the BTO management system.
 * This enum defines the different states a withdrawal request can be in
 * during the processing workflow.
 */
public enum WithdrawalStatus {
    /**
     * Indicates that no withdrawal application has been submitted.
     * This is the default state for applications that have not requested withdrawal.
     */
    NULL,

    /**
     * Indicates that a withdrawal request has been submitted but not yet reviewed.
     * The application is awaiting manager evaluation.
     */
    PENDING,

    /**
     * Indicates that a withdrawal request has been reviewed and approved.
     * The application is officially withdrawn from the BTO process.
     */
    APPROVED,

    /**
     * Indicates that a withdrawal request has been reviewed and rejected.
     * The application remains active in the BTO process.
     */
    REJECTED,
}