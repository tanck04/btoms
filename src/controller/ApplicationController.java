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
import repository.ProjectRepository;
import java.util.UUID;
import java.util.Map;

public class ApplicationController {
    public Map<String, Project> getAvailableProjects() {
        return ProjectRepository.PROJECTS;
    }

    public Applicant getApplicantByNRIC(String nric) {
        // Ensure repository is loaded
        if (ApplicantRepository.APPLICANTS.isEmpty() && !ApplicantRepository.isRepoLoaded()) {
            ApplicantRepository repo = new ApplicantRepository();
            repo.loadFromCSV();
        }
        return ApplicantRepository.APPLICANTS.get(nric);
    }
    public boolean submitApplication(Applicant applicant, Project project, FlatType flatType) {
        // Validate input
        if (applicant == null) {
            System.out.println("Invalid input: Applicant is null.");
            return false;
        }

        System.out.println("Processing application for applicant: " + applicant.getNRIC());

        if (ProjectRepository.PROJECTS.isEmpty() && !ProjectRepository.isRepoLoaded()) {
            ProjectRepository projectRepo = new ProjectRepository();
            projectRepo.loadFromCSV();
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

        // Update the applicant's application ID
        applicant.setApplicationID(applicationID);
        applicant.setApplicantAppStatus(ApplicantAppStatus.PENDING);

        // Update both repositories in CSV
        ApplicantRepository.saveAllApplicantsToCSV();
        ApplicationRepository.saveAllApplicationsToCSV();

        System.out.println("Application submitted successfully. Application ID: " + applicationID);
        return true;
    }

    public boolean withdrawApplication(Applicant applicant) {
        // Validate input
        if (applicant == null || applicant.getApplicationID() == null) {
            System.out.println("Invalid input: Applicant or Application ID is null.");
            return false;
        }

        // Check if the application exists
        Application application = ApplicationRepository.APPLICATIONS.get(applicant.getApplicationID());
        if (application == null) {
            System.out.println("Application not found.");
            return false;
        }

        // Update the application status to withdrawn
        application.setApplicationStatus(ApplicantAppStatus.SUCCESSFUL);
        application.setWithdrawalStatus(WithdrawalStatus.APPROVED);

        // Save the updated application to the repository
        ApplicationRepository.saveAllApplicationsToCSV();

        System.out.println("Application withdrawn successfully.");
        return true;
    }

    public void viewApplicationStatus(Applicant applicant) {
        // Validate input
        if (applicant == null || applicant.getApplicationID() == null) {
            System.out.println("Invalid input: Applicant or Application ID is null.");
            return;
        }

        // Retrieve the application for the given applicant
        Application application = ApplicationRepository.APPLICATIONS.get(applicant.getApplicationID());
        if (application == null) {
            System.out.println("No application found for the given applicant.");
            return;
        }
        if (application.getWithdrawalStatus().equals(WithdrawalStatus.APPROVED)){
            System.out.println("Application has been withdrawn.");
            return;
        }
        // Display the application status
        System.out.println("Application Status for Applicant ID " + applicant.getApplicationID() + ":");
        System.out.println("Status: " + application.getApplicationStatus());

    }
}
