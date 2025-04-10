package controller;

import model.Application;
import model.Applicant;
import model.Project;
import repository.ApplicantRepository;
import repository.ApplicationRepository;
import enums.ApplicantAppStatus;
import enums.WithdrawalStatus;
import enums.FlatType;

import java.util.UUID;

public class ApplicationController {

    public boolean submitApplication(Applicant applicant, Project project, FlatType flatType) {
        // Test submitApplication
        System.out.println("Testing submitApplication...");

        // Validate input
        if (applicant == null || project == null || flatType == null) {
            System.out.println("Invalid input: Applicant, Project, or FlatType is null.");
            return false;
        }

        // Check if the applicant already has an application
        if (applicant.getApplicationID() != null && ApplicationRepository.APPLICATIONS.containsKey(applicant.getApplicationID())) {
            System.out.println("Applicant already has an application.");
            return false;
        }

        // Create a new application
        String applicationID = UUID.randomUUID().toString(); // Generate a unique ID
        Application application = new Application(
            applicationID,
            applicant,
            project,
            flatType,
            ApplicantAppStatus.SUCCESSFUL,
            WithdrawalStatus.NULL
        );

        // Save the application to the repository
        ApplicationRepository.APPLICATIONS.put(applicationID, application);
        ApplicationRepository.saveAllApplicationsToCSV();

        // Link the application to the applicant
        applicant.setApplicationID(applicationID);

        // Update the applicant in the CSV file with the new application ID
        ApplicantRepository.saveAllApplicantsToCSV();

        System.out.println("Application submitted successfully.");
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
