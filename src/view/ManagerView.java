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
            System.out.println("| 5. Manage Project Visibility                  |");
            System.out.println("| 6. Review Applications                        |");
            System.out.println("| 7. Approve or Reject Application              |");
            System.out.println("| 8. Approve or Reject Withdrawal               |");
            System.out.println("| 9. Review Officer Registrations               |");
            System.out.println("| 10. Approve or Reject Officers Registration   |");
            System.out.println("| 11. Reply Enquiries                           |");
            System.out.println("| 12. Logout                                    |");
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
                    createProject();
                    break;
                case 2:
                    viewAllProjects();
                    break;
                case 3:
                    updateProjectDetails();
                    break;
                case 4:
                    deleteProject();
                    break;
                case 5:
                    manageProjectVisibility();
                    break;
                case 6:
                    reviewApplications();
                    break;
                case 7:
                    approveApplication(user);
                    break;
                case 8:
                    approveWithdrawal();
                    break;
                case 9:
                    reviewOfficerRegistrations();
                    break;
                case 10:
                    approveOfficerRegistration();
                    break;
                case 11:
                    enquiryController.viewEnquiry(user);
                    break;
                case 12:
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

    public Role getUserType() {
        return Role.HDBMANAGER;
    }



    public String generateNextProjectID() {
        int max = 0;
        ProjectRepository projectRepository = new ProjectRepository();

        try {
            // Load all projects directly from CSV
            List<Project> projects = projectRepository.loadProjects();

            // Find the highest project number
            for (Project project : projects) {
                String existingID = project.getProjectID();
                if (existingID.matches("P\\d+")) {
                    int number = Integer.parseInt(existingID.substring(1));
                    if (number > max) {
                        max = number;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading projects: " + e.getMessage());
            // If we can't load existing projects, start from 1
        }

        int nextNumber = max + 1;
        return String.format("P%04d", nextNumber);  // e.g., P0001, P0002
    }

    private void createProject() {
        try {
            // Collect project data from user input
            String projectID = generateNextProjectID();  // auto-generate
            System.out.println("Auto-generated Project ID: " + projectID);

            System.out.print("Enter Project Name: ");
            String projectName = scanner.nextLine().trim();

            System.out.print("Enter Neighborhood: ");
            String neighborhood = scanner.nextLine().trim();

            // Collect TWO_ROOMS data
            System.out.println("\nTWO_ROOMS Flat Type");
            System.out.print("Enter Number of Units: ");
            int twoRoomUnits = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Enter Price: ");
            double twoRoomPrice = Double.parseDouble(scanner.nextLine().trim());

            // Collect THREE_ROOMS data
            System.out.println("\nTHREE_ROOMS Flat Type");
            System.out.print("Enter Number of Units: ");
            int threeRoomUnits = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Enter Price: ");
            double threeRoomPrice = Double.parseDouble(scanner.nextLine().trim());

            // Collect remaining project details
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            sdf.setLenient(false);

            Date openingDate = null;

            while (openingDate == null) {
                try {
                    System.out.print("Enter Opening Date (MM/dd/yyyy): ");
                    String input = scanner.nextLine().trim();
                    openingDate = sdf.parse(input);  // Parse as strict format

                    // Confirm what the user entered
                    System.out.println("✅ Parsed Opening Date: " + sdf.format(openingDate));
                } catch (ParseException e) {
                    System.out.println("❌ Invalid date format. Please follow MM/dd/yyyy.");
                }
            }


            Date closingDate = null;

            while (closingDate == null) {
                try {
                    System.out.print("Enter Closing Date (MM/dd/yyyy): ");
                    String input = scanner.nextLine().trim();
                    closingDate = sdf.parse(input);  // Parse as strict format

                    // Confirm what the user entered
                    System.out.println("✅ Parsed Closing Date: " + sdf.format(closingDate));
                } catch (ParseException e) {
                    System.out.println("❌ Invalid date format. Please follow MM/dd/yyyy.");
                }
            }

            System.out.print("Enter Manager ID: ");
            String managerID = scanner.nextLine().trim();

            System.out.print("Enter Officer Slot (number): ");
            int officerSlot = Integer.parseInt(scanner.nextLine().trim());

            List<String> officerIDs = new ArrayList<>();
            System.out.print("Do you want to add officers now? (yes/no): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("yes")) {
                System.out.print("Enter Officer IDs (comma-separated): ");
                String officerIDsInput = scanner.nextLine().trim();
                if (!officerIDsInput.isEmpty()) {
                    officerIDs = Arrays.asList(officerIDsInput.split(","));
                }
            }

            // Prepare flat type data
            Map<FlatType, Integer> flatTypeUnits = new HashMap<>();
            flatTypeUnits.put(FlatType.TWO_ROOMS, twoRoomUnits);
            flatTypeUnits.put(FlatType.THREE_ROOMS, threeRoomUnits);

            Map<FlatType, Double> flatTypePrices = new HashMap<>();
            flatTypePrices.put(FlatType.TWO_ROOMS, twoRoomPrice);
            flatTypePrices.put(FlatType.THREE_ROOMS, threeRoomPrice);

            // Call controller to handle business logic
            Project result = projectController.createProject(
                    projectID, projectName, neighborhood,
                    flatTypeUnits, flatTypePrices,
                    String.valueOf(openingDate), String.valueOf(closingDate),
                    managerID, officerSlot, officerIDs
            );

            if (result != null) {
                System.out.println("Project created successfully!");
            } else {
                System.out.println("Failed to create project.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void viewAllProjects() {
        ProjectRepository projectRepository = new ProjectRepository();
        System.out.println("\n===== All Projects =====");

        try {
            for (Project project : projectRepository.loadProjects()) {
                System.out.println("\nID: " + project.getProjectID());
                System.out.println("Name: " + project.getProjectName());
                System.out.println("Neighborhood: " + project.getNeighborhood());
                System.out.println("TWO_ROOMS Units: " + project.getUnitsForFlatType(FlatType.TWO_ROOMS) +
                                  ", Price: $" + project.getPriceForFlatType(FlatType.TWO_ROOMS));
                System.out.println("THREE_ROOMS Units: " + project.getUnitsForFlatType(FlatType.THREE_ROOMS) +
                                  ", Price: $" + project.getPriceForFlatType(FlatType.THREE_ROOMS));
                System.out.println("Application Period: " + project.getApplicationOpeningDate() +
                                  " to " + project.getApplicationClosingDate());
                System.out.println("Manager: " + project.getManagerID());
                System.out.println("Officer Slots: " + project.getOfficerSlot());
                System.out.println("Visibility: " + project.getVisibility());
                System.out.println("-----------------------------");
            }
        } catch (IOException e) {
            System.out.println("Error loading projects: " + e.getMessage());
        }
    }

    private void updateProjectDetails() {
        System.out.println("\n===== Update Project Details =====");

        System.out.print("Enter the Project ID to update: ");
        String projectId = scanner.nextLine().trim();

        try {
            Project project = projectController.getProjectById(projectId);
            if (project == null) {
                System.out.println("Project not found with ID: " + projectId);
                return;
            }

            System.out.println("Updating Project: " + project.getProjectName());
            System.out.println("\nWhat would you like to update?");
            System.out.println("1. Project Name");
            System.out.println("2. Two-Room Units");
            System.out.println("3. Two-Room Price");
            System.out.println("4. Three-Room Units");
            System.out.println("5. Three-Room Price");
            System.out.println("6. Application Opening Date");
            System.out.println("7. Application Closing Date");
            System.out.println("8. Officer Slots");
            System.out.print("Enter your choice: ");

            int choice = Integer.parseInt(scanner.nextLine().trim());

            // Implement update logic based on choice
            // This would call appropriate methods in your ProjectController
            // For brevity, I'll show a simple example:

            switch (choice) {
                case 1:
                    System.out.print("Enter new Project Name: ");
                    String newName = scanner.nextLine().trim();
                    // Here you would call projectController method to update
                    System.out.println("Project name updated successfully.");
                    break;
                // Implement other cases similarly
                default:
                    System.out.println("Invalid option.");
                    break;
            }
        } catch (Exception e) {
            System.out.println("Error retrieving project: " + e.getMessage());
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

    private void manageProjectVisibility() {
        System.out.println("\n===== Manage Project Visibility =====");

        System.out.print("Enter the Project ID to manage visibility: ");
        String projectId = scanner.nextLine().trim();

        try {
            Project project = projectController.getProjectById(projectId);
            if (project == null) {
                System.out.println("Project not found with ID: " + projectId);
                return;
            }

            System.out.println("Current visibility: " + project.getVisibility());
            System.out.println("1. Set to VISIBLE");
            System.out.println("2. Set to HIDDEN");
            System.out.print("Enter your choice: ");

            int choice = Integer.parseInt(scanner.nextLine().trim());

            // Call controller to update visibility
            // projectController.updateVisibility(projectId, choice == 1 ? Visibility.VISIBLE : Visibility.HIDDEN);
            System.out.println("Project visibility updated successfully.");
        } catch (Exception e) {
            System.out.println("Error retrieving project: " + e.getMessage());
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
            if (ManagerRepository.MANAGERS.isEmpty() && !ManagerRepository.isRepoLoaded()) {
                ManagerRepository repo = new ManagerRepository();
                repo.loadFromCSV();
            }


            // Find manager in the repository
            Manager currentManager = ManagerRepository.MANAGERS.get(manager.getNRIC());

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

    public void reviewOfficerRegistrations() {
        System.out.println("\n===== Review Officer Registrations =====");

        System.out.print("Enter project ID to review officer registrations: ");
        String projectID = scanner.nextLine().trim();

        List<OfficerRegistration> pending = OfficerRegRepository.getPendingByProject(projectID);

        if (pending.isEmpty()) {
            System.out.println("No pending officer registrations for this project.");
            return;
        }

        for (OfficerRegistration reg : pending) {
            System.out.println("\nOfficer ID: " + reg.getNric());
            System.out.println("Project ID: " + reg.getProjectId());
            System.out.println("Status: " + reg.getStatus());
            System.out.print("Approve this officer? (yes/no): ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("yes")) {
                officerRegController.approveRegistration(reg);
                System.out.println("Officer registration approved.");
            } else {
                officerRegController.rejectRegistration(reg);
                System.out.println("Officer registration rejected.");
            }
        }
    }

    private void approveOfficerRegistration() {
        // This function is similar to reviewOfficerRegistrations
        // Could call reviewOfficerRegistrations() directly
        reviewOfficerRegistrations();
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