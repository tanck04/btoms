package entity;

import enums.MaritalStatus;import enums.ApplicantAppStatus;
import enums.WithdrawalStatus;
import enums.Role;

public class Applicant extends User {
    private final Role role = Role.APPLICANT;
    private ApplicantAppStatus applicantAppStatus = null;
    private String applicationID;
    private String enquiryID = null;
    private WithdrawalStatus withdrawalStatus = WithdrawalStatus.NULL;

    public Applicant(String nric,
                     String name,
                     String password,
                     int age,
                     MaritalStatus maritalStatus,
                     String applicationID,
                     String enquiryID,
                     ApplicantAppStatus applicantAppStatus,
                     WithdrawalStatus withdrawalStatus) {
        super(nric, name, Role.APPLICANT, password, maritalStatus, age);
        this.applicantAppStatus = applicantAppStatus;
        this.applicationID = applicationID;
        this.enquiryID = enquiryID;
        this.withdrawalStatus = (withdrawalStatus != null) ? withdrawalStatus : WithdrawalStatus.NULL;
    }

    // Constructor for creating an Applicant initially without applicationID and enquiryID
    public Applicant(String nric,
                     String name,
                     String password,
                     int age,
                     MaritalStatus maritalStatus) {
        this(nric, name, password, age, maritalStatus, null, null, null, WithdrawalStatus.NULL);
    }

    public ApplicantAppStatus getApplicantAppStatus() {
        return applicantAppStatus;
    }

    public void setApplicantAppStatus(ApplicantAppStatus applicantAppStatus) {
        this.applicantAppStatus = applicantAppStatus;
    }

    public String getApplicationID() {
        return applicationID;
    }

    public void setEnquiryID(String enquiryID) {
        this.enquiryID = enquiryID;
    }

    public String getEnquiryID() {
        return enquiryID;
    }

    public void setApplicationID(String applicationID) {
        this.applicationID = applicationID;
    }
    public WithdrawalStatus getWithdrawalStatus() {
        return withdrawalStatus;
    }

    public void setWithdrawalStatus(WithdrawalStatus withdrawalStatus) {
        this.withdrawalStatus = withdrawalStatus;
    }

    public Role getRole() {
        return role;
    }
}

