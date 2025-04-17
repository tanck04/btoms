package view;

import controller.EnquiryController;
import controller.HDBOfficerRegController;
import controller.ProjectController;
import controller.HDBManagerController;
import enums.FlatType;
import enums.Role;
import model.*;
import repository.OfficerRegRepository;
import repository.ManagerRepository;
import repository.ProjectRepository;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ManagerView implements MenuInterface {
    private final ProjectController projectController = new ProjectController();;
    private final Scanner scanner = new Scanner(System.in);
    private final HDBOfficerRegController officerRegController = new HDBOfficerRegController();
    private final HDBManagerController hdbManagerController = new HDBManagerController();
    private final EnquiryController enquiryController = new EnquiryController();

    @Override
    public void displayMenu(User user) {
        boolean running = true;

        while (running) {
            System.out.println("\n+-----------------------------------------------+");
            System.out.println("|             HDB Manager Menu                  |");
            System.out.println("+-----------------------------------------------+");
            System.out.println("| 1. Create New Project                         |");
            System.out.println("| 2. View All Projects                          |");
            System.out.println("| 3. Update Project Details                     |");
            System.out.println("| 4. Delete Project                             |");
            System.out.println("| 5. Review Applications                        |");
            System.out.println("| 6. Approve or Reject Application              |");
            System.out.println("| 7. Approve or Reject Withdrawal               |");
            System.out.println("| 8. Review Officer Registrations               |");
            System.out.println("| 9. Approve or Reject Officers Registration    |");
            System.out.println("| 10. Reply Enquiries                           |");
            System.out.println("| 11. Logout                                    |");
            System.out.println("+-----------------------------------------------+");
            System.out.print("Enter your choice: ");

            String input = scanner.nextLine().trim();
            int choice;

            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            switch (choice) {
                case 1:
                    projectController.createProject(user);
                    break;
                case 2:
                    hdbManagerController.viewProject(user);
                    break;
                case 3:
                    projectController.updateProjectDetails(user);
                    break;
                case 4:
                    deleteProject();
                    break;
                case 5:
                    reviewApplications();
                    break;
                case 6:
                    approveApplication(user);
                    break;
                case 7:
                    hdbManagerController.approveOrRejectWithdrawal(user);
                    break;
                case 8:
                    //reviewOfficerRegistrations();
                    break;
                case 9:
                    hdbManagerController.approveOrRejectOfficerRegistration(user);
                    //approveOfficerRegistration();
                    break;
                case 10:
                    enquiryController.viewEnquiry(user);
                    enquiryController.replyToEnquiry(user);
                    break;
                case 11:
                    System.out.println("Logging out...");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }

            if (running) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }
    }

    private void deleteProject() {
        System.out.println("\n===== Delete Project =====");

        System.out.print("Enter the Project ID to delete: ");
        String projectId = scanner.nextLine().trim();

        try {
            ProjectRepository projectRepository = new ProjectRepository();
            Project project = projectRepository.findProjectById(projectId);

            if (project == null) {
                System.out.println("Project not found with ID: " + projectId);
                return;
            }

            System.out.println("Are you sure you want to delete project: " + project.getProjectName() + "? (yes/no)");
            String confirm = scanner.nextLine().trim();

            if (confirm.equalsIgnoreCase("yes")) {
                // Call controller to delete the project
                // projectController.deleteProject(projectId);
                System.out.println("Project deleted successfully.");
            } else {
                System.out.println("Delete operation cancelled.");
            }
        } catch (IOException e) {
            System.out.println("Error finding project: " + e.getMessage());
        }
    }

    private void reviewApplications() {
        System.out.println("\n===== Review Applications =====");

        System.out.print("Enter the Project ID to review applications: ");
        String projectId = scanner.nextLine().trim();

        try {
            Project project = projectController.getProjectById(projectId);
            if (project == null) {
                System.out.println("Project not found with ID: " + projectId);
                return;
            }

            HDBManagerController controller = new HDBManagerController();
            List<Application> pending_project_list = controller.getPendingApplicationsByProject(project);

            for (Application application : pending_project_list) {
                System.out.println("Name: " + application.getApplicant().getName());
                System.out.println("NRIC: " + application.getApplicant().getNRIC());
                System.out.print("Approve this application? (yes/no): ");
                String input = scanner.nextLine().trim();
            }
        } catch (Exception e) {
            System.out.println("Error retrieving project: " + e.getMessage());
        }
    }

    private void approveApplication(User user) {
        Manager manager = (Manager) user;
        try {
            System.out.println("\n===== Approve Application =====");

            // Ensure repository is loaded


            // Find manager in the repository
            Manager currentManager = hdbManagerController.getManagerById(user.getNRIC());

            // Validate manager exists and password is correct
            if (currentManager == null) {
                System.out.println("Error: No manager found with NRIC " + manager.getNRIC());
                return;
            }

            // Create instance of HDBManagerController and process approval
            boolean success = hdbManagerController.approveApplication(currentManager);

            if (!success) {
                System.out.println("Application approval process was not completed.");
            }

        } catch (Exception e) {
            System.out.println("Error during approval process: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void approveWithdrawal() {
        System.out.println("\n===== Approve Withdrawal =====");

        System.out.print("Enter the Application ID to review withdrawal: ");
        String applicationId = scanner.nextLine().trim();

        // Call controller to get withdrawal details
        // Withdrawal withdrawal = withdrawalController.getWithdrawalByApplicationId(applicationId);

        System.out.println("Withdrawal details:");
        // Display withdrawal details

        System.out.print("Approve withdrawal? (yes/no): ");
        String decision = scanner.nextLine().trim();

        if (decision.equalsIgnoreCase("yes")) {
            // Call controller to approve withdrawal
            // withdrawalController.approveWithdrawal(applicationId);
            System.out.println("Withdrawal approved successfully.");
        } else {
            // Call controller to reject withdrawal
            // withdrawalController.rejectWithdrawal(applicationId);
            System.out.println("Withdrawal rejected.");
        }
    }


    private void replyEnquiries() {
        System.out.println("\n===== Reply to Enquiries =====");

        System.out.println("Pending enquiries:");
        // Display list of pending enquiries
        // Logic to fetch and display enquiries

        System.out.println("No pending enquiries found."); // Placeholder

        // If there were enquiries, you would:
        // 1. Let the manager select an enquiry to reply to
        // 2. Let them enter a response
        // 3. Call the controller to save the response
    }
}