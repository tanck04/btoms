package enums;

/**
 * Represents the possible application statuses for BTO applicants.
 * This enum defines the different states an applicant's application can be in
 * throughout the BTO application process.
 */
public enum ApplicantAppStatus {
    /**
     * Application has been submitted but not yet processed.
     */
    PENDING,

    /**
     * Application was processed but did not meet selection criteria.
     */
    UNSUCCESSFUL,

    /**
     * Application was selected in the ballot but unit selection is not finalized.
     */
    SUCCESSFUL,

    /**
     * Unit has been selected and officially booked by the applicant.
     */
    BOOKED,
}