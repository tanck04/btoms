package controller;

import enums.FlatType;
import enums.Visibility;
import model.*;
import enums.MaritalStatus;
import repository.ApplicantRepository;
import repository.ApplicationRepository;
import repository.ProjectRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ApplicantController{
    private final ApplicantRepository applicantRepository = new ApplicantRepository();
    private final ApplicationRepository applicationRepository = new ApplicationRepository();
    private final ProjectRepository projectRepository = new ProjectRepository();
    // Method to check application status
    public void checkApplicationStatus(User user) {
        // Logic to check application status
        try {
            for (Application application : applicationRepository.loadApplications()) {
                if (application.getUser().getNRIC().equals(user.getNRIC())) {
                    System.out.println("Application ID: " + application.getApplicationID());
                    System.out.println("Project ID: " + application.getProject().getProjectID());
                    System.out.println("Flat Type: " + application.getFlatType());
                    System.out.println("Application Status: " + application.getApplicationStatus());
                    System.out.println("Withdrawal Status: " + application.getWithdrawalStatus());
                    System.out.println();
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading applications: " + e.getMessage());
        }
    }

    public Applicant getApplicantById(String applicantID) {
        try {
            return applicantRepository.findApplicantById(applicantID);
        } catch (IOException e) {
            System.out.println("Error retrieving project: " + e.getMessage());
            return null;
        }
    }

    public List<Project> listProject(Applicant applicant, String neighbourhoodFilter, FlatType flatTypeFilter) {
        try {
            return projectRepository.loadProjects().stream()
                    .filter(project ->
                            project.getVisibility() == Visibility.ON || isApplyProject(project, applicant)
                    )
                    .filter(project ->
                            neighbourhoodFilter == null || neighbourhoodFilter.isEmpty() ||
                                    project.getNeighborhood().equalsIgnoreCase(neighbourhoodFilter)
                    )
                    .filter(project ->
                            flatTypeFilter == null ||
                                    project.getFlatTypePrices().containsKey(flatTypeFilter)
                    )
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.out.println("Error loading projects: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean isApplyProject(Project project, User user) {
        try {
            for (Application application : applicationRepository.loadApplications()) {
                if (application.getProject().equals(project) && application.getApplicant().equals(user)) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading applications: " + e.getMessage());
        }
        return false;
    }

    private void printProjectList(List<Project> projects, FlatType flatTypeFilter) {
        System.out.println("+------------------------------------------------------------------------------------------------------------------------------------------+");
        System.out.println("|                                                             Project List                                                                 |");
        System.out.println("+------------------------------------------------------------------------------------------------------------------------------------------+");
        System.out.printf("| %-10s | %-20s | %-15s | %-20s | %-22s |\n", "Project ID", "Project Name", "Neighbourhood", "App. Start", "App. End");
        System.out.println("+------------------------------------------------------------------------------------------------------------------------------------------+");

        for (Project project : projects) {
            System.out.printf("| %-10s | %-20s | %-15s | %-20s | %-22s |\n",
                    project.getProjectID(),
                    project.getProjectName(),
                    project.getNeighborhood(),
                    project.getApplicationOpeningDate(),
                    project.getApplicationClosingDate()
            );

            System.out.println("| Flat Types:                                                                                                                              |");

            if (flatTypeFilter != null) {
                // Show only filtered flat type
                Double price = project.getFlatTypePrices().get(flatTypeFilter);
                int units = project.getUnitsForFlatType(flatTypeFilter);
                if (price != null) {
                    System.out.printf("|    - %-10s : $%-10.2f (%-3d units available)                                                                                      |\n",
                            flatTypeFilter.toString(), price, units);
                } else {
                    System.out.println("|    - No data available for selected flat type                                                                                           |");
                }
            } else {
                // Show all flat types
                for (Map.Entry<FlatType, Double> entry : project.getFlatTypePrices().entrySet()) {
                    FlatType flatType = entry.getKey();
                    Double price = entry.getValue();
                    int units = project.getUnitsForFlatType(flatType);
                    System.out.printf("|    - %-10s : $%-10.2f (%-3d units available)                                                                                      |\n",
                            flatType.toString(), price, units);
                }
            }

            System.out.println("+------------------------------------------------------------------------------------------------------------------------------------------+");
        }
    }


    public void viewProject(User user) {
        Scanner scanner = new Scanner(System.in);
        Applicant applicant = (Applicant) user;
        MaritalStatus maritalStatus = applicant.getMaritalStatus();
        // Step 1: Show all projects
        List<Project> allProjects = listProject(applicant, null, null);
        System.out.println("All Available Projects:");
        if (maritalStatus == MaritalStatus.MARRIED) {
            printProjectList(allProjects, null);
        } else {
            printProjectList(allProjects, FlatType.TWO_ROOMS);
        }

        // Step 2: Ask user if they want to filter
        System.out.print("\nWould you like to apply a filter? (yes/no): ");
        String response = scanner.nextLine().trim().toLowerCase();

        if (response.equals("yes")) {
            System.out.print("Enter neighbourhood to filter by (or leave blank): ");
            String neighbourhood = scanner.nextLine().trim();

            FlatType flatType = maritalStatus == MaritalStatus.SINGLE ? FlatType.TWO_ROOMS : null;

            List<Project> filteredProjects = listProject(applicant, neighbourhood, flatType);
            System.out.println("\nFiltered Projects:");
            printProjectList(filteredProjects, flatType);
        }
    }

    public void submitApplication(User user) {
        FlatType selectedFlatType;
        List<Project> availableProjects;
        Scanner scanner = new Scanner(System.in);
        try {
            // First, get the logged in applicant
            Applicant applicant = (Applicant) user;
            String nric = applicant.getNRIC();

            // Use ApplicationController instead of ApplicantController for this method
            ApplicationController applicationController = new ApplicationController();
            ProjectController projectController = new ProjectController();


            // Get available projects for the applicant
            availableProjects = projectController.getAvailableProjects();

            for (Project project : availableProjects) {
                System.out.println(project.getProjectID() + ": " + project.getProjectName() + " - " + project.getNeighborhood());
            }

            // Get project selection
            System.out.print("\nEnter Project ID to apply for: ");
            String projectID = scanner.nextLine().trim();

            Project selectedProject = projectController.getProjectById(projectID);

            if (selectedProject == null) {
                System.out.println("Invalid project ID. Please try again.");
                return;
            }

            // Display available flat types for the selected project
            System.out.println("\n===== Available Flat Types =====");
            Map<FlatType, Integer> flatTypes = selectedProject.getFlatTypeUnits();

            // Filter and display flat types based on marital status
            MaritalStatus maritalStatus = user.getMaritalStatus();
            List<FlatType> availableOptions = new ArrayList<>();

            for (Map.Entry<FlatType, Integer> entry : flatTypes.entrySet()) {
                FlatType type = entry.getKey();
                int units = entry.getValue();

                if (units > 0) {
                    // For SINGLE, only show TWO_ROOMS
                    if (maritalStatus == MaritalStatus.SINGLE && type == FlatType.TWO_ROOMS) {
                        System.out.println(type + " - Units available: " + units);
                        availableOptions.add(type);
                    }

                    // For MARRIED, show TWO_ROOMS and THREE_ROOMS
                    if (maritalStatus == MaritalStatus.MARRIED &&
                            (type == FlatType.TWO_ROOMS || type == FlatType.THREE_ROOMS)) {
                        System.out.println(type + " - Units available: " + units);
                        availableOptions.add(type);
                    }
                }
            }

            // Flat type selection
            if (availableOptions.isEmpty()) {
                System.out.println("No available flat types for your eligibility.");
                return;
            }

            if (maritalStatus == MaritalStatus.MARRIED) {
                // Let the user choose from available options
                System.out.println("\nSelect Flat Type:");
                for (int i = 0; i < availableOptions.size(); i++) {
                    System.out.printf("%d - %s\n", i + 1, availableOptions.get(i));
                }
                System.out.print("Enter your choice (number): ");
                String input = scanner.nextLine().trim();

                try {
                    int choice = Integer.parseInt(input);
                    if (choice < 1 || choice > availableOptions.size()) {
                        System.out.println("Invalid selection. Please try again.");
                        return;
                    }
                    selectedFlatType = availableOptions.get(choice - 1);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    return;
                }
            } else {
                // SINGLE: auto-assign TWO_ROOMS if available
                selectedFlatType = FlatType.TWO_ROOMS;
                if (!availableOptions.contains(selectedFlatType)) {
                    System.out.println("TWO_ROOMS is not available at the moment.");
                    return;
                }
                System.out.println("Single applicants can only apply for TWO_ROOMS flat types.");
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

}
