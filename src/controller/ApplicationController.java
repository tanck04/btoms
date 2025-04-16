package controller;

import model.*;
import repository.ApplicantRepository;
import repository.ApplicationRepository;
import enums.ApplicantAppStatus;
import enums.WithdrawalStatus;
import enums.FlatType;
import enums.MaritalStatus;
import enums.Visibility;
import repository.ProjectRepository;

import java.io.IOException;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;

public class ApplicationController {
    private final ApplicantRepository applicantRepo = new ApplicantRepository();
    private final ProjectRepository projectRepo = new ProjectRepository();
    private final ApplicationRepository applicationRepo = new ApplicationRepository();

    public boolean submitApplication(Applicant applicant, Project project, FlatType flatType) {
        // Validate input
        if (applicant == null || project == null) {
            System.out.println("Invalid input: Applicant or project is null.");
            return false;
        }

        System.out.println("Processing application for applicant: " + applicant.getNRIC());

        try {
            // Use repository instance methods instead of static methods
            // Validate that the project exists
            Project validProject = projectRepo.findProjectById(project.getProjectID());
            if (validProject == null) {
                System.out.println("Project does not exist in the system.");
                return false;
            }

            // Check if applicant has already applied for a project
            for (Application application :applicationRepo.loadApplications()) {
                if (application.getApplicant().getNRIC().equals(applicant.getNRIC()) &&
                        (application.getWithdrawalStatus().equals(WithdrawalStatus.PENDING) ||
                                application.getWithdrawalStatus().equals(WithdrawalStatus.NULL) ||
                                application.getWithdrawalStatus().equals(WithdrawalStatus.REJECTED))) {
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


    /// Method to withdraw an application
    public boolean requestWithdrawal(User user) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter 1 to confirm withdrawal, 0 to cancel:");
        int choice = scanner.nextInt();
        if (choice == 0) {
            System.out.println("Withdrawal cancelled.");
            return false;
        } else if (choice != 1) {
            System.out.println("Invalid choice. Please enter 1 to confirm or 0 to cancel.");
            return false;
        }
        if (user == null) {
            System.out.println("Invalid input: Applicant is null.");
            return false;
        }
        System.out.println("Processing withdrawal for applicant: " + user.getNRIC());
        try {
            ApplicationRepository applicationRepo = new ApplicationRepository();
            ProjectRepository projectRepo = new ProjectRepository();

            // Check if the applicant has any applications
            boolean hasApplication = false;
            for (Application application : applicationRepo.loadApplications()) {
                if (application.getApplicant().getNRIC().equals(user.getNRIC())) {
                    hasApplication = true;
                    break;
                }
            }

            if (!hasApplication) {
                System.out.println("No applications found for the applicant.");
                return false;
            }

            // Withdraw the application
            for (Application application : applicationRepo.loadApplications()) {
                if (application.getApplicant().getNRIC().equals(user.getNRIC())) {
                    application.setWithdrawalStatus(WithdrawalStatus.PENDING);
                    ApplicationRepository.updateApplicationInCSV(application);
                    System.out.println("Application withdrawn successfully.");
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            System.out.println("Error processing withdrawal: " + e.getMessage());
            return false;
        }
    }
}
