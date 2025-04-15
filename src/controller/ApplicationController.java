package controller;

import model.Application;
import model.Applicant;
import model.Project;
import repository.ApplicantRepository;
import repository.ApplicationRepository;
import enums.ApplicantAppStatus;
import enums.WithdrawalStatus;
import enums.FlatType;
import enums.MaritalStatus;
import enums.Visibility;
import repository.ProjectRepository;

import java.io.IOException;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

public class ApplicationController {

    public boolean submitApplication(Applicant applicant, Project project, FlatType flatType) {
        // Validate input
        if (applicant == null || project == null) {
            System.out.println("Invalid input: Applicant or project is null.");
            return false;
        }

        System.out.println("Processing application for applicant: " + applicant.getNRIC());

        try {
            // Use repository instance methods instead of static methods
            ApplicantRepository applicantRepo = new ApplicantRepository();
            ProjectRepository projectRepo = new ProjectRepository();
            ApplicationRepository applicationRepo = new ApplicationRepository();

            // Validate that the project exists
            Project validProject = projectRepo.findProjectById(project.getProjectID());
            if (validProject == null) {
                System.out.println("Project does not exist in the system.");
                return false;
            }

            // Check if applicant has already applied for a project
            for (Application application :applicationRepo.loadApplications()) {
                if (application.getApplicant().getNRIC().equals(applicant.getNRIC())) {
                    System.out.println("Applicant has already applied for a project.");
                    return false;
                }
            }

            // Check marital status restrictions
            if (applicant.getMaritalStatus() == MaritalStatus.SINGLE && flatType != FlatType.TWO_ROOMS) {
                System.out.println("Single applicants can only apply for TWO_ROOMS flat types.");
                return false;
            }

            // Generate a unique application ID
            String applicationID = ApplicationRepository.generateNextApplicationId();

            // Create a new application using the validated project
            Application application = new Application(
                applicationID,
                applicant,
                validProject,
                flatType,
                ApplicantAppStatus.PENDING,  // Starting with PENDING status
                WithdrawalStatus.NULL
            );

            // Save the application to the repository
            applicationRepo.createNewApplication(application);

            System.out.println("Application submitted successfully. Application ID: " + applicationID);
            return true;

        } catch (IOException e) {
            System.out.println("Error processing application: " + e.getMessage());
            return false;
        }
    }

    // Helper method to save a new applicant without using the static HashMap

}
