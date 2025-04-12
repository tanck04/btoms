package boundary;

import controller.ApplicantController;
import controller.ApplicationController;
import enums.MaritalStatus;
import enums.Role;
import enums.FlatType;
import entity.Applicant;
import entity.Project;
import java.util.Scanner;
import java.util.Map;

public class ApplicantView implements MenuInterface {
    private ApplicantController controller;
    private Scanner scanner;

    public ApplicantView() {
        this.controller = new ApplicantController();
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void displayMenu() {
        System.out.println("\n===== Applicant Menu =====");
        System.out.println("1. Create New Applicant Profile");
        System.out.println("2. View Application Status");
        System.out.println("3. Submit Application");
        System.out.println("4. Update Personal Details");
        System.out.println("5. View Available Flats");
        System.out.println("6. Exit");
        System.out.print("Enter your choice: ");
    }

    @Override
    public void handleUserInput(String input) {
        switch (input) {
            case "1":
                createApplicantProfile();
                break;
            case "2":
                controller.checkApplicationStatus();
                break;
            case "3":
                submitApplication();
                break;
            case "4":
                updatePersonalDetails();
                break;
            case "5":
                controller.getAvailableProjects();
                break;
            case "6":
                System.out.println("Exiting...");
                break;
            default:
                System.out.println("Invalid option. Please try again.");
                break;
        }
    }

    @Override
    public Role getUserType() {
        return Role.APPLICANT;
    }

    private void createApplicantProfile() {
        try {
            System.out.print("Enter NRIC: ");
            String nric = scanner.nextLine().trim();

            System.out.print("Enter Name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Enter Password: ");
            String password = scanner.nextLine().trim();

            System.out.print("Enter Age: ");
            int age = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Enter Marital Status (SINGLE/MARRIED): ");
            String statusInput = scanner.nextLine().trim().toUpperCase();
            MaritalStatus maritalStatus = MaritalStatus.valueOf(statusInput);

            // Call controller to handle business logic
            Applicant result = controller.createApplicant(nric, name, password, age, maritalStatus);

            if (result != null) {
                System.out.println("Applicant profile created successfully!");
            } else {
                System.out.println("Failed to create applicant profile.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void submitApplication() {
        try {
            // First, get the logged in applicant
            System.out.print("Enter your NRIC: ");
            String nric = scanner.nextLine().trim();

            // Use ApplicationController instead of ApplicantController for this method
            ApplicationController applicationController = new ApplicationController();
            Applicant applicant = applicationController.getApplicantByNRIC(nric);

            if (applicant == null) {
                System.out.println("Applicant not found. Please create a profile first.");
                return;
            }

            // Display available projects for the user to select from
            System.out.println("\n===== Available Projects =====");
            Map<String, Project> availableProjects = applicationController.getAvailableProjects();

            if (availableProjects.isEmpty()) {
                System.out.println("No projects available for application at this time.");
                return;
            }

            for (Project project : availableProjects.values()) {
                System.out.println(project.getProjectID() + ": " + project.getProjectName() + " - " + project.getNeighborhood());
            }

            // Get project selection
            System.out.print("\nEnter Project ID to apply for: ");
            String projectID = scanner.nextLine().trim();

            Project selectedProject = availableProjects.get(projectID);
            if (selectedProject == null) {
                System.out.println("Invalid project ID. Please try again.");
                return;
            }

            // Display available flat types for the selected project
            System.out.println("\n===== Available Flat Types =====");
            Map<FlatType, Integer> flatTypes = selectedProject.getFlatTypeUnits();

            for (Map.Entry<FlatType, Integer> entry : flatTypes.entrySet()) {
                if (entry.getValue() > 0) {
                    System.out.println(entry.getKey() + " - Units available: " + entry.getValue());
                }
            }

            // Get flat type selection
            System.out.println("\nSelect Flat Type:");
            System.out.println("2 - TWO_ROOMS");
            System.out.println("3 - THREE_ROOMS");
            System.out.print("Enter your choice (2 or 3): ");
            String flatTypeInput = scanner.nextLine().trim();
            FlatType selectedFlatType;

            try {
                switch (flatTypeInput) {
                    case "2":
                        selectedFlatType = FlatType.TWO_ROOMS;
                        break;
                    case "3":
                        selectedFlatType = FlatType.THREE_ROOMS;
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid selection");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid flat type selection. Please try again.");
                return;
            }


            // Confirm submission
            System.out.print("\nConfirm application submission? (Y/N): ");
            String confirm = scanner.nextLine().trim().toUpperCase();

            if (confirm.equals("Y")) {
                boolean success = applicationController.submitApplication(applicant, selectedProject, selectedFlatType);

                if (success) {
                    System.out.println("Application submitted successfully!");
                } else {
                    System.out.println("Failed to submit application. Please try again.");
                }
            } else {
                System.out.println("Application submission cancelled.");
            }

        } catch (Exception e) {
            System.out.println("Error submitting application: " + e.getMessage());
        }
    }
    // Method to get user input for updating personal details
    public String getUserInput() {
        return scanner.nextLine().trim();
    }

    private void updatePersonalDetails() {
        // Collect user input for updating details
        System.out.println("Update Personal Details - Implementation pending");
        // Then call controller method with the collected data
    }
}
