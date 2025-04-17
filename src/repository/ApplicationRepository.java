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

public class ApplicationRepository{
    private static final String folder = "data";
    private static final String fileName = "application_records.csv";
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


                Application application = createApplicationFromCSV(line);
                if (application != null) {
                    applications.add(application);
                }
            }
        }

        return applications;
    }

    public Application findApplicationById(String applicationID) throws IOException {
        List<Application> applications = this.loadApplications();
        return (Application)applications.stream().filter((application) -> application.getApplicationID().equals(applicationID)).findFirst().orElse((Application) null);
    }

    public static String getLastApplicationId() {
        try {
            ApplicationRepository repo = new ApplicationRepository();
            List<Application> applications = repo.loadApplications();

            if (applications.isEmpty()) {
                return "A0000";
            }

            return applications.stream()
                    .map(Application::getApplicationID)
                    .sorted()
                    .reduce((first, second) -> second)
                    .orElse("A0000");
        } catch (IOException e) {
            System.out.println("Error loading application data: " + e.getMessage());
            return "A0000";
        }
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

    public void createNewApplication(Application application) throws IOException {
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

            // Write the application data
            String applicationData = String.join(",",
                    application.getApplicationID(),
                    application.getApplicant().getNRIC(),
                    application.getProject().getProjectID(),
                    application.getFlatType().toString(),
                    application.getApplicationStatus().toString(),
                    application.getWithdrawalStatus().toString());

            writer.write(applicationData);
            // No need to manually add newline - the next write will handle this
        }
    }

    public static void updateApplicationInCSV(Application updatedApplication) {
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

    private String applicationToCSV(Application application) {
        return String.join(",",
                application.getApplicationID(),
                application.getApplicant().getNRIC(),
                application.getProject().getProjectID(),
                application.getFlatType().toString(),
                application.getApplicationStatus().toString(),
                application.getWithdrawalStatus().toString());
    }

}