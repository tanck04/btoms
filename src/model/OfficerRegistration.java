package model;

import enums.OfficerRegStatus;
import enums.WithdrawalStatus;

/**
 * Represents an officer registration request for a BTO housing project.
 * <p>
 * This class handles the assignment process of officers to specific projects.
 * It maintains information about the officer, target project, and the current
 * status of the registration request. Officer registrations allow officers to
 * be involved in the processing of applications for specific projects.
 * </p>
 */
public class OfficerRegistration {
    /** The officer requesting assignment to a project */
    private Officer officer;

    /** The BTO project the officer is applying to work on */
    private Project project;

    /** The current status of this registration request */
    private OfficerRegStatus status;

    /** Unique identifier for this registration request */
    private String registrationId;

    /**
     * Constructs a new OfficerRegistration with specified details.
     *
     * @param registrationId    The unique identifier for this registration request
     * @param officer           The officer requesting assignment to a project
     * @param project           The BTO project the officer is applying to work on
     * @param officerRegStatus  The initial status of this registration request
     */
    public OfficerRegistration(String registrationId, Officer officer, Project project, OfficerRegStatus officerRegStatus) {
        this.officer = officer;
        this.project = project;
        this.status = officerRegStatus;
        this.registrationId = registrationId;
    }

    /**
     * Gets the officer associated with this registration.
     *
     * @return The officer requesting project assignment
     */
    public Officer getOfficer() { return officer; }

    /**
     * Gets the project associated with this registration.
     *
     * @return The project the officer is applying to work on
     */
    public Project getProject() { return project; }

    /**
     * Gets the unique identifier for this registration.
     *
     * @return The registration's unique ID
     */
    public String getRegistrationId() { return registrationId; }

    /**
     * Gets the current status of this registration.
     *
     * @return The current registration status
     */
    public OfficerRegStatus getStatus() { return status; }

    /**
     * Sets the status of this registration.
     *
     * @param status The new status to set
     */
    public void setStatus(OfficerRegStatus status) { this.status = status; }

    /**
     * Sets the unique identifier for this registration.
     *
     * @param registrationId The new registration ID to set
     */
    public void setRegistrationId(String registrationId) { this.registrationId = registrationId; }
}