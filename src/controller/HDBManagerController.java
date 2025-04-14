package controller;


import enums.WithdrawalStatus;
import model.Applicant;
import model.Application;
import model.Manager;
import model.Project;
import enums.ApplicantAppStatus;
import repository.ApplicantRepository;
import repository.ApplicationRepository;
import repository.ProjectRepository;
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
     *
     * @param manager The HDB manager making the approval
     * @return true if approval was successful, false otherwise
     */
    public boolean approveApplication(Manager manager) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Manager: " + manager.getNRIC());

        // Make sure repositories are loaded
        if (ApplicationRepository.APPLICATIONS.isEmpty()) {
            new ApplicationRepository().loadFromCSV();
        }

        if (ApplicantRepository.APPLICANTS.isEmpty()) {
            new ApplicantRepository().loadFromCSV();
        }

        if (ProjectRepository.PROJECTS.isEmpty()) {
            new ProjectRepository().loadFromCSV();
        }

        // Find the project managed by this HDB manager
        Project managedProject = null;
        for (Project project : ProjectRepository.PROJECTS.values()) {
            if (manager.getNRIC().equals(project.getManagerID())) {
                managedProject = project;
                break;
            }
        }

        if (managedProject == null) {
            System.out.println("Error: No project found for manager " + manager.getNRIC());
            return false;
        }

        System.out.println("Found project: " + managedProject.getProjectID() + " - " + managedProject.getProjectName());

        // Find all pending applications for this project
        List<Application> pendingApplications = getPendingApplicationsByProject(managedProject);

        if (pendingApplications.isEmpty()) {
            System.out.println("No pending applications to approve for project " + managedProject.getProjectID());
            return false;
        }

        // Display pending applications
        System.out.println("\n======== PENDING APPLICATIONS ========");
        System.out.println("ID\tAPPLICATION ID\t\tNRIC\t\tNAME\t\tFLAT TYPE");
        System.out.println("------------------------------------------------------------");

        int count = 1;
        for (Application application : pendingApplications) {
            Applicant applicant = application.getApplicant();
            System.out.printf("%d\t%s\t%s\t%s\t%s\n",
                    count++,
                    application.getApplicationID(),
                    applicant.getNRIC(),
                    applicant.getName(),
                    application.getFlatType());
        }

        // Get user choice
        System.out.print("\nEnter the ID of the application to approve (or 0 to cancel): ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return false;
        }

        // Check if user wants to cancel
        if (choice == 0) {
            System.out.println("Operation cancelled.");
            return false;
        }

        // Validate choice
        if (choice < 1 || choice > pendingApplications.size()) {
            System.out.println("Invalid choice. Please select a valid ID.");
            return false;
        }

        // Get the selected application
        Application selectedApplication = pendingApplications.get(choice - 1);

        // Update application status
        selectedApplication.setApplicationStatus(ApplicantAppStatus.SUCCESSFUL);

        // Save changes to file
        ApplicationRepository.updateApplicationInCSV(selectedApplication);
        System.out.println("Application approved successfully.");
        return true;
    }

        // Rest of the method remains the same...

    /**
 * Gets all applications with PENDING status for a specific project
 * @param project The project to filter applications for
 * @return List of applications with pending status for the specified project
 */
    public List<Application> getPendingApplicationsByProject(Project project) {
        List<Application> pendingApplications = new ArrayList<>();

        // Check if project is null
        if (project == null) {
            System.out.println("Project is null - cannot search for applications");
            return pendingApplications;
        }

        String projectID = project.getProjectID();
        System.out.println("Looking for pending applications in project: " + projectID);

        // Ensure repositories are loaded
        if (ApplicationRepository.APPLICATIONS.isEmpty()) {
            new ApplicationRepository().loadFromCSV();
        }

        // Find all applications for this project with PENDING status
        for (Application application : ApplicationRepository.APPLICATIONS.values()) {
            if (projectID.equals(application.getProjectID()) &&
                    application.getApplicationStatus() == ApplicantAppStatus.PENDING &&
                    application.getWithdrawalStatus() == WithdrawalStatus.NULL) {

                pendingApplications.add(application);
            }
        }

        return pendingApplications;
    }
}