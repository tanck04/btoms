package controller;


import enums.WithdrawalStatus;
import model.Applicant;
import model.Application;
import model.HDBManager;
import model.Project;
import enums.ApplicantAppStatus;
import repository.ApplicantRepository;
import repository.ApplicationRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HDBManagerController{

    /**
     * Allows HDB manager to approve an applicant's application
     * @param hdbManager The HDB manager making the approval
     * @return true if approval was successful, false otherwise
     */
    /**
         * Allows HDB manager to approve an applicant's application
         * @param hdbManager The HDB manager making the approval
         * @return true if approval was successful, false otherwise
         */
//        public boolean approveApplication(HDBManager hdbManager) {
//            Scanner scanner = new Scanner(System.in);
//            // Add at beginning of approveApplication method
//            System.out.println("Manager: " + hdbManager.getNRIC() +
//                              ", Project: " + (hdbManager.getProject() != null ?
//                                             hdbManager.getProject().getProjectID() : "null"));
//            // Get pending applicants for this manager's project
//            List<Applicant> pendingApplicants = getPendingApplicantsByProject(hdbManager.getProject());
//            System.out.println("Found " + pendingApplicants.size() + " pending applications for project " +
//                              hdbManager.getProject().getProjectID());
//
//            if (pendingApplicants.isEmpty()) {
//                System.out.println("No pending applications to approve.");
//                return false;
//            }
//
//            // Display pending applications
//            System.out.println("\n======== PENDING APPLICATIONS ========");
//            System.out.println("ID\tNRIC\t\tNAME\t\tAPPLICATION ID");
//            System.out.println("------------------------------------------------------------");
//
//            int count = 1;
//            for (Applicant applicant : pendingApplicants) {
//                System.out.printf("%d\t%s\t%s\t%s\n",
//                    count++,
//                    applicant.getNRIC(),
//                    applicant.getName(),
//                    applicant.getApplicationID());
//            }
//
//            // Get user choice
//            System.out.print("\nEnter the ID of the application to approve (or 0 to cancel): ");
//            int choice;
//            try {
//                choice = Integer.parseInt(scanner.nextLine());
//            } catch (NumberFormatException e) {
//                System.out.println("Invalid input. Please enter a number.");
//                return false;
//            }
//
//            // Check if user wants to cancel
//            if (choice == 0) {
//                System.out.println("Operation cancelled.");
//                return false;
//            }
//
//            // Validate choice
//            if (choice < 1 || choice > pendingApplicants.size()) {
//                System.out.println("Invalid choice. Please select a valid ID.");
//                return false;
//            }
//
//            // Get the selected applicant
//            Applicant selectedApplicant = pendingApplicants.get(choice - 1);
//
//            // Get the application
//            Application application = ApplicationRepository.APPLICATIONS.get(selectedApplicant.getApplicationID());
//            if (application == null) {
//                System.out.println("Error: Application not found.");
//                return false;
//            }
//
//            // Update application status
//            application.setApplicationStatus(ApplicantAppStatus.SUCCESSFUL);
//            selectedApplicant.setApplicantAppStatus(ApplicantAppStatus.SUCCESSFUL);
//
//            // Save changes
//            ApplicationRepository.saveAllApplicationsToCSV();
//            ApplicantRepository.saveAllApplicantsToCSV();
//
//            System.out.println("Application approved successfully.");
//            return true;
//        }

        // Rest of the method remains the same...

    /**
     * Gets all applicants with PENDING status
     * @return List of applicants with pending applications
     */
    /**
     * Gets all applicants with PENDING status for a specific project
     * @param project The project to filter applicants for
     * @return List of applicants with pending applications for the specified project
     */
//    private List<Applicant> getPendingApplicantsByProject(Project project) {
//        List<Applicant> pendingApplicants = new ArrayList<>();
//
//        // Check if project is null
//        if (project == null) {
//            System.out.println("Project is null - cannot search for applications");
//            return pendingApplicants;
//        }
//
//        String projectID = project.getProjectID();
//        System.out.println("Looking for pending applications in project: " + projectID);
//
//        // Debug repository status
//        System.out.println("Applicant repository size: " + ApplicantRepository.APPLICANTS.size());
//        System.out.println("Application repository size: " + ApplicationRepository.APPLICATIONS.size());
//
//        // Ensure repositories are loaded
//        if (ApplicantRepository.APPLICANTS.isEmpty()) {
//            new ApplicantRepository().loadFromCSV();
//        }
//
//        if (ApplicationRepository.APPLICATIONS.isEmpty()) {
//            new ApplicationRepository().loadFromCSV();
//        }
//
//        for (Applicant applicant : ApplicantRepository.APPLICANTS.values()) {
//            // Debug each applicant
//            System.out.println("Checking applicant: " + applicant.getNRIC() +
//                              ", Status: " + applicant.getApplicantAppStatus() +
//                              ", AppID: " + applicant.getApplicationID());
//
//            if (applicant.getApplicantAppStatus() == ApplicantAppStatus.PENDING &&
//                applicant.getApplicationID() != null &&
//                applicant.getWithdrawalStatus() == WithdrawalStatus.NULL) {
//
//                // Get the application to check its project ID
//                Application application = ApplicationRepository.APPLICATIONS.get(applicant.getApplicationID());
//
//                // Debug output
//                if (application != null) {
//                    System.out.println("Found application: " + application.getApplicationID() +
//                                      " for project: " + application.getProjectID());
//
//                    // Only add applicants whose application is for this project
//                    if (projectID.equals(application.getProjectID())) {
//                        pendingApplicants.add(applicant);
//                        System.out.println("Added applicant to pending list");
//                    }
//                } else {
//                    System.out.println("Application not found for ID: " + applicant.getApplicationID());
//                }
//            }
//        }
//
//        return pendingApplicants;
//    }
}