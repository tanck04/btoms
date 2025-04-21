package controller;

import repository.*;

/**
 * Controller class that provides a unified access point for different repositories.
 * Acts as a factory for repository objects based on the user role in the BTO application system.
 */
public class RepositoryController {
    private final ApplicantRepository applicantRepository = new ApplicantRepository();
    private final OfficerRepository officerRepository = new OfficerRepository();
    private final ManagerRepository managerRepository = new ManagerRepository();

    /**
     * Returns the appropriate repository based on the user's role.
     * This method encapsulates the logic for repository selection, allowing
     * other controllers to work with repositories without knowing the specific
     * implementation details.
     *
     * @param role The role of the user (APPLICANT, HDBOFFICER, HDBMANAGER)
     * @return The corresponding repository for the role, or null if the role is invalid
     */
    public Object getRepository(String role) {
        return switch (role) {
            case "APPLICANT" -> applicantRepository;
            case "HDBOFFICER" -> officerRepository;
            case "HDBMANAGER" -> managerRepository;
            default -> null;
        };
    }
}