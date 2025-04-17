package controller;

import enums.FlatType;
import enums.MaritalStatus;
import enums.OfficerRegStatus;
import enums.Visibility;
import model.*;
import repository.OfficerRegRepository;
import repository.ProjectRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class HDBOfficerController extends ApplicantController {
    private final ProjectRepository projectRepository = new ProjectRepository();

    public void viewProject(User user) {
        Scanner scanner = new Scanner(System.in);
        Officer officer = (Officer) user;

        // Step 1: Show all projects
        List<Project> allProjects = listProject(officer, null, null);
        System.out.println("All Available Projects:");
        printProjectList(allProjects);

        // Step 2: Ask user if they want to filter
        System.out.print("\nWould you like to apply a filter? (yes/no): ");
        String response = scanner.nextLine().trim().toLowerCase();

        if (response.equals("yes")) {
            System.out.print("Enter neighbourhood to filter by (or leave blank): ");
            String neighbourhood = scanner.nextLine().trim();

            System.out.print("Enter flat type to filter by (e.g., TWO_ROOMS, THREE_ROOMS) or leave blank: ");
            String flatTypeInput = scanner.nextLine().trim();
            FlatType flatType = null;

            if (!flatTypeInput.isEmpty()) {
                try {
                    flatType = FlatType.valueOf(flatTypeInput.toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid flat type, ignoring flat type filter.");
                }
            }

            List<Project> filteredProjects = listProject(officer, neighbourhood, flatType);
            System.out.println("\nFiltered Projects:");
            printProjectList(filteredProjects);
        }
    }

    public List<Project> listProject(Officer officer, String neighbourhoodFilter, FlatType flatTypeFilter) {
        try {
            return projectRepository.loadProjects().stream()
                    .filter(project ->
                            project.getVisibility() == Visibility.ON || project.getOfficerIDs().contains(officer.getNRIC())
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

    public void printProjectList(List<Project> projects) {
        // Assuming user is an instance of Manager

        // Header
        System.out.println("+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+");
        System.out.println("|                                                                                 Project List                                                                                 |");
        System.out.println("+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+");
        System.out.printf("| %-10s | %-20s | %-15s | %-12s | %-12s | %-10s | %-12s | %-15s |\n",
                "Project ID", "Project Name", "Neighbourhood", "App. Start", "App. End", "Visibility", "OfficerSlot", "Manager ID");
        System.out.println("+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+");

        // Project rows
        for (Project project : projects) {
            System.out.printf("| %-10s | %-20s | %-15s | %-12s | %-12s | %-10s | %-12d | %-15s |\n",
                    project.getProjectID(),
                    project.getProjectName(),
                    project.getNeighborhood(),
                    project.getApplicationOpeningDate(),
                    project.getApplicationClosingDate(),
                    project.getVisibility(),
                    project.getOfficerSlot(),
                    project.getManagerID()
            );

            // Officer IDs
            System.out.println("| Officer IDs: " + String.join(", ", project.getOfficerIDs()));
            // Flat types
            System.out.println("| Flat Types:");
            for (Map.Entry<FlatType, Double> entry : project.getFlatTypePrices().entrySet()) {
                FlatType flatType = entry.getKey();
                Double price = entry.getValue();
                int units = project.getUnitsForFlatType(flatType);
                System.out.printf("|    - %-10s : $%-10.2f (%-3d units available)\n",
                        flatType.toString(), price, units);
            }

            // Separator after each project
            System.out.println("+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+");
        }
    }

    public void submitApplication(User user) {
        FlatType selectedFlatType;
        List<Project> availableProjects;
        Scanner scanner = new Scanner(System.in);
        try {
            // First, get the logged in applicant
            Officer officer = (Officer) user;
            String nric = officer.getNRIC();

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

            try{
                if (ifRegisterOfficer(officer)) {
                    System.out.println("You have already registered to be an officer for this project. Please select another project.");
                    return;
                }
            }catch (Exception e){
                System.out.println("Error checking registration: " + e.getMessage());
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
                boolean success = applicationController.submitApplication(officer, selectedProject, selectedFlatType);

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

    // Method to check if the officer has registered to become an officer for a project
    private boolean ifRegisterOfficer(Officer officer){
        OfficerRegRepository officerRegRepository = new OfficerRegRepository();
        List <OfficerRegistration> officerRegistrations = new ArrayList<>();
        try{
            officerRegistrations = officerRegRepository.loadAllOfficerReg();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (OfficerRegistration registration : officerRegistrations) {
            if (registration.getOfficer().getNRIC().equals(officer.getNRIC()) &&
                    registration.getStatus() != OfficerRegStatus.REJECTED){
                return true;
            }
        }
        return false;
    }

}