package view;

import controller.HDBOfficerRegController;
import controller.ProjectController;
import controller.HDBManagerController;
import enums.FlatType;
import enums.Role;
import model.HDBOfficerRegistration;
import model.Project;
import model.HDBManager;
import model.User;
import repository.HDBManagerRepository;
import repository.HDBOfficerRegRepository;
import repository.ProjectRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class HDBManagerView implements MenuInterface {
    private ProjectController projectController;
    private Scanner scanner;
    //instance of HDBManagerController for officer Registration
    private HDBOfficerRegController officerRegController;

    public HDBManagerView() {
        this.projectController = new ProjectController();
        this.officerRegController = new HDBOfficerRegController();
        this.scanner = new Scanner(System.in);
    }

    public void displayMenu(User user) {
        System.out.println("\n===== HDB Manager Menu =====");
        System.out.println("1. Create New Project");
        System.out.println("2. View All Projects");
        System.out.println("3. Update Project Details");
        System.out.println("4. Manage Project Visibility");
        System.out.println("5. Assign Officers");
        System.out.println("6. Approve application");
        System.out.println("7. Review Officer Registrations");
        System.out.println("8. Exit");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        switch (choice) {
            case 1:
                createProject();
                break;
            case 2:
                // View all projects
                System.out.println("View All Projects - Implementation pending");
                break;
            case 3:
                // Update project details
                System.out.println("Update Project Details - Implementation pending");
                break;
            case 4:
                // Manage visibility
                System.out.println("Manage Project Visibility - Implementation pending");
                break;
            case 5:
                // Assign officers
                System.out.println("Assign Officers - Implementation pending");
                break;
            case 6:
                approveApplication();
                break;
            case 7:
                reviewOfficerRegistrations();
                break;
            case 8:
                System.out.println("Exiting...");
                break;
            default:
                System.out.println("Invalid option. Please try again.");
                break;
        }

    }



    public Role getUserType() {
        return Role.HDBMANAGER;
    }

    public FlatType parseFlatType(String input) {
        switch (input.replaceAll("-", "").replaceAll("\\s+", "").toUpperCase()) {
            case "2ROOM":
                return FlatType.TWO_ROOMS;
            case "3ROOM":
                return FlatType.THREE_ROOMS;
            default:
                throw new IllegalArgumentException("Unknown flat type: " + input);
        }
    }

    public String generateNextProjectID() {
        int max = 0;

        for (String existingID : ProjectRepository.PROJECTS.keySet()) {
            if (existingID.matches("P\\d+")) {
                int number = Integer.parseInt(existingID.substring(1));
                if (number > max) {
                    max = number;
                }
            }
        }

        int nextNumber = max + 1;
        return String.format("P%04d", nextNumber);  // e.g., P0001, P0002
    }

    private void createProject() {
        try {
            // Collect project data from user input

            String projectID = generateNextProjectID();  // auto-generated
            System.out.println("Auto-generated Project ID: " + projectID);

            System.out.print("Enter Project Name: ");
            String projectName = scanner.nextLine().trim();

            System.out.print("Enter Neighborhood: ");
            String neighborhood = scanner.nextLine().trim();

            FlatType type1 = null;
            while (type1 == null) {
                try {
                    System.out.print("Enter Type 1 (e.g., 2-Room): ");
                    String input = scanner.nextLine().trim();
                    type1 = parseFlatType(input);
                } catch (IllegalArgumentException e) {
                    System.out.println("❌ Invalid flat type. Please enter '2-Room' or '3-Room'.");
                }
            }


            System.out.print("Enter Number of units for Type 1: ");
            int type1Units = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Enter Selling price for Type 1: ");
            double type1Price = Double.parseDouble(scanner.nextLine().trim());

            FlatType type2 = null;
            while (type2 == null) {
                try {
                    System.out.print("Enter Type 2 (e.g., 3-Room): ");
                    String input = scanner.nextLine().trim();
                    type2 = parseFlatType(input);
                } catch (IllegalArgumentException e) {
                    System.out.println("❌ Invalid flat type. Please enter '2-Room' or '3-Room'.");
                }
            }

            System.out.print("Enter Number of units for Type 2: ");
            int type2Units = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Enter Selling price for Type 2: ");
            double type2Price = Double.parseDouble(scanner.nextLine().trim());

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            sdf.setLenient(false);
            Date openingDate = null;

            while (openingDate == null) {
                try {
                    System.out.print("Enter Opening Date (MM/dd/yyyy): ");
                    String input = scanner.nextLine().trim();
                    openingDate = sdf.parse(input);
                } catch (ParseException e) {
                    System.out.println("❌ Invalid date format. Please follow MM/dd/yyyy.");
                }
            }

            Date closingDate = null;

            while (closingDate == null) {
                try {
                    System.out.print("Enter Closing Date (MM/dd/yyyy): ");
                    String input = scanner.nextLine().trim();
                    closingDate = sdf.parse(input);
                } catch (ParseException e) {
                    System.out.println("❌ Invalid date format. Please follow MM/dd/yyyy.");
                }
            }

            System.out.print("Enter Manager ID(NRIC): ");
            String managerID = scanner.nextLine().trim();

            int officerSlot = -1;
            while (officerSlot < 0) {
                try {
                    System.out.print("Enter Officer Slot (number): ");
                    officerSlot = Integer.parseInt(scanner.nextLine().trim());
                    if (officerSlot < 0) throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    System.out.println("❌ Invalid number. Please enter a positive integer.");
                }
            }

            System.out.print("Enter Officer IDs (comma-separated): ");
            List<String> officerIDs = Arrays.asList(scanner.nextLine().trim().split("\\s*,\\s*"));

            Map<FlatType, Integer> flatTypeUnits = new HashMap<>();
            flatTypeUnits.put(type1, type1Units);
            flatTypeUnits.put(type2, type2Units);

            Map<FlatType, Double> flatTypePrices = new HashMap<>();
            flatTypePrices.put(type1, type1Price);
            flatTypePrices.put(type2, type2Price);


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
//            boolean success = hdbManagerController.approveApplication(currentManager);
//
//            if (!success) {
//                System.out.println("Application approval process was not completed.");
//            }

        } catch (Exception e) {
            System.out.println("Error during approval process: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void reviewOfficerRegistrations() {
        System.out.print("Enter project ID to review officer registrations: ");
        String projectID = scanner.nextLine().trim();

        List<HDBOfficerRegistration> pending = HDBOfficerRegRepository.getPendingByProject(projectID);

        if (pending.isEmpty()) {
            System.out.println("No pending officer registrations for this project.");
            return;
        }

        for (HDBOfficerRegistration reg : pending) {
            System.out.println("Officer ID: " + reg.getOfficerID());
            System.out.print("Approve this officer? (yes/no): ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("yes")) {
                officerRegController.approveRegistration(reg);
            } else {
                officerRegController.rejectRegistration(reg);
            }
        }
    }
}