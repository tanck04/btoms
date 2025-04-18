package controller;

import model.Application;
import model.Manager;
import model.Project;
import enums.FlatType;
import enums.Visibility;
import model.User;
import repository.ApplicationRepository;
import repository.ManagerRepository;
import repository.ProjectRepository;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ProjectController {


    private final ProjectRepository projectRepository = new ProjectRepository();
    private final ManagerRepository managerRepository = new ManagerRepository();
    /**
     * Creates a new project with the provided details and saves it to the repository
     * @return the created Project object if successful, null otherwise
     */
    private Project createProject(
            String projectID,
            String projectName,
            String neighborhood,
            Map<FlatType, Integer> flatTypeUnits,
            Map<FlatType, Double> flatTypePrices,
            String openingDate,
            String closingDate,
            String managerID,
            int officerSlot,
            List<String> officerIDs) {
        // Validate inputs


        try {
            // Validate project ID doesn't already exist
            if (projectRepository.findProjectById(projectID) != null) {
                System.out.println("A project with this ID already exists.");
                return null;
            }

            // Perform any additional business validations
            // (e.g., date format validation, manager existence check, etc.)
            try{
                managerRepository.findManagerById(managerID);
            } catch (IOException e) {
                System.out.println("Manager ID does not exist.");
                return null;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            sdf.setLenient(false);
            Date currentDate = new Date();
            Project managedProject = null;

            try {
                for (Project project : projectRepository.loadProjects()) {
                    try {
                        String closingDateStr = project.getApplicationClosingDate();

                        if (closingDateStr == null || closingDateStr.isEmpty()) {
                            System.out.println("Skipping project " + project.getProjectID() + ": Missing closing date.");
                            continue;
                        }

                        Date projectClosingDate = sdf.parse(closingDateStr); // Renamed variable

                        if (managerID.equals(project.getManagerID()) && projectClosingDate.after(currentDate)) {
                            managedProject = project;
                            break;
                        }
                    } catch (java.text.ParseException e) {
                        System.out.println("Error parsing date for project " + project.getProjectID() + ": " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                System.out.println("Error loading projects: " + e.getMessage());
                return null; // Return null instead of void
            }

            if (managedProject != null) {
                System.out.println("You are already managing a project with an open application period.");
                return null;
            }
            

            // Create project object
            Project newProject = new Project(
                    projectID,
                    projectName,
                    neighborhood,
                    flatTypeUnits,
                    flatTypePrices,
                    openingDate,
                    closingDate,
                    managerID,
                    officerSlot,
                    officerIDs,
                    Visibility.ON
            );

            // Save to repository
            projectRepository.createNewProject(newProject);
            managerRepository.updateManagerInCSV(managerRepository.findManagerById(managerID), newProject);

            return newProject;
        } catch (Exception e) {
            System.out.println("Error creating project: " + e.getMessage());
            return null;
        }
    }

    public void createProject(User user) {
        Scanner scanner = new Scanner(System.in);
        try {
            // Collect project data from user input
            String projectID = projectRepository.generateNextProjectID();  // auto-generate
            System.out.println("Auto-generated Project ID: " + projectID);

            System.out.print("Enter Project Name: ");
            String projectName = scanner.nextLine().trim();

            System.out.print("Enter Neighborhood: ");
            String neighborhood = scanner.nextLine().trim();

            // Collect TWO_ROOMS data
            System.out.println("\nTWO_ROOMS Flat Type");
            System.out.print("Enter Number of Units: ");
            int twoRoomUnits = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Enter Price: ");
            double twoRoomPrice = Double.parseDouble(scanner.nextLine().trim());

            // Collect THREE_ROOMS data
            System.out.println("\nTHREE_ROOMS Flat Type");
            System.out.print("Enter Number of Units: ");
            int threeRoomUnits = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Enter Price: ");
            double threeRoomPrice = Double.parseDouble(scanner.nextLine().trim());

            // Collect remaining project details
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            sdf.setLenient(false);

            Date openingDate = null;
            String formattedOpeningDate = null;

            while (openingDate == null) {
                try {
                    System.out.print("Enter Opening Date (MM/dd/yyyy): ");
                    String input = scanner.nextLine().trim();
                    openingDate = sdf.parse(input);  // Parse as strict format
                    formattedOpeningDate = sdf.format(openingDate);
                    // Confirm what the user entered
                    System.out.println("✅ Parsed Opening Date: " + sdf.format(openingDate));
                } catch (ParseException e) {
                    System.out.println("❌ Invalid date format. Please follow MM/dd/yyyy.");
                }
            }


            Date closingDate = null;
            String formattedClosingDate = null;

            while (closingDate == null) {
                try {
                    System.out.print("Enter Closing Date (MM/dd/yyyy): ");
                    String input = scanner.nextLine().trim();
                    closingDate = sdf.parse(input);  // Parse as strict format
                    formattedClosingDate = sdf.format(closingDate);
                    // Confirm what the user entered
                    System.out.println("✅ Parsed Closing Date: " + sdf.format(closingDate));
                } catch (ParseException e) {
                    System.out.println("❌ Invalid date format. Please follow MM/dd/yyyy.");
                }
            }



            // Prepare flat type data
            Map<FlatType, Integer> flatTypeUnits = new HashMap<>();
            flatTypeUnits.put(FlatType.TWO_ROOMS, twoRoomUnits);
            flatTypeUnits.put(FlatType.THREE_ROOMS, threeRoomUnits);

            Map<FlatType, Double> flatTypePrices = new HashMap<>();
            flatTypePrices.put(FlatType.TWO_ROOMS, twoRoomPrice);
            flatTypePrices.put(FlatType.THREE_ROOMS, threeRoomPrice);

            // Call controller to handle business logic
            Project result = createProject(
                    projectID, projectName, neighborhood,
                    flatTypeUnits, flatTypePrices,
                    formattedOpeningDate, formattedClosingDate,
                    user.getNRIC(), 0, new ArrayList<>()
            );

            if (result != null) {
                System.out.println("Project created successfully!");
            } else {
                System.out.println("Failed to create project.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateProjectDetails(User user) {
        Manager manager = (Manager) user;
        Scanner scanner = new Scanner(System.in);
        HDBManagerController hdbManagerController = new HDBManagerController();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        sdf.setLenient(false);
        Date newOpeningDate = null;
        Date newClosingDate = null;
        Project project = null;
        hdbManagerController.viewProject(manager);
        System.out.println("\n===== Update Project Details =====");
        System.out.print("Enter the Project ID to update: ");
        String projectId = scanner.nextLine().trim();

        try {
            project = getProjectById(projectId);
            if (project == null) {
                System.out.println("Project not found with ID: " + projectId);
                return;
            }

            System.out.println("Updating Project: " + project.getProjectName());
            System.out.println("\nWhat would you like to update?");
            System.out.println("1. Project Name");
            System.out.println("2. Two-Room Units");
            System.out.println("3. Two-Room Price");
            System.out.println("4. Three-Room Units");
            System.out.println("5. Three-Room Price");
            System.out.println("6. Application Opening Date");
            System.out.println("7. Application Closing Date");
            System.out.println("8. Visibility");
            System.out.print("Enter your choice: ");

            int choice = Integer.parseInt(scanner.nextLine().trim());

            // Implement update logic based on choice
            // This would call appropriate methods in your ProjectController
            // For brevity, I'll show a simple example:

            switch (choice) {
                case 1:
                    System.out.print("Enter new Project Name: ");
                    String newName = scanner.nextLine().trim();
                    project.setProjectName(newName);
                    break;
                case 2:
                    System.out.print("Enter updated number of Two Rooms Units: ");
                    int twoRoomUnits = Integer.parseInt(scanner.nextLine().trim());
                    Map<FlatType, Integer> newTwoRoomsTypeUnits = new HashMap<>();
                    newTwoRoomsTypeUnits.put(FlatType.TWO_ROOMS, twoRoomUnits);
                    project.setFlatTypeUnits(newTwoRoomsTypeUnits);
                    break;
                case 3:
                    System.out.print("Enter updated price of Two Rooms Units: ");
                    double newTwoRoomPrice = Integer.parseInt(scanner.nextLine().trim());
                    Map<FlatType, Double> newTwoRoomsTypePrice = new HashMap<>();
                    newTwoRoomsTypePrice.put(FlatType.TWO_ROOMS, newTwoRoomPrice);
                    project.setFlatTypePrices(newTwoRoomsTypePrice);
                    break;
                case 4:
                    System.out.print("Enter updated number of Three Rooms Units: ");
                    int threeRoomUnits = Integer.parseInt(scanner.nextLine().trim());
                    Map<FlatType, Integer> newThreeRoomsTypeUnits = new HashMap<>();
                    newThreeRoomsTypeUnits.put(FlatType.THREE_ROOMS, threeRoomUnits);
                    project.setFlatTypeUnits(newThreeRoomsTypeUnits);
                    break;
                case 5:
                    System.out.print("Enter updated price of Three Rooms Units: ");
                    double newThreeRoomPrice = Integer.parseInt(scanner.nextLine().trim());
                    Map<FlatType, Double> newThreeRoomsTypePrice = new HashMap<>();
                    newThreeRoomsTypePrice.put(FlatType.THREE_ROOMS, newThreeRoomPrice);
                    project.setFlatTypePrices(newThreeRoomsTypePrice);
                    break;
                case 6:
                    while (newOpeningDate == null) {
                        try {
                            System.out.print("Enter updated Opening Date (MM/dd/yyyy): ");
                            String input = scanner.nextLine().trim();
                            newOpeningDate = sdf.parse(input);  // Parse as strict format

                            // Confirm what the user entered
                            System.out.println("✅ Parsed Opening Date: " + sdf.format(newOpeningDate));
                        } catch (ParseException e) {
                            System.out.println("❌ Invalid date format. Please follow MM/dd/yyyy.");
                        }
                    }
                    project.setApplicationOpeningDate(sdf.format(newOpeningDate));

                case 7:
                    while (newClosingDate == null) {
                        try {
                            System.out.print("Enter updated Closing Date (MM/dd/yyyy): ");
                            String input = scanner.nextLine().trim();
                            newClosingDate = sdf.parse(input);  // Parse as strict format
                            // Confirm what the user entered
                            System.out.println("✅ Parsed Closing Date: " + sdf.format(newClosingDate));
                        } catch (ParseException e) {
                            System.out.println("❌ Invalid date format. Please follow MM/dd/yyyy.");
                        }
                    }
                    project.setApplicationClosingDate(sdf.format(newClosingDate));

                case 8:
                    System.out.print("Current project Visibility" + project.getVisibility());
                    System.out.println("Enter new Visibility (ON/OFF): ");
                    String visibilityInput = scanner.nextLine().trim();
                    if (visibilityInput.toUpperCase().trim().equals("ON")) {
                        project.setVisibility(Visibility.ON);
                    } else if (visibilityInput.toUpperCase().trim().equals("OFF")) {
                        project.setVisibility(Visibility.OFF);
                    }
                    break;
                default:
                    System.out.println("Invalid option.");
                    break;
            }
        } catch (Exception e) {
            System.out.println("Error retrieving project: " + e.getMessage());
        }
        if (project != null){
            ProjectRepository.updateProjectInCSV(project);
            System.out.println("Project updated successfully!");
        }
    }

    public void deleteProject(User user) {
        Scanner scanner = new Scanner(System.in);
        HDBManagerController hdbManagerController = new HDBManagerController();
        ApplicationRepository applicationRepository = new ApplicationRepository();
        hdbManagerController.viewProject(user);
        Project deletedProject = null;
        System.out.println("\n===== Delete Project =====");
        System.out.print("Enter the Project ID to delete: ");
        String projectId = scanner.nextLine().trim();


        Project project = getProjectById(projectId);
        if (project == null) {
            System.out.println("Project not found with ID: " + projectId);
            return;
        }

        try{
            // Check if there are any applications associated with the project
            List<Application> applications = applicationRepository.loadApplications();
            for (Application application : applications) {
                if (application.getProject().getProjectID().equals(projectId)) {
                    System.out.println("Cannot delete project with existing applications.");
                    return;
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading applications: " + e.getMessage());
            return;
        }

        System.out.println("Are you sure you want to delete project: " + project.getProjectName() + "? (yes/no)");
        String confirm = scanner.nextLine().trim();

        if (confirm.equalsIgnoreCase("yes")) {
            // Call controller to delete the project
            try{
                deletedProject = projectRepository.findProjectById(projectId);
            } catch (IOException e) {
                System.out.println("Project not found with ID: " + projectId);
                return;
            }
            try{
                projectRepository.deleteProject(deletedProject);
                System.out.println("Project deleted successfully.");
            } catch (IOException e) {
                System.out.println("Error deleting project: " + e.getMessage());
            }
        } else {
            System.out.println("Delete operation cancelled.");
        }
    }


    /**
     * Retrieves a project by its ID
     * @param projectID the ID of the project to retrieve
     * @return the Project object if found, null otherwise
     */
    public Project getProjectById(String projectID) {
        try {
            return projectRepository.findProjectById(projectID);
        } catch (IOException e) {
            System.out.println("Error retrieving project: " + e.getMessage());
            return null;
        }
    }
}