package model;

/**
 * Represents an enquiry in the BTO housing system.
 * <p>
 * An Enquiry stores information about questions or concerns submitted by applicants
 * regarding specific housing projects. It tracks the enquiry content, status, associated
 * applicant, project, and officer response details.
 * </p>
 */
public class Enquiry {
    /** Unique identifier for this enquiry */
    private String enquiryID;

    /** The ID of the applicant who submitted this enquiry */
    private String applicantID;

    /** The ID of the project this enquiry relates to */
    private String projectID;

    /** The text content of the enquiry submitted by the applicant */
    private String enquiryText;

    /** The reply text provided by an officer (null if not yet replied) */
    private String enquiryReply;

    /** The current status of this enquiry (e.g., "Pending", "Replied", "Closed") */
    private String enquiryStatus;

    /** The ID of the officer who replied to this enquiry (null if not yet replied) */
    private String replyingOfficerID;

    /**
     * Constructs a new Enquiry with all required fields.
     *
     * @param enquiryID         The unique identifier for this enquiry
     * @param applicantID       The ID of the applicant who submitted this enquiry
     * @param projectID         The ID of the project this enquiry relates to
     * @param enquiryText       The text content of the enquiry
     * @param enquiryReply      The reply text (can be null or empty if not yet replied)
     * @param enquiryStatus     The current status of this enquiry
     * @param replyingOfficerID The ID of the officer who replied (can be null if not yet replied)
     */
    public Enquiry(String enquiryID, String applicantID, String projectID, String enquiryText, String enquiryReply, String enquiryStatus, String replyingOfficerID) {
        this.enquiryID = enquiryID;
        this.applicantID = applicantID;
        this.projectID = projectID;
        this.enquiryText = enquiryText;
        this.enquiryReply = enquiryReply;
        this.enquiryStatus = enquiryStatus;
        this.replyingOfficerID = replyingOfficerID;
    }

    /**
     * Gets the unique identifier for this enquiry.
     *
     * @return The enquiry's unique ID
     */
    public String getEnquiryID() {
        return enquiryID;
    }

    /**
     * Sets the unique identifier for this enquiry.
     *
     * @param enquiryID The new enquiry ID to set
     */
    public void setEnquiryID(String enquiryID) {
        this.enquiryID = enquiryID;
    }

    /**
     * Gets the ID of the applicant who submitted this enquiry.
     *
     * @return The applicant's ID
     */
    public String getApplicantID() {
        return applicantID;
    }

    /**
     * Sets the ID of the applicant who submitted this enquiry.
     *
     * @param applicantID The new applicant ID to set
     */
    public void setApplicantID(String applicantID) {
        this.applicantID = applicantID;
    }

    /**
     * Gets the ID of the project this enquiry relates to.
     *
     * @return The project's ID
     */
    public String getProjectID() {
        return projectID;
    }

    /**
     * Sets the ID of the project this enquiry relates to.
     *
     * @param projectID The new project ID to set
     */
    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    /**
     * Gets the text content of the enquiry submitted by the applicant.
     *
     * @return The enquiry text
     */
    public String getEnquiryText() {
        return enquiryText;
    }

    /**
     * Sets the text content of the enquiry.
     *
     * @param enquiryText The new enquiry text to set
     */
    public void setEnquiryText(String enquiryText) {
        this.enquiryText = enquiryText;
    }

    /**
     * Gets the reply text provided by an officer.
     *
     * @return The reply text, or null if not yet replied
     */
    public String getEnquiryReply() {
        return enquiryReply;
    }

    /**
     * Sets the reply text for this enquiry.
     *
     * @param enquiryReply The new reply text to set
     */
    public void setEnquiryReply(String enquiryReply) {
        this.enquiryReply = enquiryReply;
    }

    /**
     * Gets the current status of this enquiry.
     *
     * @return The enquiry status (e.g., "Pending", "Replied", "Closed")
     */
    public String getEnquiryStatus() {
        return enquiryStatus;
    }

    /**
     * Sets the status of this enquiry.
     *
     * @param enquiryStatus The new status to set
     */
    public void setEnquiryStatus(String enquiryStatus) {
        this.enquiryStatus = enquiryStatus;
    }

    /**
     * Gets the ID of the officer who replied to this enquiry.
     *
     * @return The replying officer's ID, or null if not yet replied
     */
    public String getReplyingOfficerID() {
        return replyingOfficerID;
    }

    /**
     * Sets the ID of the officer who replied to this enquiry.
     *
     * @param replyingOfficerID The new replying officer ID to set
     */
    public void setReplyingOfficerID(String replyingOfficerID) {
        this.replyingOfficerID = replyingOfficerID;
    }
}