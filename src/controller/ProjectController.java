package controller;

import model.HDBManager;
import model.Project;
import enums.FlatType;
import enums.Visibility;
import repository.ProjectRepository;

import java.text.SimpleDateFormat;
import java.util.*;

public class ProjectController {

    /**
     * Creates a new project from user input and saves it to the repository.
     * Prevents duplicate IDs and overlapping application periods.
     * Links the created project to the HDBManager.
     *
     * @return The created Project object if successful, null otherwise
     */
    public Project createProject(HDBManager manager) {
        Scanner scanner = new Scanner(System.in);
        try {
            // 1. Project ID (must be unique)
            System.out.print("Enter Project ID: ");
            String projectID = scanner.nextLine().trim();

            if (ProjectRepository.PROJECTS.containsKey(projectID)) {
                System.out.println("❌ A project with this ID already exists.");
                return null;
            }

            // 2. Basic details
            System.out.print("Enter Project Name: ");
            String projectName = scanner.nextLine().trim();

            System.out.print("Enter Neighborhood: ");
            String neighborhood = scanner.nextLine().trim();

            // 3. Flat type data
            Map<FlatType, Integer> flatTypeUnits = new HashMap<>();
            Map<FlatType, Double> flatTypePrices = new HashMap<>();

            System.out.println("\nTWO_ROOMS Flat Type:");
            System.out.print("Enter Number of Units: ");
            int twoRoomUnits = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Enter Price: ");
            double twoRoomPrice = Double.parseDouble(scanner.nextLine().trim());
            flatTypeUnits.put(FlatType.TWO_ROOMS, twoRoomUnits);
            flatTypePrices.put(FlatType.TWO_ROOMS, twoRoomPrice);

            System.out.println("\nTHREE_ROOMS Flat Type:");
            System.out.print("Enter Number of Units: ");
            int threeRoomUnits = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Enter Price: ");
            double threeRoomPrice = Double.parseDouble(scanner.nextLine().trim());
            flatTypeUnits.put(FlatType.THREE_ROOMS, threeRoomUnits);
            flatTypePrices.put(FlatType.THREE_ROOMS, threeRoomPrice);

            // 4. Application Dates
            System.out.print("Enter Application Opening Date (MM/dd/yyyy): ");
            String openingDate = scanner.nextLine().trim();
            System.out.print("Enter Application Closing Date (MM/dd/yyyy): ");
            String closingDate = scanner.nextLine().trim();

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            sdf.setLenient(false);
            Date newOpen = sdf.parse(openingDate);
            Date newClose = sdf.parse(closingDate);

            // 🔒 Check for overlapping application period with manager's existing projects

            for (String existingID : manager.getCreatedProjectIDs()) {
                Project existing = ProjectRepository.PROJECTS.get(existingID);
                if (existing == null) continue;

                Date existingOpen = sdf.parse(existing.getApplicationOpeningDate());
                Date existingClose = sdf.parse(existing.getApplicationClosingDate());

                boolean isOverlapping = !(newClose.before(existingOpen) || newOpen.after(existingClose));
                if (isOverlapping) {
                    System.out.println("❌ You already have a project during this application period (" +
                            existing.getProjectID() + "). Cannot create overlapping projects.");
                    return null;
                }
            }

            // 5. Officer slots
            System.out.print("Enter Officer Slot (number): ");
            int officerSlot = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Enter Officer IDs (comma-separated): ");
            String officerIDsInput = scanner.nextLine().trim();
            List<String> officerIDs = new ArrayList<>();
            if (!officerIDsInput.isBlank()) {
                officerIDs = Arrays.asList(officerIDsInput.split(","));
            }

            // 6. Visibility
            System.out.print("Set Visibility (ON/OFF): ");
            String visibilityInput = scanner.nextLine().trim();
            Visibility visibility = visibilityInput.equalsIgnoreCase("ON") ? Visibility.ON : Visibility.OFF;


            //7. ManagerID
            System.out.print("Enter Manager ID: ");
            String managerID = scanner.nextLine().trim();

            // ✅ Create the project object
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
                    visibility
            );

            // 💾 Save to repository
            ProjectRepository.PROJECTS.put(projectID, newProject);
            ProjectRepository.saveAllProjectsToCSV();

            // 🔗 Link project to manager
            manager.addProject(projectID);

            System.out.println("✅ Project created successfully!");
            return newProject;

        } catch (Exception e) {
            System.out.println("❌ Error creating project: " + e.getMessage());
            return null;
        }
    }
}

