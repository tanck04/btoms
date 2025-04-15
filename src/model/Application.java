package model;

import enums.ApplicantAppStatus;
import enums.FlatType;
import enums.WithdrawalStatus;

public class Application {
    private String applicationID;
    private Applicant applicant;
    private final Project project;
    private FlatType flatType;
    private WithdrawalStatus withdrawalStatus;
    private ApplicantAppStatus applicationStatus;

    // Constructor for creating a new Application
//    public Application(Applicant applicant, Project project, FlatType flatType) {
//        this.applicant = applicant;
//        this.project = project;
//        this.flatType = flatType;
//        this.withdrawalStatus = WithdrawalStatus.NULL;
//        this.applicationStatus = ApplicantAppStatus.PENDING;
//    }

    // Constructor for creating an Application from CSV data
    public Application(String applicationID, Applicant applicant, Project project, FlatType flatType,
                       ApplicantAppStatus applicationStatus, WithdrawalStatus withdrawalStatus) {
        this.applicationID = applicationID;
        this.applicant = applicant;
        this.project = project;
        this.flatType = flatType;
        this.applicationStatus = applicationStatus;
        this.withdrawalStatus = withdrawalStatus;
    }

    // Converts the Application object to a CSV-formatted string
    // In Application.java
    public String toCSV() {
        return String.format("%s,%s,%s,%s,%s,%s",
            applicationID,
            applicant.getNRIC(),
            project.getProjectID(),
            flatType.toString(),
            applicationStatus.toString(),
            withdrawalStatus.toString());
    }

    // Getter and Setter methods
    public String getApplicationID() {
        return applicationID;
    }

    public void setApplicationID(String applicationID) {
        this.applicationID = applicationID;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public Project getProject() {
        return project;
    }

    public FlatType getFlatType() {
        return flatType;
    }

    public void setFlatType(FlatType flatType) {
        this.flatType = flatType;
    }

    public WithdrawalStatus getWithdrawalStatus() {
        return withdrawalStatus;
    }

    public void setWithdrawalStatus(WithdrawalStatus withdrawalStatus) {
        this.withdrawalStatus = withdrawalStatus;
    }

    public ApplicantAppStatus getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(ApplicantAppStatus applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

}