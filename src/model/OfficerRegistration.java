package model;

import enums.OfficerRegStatus;
import enums.WithdrawalStatus;

public class OfficerRegistration {
    private Officer officer;
    private Project project;
    private OfficerRegStatus status;
    private String registrationId;

    public OfficerRegistration(String registrationId, Officer officer, Project project, OfficerRegStatus officerRegStatus) {
        this.officer = officer;
        this.project = project;
        this.status = officerRegStatus;
        this.registrationId = registrationId;
    }

    public Officer getOfficer() { return officer;}
    public Project getProject() { return project; }
    public String getRegistrationId() { return registrationId; }
    public OfficerRegStatus getStatus() { return status; }
    public void setStatus(OfficerRegStatus status) { this.status = status; }
    public void setRegistrationId(String registrationId) { this.registrationId = registrationId; }
}

