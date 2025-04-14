package view;

import controller.ApplicantController;
import controller.ApplicationController;
import enums.MaritalStatus;
import enums.Role;
import enums.FlatType;
import model.Applicant;
import model.Project;
import model.User;

import java.util.Scanner;
import java.util.Map;

public class ApplicantView implements MenuInterface {
    private final ApplicantController controller = new ApplicantController();
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void displayMenu(User user) {
        System.out.println();
        System.out.println("+-----------------------------------------------+");
        System.out.println("|                 Applicant Menu                |");
        System.out.println("+-----------------------------------------------+");
        System.out.println("| 1. View Projects                              |");
        System.out.println("| 2. Submit Application                         |");
        System.out.println("| 3. View Application Status                    |");
        System.out.println("| 4. Request Withdrawal for Application         |");
        System.out.println("| 5. Enquiry (Submit, View, Edit, Delete)       |");
        System.out.println("| 6. Logout                                     |");
        System.out.println("+-----------------------------------------------+");
        System.out.println();
        System.out.print("Enter your choice: ");
    }

    public void handleUserInput(String input) {
        switch (input) {
            case "1":
//                createApplicantProfile();
                break;
            case "2":
                controller.checkApplicationStatus();
                break;
            case "3":
                submitApplication();
                break;
            case "4":
//                updatePersonalDetails();
                break;
            case "5":
//                controller.getAvailableProjects();
                break;
            case "6":
                System.out.println("Exiting...");
                break;
            default:
                System.out.println("Invalid option. Please try again.");
                break;
        }
    }

    public Role getUserType() {
        return Role.APPLICANT;
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
