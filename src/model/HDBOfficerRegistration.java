package model;

import enums.OfficerRegStatus;

public class HDBOfficerRegistration {
    private HDBManager manager;
    private String officerID;
    private String projectID;
    private OfficerRegStatus status;

    public HDBOfficerRegistration(String officerID, String projectID) {
        this.officerID = officerID;
        this.projectID = projectID;
        this.status = OfficerRegStatus.PENDING;
    }

    public String getOfficerID() { return officerID; }
    public String getProjectID() { return projectID; }
    public OfficerRegStatus getStatus() { return status; }
    public void setStatus(OfficerRegStatus status) { this.status = status; }
}

