package repository;

import controller.PasswordChangerInterface;
import controller.PasswordController;
import controller.VerificationInterface;
import model.HDBManager;
import enums.MaritalStatus;
import model.Project;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HDBManagerRepository extends Repository implements PasswordChangerInterface,VerificationInterface {
    private static final String folder = "data";
    private static final String fileName = "manager_records.csv";
    private static boolean isRepoLoaded = false;
    public static HashMap<String, HDBManager> MANAGERS = new HashMap<>();
    private static final String filePath = "./src/repository/" + folder + "/" + fileName;

    @Override
    public boolean loadFromCSV() {
        try {
            loadManagersFromCSV(filePath, MANAGERS);
            isRepoLoaded = true;
            return true;
        } catch (Exception e) {
            System.out.println("Error loading HDB Manager repository: " + e.getMessage());
            return false;
        }
    }

    private static void loadManagersFromCSV(String fileName, HashMap<String, HDBManager> managersMap) {
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

    public HDBManager verifyCredentials(String id, String password) {
        PasswordController pc = new PasswordController();
        String hashedInputPassword = pc.hashPassword(password);

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                HDBManager hdbmanager = csvToManager(line);
                if (hdbmanager != null && hdbmanager.getNRIC().equals(id)) {
                    // Check for default password OR hashed password match
                    if (hdbmanager.getPassword().equals("password") && password.equals("password")) {
                        return hdbmanager;
                    } else if (hdbmanager.getPassword().equals(hashedInputPassword)) {
                        return hdbmanager;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null; // Login failed
    }
    public boolean changePassword(String nric, String newHashedPassword) {
        List<String[]> allRecords = new ArrayList<>();
        boolean passwordUpdated = false;

        // Load all records from the file
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(nric)) {
                    parts[4] = newHashedPassword; // Update password
                    passwordUpdated = true;
                }
                allRecords.add(parts);
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
            return false; // Indicate failure
        }

        // Rewrite the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String[] record : allRecords) {
                writer.write(String.join(",", record));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to the file: " + e.getMessage());
            return false; // Indicate failure
        }

        return passwordUpdated;
    }
    public static boolean isRepoLoaded() {
        return isRepoLoaded;
    }

    public static void setRepoLoaded(boolean repoLoaded) {
        isRepoLoaded = repoLoaded;
    }
}
