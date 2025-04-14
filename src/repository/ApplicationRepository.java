package repository;

import model.Application;
import model.Applicant;
import model.Project;
import enums.ApplicantAppStatus;
import enums.FlatType;
import enums.WithdrawalStatus;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class ApplicationRepository extends Repository {
    private static final String folder = "data";
    private static final String fileName = "application_records.csv";
    private static Boolean isRepoLoaded = true;
    public static HashMap<String, Application> APPLICATIONS = new HashMap<>();

    @Override
    public boolean loadFromCSV() {
        try {
            loadApplicationsFromCSV(fileName, APPLICATIONS);
            isRepoLoaded = true;
            return true;
        } catch (Exception e) {
            System.out.println("Error loading applications repository: " + e.getMessage());
            return false;
        }
    }

    public static void saveAllApplicationsToCSV() {
        saveApplicationsToCSV(fileName, APPLICATIONS);
    }

    // In ApplicationRepository.java
    private static void saveApplicationsToCSV(String fileName, HashMap<String, Application> applicationsMap) {
        String filePath = "./src/repository/" + folder + "/" + fileName;

        File directory = new File("./src/repository/" + folder);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(filePath);
        boolean writeHeader = !file.exists() || file.length() == 0;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) { // true = append mode
            if (writeHeader) {
                writer.write("Application ID,Applicant ID,Project ID,Flat Type,Application Status,Withdrawal Status");
                writer.newLine();
            }

            // Write all applications from the HashMap
            for (Application application : applicationsMap.values()) {
                writer.write(application.toCSV());
                writer.newLine();
            }
            System.out.println("Applications successfully saved to " + fileName);
        } catch (IOException e) {
            System.out.println("Error saving application data: " + e.getMessage());
        }
    }

    public static void updateApplicationInCSV(Application updatedApplication) {
        String filePath = "./src/repository/" + folder + "/" + fileName;
        File inputFile = new File(filePath);
        List<String> updatedLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    updatedLines.add(line); // Header
                    isFirstLine = false;
                    continue;
                }

                if (line.startsWith(updatedApplication.getApplicationID() + ",")) {
                    updatedLines.add(updatedApplication.toCSV()); // Replace the old line
                } else {
                    updatedLines.add(line); // Keep as is
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV for update: " + e.getMessage());
            return;
        }

        // Write updated content back to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            for (String updatedLine : updatedLines) {
                writer.write(updatedLine);
                writer.newLine();
            }
            System.out.println("Updated application saved successfully.");
        } catch (IOException e) {
            System.out.println("Error writing updated CSV: " + e.getMessage());
        }
    }


    private static boolean isApplicationInFile(File file, String applicationID) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(applicationID + ",")) {
                    return true; // Application already exists
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file to check for duplicates: " + e.getMessage());
        }
        return false;
    }

    // In ApplicationRepository.java - fix the loadApplicationsFromCSV method
    private static void loadApplicationsFromCSV(String fileName, HashMap<String, Application> applicationsMap) {
        String filePath = "./src/repository/" + folder + "/" + fileName;

        File file = new File(filePath);
        if (!file.exists()) {
            try {
                new File("./src/repository/" + folder).mkdirs();
                file.createNewFile();
                // Write header to new file
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write("Application ID,Applicant ID,Project ID,Flat Type,Application Status,Withdrawal Status");
                    writer.newLine();
                }
            } catch (IOException e) {
                System.out.println("Error creating file: " + e.getMessage());
            }
            return;
        }

        // Make sure repositories are loaded first
        if (ApplicantRepository.APPLICANTS.isEmpty()) {
            new ApplicantRepository().loadFromCSV();
        }
        if (ProjectRepository.PROJECTS.isEmpty()) {
            new ProjectRepository().loadFromCSV();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;
            int linesRead = 0;
            int applicationsLoaded = 0;

            while ((line = reader.readLine()) != null) {
                linesRead++;
                // Skip the header row
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                if (!line.trim().isEmpty()) {
                    Application application = csvToApplication(line);
                    if (application != null) {
                        applicationsMap.put(application.getApplicationID(), application);
                        applicationsLoaded++;
                    } else {
                        System.out.println("Failed to load application from line: " + line);
                    }
                }
            }
            System.out.println("Read " + linesRead + " lines, loaded " + applicationsLoaded + " applications from " + fileName);
        } catch (IOException e) {
            System.out.println("Error reading application data: " + e.getMessage());
        }
    }

    private static Application csvToApplication(String csv) {
        String[] fields = csv.split(",");
        try {
            // Skip header row
            if (fields[0].equals("Application ID") || fields[0].trim().isEmpty()) {
                return null;
            }

            String applicationID = fields[0];
            String applicantNRIC = fields[1];
            String projectID = fields[2];
            FlatType flatType = FlatType.valueOf(fields[3]);
            ApplicantAppStatus applicationStatus = ApplicantAppStatus.valueOf(fields[4]);
            WithdrawalStatus withdrawalStatus = WithdrawalStatus.valueOf(fields[5]);

            // Retrieve Applicant and Project objects from their respective repositories
            Applicant applicant = ApplicantRepository.APPLICANTS.get(applicantNRIC);
            Project project = ProjectRepository.PROJECTS.get(projectID);

            if (applicant == null || project == null) {
                System.out.println("Missing reference - Applicant: " + (applicant == null ? "null" : "found") +
                                 ", Project: " + (project == null ? "null" : "found"));
                return null;
            }

            return new Application(applicationID, applicant, project, flatType, applicationStatus, withdrawalStatus);
        } catch (Exception e) {
            System.out.println("Error parsing application data: " + csv + " - " + e.getMessage());
        }
        return null;
    }

    public static boolean clearApplicationDatabase() {
        APPLICATIONS.clear();
        saveAllApplicationsToCSV();
        isRepoLoaded = false;
        return true;
    }

    public static boolean isRepoLoaded() {
        return isRepoLoaded;
    }

    public static void setRepoLoaded(boolean isRepoLoaded) {
        ApplicationRepository.isRepoLoaded = isRepoLoaded;
    }
}