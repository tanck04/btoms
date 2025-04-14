package controller;

import view.*;

public class ViewController {
    private final ApplicantView applicantView = new ApplicantView();
    private final HDBManagerView hdbManagerView = new HDBManagerView();
    private final HDBOfficerView hdbOfficerView = new HDBOfficerView();

    public Object getView(String role) {
        return switch (role) {
            case "APPLICANT" -> applicantView;
            case "HDBMANAGER" -> hdbManagerView;
            case "HDBOFFICER" -> hdbOfficerView;
            default -> null;
        };
    }

}
