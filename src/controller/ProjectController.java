package controller;

import model.Project;
import enums.FlatType;
import enums.Visibility;
import repository.ProjectRepository;

import java.util.*;

public class ProjectController {

    /**
     * Creates a new project with the provided details and saves it to the repository
     * @return the created Project object if successful, null otherwise
     */
    public Project createProject(
            String projectID,
            String projectName,
            String neighborhood,
            Map<FlatType, Integer> flatTypeUnits,
            Map<FlatType, Double> flatTypePrices,
            String openingDate,
            String closingDate,
            String managerID,
            int officerSlot,
            List<String> officerIDs) {

        try {
            // Validate project ID doesn't already exist
            if (ProjectRepository.PROJECTS.containsKey(projectID)) {
                System.out.println("A project with this ID already exists.");
                return null;
            }

            // Perform any additional business validations
            // (e.g., date format validation, manager existence check, etc.)

            // Create project object
            Project newProject = new Project(
                    projectID,
                    projectName,
                    neighborhood,
                    flatTypeUnits,
                    flatTypePrices,
                    openingDate,
                    closingDate,
                    managerID,
                    officerSlot,
                    officerIDs,
                    Visibility.ON
            );

            // Save to repository
            ProjectRepository.PROJECTS.put(projectID, newProject);
            ProjectRepository.saveAllProjectsToCSV();

            return newProject;
        } catch (Exception e) {
            System.out.println("Error creating project: " + e.getMessage());
            return null;
        }
    }

    public Map<String, Project> getAvailableProjects() {
        // Simply check if projects are empty
        if (ProjectRepository.PROJECTS.isEmpty()) {
            ProjectRepository repo = new ProjectRepository();
            repo.loadFromCSV();
        }

        // Filter projects based on visibility
        Map<String, Project> visibleProjects = new HashMap<>();
        for (Project project : ProjectRepository.PROJECTS.values()) {
            if (project.getVisibility() == Visibility.ON) {
                visibleProjects.put(project.getProjectID(), project);
            }
        }

        return visibleProjects;
    }

    // Add other business logic methods related to projects
    // For example:
    // - updateProject()
    // - toggleProjectVisibility()
    // - assignOfficers()
    // - getProjectsByNeighborhood()
}