package model;

import enums.ApplicantAppStatus;
import enums.FlatType;
import enums.WithdrawalStatus;

/**
 * Represents a BTO housing application in the system.
 * <p>
 * An Application contains information about a housing application including
 * its unique ID, the associated applicant, processing officer, project details,
 * flat type, and current status information. This class serves as the central
 * model for tracking BTO applications throughout their lifecycle.
 * </p>
 */
public class Application {
    /** Unique identifier for this application */
    private String applicationID;

    /** The applicant who submitted this application */
    private Applicant applicant;

    /** The officer assigned to process this application */
    private Officer officer;

    /** The BTO housing project this application is for */
    private final Project project;

    /** The type of flat requested in this application */
    private FlatType flatType;

    /** The current withdrawal status of this application */
    private WithdrawalStatus withdrawalStatus;

    /** The current processing status of this application */
    private ApplicantAppStatus applicationStatus;

    /**
     * Constructs an Application with an applicant as the user.
     * <p>
     * This constructor is used for creating an Application from CSV data or when
     * an applicant submits a new application.
     * </p>
     *
     * @param applicationID    The unique identifier for this application
     * @param applicant        The applicant who submitted this application
     * @param project          The BTO project this application is for
     * @param flatType         The type of flat requested
     * @param applicationStatus The current status of this application
     * @param withdrawalStatus  The current withdrawal status of this application
     */
    public Application(String applicationID, Applicant applicant, Project project, FlatType flatType,
                       ApplicantAppStatus applicationStatus, WithdrawalStatus withdrawalStatus) {
        this.applicationID = applicationID;
        this.applicant = applicant;
        this.project = project;
        this.flatType = flatType;
        this.applicationStatus = applicationStatus;
        this.withdrawalStatus = withdrawalStatus;
    }

    /**
     * Constructs an Application with an officer as the user.
     * <p>
     * This constructor is used when an officer is creating or managing an application.
     * </p>
     *
     * @param applicationID    The unique identifier for this application
     * @param officer          The officer managing this application
     * @param project          The BTO project this application is for
     * @param flatType         The type of flat requested
     * @param applicationStatus The current status of this application
     * @param withdrawalStatus  The current withdrawal status of this application
     */
    public Application(String applicationID, Officer officer, Project project, FlatType flatType,
                       ApplicantAppStatus applicationStatus, WithdrawalStatus withdrawalStatus) {
        this.applicationID = applicationID;
        this.officer = officer;
        this.project = project;
        this.flatType = flatType;
        this.applicationStatus = applicationStatus;
        this.withdrawalStatus = withdrawalStatus;
    }

    /**
     * Gets the unique identifier for this application.
     *
     * @return The application's unique ID
     */
    public String getApplicationID() {
        return applicationID;
    }

    /**
     * Sets the unique identifier for this application.
     *
     * @param applicationID The new application ID to set
     */
    public void setApplicationID(String applicationID) {
        this.applicationID = applicationID;
    }

    /**
     * Gets the applicant who submitted this application.
     *
     * @return The applicant, or null if no applicant is associated
     */
    public Applicant getApplicant() {
        return applicant;
    }

    /**
     * Sets the applicant for this application.
     *
     * @param applicant The applicant to associate with this application
     */
    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    /**
     * Gets the officer assigned to process this application.
     *
     * @return The officer, or null if no officer has been assigned
     */
    public Officer getOfficer() {
        return officer;
    }

    /**
     * Sets the officer assigned to process this application.
     *
     * @param officer The officer to assign to this application
     */
    public void setOfficer(Officer officer) {
        this.officer = officer;
    }

    /**
     * Gets the BTO project this application is for.
     *
     * @return The project associated with this application
     */
    public Project getProject() {
        return project;
    }

    /**
     * Gets the type of flat requested in this application.
     *
     * @return The flat type requested
     */
    public FlatType getFlatType() {
        return flatType;
    }

    /**
     * Sets the type of flat requested in this application.
     *
     * @param flatType The new flat type to set
     */
    public void setFlatType(FlatType flatType) {
        this.flatType = flatType;
    }

    /**
     * Gets the current withdrawal status of this application.
     *
     * @return The current withdrawal status
     */
    public WithdrawalStatus getWithdrawalStatus() {
        return withdrawalStatus;
    }

    /**
     * Sets the withdrawal status of this application.
     *
     * @param withdrawalStatus The new withdrawal status to set
     */
    public void setWithdrawalStatus(WithdrawalStatus withdrawalStatus) {
        this.withdrawalStatus = withdrawalStatus;
    }

    /**
     * Gets the current processing status of this application.
     *
     * @return The current application status
     */
    public ApplicantAppStatus getApplicationStatus() {
        return applicationStatus;
    }

    /**
     * Sets the processing status of this application.
     *
     * @param applicationStatus The new application status to set
     */
    public void setApplicationStatus(ApplicantAppStatus applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    /**
     * Gets the primary user associated with this application.
     * <p>
     * This method returns either the applicant or officer associated with this application,
     * giving priority to the applicant if both are present.
     * </p>
     *
     * @return The associated user (applicant or officer), or null if neither is set
     */
    public User getUser() {
        if (applicant != null) {
            return applicant;
        } else if (officer != null) {
            return officer;
        }
        return null;
    }
}