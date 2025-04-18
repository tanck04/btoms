package controller;

import enums.*;
import model.*;
import repository.ApplicantRepository;
import repository.ApplicationRepository;
import repository.OfficerRegRepository;
import repository.ProjectRepository;

import java.io.IOException;
import java.util.*;

public class ApplicationController {
    private final ApplicantRepository applicantRepo = new ApplicantRepository();
    private final ProjectRepository projectRepo = new ProjectRepository();
    private final ApplicationRepository applicationRepo = new ApplicationRepository();

    public boolean submitApplication(User user, Project project, FlatType flatType) {
        if (user == null || project == null) {
            System.out.println("Invalid input: User or project is null.");
            return false;
        }

        String nric;
        MaritalStatus maritalStatus;

        try {
            // Get user details
            if (user instanceof Applicant) {
                Applicant applicant = (Applicant) user;
                nric = applicant.getNRIC();
                maritalStatus = applicant.getMaritalStatus();

                // Validate duplicate application
                for (Application app : applicationRepo.loadApplications()) {
                    if (app.getApplicant() != null && app.getApplicant().getNRIC().equals(nric) &&
                            (app.getWithdrawalStatus() == WithdrawalStatus.PENDING ||
                                    app.getWithdrawalStatus() == WithdrawalStatus.NULL ||
                                    app.getWithdrawalStatus() == WithdrawalStatus.REJECTED)) {
                        System.out.println("Applicant has already applied for a project.");
                        return false;
                    }
                }

                if (maritalStatus == MaritalStatus.SINGLE && flatType != FlatType.TWO_ROOMS) {
                    System.out.println("Single applicants can only apply for TWO_ROOMS flat types.");
                    return false;
                }

                String applicationID = ApplicationRepository.generateNextApplicationId();
                Project validProject = projectRepo.findProjectById(project.getProjectID());

                if (validProject == null) {
                    System.out.println("Project does not exist in the system.");
                    return false;
                }

                Application application = new Application(applicationID, applicant, validProject, flatType,
                        ApplicantAppStatus.PENDING, WithdrawalStatus.NULL);

                applicationRepo.createNewApplication(application);
                System.out.println("Application submitted successfully. Application ID: " + applicationID);
                return true;

            } else if (user instanceof Officer) {
                Officer officer = (Officer) user;
                nric = officer.getNRIC();
                maritalStatus = officer.getMaritalStatus();

                for (Application app : applicationRepo.loadApplications()) {
                    if (app.getOfficer() != null && app.getOfficer().getNRIC().equals(nric) &&
                            (app.getWithdrawalStatus() != WithdrawalStatus.REJECTED)) {
                        System.out.println("Officer has already applied for a project.");
                        return false;
                    }
                }

                if (maritalStatus == MaritalStatus.SINGLE && flatType != FlatType.TWO_ROOMS) {
                    System.out.println("Single officers can only apply for TWO_ROOMS flat types.");
                    return false;
                }

                String applicationID = ApplicationRepository.generateNextApplicationId();
                Project validProject = projectRepo.findProjectById(project.getProjectID());

                if (validProject == null) {
                    System.out.println("Project does not exist in the system.");
                    return false;
                }

                Application application = new Application(applicationID, officer, validProject, flatType,
                        ApplicantAppStatus.PENDING, WithdrawalStatus.NULL);

                applicationRepo.createNewApplication(application);
                System.out.println("Application submitted successfully. Application ID: " + applicationID);
                return true;

            } else {
                System.out.println("Only Applicants or Officers can apply.");
                return false;
            }
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
                if (application.getUser().getNRIC().equals(user.getNRIC())) {
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
                if (application.getUser().getNRIC().equals(user.getNRIC())) {
                    application.setWithdrawalStatus(WithdrawalStatus.PENDING);
                    application.setApplicationStatus(ApplicantAppStatus.UNSUCCESSFUL);
                    ApplicationRepository.updateApplicationInCSV(application);
                    System.out.println("Withdrawal request submitted successfully.");
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
