package repository;

import model.HDBManager;
import enums.MaritalStatus;
import model.Project;

import java.io.*;
import java.util.HashMap;

public class HDBManagerRepository extends Repository {
    private static final String folder = "data";
    private static final String fileName = "manager_records.csv";
    private static boolean isRepoLoaded = false;
    public static HashMap<String, HDBManager> MANAGERS = new HashMap<>();

    @Override
    public boolean loadFromCSV() {
        try {
            loadManagersFromCSV(fileName, MANAGERS);
            isRepoLoaded = true;
            return true;
        } catch (Exception e) {
            System.out.println("Error loading HDB Manager repository: " + e.getMessage());
            return false;
        }
    }

    private static void loadManagersFromCSV(String fileName, HashMap<String, HDBManager> managersMap) {
        String filePath = "./src/repository/" + folder + "/" + fileName;

        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("File not found: " + filePath);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                // Skip the header row
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                HDBManager manager = csvToManager(line);
                if (manager != null) {
                    managersMap.put(manager.getNRIC(), manager);
                }
            }
            System.out.println("Successfully loaded " + managersMap.size() + " managers from " + fileName);
        } catch (IOException e) {
            System.out.println("Error reading manager data: " + e.getMessage());
        }
    }

    private static HDBManager csvToManager(String csv) {
        String[] fields = csv.split(",");
        try {
            // Skip if this looks like a header row
            if (fields[0].equalsIgnoreCase("Name")) {
                return null;
            }

            String name = fields[0];
            String nric = fields[1];
            int age = Integer.parseInt(fields[2]);
            MaritalStatus maritalStatus = MaritalStatus.valueOf(fields[3].toUpperCase());
            String password = fields[4];

            // Handle project ID (may be empty)
            String projectId = (fields.length > 5 && !fields[5].isEmpty()) ? fields[5] : null;
            Project project = null;
            if (projectId != null) {
                project = ProjectRepository.PROJECTS.get(projectId); // Fixed variable name here
            }

            return new HDBManager(nric, name, password, maritalStatus, age, project);
        } catch (Exception e) {
            System.out.println("Error parsing manager data: " + csv + " - " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static void saveAllManagersToCSV() {
        // Implementation for saving would go here
    }

    public static boolean isRepoLoaded() {
        return isRepoLoaded;
    }

    public static void setRepoLoaded(boolean repoLoaded) {
        isRepoLoaded = repoLoaded;
    }
}
