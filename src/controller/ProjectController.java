package controller;

import model.Project;
import enums.FlatType;
import enums.Visibility;
import repository.ProjectRepository;

import java.io.IOException;
import java.util.*;

public class ProjectController {

    /**
     * Creates a new project with the provided details and saves it to the repository
     * @return the created Project object if successful, null otherwise
     */
    private final ProjectRepository projectRepository = new ProjectRepository();
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
        // Validate inputs


        try {
            // Validate project ID doesn't already exist
            if (projectRepository.findProjectById(projectID) != null) {
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
            projectRepository.createNewProject(newProject);

            return newProject;
        } catch (Exception e) {
            System.out.println("Error creating project: " + e.getMessage());
            return null;
        }
    }

    public List<Project> getAvailableProjects() {
        // Simply check if projects are empty
        ArrayList<Project> visibleProjects = new ArrayList<>();
        try {
            // Load projects using the method that returns a List<Project>
            List<Project> projectsList = projectRepository.loadProjects();

            // Filter projects based on visibility
            for (Project project : projectsList) {
                if (project.getVisibility() == Visibility.ON) {
                    visibleProjects.add(project);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading available projects: " + e.getMessage());
            // Return empty map on error (already initialized)
        }
        return visibleProjects;
    }

    public Project getProjectById(String projectID) {
        try {
            return projectRepository.findProjectById(projectID);
        } catch (IOException e) {
            System.out.println("Error retrieving project: " + e.getMessage());
            return null;
        }
    }

    // Add other business logic methods related to projects
    // For example:
    // - updateProject()
    // - toggleProjectVisibility()
    // - assignOfficers()
    // - getProjectsByNeighborhood()
}