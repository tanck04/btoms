package controller;

import enums.*;
import model.*;
import repository.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Controller class for HDB Manager operations in the BTO system.
 * Handles project management, application approval/rejection, officer registrations,
 * and report generation for HDB managers.
 * Implements the ViewProjectInterface to provide project viewing capabilities.
 */
public class HDBManagerController implements ViewProjectInterface {
    private final ProjectRepository projectRepository = new ProjectRepository();
    private final ManagerRepository managerRepository = new ManagerRepository();
    private final ApplicationRepository applicationRepository = new ApplicationRepository();

    /**
     * Retrieves a Manager by their ID from the repository.
     *
     * @param managerID The ID of the manager to retrieve
     * @return The Manager object if found, null otherwise
     */
    public Manager getManagerById(String managerID) {
        try {
            return managerRepository.findManagerById(managerID);
        } catch (IOException e) {
            System.out.println("Error retrieving project: " + e.getMessage());
            return null;
        }
    }

    /**
     * Gets the active project managed by the specified manager.
     * A project is considered active if its closing date is after the current date.
     *
     * @param manager The manager whose project we're looking for
     * @return The managed project if found and still active, null otherwise
     */
    public Project getManagedActiveProject(Manager manager) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        sdf.setLenient(false);
        Date currentDate = new Date();

