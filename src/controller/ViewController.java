package controller;

import view.*;

public class ViewController {
    private final ApplicantView applicantView = new ApplicantView();
    private final ManagerView hdbManagerView = new ManagerView();
    private final OfficerView officerView = new OfficerView();

    public Object getView(String role) {
        return switch (role) {
            case "APPLICANT" -> applicantView;
            case "HDBMANAGER" -> hdbManagerView;
            case "HDBOFFICER" -> officerView;
            default -> null;
        };
    }

}
