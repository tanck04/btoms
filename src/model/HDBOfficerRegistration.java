package model;

import enums.OfficerRegStatus;

public class HDBOfficerRegistration {
    private HDBManager manager;
    private String nric;
    private String projectId;
    private OfficerRegStatus status;
    private String registrationId;

    public HDBOfficerRegistration(String registrationId, String nric, String projectId) {
        this.nric = nric;
        this.projectId = projectId;
        this.status = OfficerRegStatus.PENDING;
        this.registrationId = registrationId;
    }

    public String getNric() { return nric; }
    public String getProjectId() { return projectId; }
    public String getRegistrationId() { return registrationId; }
    public OfficerRegStatus getStatus() { return status; }
    public void setStatus(OfficerRegStatus status) { this.status = status; }
    public void setRegistrationId(String registrationId) { this.registrationId = registrationId; }
}

