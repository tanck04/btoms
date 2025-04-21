package controller;

import model.User;

/**
 * Interface defining methods for viewing project information.
 * Implementations provide functionality for users to access and display
 * project details based on their permissions and roles.
 */
public interface ViewProjectInterface {

    /**
     * Displays project information to the user.
     * The specific projects shown and the level of detail may vary
     * depending on the user's role and access permissions.
     *
     * @param user The user requesting to view projects
     */
    void viewProject(User user);
}