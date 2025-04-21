package controller;

import view.*;

/**
 * Controller class responsible for providing appropriate view objects based on user roles.
 * This class acts as a factory for view objects, returning the correct view implementation
 * for each supported role in the system.
 */
public class ViewController {
    /**
     * View for applicant users.
     * Provides the user interface for applicant-specific functionality.
     */
    private final ApplicantView applicantView = new ApplicantView();

    /**
     * View for HDB manager users.
     * Provides the user interface for manager-specific functionality.
     */
    private final ManagerView hdbManagerView = new ManagerView();

    /**
     * View for HDB officer users.
     * Provides the user interface for officer-specific functionality.
     */
    private final OfficerView officerView = new OfficerView();

    /**
     * Returns the appropriate view object for a given user role.
     * The returned view object will provide the user interface appropriate for the specified role.
     *
     * @param role The user role (APPLICANT, HDBMANAGER, HDBOFFICER)
     * @return The view object for the specified role, or null if no matching view exists
     */
    public Object getView(String role) {
        return switch (role) {
            case "APPLICANT" -> applicantView;
            case "HDBMANAGER" -> hdbManagerView;
            case "HDBOFFICER" -> officerView;
            default -> null;
        };
    }
}