package controller;

import repository.*;

public class RepositoryController {
    private final ApplicantRepository applicantRepository = new ApplicantRepository();
    private final OfficerRepository officerRepository = new OfficerRepository();
    private final ManagerRepository managerRepository = new ManagerRepository();

    public Object getRepository(String role) {
        return switch (role) {
            case "APPLICANT" -> applicantRepository;
            case "HDBOFFICER" -> officerRepository;
            case "HDBMANAGER" -> managerRepository;
            default -> null;
        };
    }
}
