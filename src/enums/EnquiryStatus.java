package enums;

/**
 * Represents the possible statuses for enquiries in the BTO system.
 * This enum defines the different states an enquiry can be in
 * during its lifecycle in the system.
 */
public enum EnquiryStatus {
    /**
     * Enquiry has been submitted but has not yet been addressed.
     */
    PENDING,

    /**
     * Enquiry has been addressed and a resolution has been provided.
     */
    RESOLVED,
}