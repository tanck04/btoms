package repository;

import model.Application;
import model.Applicant;
import model.Project;
import enums.ApplicantAppStatus;
import enums.FlatType;
import enums.WithdrawalStatus;

import java.io.*;
import java.util.HashMap;

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

    private static void saveApplicationsToCSV(String fileName, HashMap<String, Application> applicationsMap) {
        String filePath = "./src/repository/" + folder + "/" + ApplicationRepository.fileName;

        File directory = new File("./src/repository/" + folder);
        if (!directory.exists() && !directory.mkdirs()) {
            System.out.println("Failed to create directory: " + directory.getAbsolutePath());
            return;
        }

        File file = new File(filePath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) { // Open in append mode
            for (Application application : applicationsMap.values()) {
                // Check if the application already exists in the file
                if (!isApplicationInFile(file, application.getApplicationID())) {
                    writer.write(application.toCSV());
                    writer.newLine();
                }
            }
            System.out.println("Applications successfully saved to " + ApplicationRepository.fileName);
        } catch (IOException e) {
            System.out.println("Error saving application data: " + e.getMessage());
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

    private static void loadApplicationsFromCSV(String fileName, HashMap<String, Application> applicationsMap) {
        String filePath = "./src/repository/" + folder + "/" + ApplicationRepository.fileName;

        File directory = new File("./src/repository/" + folder);
        if (!directory.exists() && !directory.mkdirs()) {
            System.out.println("Failed to create directory: " + directory.getAbsolutePath());
            return;
        }

        File file = new File(filePath);

        if (!file.exists()) {
            try {
                boolean isFileCreated = file.createNewFile();
                if (isFileCreated) {
                    System.out.println("Created empty file: " + filePath);
                } else {
                    System.out.println("File already exists: " + filePath);
                }
            } catch (IOException e) {
                System.out.println("Error creating file: " + e.getMessage());
            }
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Application application = csvToApplication(line);
                if (application != null) {
                    applicationsMap.put(application.getApplicationID(), application);
                }
            }
            System.out.println("Successfully loaded " + applicationsMap.size() + " applications from " + ApplicationRepository.fileName);
        } catch (IOException e) {
            System.out.println("Error reading application data: " + e.getMessage());
        }
    }

    private static Application csvToApplication(String csv) {
        String[] fields = csv.split(",");
        try {
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
                throw new IllegalArgumentException("Invalid Applicant or Project reference in CSV data.");
            }

            return new Application(applicationID, applicant, project, flatType, applicationStatus, withdrawalStatus);
        } catch (Exception e) {
            System.out.println("Error parsing application data: " + e.getMessage());
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