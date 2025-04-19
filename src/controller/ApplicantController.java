package controller;

import enums.ApplicantAppStatus;
import enums.FlatType;
import enums.Visibility;
import model.*;
import enums.MaritalStatus;
import repository.ApplicantRepository;
import repository.ApplicationRepository;
import repository.ProjectRepository;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ApplicantController{
    private final ApplicantRepository applicantRepository = new ApplicantRepository();
    private final ApplicationRepository applicationRepository = new ApplicationRepository();
    private String lastNeighbourhoodFilter = null;
    private FlatType lastFlatTypeFilter = null;
    // Method to check application status
    public void checkApplicationStatus(User user) {
        try {
            List<Application> applications = applicationRepository.loadApplications();
            boolean found = false;

            for (Application application : applications) {
                if (application.getUser().getNRIC().equals(user.getNRIC())) {
                    found = true;
                    System.out.println("Application ID: " + application.getApplicationID());
                    System.out.println("Project ID: " + application.getProject().getProjectID());
                    System.out.println("Flat Type: " + application.getFlatType());
                    System.out.println("Application Status: " + application.getApplicationStatus());
                    System.out.println("Withdrawal Status: " + application.getWithdrawalStatus());
                    System.out.println();

                    // Offer to show receipt if application is BOOKED
                    if (application.getApplicationStatus() == ApplicantAppStatus.BOOKED) {
                        Scanner scanner = new Scanner(System.in);
                        System.out.print("Would you like to view your booking receipt for Application ID "
                                + application.getApplicationID() + "? (Y/N): ");
                        String choice = scanner.nextLine().trim().toUpperCase();
                        if (choice.equals("Y")) {
                            ReceiptController receiptController = new ReceiptController();
                            receiptController.viewReceiptByApplicationID(application.getApplicationID());
                        }
                    }
                }
            }

            if (!found) {
                System.out.println("No applications found for your NRIC.");
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
        ProjectRepository projectRepository = new ProjectRepository();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy"); // Adjust the format to your actual date format
            Date now = new Date();

            return projectRepository.loadProjects().stream()
                    .filter(project -> {
                        try {
                            Date closingDate = sdf.parse(project.getApplicationClosingDate());
                            return closingDate.after(now); // Only include projects with future closing dates
                        } catch (ParseException e) {
                            System.out.println("Invalid date format for project: " + project.getProjectID());
                            return false; // Skip invalid dates
                        }
                    })
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
                    .filter(project ->
                            // Exclude if applicant's NRIC is in officerIDs list
                            !project.getOfficerIDs().contains(applicant.getNRIC())
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
                if (application.getProject().getProjectID().equals(project.getProjectID()) &&
                    application.getUser().getNRIC().equals(user.getNRIC())) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading applications: " + e.getMessage());
        }
        return false;
    }

    private void printProjectList(List<Project> projects, FlatType flatTypeFilter) {
        System.out.println("+------------+----------------------+----------------+----------------------+------------------------+");
        System.out.println("|                                            Project List                                            |");
        System.out.println("+------------+----------------------+----------------+----------------------+------------------------+");
        System.out.printf("| %-10s | %-20s | %-14s | %-20s | %-22s |\n", "Project ID", "Project Name", "Neighbourhood", "App. Start", "App. End");
        System.out.println("+------------+----------------------+----------------+----------------------+------------------------+");

        for (Project project : projects) {
            System.out.printf("| %-10s | %-20s | %-14s | %-20s | %-22s |\n",
                    project.getProjectID(),
                    project.getProjectName(),
                    project.getNeighborhood(),
                    project.getApplicationOpeningDate(),
                    project.getApplicationClosingDate()
            );
            System.out.println("+------------+----------------------+----------------+----------------------+------------------------+");
            System.out.println("| Flat Types:                                                                                        |");
            if (flatTypeFilter != null) {
                // Show only filtered flat type
                Double price = project.getFlatTypePrices().get(flatTypeFilter);
                int units = project.getUnitsForFlatType(flatTypeFilter);
                if (price != null) {
                    System.out.printf("|    - %-12s: $%-13.2f (%3d units available)%44s|\n",
                            flatTypeFilter.toString(), price, units, "");
                } else {
                    System.out.printf("|    - No data available for selected flat type%47s|\n", "");
                }
            } else {
                // Show all flat types
                for (Map.Entry<FlatType, Double> entry : project.getFlatTypePrices().entrySet()) {
                    FlatType flatType = entry.getKey();
                    Double price = entry.getValue();
                    int units = project.getUnitsForFlatType(flatType);
                    System.out.printf("|    - %-12s: $%-13.2f (%3d units available)%44s|\n",
                            flatType.toString(), price, units, "");
                }
            }

            System.out.println("+------------+----------------------+----------------+----------------------+------------------------+");
        }
    }


    public void viewProject(User user) {
        Scanner scanner = new Scanner(System.in);
        Applicant applicant = (Applicant) user;
        MaritalStatus maritalStatus = applicant.getMaritalStatus();
        int age = user.getAge();
        // Reset filters initially
        if (maritalStatus == MaritalStatus.SINGLE && age < 35) {
            System.out.println("\nYou are not eligible to view BTO projects. Singles must be at least 35 years old.");
            return;
        }else if (maritalStatus == MaritalStatus.MARRIED && age < 21) {
            System.out.println("\nYou are not eligible to view BTO projects. Applicant must be at least 21 years old.");
            return;
        }
        lastFlatTypeFilter = (maritalStatus == MaritalStatus.SINGLE) ? FlatType.TWO_ROOMS : null;
        lastNeighbourhoodFilter = null;

        // Step 1: Show all projects with initial filters
        List<Project> allProjects = listProject(applicant, lastNeighbourhoodFilter, lastFlatTypeFilter);
        System.out.println("All Available Projects:");
        printProjectList(allProjects, lastFlatTypeFilter);

        // Step 2: Ask user if they want to filter
        System.out.print("\nWould you like to apply a filter? (yes/no): ");
        String response = scanner.nextLine().trim().toLowerCase();

        if (response.equals("yes")) {
            System.out.print("Enter neighbourhood to filter by (or leave blank): ");
            String neighbourhood = scanner.nextLine().trim();
            lastNeighbourhoodFilter = neighbourhood.isEmpty() ? null : neighbourhood;

            List<Project> filteredProjects = listProject(applicant, lastNeighbourhoodFilter, lastFlatTypeFilter);
            System.out.println("\nFiltered Projects:");
            printProjectList(filteredProjects, lastFlatTypeFilter);
        }
    }

    public void submitApplication(User user) {
        FlatType selectedFlatType;
        Scanner scanner = new Scanner(System.in);
        try {
            // First, get the logged in applicant
            Applicant applicant = (Applicant) user;
            MaritalStatus maritalStatus = applicant.getMaritalStatus();

            // check eligibility
            if (user.getAge()<35 && maritalStatus == MaritalStatus.SINGLE) {
                System.out.println("You are not eligible to apply for a flat.");
                return;
            }
            if (user.getAge()<21 && maritalStatus == MaritalStatus.MARRIED) {
                System.out.println("You are not eligible to apply for a flat. Applicant needs to be at least 21 years old.");
                return;
            }
            // Use ApplicationController for submission
            ApplicationController applicationController = new ApplicationController();

            // If no filters were previously set, initialize them
            if (lastFlatTypeFilter == null) {
                lastFlatTypeFilter = maritalStatus == MaritalStatus.SINGLE ? FlatType.TWO_ROOMS : null;
            }

            // Show currently applied filters
            System.out.println("\nCurrently applied filters:");
            System.out.println("Neighbourhood: " + (lastNeighbourhoodFilter == null ? "None" : lastNeighbourhoodFilter));
            System.out.println("Flat Type: " + (lastFlatTypeFilter == null ? "All" : lastFlatTypeFilter));
            List<Project> availableProjects = listProject(applicant, lastNeighbourhoodFilter, lastFlatTypeFilter);
            printProjectList(availableProjects, lastFlatTypeFilter);

            System.out.print("Do you want to apply/change the filters? (yes/no): ");
            String changeFilters = scanner.nextLine().trim().toLowerCase();

            if (changeFilters.equals("yes")) {
                System.out.print("Enter neighbourhood to filter by (or leave blank): ");
                String neighbourhood = scanner.nextLine().trim();
                lastNeighbourhoodFilter = neighbourhood.isEmpty() ? null : neighbourhood;
            }

            // Use the filters to get available projects
            List<Project> filterProjectsAgain = listProject(applicant, lastNeighbourhoodFilter, lastFlatTypeFilter);

            if (filterProjectsAgain.isEmpty()) {
                System.out.println("No projects available with the selected filters.");
                return;
            }

            printProjectList(filterProjectsAgain, lastFlatTypeFilter);

            // Get project selection
            System.out.print("\nEnter Project ID to apply for: ");
            String projectID = scanner.nextLine().trim();

            // Find the project from our filtered list
            Project selectedProject = availableProjects.stream()
                    .filter(p -> p.getProjectID().equals(projectID))
                    .findFirst()
                    .orElse(null);

            if (selectedProject == null) {
                System.out.println("Invalid project ID or project not available with current filters.");
                return;
            }

            // Display available flat types for the selected project
            System.out.println("\n====== Available Flat Types ======");
            Map<FlatType, Integer> flatTypes = selectedProject.getFlatTypeUnits();

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
