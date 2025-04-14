package repository;

import enums.Visibility;
import model.Project;
import enums.FlatType;
import java.util.Map;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ProjectRepository extends Repository {
    private static final String folder = "data";
    private static final String fileName = "project_records" + ".csv";
    private static Boolean isRepoLoaded = false;
    public static HashMap<String, Project> PROJECTS = new HashMap<>();
    private static final String filePath = "./src/repository/" + folder + "/" + fileName;
    @Override
    public boolean loadFromCSV() {
        try {

            loadProjectsFromCSV(filePath, PROJECTS);
            System.out.println("Loaded " + PROJECTS.size() + " projects");
            isRepoLoaded = true;
            return true;
        } catch (Exception e) {
            System.out.println("Error loading projects repository: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static void saveAllProjectsToCSV() {
        saveProjectsToCSV(fileName, PROJECTS);
    }

    private static void saveProjectsToCSV(String fileName, HashMap<String, Project> projectsMap) {


        File directory = new File("./src/repository/" + folder);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            // Updated header
            writer.write("ProjectID,ProjectName,Neighborhood,No of TWO_ROOMS,Price of TWO_ROOMS,No of THREE_ROOMS,Price of THREE_ROOMS,ApplicationOpeningDate,ApplicationClosingDate,ManagerID,OfficerSlot,OfficerIDs,Visibility");
            writer.newLine();

            // Write all projects
            for (Project project : projectsMap.values()) {
                writer.write(projectToCSV(project));
                writer.newLine();
            }
            System.out.println("Projects successfully saved to " + fileName);
        } catch (IOException e) {
            System.out.println("Error saving project data: " + e.getMessage());
        }
    }

    // Helper method to determine if a project is new (not yet in file)
    private static boolean isNewProject(Project project) {
        // This is a simple implementation - in practice, you might need more sophisticated logic
        // For now, always return true to mimic the behavior in ApplicantRepository
        return true;
    }

    // Update this method to use the new CSV format
    private static String projectToCSV(Project project) {
        return String.format("%s,%s,%s,%d,%f,%d,%f,%s,%s,%s,%d,%s,%s",
                project.getProjectID(),
                project.getProjectName(),
                project.getNeighborhood(),
                project.getUnitsForFlatType(FlatType.TWO_ROOMS),
                project.getPriceForFlatType(FlatType.TWO_ROOMS),
                project.getUnitsForFlatType(FlatType.THREE_ROOMS),
                project.getPriceForFlatType(FlatType.THREE_ROOMS),
                project.getApplicationOpeningDate(),
                project.getApplicationClosingDate(),
                project.getManagerID(),
                project.getOfficerSlot(),
                String.join(";", project.getOfficerIDs()),
                project.getVisibility()
        );
    }

    // Update this method to parse the new CSV format
    private static Project csvToProject(String csv) {
        String[] fields = csv.split(",");
        try {
            if (fields.length < 13) {
                System.out.println("Invalid CSV format: insufficient fields");
                return null;
            }

            String projectID = fields[0];
            String projectName = fields[1];
            String neighborhood = fields[2];

            // Parse flat type data from individual fields
            Map<FlatType, Integer> flatTypeUnits = new HashMap<>();
            Map<FlatType, Double> flatTypePrices = new HashMap<>();

            // TWO_ROOMS data
            int twoRoomUnits = Integer.parseInt(fields[3]);
            double twoRoomPrice = Double.parseDouble(fields[4]);
            flatTypeUnits.put(FlatType.TWO_ROOMS, twoRoomUnits);
            flatTypePrices.put(FlatType.TWO_ROOMS, twoRoomPrice);

            // THREE_ROOMS data
            int threeRoomUnits = Integer.parseInt(fields[5]);
            double threeRoomPrice = Double.parseDouble(fields[6]);
            flatTypeUnits.put(FlatType.THREE_ROOMS, threeRoomUnits);
            flatTypePrices.put(FlatType.THREE_ROOMS, threeRoomPrice);

            String applicationOpeningDate = fields[7];
            String applicationClosingDate = fields[8];
            String managerID = fields[9];
            int officerSlot = Integer.parseInt(fields[10]);

            List<String> officerIDs = new ArrayList<>();
            if (fields[11].contains(";")) {
                officerIDs = Arrays.asList(fields[11].split(";"));
            } else if (!fields[11].isEmpty()) {
                officerIDs = Collections.singletonList(fields[11]);
            }

            Visibility visibility = Visibility.valueOf(fields[12]);

            return new Project(
                    projectID,
                    projectName,
                    neighborhood,
                    flatTypeUnits,
                    flatTypePrices,
                    applicationOpeningDate,
                    applicationClosingDate,
                    managerID,
                    officerSlot,
                    officerIDs,
                    visibility
            );
        } catch (Exception e) {
            System.out.println("Error parsing project data: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private static void loadProjectsFromCSV(String filePath, HashMap<String, Project> projectsMap) {
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("Projects file not found, starting with empty repository");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true; // Flag to identify header row

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    // Skip the header row
                    firstLine = false;
                    continue;
                }

                Project project = csvToProject(line);
                if (project != null) {
                    projectsMap.put(project.getProjectID(), project);
                }
            }
            System.out.println("Projects loaded successfully from " + filePath);
        } catch (IOException e) {
            System.out.println("Error loading project data: " + e.getMessage());
        }
    }

    public static boolean isRepoLoaded() {
        return isRepoLoaded;
    }

    public static void setRepoLoaded(boolean isRepoLoaded) {
        ProjectRepository.isRepoLoaded = isRepoLoaded;
    }

    public static void addOfficerToProject(String projectID, String officerID) {
        Project p = PROJECTS.get(projectID);
        if (p != null && !p.getOfficerIDs().contains(officerID)) {
            p.getOfficerIDs().add(officerID);
            saveAllProjectsToCSV(); // if implemented
            System.out.println("✅ Officer " + officerID + " added to project " + projectID);
        }
    }
}


