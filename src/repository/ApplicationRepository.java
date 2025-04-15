package repository;

import controller.ApplicantController;
import controller.ProjectController;
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
    private final ApplicantController applicantController = new ApplicantController();
    private final ProjectController projectController = new ProjectController();
    private static final String filePath = "./src/repository/" + folder + "/" + fileName;


    private static Application createApplicationFromCSV(String csv) {
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

            // Create repository instances
            ApplicantRepository applicantRepo = new ApplicantRepository();
            ProjectRepository projectRepo = new ProjectRepository();

            // Use instance methods instead of static HashMaps
            Applicant applicant = null;
            Project project = null;

            try {
                applicant = applicantRepo.findApplicantById(applicantNRIC);
                project = projectRepo.findProjectById(projectID);
            } catch (IOException e) {
                System.out.println("Error finding references: " + e.getMessage());
                return null;
            }

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

    public List<Application> loadApplications() throws IOException {
        List<Application> applications = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("Applications file not found, returning empty list");
            return applications;
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
                Application application = createApplicationFromCSV(data);
                if (application != null) {
                    applications.add(application);
                }
            }
        }

        return applications;
    }
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
        applicationsMap.clear();
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
        ApplicantRepository applicantRepo = new ApplicantRepository();
        try {
            List<Applicant> applicants = applicantRepo.loadApplicants();
            // If you need applicants data elsewhere in the method, store it in a local variable
            // Continue with your logic using the applicants list instead of the HashMap
        } catch (IOException e) {
            System.out.println("Error loading applicants: " + e.getMessage());
        }
        try {
            ProjectRepository projectRepo = new ProjectRepository();
            List<Project> projects = projectRepo.loadProjects();
        } catch (IOException e) {
            System.out.println("Error loading projects: " + e.getMessage());
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

    public static String getLastApplicationId() {
        if (APPLICATIONS.isEmpty()) {
            new ApplicationRepository().loadFromCSV(); // <- Ensures latest state
        }
        if (APPLICATIONS.isEmpty()) {
            return "A0000";
        }

        return APPLICATIONS.keySet().stream()
                .sorted()
                .reduce((first, second) -> second)
                .orElse("A0000");
    }


    public static String generateNextApplicationId() {
        String lastId = getLastApplicationId(); // e.g., A0042
        if (lastId == null || !lastId.matches("A\\d{4}")) {
            return "A0001"; // Safe default if nothing exists or malformed
        }

        int number = Integer.parseInt(lastId.substring(1));
        number++;
        return "A" + String.format("%04d", number); // e.g., A0043
    }

}