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
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

public class ApplicationController {

    public boolean submitApplication(Applicant applicant, Project project, FlatType flatType) {
        // Validate input
        if (applicant == null) {
            System.out.println("Invalid input: Applicant is null.");
            return false;
        }

        System.out.println("Processing application for applicant: " + applicant.getNRIC());

        // Ensure both repositories are loaded
        if (ProjectRepository.PROJECTS.isEmpty()) {
            ProjectRepository projectRepo = new ProjectRepository();
            projectRepo.loadFromCSV();
        }

        if (ApplicantRepository.APPLICANTS.isEmpty()) {
            ApplicantRepository applicantRepo = new ApplicantRepository();
            applicantRepo.loadFromCSV();

            // Make sure the current applicant is in the map
            if (!ApplicantRepository.APPLICANTS.containsKey(applicant.getNRIC())) {
                ApplicantRepository.APPLICANTS.put(applicant.getNRIC(), applicant);
            }
        }

        // Check if the project exists in the repository
        if (!ProjectRepository.PROJECTS.containsKey(project.getProjectID())) {
            System.out.println("Project does not exist in the system.");
            return false;
        }

        // Check marital status restrictions
        if (applicant.getMaritalStatus() == MaritalStatus.SINGLE && flatType != FlatType.TWO_ROOMS) {
            System.out.println("Single applicants can only apply for TWO_ROOMS flat types.");
            return false;
        }

        // Generate a unique application ID
        String applicationID = UUID.randomUUID().toString();

        // Create a new application
        Application application = new Application(
            applicationID,
            applicant,
            project,
            flatType,
            ApplicantAppStatus.PENDING,  // Starting with PENDING status
            WithdrawalStatus.NULL
        );

        // Save the application to the repository
        ApplicationRepository.APPLICATIONS.put(applicationID, application);

        // Update both repositories in CSV
        ApplicantRepository.saveAllApplicantsToCSV();
        ApplicationRepository.saveAllApplicationsToCSV();

        System.out.println("Application submitted successfully. Application ID: " + applicationID);
        return true;
    }
}