        try {
            for (Project project : projectRepository.loadProjects()) {
                try {
                    String closingDateStr = project.getApplicationClosingDate();

                    if (closingDateStr == null || closingDateStr.isEmpty()) {
                        System.out.println("Skipping project " + project.getProjectID() + ": Missing closing date.");
                        continue;
                    }

                    Date projectClosingDate = sdf.parse(closingDateStr);

                    if (manager.getNRIC().equals(project.getManagerID()) && projectClosingDate.after(currentDate)) {
                        return project;
                    }
                } catch (java.text.ParseException e) {
                    System.out.println("Error parsing date for project " + project.getProjectID() + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading projects: " + e.getMessage());
        }
        return null;
    }

    /**
     * Allows HDB manager to approve or reject an applicant's application.
     * Displays pending applications for the manager's project and processes the approval or rejection.
     *
     * @param manager The HDB manager making the approval
     * @return true if approval was successful, false otherwise
     */
    public boolean approveOrRejectApplication(Manager manager) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Manager: " + manager.getNRIC());

        // Find the project managed by this HDB manager
        Project managedProject = null;
        managedProject = getManagedActiveProject(manager);

        if (managedProject == null) {
            System.out.println("Error: No project found for manager " + manager.getNRIC());
            return false;
        }

        System.out.println("Found project: " + managedProject.getProjectID() + " - " + managedProject.getProjectName());

        // Find all pending applications for this project
        List<Application> pendingApplications = getPendingApplicationsByProject(managedProject);

        if (pendingApplications.isEmpty()) {
            System.out.println("No pending applications to approve for project " + managedProject.getProjectID());
            return false;
        }

        // Display pending applications
        System.out.println("\n======== PENDING APPLICATIONS ========");
        System.out.println("+-----------------------------------------------+");
        System.out.printf("| %-15s | %-12s | %-20s | %-12s |\n",
                "APPLICATION ID", "NRIC", "NAME", "FLAT TYPE");
        System.out.println("+-----------------------------------------------+");

        for (Application application : pendingApplications) {
            User user = application.getUser();
            System.out.printf("| %-15s | %-12s | %-20s | %-12s |\n",
                    application.getApplicationID(),
                    user.getNRIC(),
                    user.getName(),
                    application.getFlatType());
        }
        System.out.println("+-----------------------------------------------+");

        // Get user choice
        System.out.print("\nEnter the ID of the application to approve or reject (or 0 to cancel): ");
        String choice = scanner.nextLine().trim();

        // Check if user wants to cancel
        if (choice.equals("0")) {
            System.out.println("Operation cancelled.");
            return false;
        }
        // Validate choice
        Application selectedApplication = null;
        try {
            selectedApplication = applicationRepository.findApplicationById(choice);
        } catch (IOException e) {
            System.out.println("Error finding application: " + e.getMessage());
            return false;
        }
        if (selectedApplication == null) {
            System.out.println("Invalid Application ID.");
            return false;
        }
        System.out.println("Enter 1 to approve or 0 to reject the application:");
        String actionInput = scanner.nextLine().trim();
        if (!actionInput.equals("0") && !actionInput.equals("1")) {
            System.out.println("Invalid input. Please enter 0 or 1.");
            return false;
        }
        if (actionInput.equals("0")) {
            // Reject the application
            selectedApplication.setApplicationStatus(ApplicantAppStatus.UNSUCCESSFUL);
            System.out.println("Application rejected successfully.");
        } else if (actionInput.equals("1")) {
            // Approve the application
            selectedApplication.setApplicationStatus(ApplicantAppStatus.SUCCESSFUL);
        }
        // Update the application status
        // Save changes to file
        ApplicationRepository.updateApplicationInCSV(selectedApplication);
        System.out.println("Application approved successfully.");
        return true;
    }

    /**
     * Gets all applications with PENDING status for a specific project.
     *
     * @param project The project to filter applications for
     * @return List of applications with pending status for the specified project
     */
    public List<Application> getPendingApplicationsByProject(Project project) {
        List<Application> pendingApplications = new ArrayList<>();

        // Check if project is null
        if (project == null) {
            System.out.println("Project is null - cannot search for applications");
            return pendingApplications;
        }

        String projectID = project.getProjectID();
        System.out.println("Looking for pending applications in project: " + projectID);

        // Find all applications for this project with PENDING status
        try {
            for (Application application : applicationRepository.loadApplications()) {
                if (projectID.equals(application.getProject().getProjectID()) &&
                        application.getApplicationStatus() == ApplicantAppStatus.PENDING &&
                        application.getWithdrawalStatus() == WithdrawalStatus.NULL) {

                    pendingApplications.add(application);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading applications: " + e.getMessage());
            System.out.println("Returning empty list of pending applications");
        }

        return pendingApplications;
    }

    /**
     * Lists projects based on filter criteria.
     * Projects can be filtered by neighborhood and whether they are managed by the current manager.
     *
     * @param manager The HDB manager viewing the projects
     * @param neighbourhoodFilter The neighborhood to filter by (can be null for no filter)
     * @param filterproject Flag to view projects managed by the manager ("Y") or all projects
     * @return List of projects matching the filter criteria
     */
    public List<Project> listProject(Manager manager, String neighbourhoodFilter, String filterproject) {
        try {
            return projectRepository.loadProjects().stream()
                    .filter(project ->
                            // Filter implementation
                            neighbourhoodFilter == null || neighbourhoodFilter.isEmpty() || project.getNeighborhood().equalsIgnoreCase(neighbourhoodFilter)
                    )
                    .filter(project ->
                            // Additional filter implementation
                            filterproject == null || filterproject.equalsIgnoreCase("N") || project.getManagerID().equals(manager.getNRIC())
                    )
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.out.println("Error loading projects: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Prints the list of projects in a formatted table.
     * Displays project details including ID, name, neighborhood, application dates,
     * visibility status, officer slots, manager ID, and flat type information.
     *
     * @param projects The list of projects to print
     */
    public void printProjectList(List<Project> projects) {
        // Assuming user is an instance of Manager
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
            // Main project row
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
            System.out.printf("| %-126s |\n", "   " + officerLine);

            // Flat Types header
            System.out.printf("| %-126s |\n", "   Flat Types:");

            // Flat Types rows
            for (Map.Entry<FlatType, Double> entry : project.getFlatTypePrices().entrySet()) {
                FlatType flatType = entry.getKey();
                Double price = entry.getValue();
                int units = project.getUnitsForFlatType(flatType);

                String flatTypeLine = String.format("    - %-12s: $%-10.2f (%-3d units available)",
                        flatType.toString(), price, units);
                System.out.printf("| %-126s |\n", flatTypeLine);
            }

            // End divider after each project
            System.out.println("+------------+----------------------+-----------------+--------------+--------------+------------+--------------+----------------+");
        }
    }

    /**
     * Implements the ViewProjectInterface method to display projects.
     * Allows the manager to view all projects and filter them by neighborhood or by projects they manage.
     *
     * @param user The user (HDB manager) viewing the projects
     */
    public void viewProject(User user) {
        Scanner scanner = new Scanner(System.in);
        Manager manager = (Manager) user;

        // Step 1: Show all projects
        List<Project> allProjects = listProject(manager, null, null);
        System.out.println("All Available Projects:");
        printProjectList(allProjects);

        // Step 2: Ask user if they want to filter
        System.out.print("\nWould you like to apply a filter? (yes/no): ");
        String response = scanner.nextLine().trim().toLowerCase();

        if (response.equals("yes")) {
            System.out.print("Enter neighbourhood to filter by (or leave blank): ");
            String neighbourhood = scanner.nextLine().trim();

            System.out.print("View project you are managing? (Y/N): ");
            String filterProject = scanner.nextLine().trim();

            List<Project> filteredProjects = listProject(manager, neighbourhood, filterProject);
            System.out.println("\nFiltered Projects:");
            printProjectList(filteredProjects);
        }
    }

    /**
     * Allows HDB manager to approve or reject withdrawal applications.
     * Displays withdrawal requests for the manager's project and processes the approval or rejection.
     *
     * @param user The HDB manager approving/rejecting the withdrawal
     */
    public void approveOrRejectWithdrawal(User user) {
        Manager manager = (Manager) user;
        ArrayList<Application> pendingWithdrawal = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        Project managedProject = null;
        Application selectedApp = null; // Added declaration for selectedApp

        managedProject = getManagedActiveProject(manager);

        if (managedProject == null) {
            System.out.println("Error: No project found for manager " + manager.getNRIC());
            return;
        }

        System.out.println("Found project: " + managedProject.getProjectID() + " - " + managedProject.getProjectName());
        System.out.println("Looking for pending withdrawal applications in project: " + managedProject.getProjectID());

        try {
            for (Application application : applicationRepository.loadApplications()) {
                if (application.getProject().getProjectID().equals(managedProject.getProjectID()) &&
                        application.getWithdrawalStatus() == WithdrawalStatus.PENDING) {
                    pendingWithdrawal.add(application);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading applications: " + e.getMessage());
            return;
        }

        if (pendingWithdrawal.isEmpty()) {
            System.out.println("No pending withdrawal applications found for project " + managedProject.getProjectID());
            System.out.println("Application approval process was not completed.");
            return;
        }

        System.out.println("\n====== PENDING WITHDRAWAL APPLICATIONS ======");
        for (Application application : pendingWithdrawal) {
            System.out.println("Application ID: " + application.getApplicationID());
            System.out.println("Applicant NRIC: " + application.getApplicant().getNRIC());
            System.out.println("Applicant Name: " + application.getApplicant().getName());
            System.out.println("Withdrawal Status: " + application.getWithdrawalStatus());
            System.out.println("Project ID: " + application.getProject().getProjectID());
            System.out.println("Project Name: " + application.getProject().getProjectName());
            System.out.println("Flat Type: " + application.getFlatType());
            System.out.println("Application Status: " + application.getApplicationStatus());
            System.out.println("--------------------------------------------------------------");
        }

        // Prompt for Application ID
        System.out.println("\nEnter the ID of the application to approve/reject (or 0 to cancel): ");
        String appIdInput = scanner.nextLine().trim();

        if (appIdInput.equals("0")) {
            System.out.println("Operation cancelled.");
            return;
        }

        // Find the application by ID
        try {
            selectedApp = applicationRepository.findApplicationById(appIdInput);
        } catch (Exception e) {
            System.out.println("Error finding application: " + e.getMessage());
            return;
        }

        if (selectedApp == null) {
            System.out.println("Invalid Application ID.");
            return;
        }

        // Prompt for approval or rejection
        System.out.println("Enter 1 to approve or 0 to reject the withdrawal:");
        String actionInput = scanner.nextLine().trim();

        if (!actionInput.equals("0") && !actionInput.equals("1")) {
            System.out.println("Invalid input. Please enter 0 or 1.");
            return;
        }

        if (actionInput.equals("1")) {
            selectedApp.setWithdrawalStatus(WithdrawalStatus.APPROVED);
            System.out.println("Withdrawal approved for Application ID: " + selectedApp.getApplicationID());
            // Save changes to CSV
            ApplicationRepository.updateApplicationInCSV(selectedApp);
        } else {
            selectedApp.setWithdrawalStatus(WithdrawalStatus.REJECTED);
            System.out.println("Withdrawal rejected for Application ID: " + selectedApp.getApplicationID());
            // Save changes to CSV
            ApplicationRepository.updateApplicationInCSV(selectedApp);
        }
    }

    /**
     * Allows HDB manager to review officer registration requests.
     * Displays registrations and allows filtering by project.
     *
     * @param user The HDB manager reviewing the registrations
     */
    public void reviewOfficerRegistration(User user) {
        Scanner scanner = new Scanner(System.in);
        Manager manager = (Manager) user;
        OfficerRegRepository officerRegRepository = new OfficerRegRepository();
        List<OfficerRegistration> registrations;
        try {
            registrations = officerRegRepository.loadAllOfficerReg();
        } catch (IOException e) {
            System.out.println("Error loading officer registrations: " + e.getMessage());
            return;
        }

        showRegistrations(registrations);

        System.out.println("Apply filter to view registrations? (yes/no): ");
        String response = scanner.nextLine().trim().toLowerCase();
        if (response.equals("yes")) {

            System.out.print("View registrations you are managing? (Y/N): ");
            String filterProject = scanner.nextLine().trim();

            List<OfficerRegistration> filteredRegistrations = registrations.stream()
                    .filter(registration -> {
                        // Filter implementation
                        if (filterProject.equalsIgnoreCase("Y")) {
                            return registration.getProject().getManagerID().equals(manager.getNRIC());
                        } else {
                            return true; // Show all if not filtering by managed projects
                        }
                    })
                    .collect(Collectors.toList());

            showRegistrations(filteredRegistrations);
        }
    }

    /**
     * Displays a formatted table of officer registrations.
     *
     * @param registrations The list of officer registrations to display
     */
    private void showRegistrations(List<OfficerRegistration> registrations) {
        System.out.println("+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+");
        System.out.println("|                                                                                 Officer Registrations                                                                                 |");
        System.out.println("+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+");
        System.out.printf("| %-15s | %-12s | %-20s | %-12s |\n",
                "REGISTRATION ID", "OFFICER NRIC", "OFFICER NAME", "STATUS");
        System.out.println("+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+");

        for (OfficerRegistration registration : registrations) {
            System.out.printf("| %-15s | %-12s | %-20s | %-12s |\n",
                    registration.getRegistrationId(),
                    registration.getOfficer().getNRIC(),
                    registration.getOfficer().getName(),
                    registration.getStatus());
        }
        System.out.println("+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+");
    }

    /**
     * Allows HDB manager to approve or reject officer registration requests.
     * Verifies officer slots are available before approval and updates the project with new officers.
     *
     * @param user The HDB manager approving/rejecting the registration
     */
    public void approveOrRejectOfficerRegistration(User user) {
        Manager manager = (Manager) user;
        Scanner scanner = new Scanner(System.in);
        Project managedProject;

        managedProject = getManagedActiveProject(manager);

        if (managedProject == null) {
            System.out.println("Error: No project found for manager " + manager.getNRIC());
            return;
        }
        System.out.println("Found project: " + managedProject.getProjectID() + " - " + managedProject.getProjectName());

        // Get pending officer registrations for this project
        List<OfficerRegistration> pendingRegistrations = OfficerRegRepository.getPendingByProject(managedProject.getProjectID());

        if (pendingRegistrations.isEmpty()) {
            System.out.println("No pending officer registrations found for project " + managedProject.getProjectID());
            return;
        }

        // Display pending registrations
        System.out.println("\n====== PENDING OFFICER REGISTRATIONS ======");
        System.out.println("ID\tREGISTRATION ID\tOFFICER NRIC\tOFFICER NAME\tPROJECT ID");
        System.out.println("--------------------------------------------------------------");

        int count = 1;
        for (OfficerRegistration reg : pendingRegistrations) {
            System.out.printf("%d\t%s\t%s\t%s\t%s\n",
                    count++,
                    reg.getRegistrationId(),
                    reg.getOfficer().getNRIC(),
                    reg.getOfficer().getName(),
                    reg.getProject().getProjectID());
        }

        // Get user choice
        System.out.print("\nEnter the ID of the registration to approve/reject (or 0 to cancel): ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        // Check if user wants to cancel
        if (choice == 0) {
            System.out.println("Operation cancelled.");
            return;
        }

        // Validate choice
        if (choice < 1 || choice > pendingRegistrations.size()) {
            System.out.println("Invalid choice. Please select a valid ID.");
            return;
        }

        // Get the selected registration
        OfficerRegistration selectedReg = pendingRegistrations.get(choice - 1);

        // Prompt for approval or rejection
        System.out.print("Enter 1 to APPROVE or 0 to REJECT this officer registration: ");
        int action;
        try {
            action = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter 0 or 1.");
            return;
        }

        // Process the action
        if (action == 1) {
            // Check if there are available officer slots
            if (managedProject.getOfficerSlot() > 10) {
                System.out.println("Cannot approve: Maximum number of officers already reached for this project.");
                return;
            }
            selectedReg.setStatus(OfficerRegStatus.APPROVED);
            addOfficerToProject(selectedReg.getProject(), selectedReg.getOfficer());
            OfficerRegRepository.updateOfficerRegInCSV(selectedReg);
            System.out.println("Registration approved successfully.");
        } else if (action == 0) {
            selectedReg.setStatus(OfficerRegStatus.REJECTED);
            OfficerRegRepository.updateOfficerRegInCSV(selectedReg);
            System.out.println("Registration rejected.");
        } else {
            System.out.println("Invalid choice. Please enter 0 or 1.");
        }
    }

    /**
     * Adds an officer to a project by updating the officer IDs list and officer slot count.
     * Updates the project in the repository after modification.
     *
     * @param project The project to add the officer to
     * @param officer The officer to add to the project
     */
    public void addOfficerToProject(Project project, Officer officer) {
        if (!project.getOfficerIDs().contains(officer.getNRIC())) {
            List<String> newOfficerIDs = new ArrayList<>(project.getOfficerIDs());
            // Add the officer to the project
            newOfficerIDs.add(officer.getNRIC());
            project.setOfficerIDs(newOfficerIDs);
            project.setOfficerSlot(project.getOfficerSlot() + 1);

            // Save the updated project
            ProjectRepository.updateProjectInCSV(project);

            System.out.println("✅ Officer " + officer.getNRIC() + " added to project " + project.getProjectID());
        } else {
            System.out.println("Officer " + officer.getNRIC() + " is already assigned to this project");
        }
    }

    /**
     * Allows HDB manager to generate and view various reports about applications and projects.
     * Provides options for application status reports, booked applications reports, and project summary reports.
     *
     * @param user The HDB manager generating the report
     */
    public void generateReports(User user) {
        Scanner scanner = new Scanner(System.in);
        ReportController reportController = new ReportController();

        System.out.println("\n+---------------------------------------------------+");
        System.out.println("|              HDB REPORT GENERATION                |");
        System.out.println("+---------------------------------------------------+");
        System.out.println("| 1. Application Status Report                      |");
        System.out.println("| 2. Booked Applications Report                     |");
        System.out.println("| 3. Project Summary Report                         |");
        System.out.println("| 0. Back to Main Menu                              |");
        System.out.println("+---------------------------------------------------+");
        System.out.print("Enter your choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());

            switch (choice) {
                case 1:
                    reportController.generateReport("APPLICATION_STATUS");
                    break;
                case 2:
                    reportController.generateReport("BOOKED_APPLICATIONS");
                    break;
                case 3:
                    reportController.generateReport("PROJECT_SUMMARY");
                    break;
                case 0:
                    System.out.println("Returning to main menu...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }

            // Prompt to continue or generate another report
            System.out.print("\nGenerate another report? (Y/N): ");
            String response = scanner.nextLine().trim().toUpperCase();

            if (response.equals("Y")) {
                generateReports(user);
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        } catch (Exception e) {
            System.out.println("Error generating report: " + e.getMessage());
        }
    }
}