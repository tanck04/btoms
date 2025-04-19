package controller;

import enums.*;
import model.*;
import repository.ApplicationRepository;
import repository.OfficerRegRepository;
import repository.ProjectRepository;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class HDBOfficerController extends ApplicantController {
    private final ProjectRepository projectRepository = new ProjectRepository();
    private final ApplicantController applicantController = new ApplicantController();

    public void viewProject(User user) {
        Scanner scanner = new Scanner(System.in);
        Officer officer = (Officer) user;

        // Step 1: Show all projects
        String lastNeighbourhoodFilter = null;
        FlatType lastFlatTypeFilter = null;
        List<Project> allProjects = listProject(officer, lastNeighbourhoodFilter, null);
        System.out.println("All Available Projects:");
        printProjectList(allProjects, null);

        // Step 2: Ask user if they want to filter
        System.out.print("\nWould you like to apply a filter? (yes/no): ");
        String response = scanner.nextLine().trim().toLowerCase();

        if (response.equals("yes")) {
            System.out.print("Enter neighbourhood to filter by (or leave blank): ");
            String neighbourhood = scanner.nextLine().trim();
            lastNeighbourhoodFilter = neighbourhood.isEmpty() ? null : neighbourhood;

            System.out.print("Enter flat type to filter by (e.g., TWO_ROOMS, THREE_ROOMS) or leave blank: ");
            String flatTypeInput = scanner.nextLine().trim().toUpperCase();
            lastFlatTypeFilter = flatTypeInput.isEmpty() ? null : FlatType.valueOf(flatTypeInput);

            List<Project> filteredProjects = listProject(officer, lastNeighbourhoodFilter, lastFlatTypeFilter);
            System.out.println("\nFiltered Projects:");
            printProjectList(filteredProjects, lastFlatTypeFilter);
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

    public void printProjectList(List<Project> projects, FlatType flatTypeFilter){
        // Assuming user is an instance of Officer

//        // Header
//        System.out.println("+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+");
//        System.out.println("|                                                                                 Project List                                                                                 |");
//        System.out.println("+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+");
//        System.out.printf("| %-10s | %-20s | %-15s | %-12s | %-12s | %-10s | %-12s | %-15s |\n",
//                "Project ID", "Project Name", "Neighbourhood", "App. Start", "App. End", "Visibility", "OfficerSlot", "Manager ID");
//        System.out.println("+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+");
//
//        // Project rows
//        for (Project project : projects) {
//            System.out.printf("| %-10s | %-20s | %-15s | %-12s | %-12s | %-10s | %-12d | %-15s |\n",
//                    project.getProjectID(),
//                    project.getProjectName(),
//                    project.getNeighborhood(),
//                    project.getApplicationOpeningDate(),
//                    project.getApplicationClosingDate(),
//                    project.getVisibility(),
//                    project.getOfficerSlot(),
//                    project.getManagerID()
//            );
//
//            // Officer IDs
//            System.out.println("| Officer IDs: " + String.join(", ", project.getOfficerIDs()));
//            // Flat types
//            System.out.println("| Flat Types:");
//            for (Map.Entry<FlatType, Double> entry : project.getFlatTypePrices().entrySet()) {
//                FlatType flatType = entry.getKey();
//                Double price = entry.getValue();
//                int units = project.getUnitsForFlatType(flatType);
//
//                // Filter logic based on flatTypeFilter
//                if (flatTypeFilter == null || flatTypeFilter == flatType) {
//                    System.out.printf("|    - %-10s : $%-10.2f (%-3d units available)\n",
//                            flatType.toString(), price, units);
//                }
//            }
//
//
//            // Separator after each project
//            System.out.println("+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+");
//        }

        // print centered title
        System.out.println("+------------+----------------------+-----------------+--------------+--------------+------------+--------------+----------------+");
        System.out.println("|                                                          Project List                                                          |");
        System.out.println("+------------+----------------------+-----------------+--------------+--------------+------------+--------------+----------------+");
        // print project header
        System.out.printf("| %-10s | %-20s | %-15s | %-12s | %-12s | %-10s | %-12s | %-14s |\n",
                "Project ID", "Project Name", "Neighbourhood", "App. Start", "App. End", "Visibility", "OfficerSlot", "Manager ID");
        System.out.println("+------------+----------------------+-----------------+--------------+--------------+------------+--------------+----------------+");

        // print projects
        for (Project project : projects) {
            System.out.printf("| %-10s | %-20s | %-15s | %-12s | %-12s | %-10s | %-12d | %-14s |\n",
                    project.getProjectID(),
                    project.getProjectName(),
                    project.getNeighborhood(),
                    project.getApplicationOpeningDate(),
                    project.getApplicationClosingDate(),
                    project.getVisibility(),
                    project.getOfficerSlot(),
                    project.getManagerID());

            System.out.println("+------------+----------------------+-----------------+--------------+--------------+------------+--------------+----------------+");
            // Officer IDs
            String officerLine = "Officer IDs: " + String.join(", ", project.getOfficerIDs());
            System.out.printf("| %-126s |\n", "   " + officerLine); // Indented under full-width

            // Flat Types Header
            System.out.printf("| %-126s |\n", "   Flat Types:");
            for (Map.Entry<FlatType, Double> entry : project.getFlatTypePrices().entrySet()) {
                FlatType flatType = entry.getKey();
                Double price = entry.getValue();
                int units = project.getUnitsForFlatType(flatType);

                if (flatTypeFilter == null || flatTypeFilter == flatType) {
                    String flatTypeLine = String.format("    - %-12s: $%-10.2f (%-3d units available)",
                            flatType.toString(), price, units);
                    System.out.printf("| %-126s |\n", flatTypeLine);
                }
            }

            // End divider
            System.out.println("+------------+----------------------+-----------------+--------------+--------------+------------+--------------+----------------+");
        }
    }

    public void submitApplication(User user) {
        applicantController.submitApplication(user);
//        FlatType selectedFlatType;
//        Scanner scanner = new Scanner(System.in);
//        try {
//            // First, get the logged in applicant
//            Officer officer = (Officer) user;
//            String nric = officer.getNRIC();
//
//            // check eligibility
//            if (user.getAge()<35 && user.getMaritalStatus() == MaritalStatus.SINGLE) {
//                System.out.println("You are not eligible to apply for a flat.");
//                return;
//            }
//            if (user.getAge()<21 && user.getMaritalStatus() == MaritalStatus.MARRIED) {
//                System.out.println("You are not eligible to apply for a flat. Applicant needs to be at least 21 years old.");
//                return;
//            }
//
//            // Use ApplicationController instead of ApplicantController for this method
//            ApplicationController applicationController = new ApplicationController();
//            ProjectController projectController = new ProjectController();
//
//            // Show currently applied filters
//            System.out.println("\nCurrently applied filters:");
//            System.out.println("Neighbourhood: " + (lastNeighbourhoodFilter == null ? "None" : lastNeighbourhoodFilter));
//            System.out.println("Flat Type: " + (lastFlatTypeFilter == null ? "All" : lastFlatTypeFilter));
//            List<Project> availableProjects = listProject(officer, lastNeighbourhoodFilter, lastFlatTypeFilter);
//            printProjectList(availableProjects, lastFlatTypeFilter);
//
//            System.out.print("Do you want to apply/change the filters? (yes/no): ");
//            String changeFilters = scanner.nextLine().trim().toLowerCase();
//
//            if (changeFilters.equals("yes")) {
//                System.out.print("Enter neighbourhood to filter by (or leave blank): ");
//                String neighbourhood = scanner.nextLine().trim();
//                lastNeighbourhoodFilter = neighbourhood.isEmpty() ? null : neighbourhood;
//                System.out.print("Enter flat type to filter by (e.g., TWO_ROOMS, THREE_ROOMS) or leave blank: ");
//                String flatTypeInput = scanner.nextLine().trim();
//                lastFlatTypeFilter = flatTypeInput.isEmpty() ? null : FlatType.valueOf(flatTypeInput);
//            }
//
//            // Use the filters to get available projects
//            List<Project> filterProjectsAgain = listProject(officer, lastNeighbourhoodFilter, lastFlatTypeFilter);
//
//            if (filterProjectsAgain.isEmpty()) {
//                System.out.println("No projects available with the selected filters.");
//                return;
//            }
//
//            printProjectList(filterProjectsAgain, lastFlatTypeFilter);
//            // Get project selection
//            System.out.print("\nEnter Project ID to apply for: ");
//            String projectID = scanner.nextLine().trim();
//
//            Project selectedProject = projectController.getProjectById(projectID);
//
//            if (selectedProject == null) {
//                System.out.println("Invalid project ID. Please try again.");
//                return;
//            }
//
//            try{
//                if (ifRegisterOfficer(officer, selectedProject)) {
//                    System.out.println("You have already registered to be an officer for this project. Please select another project.");
//                    return;
//                }
//            }catch (Exception e){
//                System.out.println("Error checking registration: " + e.getMessage());
//                return;
//            }
//
//            // Display available flat types for the selected project
//            System.out.println("\n===== Available Flat Types =====");
//            Map<FlatType, Integer> flatTypes = selectedProject.getFlatTypeUnits();
//
//            // Filter and display flat types based on marital status
//            MaritalStatus maritalStatus = user.getMaritalStatus();
//            int age = user.getAge();
//            List<FlatType> availableOptions = new ArrayList<>();
//
//            for (Map.Entry<FlatType, Integer> entry : flatTypes.entrySet()) {
//                FlatType type = entry.getKey();
//                int units = entry.getValue();
//
//                if (units > 0) {
//                    // For SINGLE and of 35 years old and above, only show TWO_ROOMS
//                    if (maritalStatus == MaritalStatus.SINGLE && age > 35 && type == FlatType.TWO_ROOMS) {
//                        System.out.println(type + " - Units available: " + units);
//                        availableOptions.add(type);
//                    }
//
//                    // For MARRIED and of 21 years old and above, show TWO_ROOMS and THREE_ROOMS
//                    if (maritalStatus == MaritalStatus.MARRIED && age > 21 &&
//                            (type == FlatType.TWO_ROOMS || type == FlatType.THREE_ROOMS)) {
//                        System.out.println(type + " - Units available: " + units);
//                        availableOptions.add(type);
//                    }
//                }
//            }
//
//            // Flat type selection
//            if (availableOptions.isEmpty()) {
//                System.out.println("No available flat types for your eligibility.");
//                return;
//            }
//
//            if (maritalStatus == MaritalStatus.MARRIED) {
//                // Let the user choose from available options
//                System.out.println("\nSelect Flat Type:");
//                for (int i = 0; i < availableOptions.size(); i++) {
//                    System.out.printf("%d - %s\n", i + 1, availableOptions.get(i));
//                }
//                System.out.print("Enter your choice (number): ");
//                String input = scanner.nextLine().trim();
//
//                try {
//                    int choice = Integer.parseInt(input);
//                    if (choice < 1 || choice > availableOptions.size()) {
//                        System.out.println("Invalid selection. Please try again.");
//                        return;
//                    }
//                    selectedFlatType = availableOptions.get(choice - 1);
//                } catch (NumberFormatException e) {
//                    System.out.println("Invalid input. Please enter a number.");
//                    return;
//                }
//            } else {
//                // SINGLE: auto-assign TWO_ROOMS if available
//                selectedFlatType = FlatType.TWO_ROOMS;
//                if (!availableOptions.contains(selectedFlatType)) {
//                    System.out.println("TWO_ROOMS is not available at the moment.");
//                    return;
//                }
//                System.out.println("Single applicants can only apply for TWO_ROOMS flat types.");
//            }
//
//
//
//            // Confirm submission
//            System.out.print("\nConfirm application submission? (Y/N): ");
//            String confirm = scanner.nextLine().trim().toUpperCase();
//
//            if (confirm.equals("Y")) {
//                boolean success = applicationController.submitApplication(officer, selectedProject, selectedFlatType);
//
//                if (success) {
//                    System.out.println("Application submitted successfully!");
//                } else {
//                    System.out.println("Failed to submit application. Please try again.");
//                }
//            } else {
//                System.out.println("Application submission cancelled.");
//            }
//
//        } catch (Exception e) {
//            System.out.println("Error submitting application: " + e.getMessage());
//        }
    }

    // Method to check if the officer has registered to become an officer for a project
    private boolean ifRegisterOfficer(Officer officer, Project project){
        OfficerRegRepository officerRegRepository = new OfficerRegRepository();
        List <OfficerRegistration> officerRegistrations = new ArrayList<>();
        try{
            officerRegistrations = officerRegRepository.loadAllOfficerReg();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (OfficerRegistration registration : officerRegistrations) {
            if (registration.getOfficer().getNRIC().equals(officer.getNRIC()) &&
                    registration.getStatus() != OfficerRegStatus.REJECTED &&
                    registration.getProject().getProjectID().equals(project.getProjectID())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Find successful applications for the project the officer is in charge of
     * @param user The officer user
     * @return List of successful applications
     */
    public List<Application> getSuccessfulApplicationsForOfficerProject(User user) {
        List<Application> successfulApplications = new ArrayList<>();
        Project inChargeProject = null;
        ApplicationRepository applicationRepo = new ApplicationRepository();

        try {
            List<Project> projects = projectRepository.loadProjects();
            for (Project project : projects) {
                if (project.getOfficerIDs().contains(user.getNRIC())) {
                    inChargeProject = project;
                    break;
                }
            }

            if (inChargeProject == null) {
                System.out.println("You are not assigned to any project as an officer.");
                return successfulApplications;
            }

            for (Application application : applicationRepo.loadApplications()) {
                if (application.getProject().getProjectID().equals(inChargeProject.getProjectID()) &&
                        application.getApplicationStatus() == ApplicantAppStatus.SUCCESSFUL &&
                        application.getWithdrawalStatus() != WithdrawalStatus.PENDING) {
                    successfulApplications.add(application);
                }
            }
        } catch (IOException e) {
            System.out.println("Error accessing data: " + e.getMessage());
        }
        return successfulApplications;
    }

    public void bookBTO(User user) {
        ApplicationController applicationController = new ApplicationController();
        ReceiptController receiptController = new ReceiptController();
        Scanner scanner = new Scanner(System.in);
        List<Application> successfulApplications = getSuccessfulApplicationsForOfficerProject(user);

        if (successfulApplications.isEmpty()) {
            System.out.println("No successful applications found for your project.");
            return;
        }

        System.out.println("\n======================= Successful Applications =======================");
        System.out.printf("%-15s %-20s %-10s %-20s %-12s %-15s\n",
                "Application ID", "Applicant Name", "Project ID", "Project Name", "Flat Type", "Status");
        System.out.println("----------------------------------------------------------------------");

        for (Application application : successfulApplications) {
            String applicationID = application.getApplicationID();
            String applicantName = application.getUser().getName();
            String projectID = application.getProject().getProjectID();
            String projectName = application.getProject().getProjectName();
            String flatType = application.getFlatType().toString();
            String applicationStatus = application.getApplicationStatus().toString();

            System.out.printf("%-15s %-20s %-10s %-20s %-12s %-15s\n",
                    applicationID, applicantName, projectID, projectName, flatType, applicationStatus);
        }

        System.out.print("Enter the Application ID to book: ");
        String applicationID = scanner.nextLine().trim();

        Application selectedApplication = successfulApplications.stream()
                .filter(app -> app.getApplicationID().equals(applicationID))
                .findFirst()
                .orElse(null);
        if (selectedApplication == null) {
            System.out.println("Invalid Application ID. Please try again.");
            return;
        }

        Project selectedProject = selectedApplication.getProject();
        FlatType selectedFlatType = selectedApplication.getFlatType();
        Map<FlatType, Integer> flatTypeUnits = selectedProject.getFlatTypeUnits();

        int currentUnits = flatTypeUnits.getOrDefault(selectedFlatType, 0);
        if (currentUnits > 0) {
            flatTypeUnits.put(selectedFlatType, currentUnits - 1);
            selectedProject.setFlatTypeUnits(flatTypeUnits);
            ProjectRepository.updateProjectInCSV(selectedProject);

            selectedApplication.setApplicationStatus(ApplicantAppStatus.BOOKED);
            ApplicationRepository.updateApplicationInCSV(selectedApplication);
            System.out.println("Booking successful! Application status updated to BOOKED.");
            receiptController.generateBookingReceipt(selectedApplication, user);
        } else {
            System.out.println("No units left to book for " + selectedFlatType);
        }
    }

}