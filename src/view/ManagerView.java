package view;

import controller.*;
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
    private final HDBManagerController hdbManagerController = new HDBManagerController();
    private final EnquiryController enquiryController = new EnquiryController();
    private final SecQuesController secQuesController = new SecQuesController();
    private final PasswordController passwordController = new PasswordController();

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
            System.out.println("| 6. View Report                                |");
            System.out.println("| 7. Approve or Reject Application              |");
            System.out.println("| 8. Approve or Reject Withdrawal               |");
            System.out.println("| 9. Review Officer Registrations               |");
            System.out.println("| 10. Approve or Reject Officers Registration    |");
            System.out.println("| 11. View and Reply Enquiries                  |");
            System.out.println("| 12. Set Security Question for Recovery        |");
            System.out.println("| 13. Change Password                           |");
            System.out.println("| 14. Logout                                    |");
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
                    projectController.deleteProject(user);
                    break;
                case 5:
                    reviewApplications();
                    break;
                case 6:
                    hdbManagerController.generateReports(user);
                    break;
                case 7:
                    approveApplication(user);
                    break;
                case 8:
                    hdbManagerController.approveOrRejectWithdrawal(user);
                    break;
                case 9:
                    hdbManagerController.reviewOfficerRegistration(user);
                    break;
                case 10:
                    hdbManagerController.approveOrRejectOfficerRegistration(user);
                    //approveOfficerRegistration();
                    break;
                case 11:
                    enquiryController.viewEnquiry(user);
                    enquiryController.replyToEnquiry(user);
                    break;
                case 12:
                    secQuesController.changeSecurityQuestionAndAnswer(user);
                    break;
                case 13:
                    passwordController.handlePasswordChange(user);
                    break;
                case 14:
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
            boolean success = hdbManagerController.approveOrRejectApplication(currentManager);

            if (!success) {
                System.out.println("Application approval process was not completed.");
            }

        } catch (Exception e) {
            System.out.println("Error during approval process: " + e.getMessage());
            e.printStackTrace();
        }
    }
}