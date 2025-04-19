package controller;

import model.Project;
import model.Application;
import enums.ApplicantAppStatus;
import enums.FlatType;
import enums.WithdrawalStatus;
import repository.ApplicationRepository;
import repository.ProjectRepository;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ReportController {
    private final ApplicationRepository applicationRepository;
    private final ProjectRepository projectRepository;

    public ReportController() {
        this.applicationRepository = new ApplicationRepository();
        this.projectRepository = new ProjectRepository();
    }

    public void generateReport(String reportType) {
        Scanner scanner = new Scanner(System.in);

        try {
            switch (reportType.toUpperCase()) {
                case "APPLICATION_STATUS":
                    generateApplicationStatusReport(scanner);
                    break;
                case "BOOKED_APPLICATIONS":
                    generateBookedApplicationsReport(scanner);
                    break;
                case "PROJECT_SUMMARY":
                    generateProjectSummaryReport(scanner);
                    break;
                default:
                    System.out.println("Invalid report type. Available reports: APPLICATION_STATUS, BOOKED_APPLICATIONS, PROJECT_SUMMARY");
            }
        } catch (IOException e) {
            System.out.println("Error generating report: " + e.getMessage());
        }
    }

    private void generateApplicationStatusReport(Scanner scanner) throws IOException {
        // Get all applications
        List<Application> applications = applicationRepository.loadApplications();

        // Filter options
        System.out.println("\n=== APPLICATION STATUS REPORT FILTER OPTIONS ===");
        System.out.println("1. Filter by Project");
        System.out.println("2. Filter by Application Status");
        System.out.println("3. Filter by Withdrawal Status");
        System.out.println("4. No filter (show all)");
        System.out.print("Enter your choice: ");

        int filterChoice = Integer.parseInt(scanner.nextLine());
        List<Application> filteredApplications = new ArrayList<>(applications);

        switch (filterChoice) {
            case 1:
                System.out.print("Enter Project ID: ");
                String projectId = scanner.nextLine();
                filteredApplications = applications.stream()
                    .filter(app -> app.getProject().getProjectID().equals(projectId))
                    .collect(Collectors.toList());
                break;
            case 2:
                System.out.println("Select Application Status:");
                System.out.println("1. PROCESSING");
                System.out.println("2. SUCCESSFUL");
                System.out.println("3. UNSUCCESSFUL");
                System.out.println("4. BOOKED");
                int statusChoice = Integer.parseInt(scanner.nextLine());

                ApplicantAppStatus selectedStatus = null;
                switch (statusChoice) {
                    case 1: selectedStatus = ApplicantAppStatus.PENDING; break;
                    case 2: selectedStatus = ApplicantAppStatus.SUCCESSFUL; break;
                    case 3: selectedStatus = ApplicantAppStatus.UNSUCCESSFUL; break;
                    case 4: selectedStatus = ApplicantAppStatus.BOOKED; break;
                }

                if (selectedStatus != null) {
                    ApplicantAppStatus finalSelectedStatus = selectedStatus;
                    filteredApplications = applications.stream()
                        .filter(app -> app.getApplicationStatus() == finalSelectedStatus)
                        .collect(Collectors.toList());
                }
                break;
            case 3:
                System.out.println("Select Withdrawal Status:");
                System.out.println("1. NIL");
                System.out.println("2. PENDING");
                System.out.println("3. APPROVED");
                System.out.println("4. REJECTED");
                int withdrawalChoice = Integer.parseInt(scanner.nextLine());

                WithdrawalStatus selectedWithdrawalStatus = null;
                switch (withdrawalChoice) {
                    case 1: selectedWithdrawalStatus = WithdrawalStatus.NULL; break;
                    case 2: selectedWithdrawalStatus = WithdrawalStatus.PENDING; break;
                    case 3: selectedWithdrawalStatus = WithdrawalStatus.APPROVED; break;
                    case 4: selectedWithdrawalStatus = WithdrawalStatus.REJECTED; break;
                }

                if (selectedWithdrawalStatus != null) {
                    WithdrawalStatus finalSelectedWithdrawalStatus = selectedWithdrawalStatus;
                    filteredApplications = applications.stream()
                        .filter(app -> app.getWithdrawalStatus() == finalSelectedWithdrawalStatus)
                        .collect(Collectors.toList());
                }
                break;
            case 4:
                // No filter, use all applications
                break;
            default:
                System.out.println("Invalid choice, showing all applications");
        }

        // Print report header
        System.out.println("\n+----------------------------------------------------------------------------------------------------------+");
        System.out.println("|                                     APPLICATION STATUS REPORT                                            |");
        System.out.println("+----------------------------------------------------------------------------------------------------------+");
        System.out.println("| Generated on: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "                                                                        |");
        System.out.println("+----------------------------------------------------------------------------------------------------------+");
        System.out.printf("| %-15s | %-15s | %-20s | %-15s | %-12s | %-12s |\n",
                "APP ID", "APPLICANT", "PROJECT", "FLAT TYPE", "APP STATUS", "WITHDRAWAL");
        System.out.println("+----------------------------------------------------------------------------------------------------------+");

        // Print applications
        for (Application app : filteredApplications) {
            System.out.printf("| %-15s | %-15s | %-20s | %-15s | %-12s | %-12s |\n",
                    app.getApplicationID(),
                    app.getUser().getName(),
                    app.getProject().getProjectName(),
                    app.getFlatType(),
                    app.getApplicationStatus(),
                    app.getWithdrawalStatus());
        }

        System.out.println("+----------------------------------------------------------------------------------------------------------+");
        System.out.println("| Total Applications: " + filteredApplications.size() + "                                                                                    |");
        System.out.println("+----------------------------------------------------------------------------------------------------------+");
    }

    private void generateBookedApplicationsReport(Scanner scanner) throws IOException {
        // Get all applications
        List<Application> applications = applicationRepository.loadApplications();

        // Get only booked applications
        List<Application> bookedApplications = applications.stream()
            .filter(app -> app.getApplicationStatus() == ApplicantAppStatus.BOOKED)
            .collect(Collectors.toList());

        // Filter options
        System.out.println("\n=== BOOKED APPLICATIONS REPORT FILTER OPTIONS ===");
        System.out.println("1. Filter by Project");
        System.out.println("2. Filter by Flat Type");
        System.out.println("3. No filter (show all booked)");
        System.out.print("Enter your choice: ");

        int filterChoice = Integer.parseInt(scanner.nextLine());
        List<Application> filteredBookedApps = new ArrayList<>(bookedApplications);

        switch (filterChoice) {
            case 1:
                System.out.print("Enter Project ID: ");
                String projectId = scanner.nextLine();
                filteredBookedApps = bookedApplications.stream()
                    .filter(app -> app.getProject().getProjectID().equals(projectId))
                    .collect(Collectors.toList());
                break;
            case 2:
                System.out.println("Select Flat Type:");
                System.out.println("1. TWO_ROOMS");
                System.out.println("2. THREE_ROOMS");
                int flatTypeChoice = Integer.parseInt(scanner.nextLine());

                FlatType selectedFlatType = switch (flatTypeChoice) {
                    case 1 -> FlatType.TWO_ROOMS;
                    case 2 -> FlatType.THREE_ROOMS;
                    default -> null;
                };

                if (selectedFlatType != null) {
                    FlatType finalSelectedFlatType = selectedFlatType;
                    filteredBookedApps = bookedApplications.stream()
                        .filter(app -> app.getFlatType() == finalSelectedFlatType)
                        .collect(Collectors.toList());
                }
                break;
            case 3:
                // No filter, use all booked applications
                break;
            default:
                System.out.println("Invalid choice, showing all booked applications");
        }

        // Print report header
        System.out.println("\n+-----------------------------------------------------------------------------------------------------------------+");
        System.out.println("|                                                BOOKED APPLICATIONS REPORT                                       |");
        System.out.println("+-----------------------------------------------------------------------------------------------------------------+");
        System.out.printf("| Generated on: %-98s|\n", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        System.out.println("+-----------------------------------------------------------------------------------------------------------------+");

        for (int i = 0; i < filteredBookedApps.size(); i++) {
            Application app = filteredBookedApps.get(i);

            System.out.printf("| Application #%d: %-96s|\n", (i + 1), app.getApplicationID());
            System.out.println("+-----------------------------------------------------------------------------------------------------------------+");

            System.out.println("| APPLICANT DETAILS:                                                                                              |");
            System.out.printf("| Name       : %-25s NRIC           : %-56s|\n", app.getUser().getName(), app.getUser().getNRIC());
            System.out.printf("| Age        : %-25d Marital Status : %-56s|\n", app.getUser().getAge(), app.getUser().getMaritalStatus());

            System.out.println("+-----------------------------------------------------------------------------------------------------------------+");
            System.out.println("| FLAT BOOKING DETAILS:                                                                                           |");
            System.out.printf("| Project ID : %-25s Project Name   : %-25s Neighborhood: %-16s|\n",
                    app.getProject().getProjectID(),
                    app.getProject().getProjectName(),
                    app.getProject().getNeighborhood());
            System.out.printf("| Flat Type  : %-25s Price          : $%-55.2f|\n",
                    app.getFlatType(),
                    app.getProject().getFlatTypePrices().get(app.getFlatType()));

            System.out.println("+-----------------------------------------------------------------------------------------------------------------+");
        }
        System.out.println("+-----------------------------------------------------------------------------------------------------------------+");
        System.out.printf("| Total Booked Applications: %-85d|\n", filteredBookedApps.size());
        System.out.println("+-----------------------------------------------------------------------------------------------------------------+");
    }

    private void generateProjectSummaryReport(Scanner scanner) throws IOException {
        // Get all projects
        List<Project> projects = projectRepository.loadProjects();
        List<Application> applications = applicationRepository.loadApplications();

        // Filter options
        System.out.println("\n=== PROJECT SUMMARY REPORT FILTER OPTIONS ===");
        System.out.println("1. Filter by Project ID");
        System.out.println("2. Filter by Neighborhood");
        System.out.println("3. No filter (show all projects)");
        System.out.print("Enter your choice: ");

        int filterChoice = Integer.parseInt(scanner.nextLine());
        List<Project> filteredProjects = new ArrayList<>(projects);

        switch (filterChoice) {
            case 1:
                System.out.print("Enter Project ID: ");
                String projectId = scanner.nextLine();
                filteredProjects = projects.stream()
                    .filter(project -> project.getProjectID().equals(projectId))
                    .collect(Collectors.toList());
                break;
            case 2:
                System.out.print("Enter Neighborhood: ");
                String neighborhood = scanner.nextLine();
                filteredProjects = projects.stream()
                    .filter(project -> project.getNeighborhood().equalsIgnoreCase(neighborhood))
                    .collect(Collectors.toList());
                break;
            case 3:
                // No filter, use all projects
                break;
            default:
                System.out.println("Invalid choice, showing all projects");
        }

        // Print report header
        System.out.println("\n+------------------------------------------------------------------------------------+");
        System.out.println("|                               PROJECT SUMMARY REPORT                               |");
        System.out.println("+------------------------------------------------------------------------------------+");
        System.out.printf("| Generated on: %-69s|\n", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        System.out.println("+------------------------------------------------------------------------------------+");

        for (Project project : filteredProjects) {
            System.out.println("+------------------------------------------------------------------------------------+");
            System.out.printf("| PROJECT: %-74s|\n", project.getProjectName() + " (ID: " + project.getProjectID() + ")");
            System.out.printf("| Location: %-73s|\n", project.getNeighborhood());
            System.out.println("+------------------------------------------------------------------------------------+");

            System.out.println("| FLAT INVENTORY:                                                                    |");
            System.out.println("| TYPE        | TOTAL UNITS     | AVAILABLE       | BOOKED          | PRICE          |");
            System.out.println("+------------------------------------------------------------------------------------+");

            for (FlatType flatType : FlatType.values()) {
                int totalUnits = project.getFlatTypeUnits().getOrDefault(flatType, 0);
                double price = project.getFlatTypePrices().getOrDefault(flatType, 0.0);

                long bookedUnits = applications.stream()
                        .filter(app -> app.getProject().getProjectID().equals(project.getProjectID())
                                && app.getFlatType() == flatType
                                && app.getApplicationStatus() == ApplicantAppStatus.BOOKED)
                        .count();

                int availableUnits = Math.max(0, totalUnits - (int) bookedUnits);

                System.out.printf("| %-12s| %-16d| %-16d| %-16d| $%-14.2f|\n",
                        flatType, totalUnits, availableUnits, bookedUnits, price);
            }

            System.out.println("+------------------------------------------------------------------------------------+");

            long totalApplications = applications.stream()
                    .filter(app -> app.getProject().getProjectID().equals(project.getProjectID()))
                    .count();

            Map<ApplicantAppStatus, Long> statusCounts = applications.stream()
                    .filter(app -> app.getProject().getProjectID().equals(project.getProjectID()))
                    .collect(Collectors.groupingBy(Application::getApplicationStatus, Collectors.counting()));

            System.out.println("| APPLICATION STATISTICS:                                                            |");
            System.out.printf("| Total Applications : %-62d|\n", totalApplications);
            System.out.printf("| Pending            : %-62d|\n", statusCounts.getOrDefault(ApplicantAppStatus.PENDING, 0L));
            System.out.printf("| Successful         : %-62d|\n", statusCounts.getOrDefault(ApplicantAppStatus.SUCCESSFUL, 0L));
            System.out.printf("| Unsuccessful       : %-62d|\n", statusCounts.getOrDefault(ApplicantAppStatus.UNSUCCESSFUL, 0L));
            System.out.printf("| Booked             : %-62d|\n", statusCounts.getOrDefault(ApplicantAppStatus.BOOKED, 0L));
            System.out.println("+------------------------------------------------------------------------------------+");
        }
    }
}
