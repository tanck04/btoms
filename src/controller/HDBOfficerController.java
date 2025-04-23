package controller;

import enums.*;
import model.*;
import repository.ApplicationRepository;
import repository.OfficerRegRepository;
import repository.ProjectRepository;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller class for HDB Officer operations in the BTO application system.
 * Extends ApplicantController to inherit applicant functionality while implementing
 * ViewProjectInterface for project viewing capabilities. This class provides
 * officer-specific functionality such as project management, application bookings,
 * and handling successful applications.
 */
public class HDBOfficerController extends ApplicantController implements ViewProjectInterface {
    private final ProjectRepository projectRepository = new ProjectRepository();
    private final ApplicantController applicantController = new ApplicantController();

    /**
     * Displays available projects and provides filtering options.
     * Implements the ViewProjectInterface method to allow officers to view and filter
     * BTO projects based on neighborhood and flat type criteria.
     *
     * @param user The officer user viewing the projects
     */
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

    /**
     * Retrieves a filtered list of projects based on specified criteria.
     * Filters projects by officer access, neighborhood, and flat type.
     *
     * @param officer The officer user for whom to list projects
     * @param neighbourhoodFilter The neighborhood to filter by (null for no filter)
     * @param flatTypeFilter The flat type to filter by (null for no filter)
     * @return A filtered list of Project objects matching the criteria
     */
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

    /**
     * Displays a formatted table of projects with their details.
     * Shows project ID, name, neighborhood, application dates, visibility,
     * officer slots, manager ID, officers assigned, and flat type information.
     *
     * @param projects The list of projects to display
     * @param flatTypeFilter Optional filter to display only specific flat types (null for all)
     */
    public void printProjectList(List<Project> projects, FlatType flatTypeFilter){
        // Assuming user is an instance of Officer
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

    /**
     * Delegates to the applicant controller to submit an application.
     * Allows officers to submit applications using the inherited functionality.
     *
     * @param user The user (officer) submitting the application
     */
    public void submitApplication(User user) {
        applicantController.submitApplication(user);
    }

    /**
     * Checks if an officer has registered to become an officer for a specific project.
     * Verifies the registration status by checking the officer registration repository.
     *
     * @param officer The officer to check
     * @param project The project to check against
     * @return true if the officer is registered for the project, false otherwise
     */
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
     * Finds successful applications for the project the officer is in charge of.
     * Retrieves applications that are marked as SUCCESSFUL and not pending withdrawal.
     *
     * @param user The officer user
     * @return List of successful applications for the officer's assigned project
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

    /**
     * Allows an officer to book a BTO flat for successful applicants.
     * Displays successful applications, allows selection, verifies unit availability,
     * updates application status to BOOKED, and generates a receipt.
     *
     * @param user The officer user performing the booking
     */
    public void bookBTO(User user) {
        ApplicationController applicationController = new ApplicationController();
        ReceiptController receiptController = new ReceiptController();
        Scanner scanner = new Scanner(System.in);
        List<Application> successfulApplications = getSuccessfulApplicationsForOfficerProject(user);

        if (successfulApplications.isEmpty()) {
            System.out.println("No successful applications found for your project.");
            return;
        }

        System.out.println("\n=================================== Successful Applications =======================================");
        System.out.printf("%-15s %-20s %-16s %-20s %-12s %-15s\n",
                "Application ID", "Applicant Name", "Project ID", "Project Name", "Flat Type", "Status");
        System.out.println("---------------------------------------------------------------------------------------------------");

        for (Application application : successfulApplications) {
            String applicationID = application.getApplicationID();
            String applicantName = application.getUser().getName();
            String projectID = application.getProject().getProjectID();
            String projectName = application.getProject().getProjectName();
            String flatType = application.getFlatType().toString();
            String applicationStatus = application.getApplicationStatus().toString();

            System.out.printf("%-15s %-20s %-16s %-20s %-12s %-15s\n",
                    applicationID, applicantName, projectID, projectName, flatType, applicationStatus);
        }
        System.out.println("===================================================================================================");
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