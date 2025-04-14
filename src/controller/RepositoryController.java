package controller;

import repository.*;

public class RepositoryController {
    private final ApplicantRepository applicantRepository = new ApplicantRepository();
    private final HDBOfficerRepository hdbOfficerRepository = new HDBOfficerRepository();
    private final HDBManagerRepository hdbManagerRepository = new HDBManagerRepository();

    public Object getRepository(String role) {
        return switch (role) {
            case "APPLICANT" -> applicantRepository;
            case "HDBOFFICER" -> hdbOfficerRepository;
            case "HDBMANAGER" -> hdbManagerRepository;
            default -> null;
        };
    }
}
