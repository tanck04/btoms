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

public class HDBManagerController{
    private final ApplicantRepository applicantRepository = new ApplicantRepository();
    private final ProjectRepository projectRepository = new ProjectRepository();
    private final ManagerRepository managerRepository = new ManagerRepository();
    private final ApplicationRepository applicationRepository = new ApplicationRepository();

    /**
     * Gets the active project managed by the specified manager
     * @param manager The manager whose project we're looking for
     * @return The managed project if found and still active (closing date after current date), null otherwise
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
     * Allows HDB manager to approve an applicant's application
     *
     * @param manager The HDB manager making the approval
     * @return true if approval was successful, false otherwise
     */

    public boolean approveApplication(Manager manager) {
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
        System.out.println("ID\tAPPLICATION ID\t\tNRIC\t\tNAME\t\tFLAT TYPE");
        System.out.println("------------------------------------------------------------");

        int count = 1;
        for (Application application : pendingApplications) {
            User user = application.getUser();
            System.out.printf("%d\t%s\t%s\t%s\t%s\n",
                    count++,
                    application.getApplicationID(),
                    user.getNRIC(),
                    user.getName(),
                    application.getFlatType());
        }

        // Get user choice
        System.out.print("\nEnter the ID of the application to approve (or 0 to cancel): ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return false;
        }

        // Check if user wants to cancel
        if (choice == 0) {
            System.out.println("Operation cancelled.");
            return false;
        }

        // Validate choice
        if (choice < 1 || choice > pendingApplications.size()) {
            System.out.println("Invalid choice. Please select a valid ID.");
            return false;
        }

        // Get the selected application
        Application selectedApplication = pendingApplications.get(choice - 1);

        // Update application status
        selectedApplication.setApplicationStatus(ApplicantAppStatus.SUCCESSFUL);

        // Save changes to file
        ApplicationRepository.updateApplicationInCSV(selectedApplication);
        System.out.println("Application approved successfully.");
        return true;
    }

        // Rest of the method remains the same...

    /**
    * Gets all applications with PENDING status for a specific project
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
            // Log could be added here if needed
            // The method will return the empty pendingApplications list created earlier
        }

        return pendingApplications;
    }
    /**
     * Allows HDB manager to view all projects
     * @param manager The HDB manager viewing the projects
     * @return List of projects that the manager can view
     */
    public List<Project> listProject(Manager manager, String neighbourhoodFilter, String filterproject) {
        try {
            return projectRepository.loadProjects().stream()
                    .filter(project ->
                            neighbourhoodFilter == null || neighbourhoodFilter.isEmpty() ||
                                    project.getNeighborhood().equalsIgnoreCase(neighbourhoodFilter)
                    )
                    .filter(project ->
                            filterproject == null || filterproject.equalsIgnoreCase("N") ||
                                    project.getManagerID().equals(manager.getNRIC())
                    )
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.out.println("Error loading projects: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    public Manager getManagerById(String managerID) {
        try {
            return managerRepository.findManagerById(managerID);
        } catch (IOException e) {
            System.out.println("Error retrieving project: " + e.getMessage());
            return null;
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

    public void approveOrRejectWithdrawal(User user) {
        Manager manager = (Manager) user;
        ArrayList<Application> pendingWithdrawal = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        Project managedProject = null;
        Application selectedApp = null; // Added declaration for selectedApp

        managedProject = getManagedActiveProject(manager);

        // Rest of the method remains unchanged
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

    public void approveOrRejectOfficerRegistration(User user) {
        Manager manager = (Manager) user;
        Scanner scanner = new Scanner(System.in);
        Project managedProject;

        managedProject = getManagedActiveProject(manager);

        // Rest of the method remains unchanged
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
            approveRegistration(selectedReg);
            System.out.println("Registration approved successfully.");
        } else if (action == 0) {
            rejectRegistration(selectedReg);
            System.out.println("Registration rejected.");
        } else {
            System.out.println("Invalid choice. Please enter 0 or 1.");
        }
    }

    public void approveRegistration(OfficerRegistration reg) {
        reg.setStatus(OfficerRegStatus.APPROVED);
        addOfficerToProject(reg.getProject(), reg.getOfficer());
        OfficerRegRepository.updateOfficerRegInCSV(reg);
    }

    public void rejectRegistration(OfficerRegistration reg) {
        reg.setStatus(OfficerRegStatus.REJECTED);
        OfficerRegRepository.updateOfficerRegInCSV(reg);
    }

    public void addOfficerToProject(Project project, Officer officer) {
        if (!project.getOfficerIDs().contains(officer.getNRIC())) {
            List<String> newOfficerIDs = new ArrayList<>(project.getOfficerIDs());
            // Add the officer to the project
            newOfficerIDs.add(officer.getNRIC());
            project.setOfficerIDs(newOfficerIDs);
            project.setOfficerSlot(project.getOfficerSlot() + 1);

            // Save the updated project
            projectRepository.updateProjectInCSV(project);

            System.out.println("✅ Officer " + officer.getNRIC() + " added to project " + project.getProjectID());
        } else {
            System.out.println("Officer " + officer.getNRIC() + " is already assigned to this project");
        }
    }
}