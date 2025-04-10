package controller;

import model.Project;
import enums.FlatType;
import enums.Visibility;
import repository.ProjectRepository;

import java.util.*;

public class ProjectController {

    /**
     * Creates a new project from user input and saves it to the repository
     * @return the created Project object if successful, null otherwise
     */
    public Project createProject() {
        {
            Scanner scanner = new Scanner(System.in);
            try {
                // Get basic project details
                System.out.print("Enter Project ID: ");
                String projectID = scanner.nextLine().trim();

                if (ProjectRepository.PROJECTS.containsKey(projectID)) {
                    System.out.println("A project with this ID already exists.");
                    return null;
                }

                System.out.print("Enter Project Name: ");
                String projectName = scanner.nextLine().trim();

                System.out.print("Enter Neighborhood: ");
                String neighborhood = scanner.nextLine().trim();

                // Initialize maps for flat types
                Map<FlatType, Integer> flatTypeUnits = new HashMap<>();
                Map<FlatType, Double> flatTypePrices = new HashMap<>();

                // TWO_ROOMS (required)
                System.out.println("\nTWO_ROOMS Flat Type");
                System.out.print("Enter Number of Units: ");
                int twoRoomUnits = Integer.parseInt(scanner.nextLine().trim());

                System.out.print("Enter Price: ");
                double twoRoomPrice = Double.parseDouble(scanner.nextLine().trim());

                flatTypeUnits.put(FlatType.TWO_ROOMS, twoRoomUnits);
                flatTypePrices.put(FlatType.TWO_ROOMS, twoRoomPrice);

                // THREE_ROOMS (required)
                System.out.println("\nTHREE_ROOMS Flat Type");
                System.out.print("Enter Number of Units: ");
                int threeRoomUnits = Integer.parseInt(scanner.nextLine().trim());

                System.out.print("Enter Price: ");
                double threeRoomPrice = Double.parseDouble(scanner.nextLine().trim());

                flatTypeUnits.put(FlatType.THREE_ROOMS, threeRoomUnits);
                flatTypePrices.put(FlatType.THREE_ROOMS, threeRoomPrice);

                // Remaining project details
                System.out.print("Enter Application Opening Date (MM/DD/YYYY): ");
                String openingDate = scanner.nextLine().trim();

                System.out.print("Enter Application Closing Date (MM/DD/YYYY): ");
                String closingDate = scanner.nextLine().trim();

                System.out.print("Enter Manager ID: ");
                String managerID = scanner.nextLine().trim();

                System.out.print("Enter Officer Slot (number): ");
                int officerSlot = Integer.parseInt(scanner.nextLine().trim());

                System.out.print("Enter Officer IDs (comma-separated): ");
                String officerIDsInput = scanner.nextLine().trim();
                List<String> officerIDs = Arrays.asList(officerIDsInput.split(","));

                // Create project with all data
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
                ProjectRepository.PROJECTS.put(projectID, newProject);
                ProjectRepository.saveAllProjectsToCSV();

                System.out.println("Project created successfully!");
                return newProject;
            } catch (Exception e) {
                System.out.println("Error creating project: " + e.getMessage());
                return null;
            }
        }
    }
}
