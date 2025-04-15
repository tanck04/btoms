package model;

public class Enquiry {
    private String enquiryID;
    private String applicantID;
    private String projectID;
    private String enquiryText;
    private String enquiryReply;
    private String enquiryStatus;
    private String replyingOfficerID;

    public Enquiry(String enquiryID, String applicantID, String projectID, String enquiryText, String enquiryReply, String enquiryStatus, String replyingOfficerID) {
        this.enquiryID = enquiryID;
        this.applicantID = applicantID;
        this.projectID = projectID;
        this.enquiryText = enquiryText;
        this.enquiryReply = enquiryReply;
        this.enquiryStatus = enquiryStatus;
        this.replyingOfficerID = replyingOfficerID;
    }

    public String getEnquiryID() {
        return enquiryID;
    }

    public void setEnquiryID(String enquiryID) {
        this.enquiryID = enquiryID;
    }

    public String getApplicantID() {
        return applicantID;
    }

    public void setApplicantID(String applicantID) {
        this.applicantID = applicantID;
    }

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public String getEnquiryText() {
        return enquiryText;
    }

    public void setEnquiryText(String enquiryText) {
        this.enquiryText = enquiryText;
    }

    public String getEnquiryReply() {
        return enquiryReply;
    }

    public void setEnquiryReply(String enquiryReply) {
        this.enquiryReply = enquiryReply;
    }

    public String getEnquiryStatus() {
        return enquiryStatus;
    }

    public void setEnquiryStatus(String enquiryStatus) {
        this.enquiryStatus = enquiryStatus;
    }

    public String getReplyingOfficerID() {
        return replyingOfficerID;
    }

    public void setReplyingOfficerID(String replyingOfficerID) {
        this.replyingOfficerID = replyingOfficerID;
    }
}
