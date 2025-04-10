package repository;

import enums.Visibility;
import model.Project;
import enums.FlatType;
import java.util.Map;
import java.io.*;
import java.util.HashMap;
import java.util.List;

public class ProjectRepository extends Repository {
    private static final String folder = "data";
    private static final String fileName = "projects.csv";
    private static Boolean isRepoLoaded = true;
    public static HashMap<String, Project> PROJECTS = new HashMap<>();

    @Override
    public boolean loadFromCSV() {
        try {
            loadProjectsFromCSV(fileName, PROJECTS);
            isRepoLoaded = true;
            return true;
        } catch (Exception e) {
            System.out.println("Error loading projects repository: " + e.getMessage());
            return false;
        }
    }

    public static void saveAllProjectsToCSV() {
        saveProjectsToCSV(fileName, PROJECTS);
    }

    private static void saveProjectsToCSV(String fileName, HashMap<String, Project> projectsMap) {
        String filePath = "./src/repository/" + folder + "/" + fileName;

        File directory = new File("./src/repository/" + folder);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Project project : projectsMap.values()) {
                writer.write(projectToCSV(project));
                writer.newLine();
            }
            System.out.println("Projects successfully saved to " + fileName);
        } catch (IOException e) {
            System.out.println("Error saving project data: " + e.getMessage());
        }
    }

    private static String projectToCSV(Project project) {
        StringBuilder flatTypeData = new StringBuilder();
        for (FlatType flatType : project.getFlatTypeUnits().keySet()) {
            int units = project.getFlatTypeUnits().get(flatType);
            double price = project.getFlatTypePrices().getOrDefault(flatType, 0.0);
            flatTypeData.append(flatType.name()).append(";").append(units).append(";").append(price).append("|");
        }

        if (flatTypeData.length() > 0) {
            flatTypeData.setLength(flatTypeData.length() - 1); // Remove trailing '|'
        }

        return String.join(",",
                project.getProjectID(),
                project.getProjectName(),
                project.getNeighborhood(),
                flatTypeData.toString(),
                project.getApplicationOpeningDate(),
                project.getApplicationClosingDate(),
                project.getManagerID(),
                String.valueOf(project.getOfficerSlot()),
                String.join(";", project.getOfficerIDs()),
                project.getVisibility().toString()
        );
    }

    private static void loadProjectsFromCSV(String fileName, HashMap<String, Project> projectsMap) {
        String filePath = "./src/repository/" + folder + "/" + fileName;

        File directory = new File("./src/repository/" + folder);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(filePath);

        if (!file.exists()) {
            try {
                file.createNewFile();
                System.out.println("Created empty file: " + filePath);
            } catch (IOException e) {
                System.out.println("Error creating file: " + e.getMessage());
            }
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Project project = csvToProject(line);
                if (project != null) {
                    projectsMap.put(project.getProjectID(), project);
                }
            }
            System.out.println("Successfully loaded " + projectsMap.size() + " projects from " + fileName);
        } catch (IOException e) {
            System.out.println("Error reading project data: " + e.getMessage());
        }
    }

    private static Project csvToProject(String csv) {
        String[] fields = csv.split(",");
        try {
            String projectID = fields[0];
            String projectName = fields[1];
            String neighborhood = fields[2];

            // Initialize maps for flat type units and prices
            Map<FlatType, Integer> flatTypeUnits = new HashMap<>();
            Map<FlatType, Double> flatTypePrices = new HashMap<>();

            // Parse flat type data dynamically
            int index = 3; // Start after neighborhood
            while (!fields[index].equalsIgnoreCase("Application opening date")) {
                FlatType flatType = FlatType.valueOf(fields[index].trim());
                int units = Integer.parseInt(fields[index + 1].trim());
                double price = Double.parseDouble(fields[index + 2].trim());
                flatTypeUnits.put(flatType, units);
                flatTypePrices.put(flatType, price);
                index += 3; // Move to the next flat type
            }

            String applicationOpeningDate = fields[index + 1];
            String applicationClosingDate = fields[index + 2];
            String managerID = fields[index + 3];
            int officerSlot = Integer.parseInt(fields[index + 4]);
            List<String> officerIDs = List.of(fields[index + 5].split(";"));
            Visibility visibility = Visibility.valueOf(fields[index + 6]);

            // Create and return the Project object
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
        }
        return null;
    }

    public static boolean isRepoLoaded() {
        return isRepoLoaded;
    }

    public static void setRepoLoaded(boolean isRepoLoaded) {
        ProjectRepository.isRepoLoaded = isRepoLoaded;
    }
}
