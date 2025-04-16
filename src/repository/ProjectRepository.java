package repository;

import enums.Visibility;
import model.Enquiry;
import model.Project;
import enums.FlatType;
import java.util.Map;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ProjectRepository{
    private static final String folder = "data";
    private static final String fileName = "project_records" + ".csv";
    private static final String filePath = "./src/repository/" + folder + "/" + fileName;

    /**
     * Creates a new project and appends it to the CSV file.
     *
     * @param project The Project object to be added.
     * @throws IOException if an error occurs while writing to the file.
     */
    public void createNewProject(Project project) throws IOException {
        File file = new File(filePath);
        boolean needsNewline = false;

        // Check if file exists and doesn't end with newline
        if (file.exists() && file.length() > 0) {
            try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
                if (raf.length() > 0) {
                    raf.seek(raf.length() - 1);
                    byte lastByte = raf.readByte();
                    needsNewline = lastByte != '\n';
                }
            }
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            // Write a newline if file has content
            if (needsNewline) {
                writer.newLine();
            }

            String projectData = String.join(",",
                    project.getProjectID(),
                    project.getProjectName(),
                    project.getNeighborhood(),
                    String.valueOf(project.getUnitsForFlatType(FlatType.TWO_ROOMS)),
                    String.valueOf(project.getPriceForFlatType(FlatType.TWO_ROOMS)),
                    String.valueOf(project.getUnitsForFlatType(FlatType.THREE_ROOMS)),
                    String.valueOf(project.getPriceForFlatType(FlatType.THREE_ROOMS)),
                    project.getApplicationOpeningDate(),
                    project.getApplicationClosingDate(),
                    project.getManagerID(),
                    String.valueOf(project.getOfficerSlot()),
                    String.join(";", project.getOfficerIDs()),
                    project.getVisibility().toString());

            writer.write(projectData);
        }
    }

    private Project createProjectFromCSV(String[] parts) {
        try {
            // Skip header row
            if (parts[0].equals("ProjectID") || parts[0].trim().isEmpty()) {
                return null;
            }

            // Parse flat type data from CSV parts
            Map<FlatType, Integer> flatTypeUnits = new HashMap<>();
            Map<FlatType, Double> flatTypePrices = new HashMap<>();

            // TWO_ROOMS data (columns 3 and 4)
            flatTypeUnits.put(FlatType.TWO_ROOMS, Integer.parseInt(parts[3]));
            flatTypePrices.put(FlatType.TWO_ROOMS, Double.parseDouble(parts[4]));

            // THREE_ROOMS data (columns 5 and 6)
            flatTypeUnits.put(FlatType.THREE_ROOMS, Integer.parseInt(parts[5]));
            flatTypePrices.put(FlatType.THREE_ROOMS, Double.parseDouble(parts[6]));

            // Parse officerIDs (column 11)
            List<String> officerIDs = new ArrayList<>();
            if (parts[11].contains(";")) {
                officerIDs = Arrays.asList(parts[11].split(";"));
            } else if (!parts[11].isEmpty()) {
                officerIDs = Collections.singletonList(parts[11]);
            }

            return new Project(
                parts[0],                     // projectID
                parts[1],                     // projectName
                parts[2],                     // neighborhood
                flatTypeUnits,                // flatTypeUnits map
                flatTypePrices,               // flatTypePrices map
                parts[7],                     // openingDate
                parts[8],                     // closingDate
                parts[9],                     // managerID
                Integer.parseInt(parts[10]),  // officerSlot
                officerIDs,                   // officerIDs
                Visibility.valueOf(parts[12]) // visibility (column 12)
            );
        } catch (Exception e) {
            System.out.println("Error parsing project data: " +
                               Arrays.toString(parts) + " - " + e.getMessage());
            return null;
        }
    }
    /**
     * Loads all projects from the CSV file.
     *
     * @return A list of Project objects.
     * @throws IOException if an error occurs while reading the file.
     */
    public List<Project> loadProjects() throws IOException {
        List<Project> projects = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("Projects file not found, returning empty list");
            return projects;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                // Skip the header row
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] data = line.split(",");
                Project project = this.createProjectFromCSV(data);
                if (project != null) {
                    projects.add(project);
                }
            }
        }

        return projects;
    }

    public Project findProjectById(String projectID) throws IOException {
        List<Project> projects = this.loadProjects();
        return (Project)projects.stream().filter((project) -> project.getProjectID().equals(projectID)).findFirst().orElse((Project)null);
    }

    public void addOfficerToProject(String projectID, String officerID) {
        try {
            // Find the project by ID
            Project project = findProjectById(projectID);

            if (project == null) {
                System.out.println("Project not found with ID: " + projectID);
                return;
            }

            // Check if officer is already assigned
            if (!project.getOfficerIDs().contains(officerID)) {
                // Add the officer to the project
                project.getOfficerIDs().add(officerID);

                // Save the updated project
                updateProject(project);

                System.out.println("✅ Officer " + officerID + " added to project " + projectID);
            } else {
                System.out.println("Officer " + officerID + " is already assigned to this project");
            }
        } catch (IOException e) {
            System.out.println("Error adding officer to project: " + e.getMessage());
        }
    }

    private void updateProject(Project project) throws IOException {
        // Get all projects
        List<Project> projects = loadProjects();

        // Find and replace the project
        for (int i = 0; i < projects.size(); i++) {
            if (projects.get(i).getProjectID().equals(project.getProjectID())) {
                projects.set(i, project);
                break;
            }
        }

        // Write all projects back to the file
        saveProjects(projects);
    }

    private void saveProjects(List<Project> projects) throws IOException {
        File directory = new File("./src/repository/" + folder);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            for (Project project : projects) {
                String projectData = String.join(",",
                    project.getProjectID(),
                    project.getProjectName(),
                    project.getNeighborhood(),
                    String.valueOf(project.getUnitsForFlatType(FlatType.TWO_ROOMS)),
                    String.valueOf(project.getPriceForFlatType(FlatType.TWO_ROOMS)),
                    String.valueOf(project.getUnitsForFlatType(FlatType.THREE_ROOMS)),
                    String.valueOf(project.getPriceForFlatType(FlatType.THREE_ROOMS)),
                    project.getApplicationOpeningDate(),
                    project.getApplicationClosingDate(),
                    project.getManagerID(),
                    String.valueOf(project.getOfficerSlot()),
                    String.join(";", project.getOfficerIDs()),
                    project.getVisibility().toString());

                writer.write(projectData);
                writer.newLine();
            }
        }
    }

    public List<Project> getProjectsByOfficerId(String nric){
        List<Project> filteredProjects = new ArrayList<>();
        List<Project> projects = new ArrayList<>();
        try {
            projects = loadProjects();  // handle exception internally
        } catch (IOException e) {
            System.out.println("Failed to load projects: " + e.getMessage());
            // Optionally log the stack trace or return empty list
            return filteredProjects; // or return Collections.emptyList();
        }
        for (Project project : projects) {
            if (project.getOfficerIDs() != null && project.getOfficerIDs().contains(nric)) {
                filteredProjects.add(project);
            }
        }
        return filteredProjects;
    }
}


