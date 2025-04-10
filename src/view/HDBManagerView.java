package view;

import controller.ProjectController;
import controller.HDBManagerController;
import enums.FlatType;
import enums.MaritalStatus;
import enums.Role;
import enums.Visibility;
import model.Project;
import model.HDBManager;
import repository.HDBManagerRepository;
import java.util.*;

public class HDBManagerView implements MenuInterface {
    private ProjectController projectController;
    private Scanner scanner;

    public HDBManagerView() {
        this.projectController = new ProjectController();
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void displayMenu() {
        System.out.println("\n===== HDB Manager Menu =====");
        System.out.println("1. Create New Project");
        System.out.println("2. View All Projects");
        System.out.println("3. Update Project Details");
        System.out.println("4. Manage Project Visibility");
        System.out.println("5. Assign Officers");
        System.out.println("6. Approve application");
        System.out.println("7. Exit");
        System.out.print("Enter your choice: ");
    }

    @Override
    public void handleUserInput(String input) {
        switch (input) {
            case "1":
                createProject();
                break;
            case "2":
                // View all projects
                System.out.println("View All Projects - Implementation pending");
                break;
            case "3":
                // Update project details
                System.out.println("Update Project Details - Implementation pending");
                break;
            case "4":
                // Manage visibility
                System.out.println("Manage Project Visibility - Implementation pending");
                break;
            case "5":
                // Assign officers
                System.out.println("Assign Officers - Implementation pending");
                break;
            case "6":
                approveApplication();
                break;
            case "7":
                System.out.println("Exiting...");
                break;
            default:
                System.out.println("Invalid option. Please try again.");
                break;
        }
    }

    @Override
    public Role getUserType() {
        return Role.HDBMANAGER;
    }

    private void createProject() {
        try {
            // Collect project data from user input
            System.out.print("Enter Project ID: ");
            String projectID = scanner.nextLine().trim();

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
            System.out.print("Enter Application Opening Date (MM/DD/YYYY): ");
            String openingDate = scanner.nextLine().trim();

            System.out.print("Enter Application Closing Date (MM/DD/YYYY): ");
            String closingDate = scanner.nextLine().trim();

            System.out.print("Enter Manager ID: ");
            String managerID = scanner.nextLine().trim();

            System.out.print("Enter Officer Slot (number): ");
            int officerSlot = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Enter Officer IDs (comma-separated): ");
            String officerIDsInput = scanner.nextLine().trim();
            List<String> officerIDs = Arrays.asList(officerIDsInput.split(","));

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
                    openingDate, closingDate,
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
        }
    }

    private void approveApplication() {
        try {
            System.out.println("\n===== Approve Application =====");

            // Ensure repository is loaded
            if (HDBManagerRepository.MANAGERS.isEmpty() && !HDBManagerRepository.isRepoLoaded()) {
                HDBManagerRepository repo = new HDBManagerRepository();
                repo.loadFromCSV();
            }

            // Get HDB Manager credentials
            System.out.print("Enter your NRIC: ");
            String managerNRIC = scanner.nextLine().trim();

            System.out.print("Enter your password: ");
            String managerPassword = scanner.nextLine().trim();

            // Find manager in the repository
            HDBManager currentManager = HDBManagerRepository.MANAGERS.get(managerNRIC);

            // Validate manager exists and password is correct
            if (currentManager == null) {
                System.out.println("Error: No manager found with NRIC " + managerNRIC);
                return;
            }

            if (!currentManager.getPassword().equals(managerPassword)) {
                System.out.println("Error: Incorrect password");
                return;
            }

            // Create instance of HDBManagerController and process approval
            HDBManagerController hdbManagerController = new HDBManagerController();
            boolean success = hdbManagerController.approveApplication(currentManager);

            if (!success) {
                System.out.println("Application approval process was not completed.");
            }

        } catch (Exception e) {
            System.out.println("Error during approval process: " + e.getMessage());
            e.printStackTrace();
        }
    }
}