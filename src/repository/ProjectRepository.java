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
    private static final String fileName = "project_records" +".csv";
    private static Boolean isRepoLoaded = true;
    public static HashMap<String, Project> PROJECTS = new HashMap<>();

    @Override
    public boolean loadFromCSV() {
        try {
            loadProjectsFromCSV(fileName); // Remove the second parameter
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

        // Use "false" parameter to overwrite the file instead of appending
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            // Always write header
            writer.write("ProjectID,ProjectName,Neighborhood,FlatTypeData,ApplicationOpeningDate,ApplicationClosingDate,ManagerID,OfficerSlot,OfficerIDs,Visibility");
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

    private static String projectToCSV(Project project) {
        // Build the flat type data string
        StringBuilder flatTypeData = new StringBuilder();
        if (project.getFlatTypeUnits() != null && !project.getFlatTypeUnits().isEmpty()) {
            boolean isFirst = true;
            for (Map.Entry<FlatType, Integer> entry : project.getFlatTypeUnits().entrySet()) {
                FlatType flatType = entry.getKey();
                int units = entry.getValue();
                double price = project.getFlatTypePrices().getOrDefault(flatType, 0.0);

                if (!isFirst) {
                    flatTypeData.append("|");
                }

                flatTypeData.append(flatType.name()).append(";")
                          .append(units).append(";")
                          .append(price);

                isFirst = false;
            }
        }

        // Join all project fields into a CSV line
        return String.format("%s,%s,%s,%s,%s,%s,%s,%d,%s,%s",
                project.getProjectID(),
                project.getProjectName(),
                project.getNeighborhood(),
                flatTypeData.toString(), // This must contain the flat type data
                project.getApplicationOpeningDate(),
                project.getApplicationClosingDate(),
                project.getManagerID(),
                project.getOfficerSlot(),
                String.join(";", project.getOfficerIDs()),
                project.getVisibility()
        );
    }

    private static void loadProjectsFromCSV(String fileName) {
        String filePath = "./src/repository/" + folder + "/" + fileName;
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
                    PROJECTS.put(project.getProjectID(), project);
                }
            }
            System.out.println("Projects loaded successfully from " + fileName);
        } catch (IOException e) {
            System.out.println("Error loading project data: " + e.getMessage());
        }
    }

    private static Project csvToProject(String csv) {
        String[] fields = csv.split(",");
        try {
            if (fields.length < 10) {
                System.out.println("Invalid CSV format: insufficient fields");
                return null;
            }

            String projectID = fields[0];
            String projectName = fields[1];
            String neighborhood = fields[2];
            String flatTypeDataStr = fields[3];

            // Parse flat type data
            Map<FlatType, Integer> flatTypeUnits = new HashMap<>();
            Map<FlatType, Double> flatTypePrices = new HashMap<>();

            if (!flatTypeDataStr.isEmpty()) {
                String[] flatTypeEntries = flatTypeDataStr.split("\\|");
                for (String entry : flatTypeEntries) {
                    String[] parts = entry.split(";");
                    if (parts.length == 3) {
                        FlatType flatType = FlatType.valueOf(parts[0]);
                        int units = Integer.parseInt(parts[1]);
                        double price = Double.parseDouble(parts[2]);
                        flatTypeUnits.put(flatType, units);
                        flatTypePrices.put(flatType, price);
                    }
                }
            }

            String applicationOpeningDate = fields[4];
            String applicationClosingDate = fields[5];
            String managerID = fields[6];
            int officerSlot = Integer.parseInt(fields[7]);

            List<String> officerIDs = new ArrayList<>();
            if (fields[8].contains(";")) {
                officerIDs = Arrays.asList(fields[8].split(";"));
            } else if (!fields[8].isEmpty()) {
                officerIDs = Collections.singletonList(fields[8]);
            }

            Visibility visibility = Visibility.valueOf(fields[9]);

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

    public static boolean isRepoLoaded() {
        return isRepoLoaded;
    }

    public static void setRepoLoaded(boolean isRepoLoaded) {
        ProjectRepository.isRepoLoaded = isRepoLoaded;
    }
}
