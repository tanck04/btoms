package repository;

import model.Application;
import model.Applicant;
import model.Officer;
import model.Project;
import enums.ApplicantAppStatus;
import enums.FlatType;
import enums.WithdrawalStatus;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Repository class for managing Application data persistence.
 * <p>
 * This class handles CRUD operations for Application objects, storing and retrieving data
 * from a CSV file. It provides methods to load applications, find applications by ID,
 * generate new application IDs, create new applications, and update existing applications.
 * </p>
 */
public class ApplicationRepository{
    /** The folder where data files are stored */
    private static final String folder = "data";

    /** The filename for application records */
    private static final String fileName = "application_records.csv";

    /** The complete file path to the application records file */
    private static final String filePath = "./src/repository/" + folder + "/" + fileName;

    /**
     * Creates an Application object from CSV record data.
     *
     * @param csv A string representing a CSV record for an application
     * @return A new Application object or null if creation fails
     */
    private static Application createApplicationFromCSV(String csv) {
        String[] fields = csv.split(",");
        try {
            // Skip header row
            if (fields[0].equals("Application ID") || fields[0].trim().isEmpty()) {
                return null;
            }

            String applicationID = fields[0];
            String userNRIC = fields[1];
            String projectID = fields[2];
            FlatType flatType = FlatType.valueOf(fields[3]);
            ApplicantAppStatus applicationStatus = ApplicantAppStatus.valueOf(fields[4]);
            WithdrawalStatus withdrawalStatus = WithdrawalStatus.valueOf(fields[5]);

            // Create repository instances
            ApplicantRepository applicantRepo = new ApplicantRepository();
            OfficerRepository officerRepo = new OfficerRepository();
            ProjectRepository projectRepo = new ProjectRepository();

            // Create references
            Applicant applicant = null;
            Project project = null;
            Officer officer = null;

            // Use instance methods
            try{
                if (officerRepo.findOfficerById(userNRIC) != null) {
                    officer = officerRepo.findOfficerById(userNRIC);
                    project = projectRepo.findProjectById(projectID);
                }
            }catch (Exception e){
                System.out.println("Error finding officer: " + e.getMessage());
                return null;
            }

            if (officer != null) {
                // If the user is an officer, we don't need to find the applicant
                return new Application(applicationID, officer, project, flatType, applicationStatus, withdrawalStatus);
            } else {
                // If the user is not an officer, we need to find the applicant
                try {
                    applicant = applicantRepo.findApplicantById(userNRIC);
                    project = projectRepo.findProjectById(projectID);
                } catch (Exception e) {
                    System.out.println("Error finding applicant: " + e.getMessage());
                    return null;
                }
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

    /**
     * Loads all applications from the CSV file.
     *
     * @return A list of Application objects
     * @throws IOException If an error occurs while reading the file
     */
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

    /**
     * Finds an application by its ID.
     *
     * @param applicationID The application ID to search for
     * @return The found Application or null if not found
     * @throws IOException If an error occurs while reading the file
     */
    public Application findApplicationById(String applicationID) throws IOException {
        List<Application> applications = this.loadApplications();
        return applications.stream()
                .filter((application) -> application.getApplicationID().equals(applicationID))
                .findFirst()
                .orElse(null);
    }

    /**
     * Generates the next sequential application ID.
     * <p>
     * The format is "A" followed by a four-digit number (e.g., A0001, A0002).
     * </p>
     *
     * @return A new unique application ID
     */
    public String generateNextApplicationID() {
        int max = 0;
        try {
            // Load all applications directly from CSV
            List<Application> applications = loadApplications();

            // Find the highest application number
            for (Application application : applications) {
                String existingID = application.getApplicationID();
                if (existingID.matches("A\\d+")) {
                    int number = Integer.parseInt(existingID.substring(1));
                    if (number > max) {
                        max = number;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading applications: " + e.getMessage());
            // If we can't load existing applications, start from 1
        }

        int nextNumber = max + 1;
        return String.format("A%04d", nextNumber);  // e.g., A0001, A0002
    }

    /**
     * Creates a new application record in the CSV file.
     *
     * @param application The Application object to be saved
     * @throws IOException If an error occurs while writing to the file
     */
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
            String applicationData = applicationToCSV(application);

            writer.write(applicationData);
            // No need to manually add newline - the next write will handle this
        }
    }

    /**
     * Updates an existing application record in the CSV file.
     * <p>
     * This method finds and replaces the record with matching application ID.
     * </p>
     *
     * @param updatedApplication The Application object with updated information
     */
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
                    updatedLines.add(applicationToCSV(updatedApplication)); // Replace the old line
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

    /**
     * Converts an Application object to a CSV record string.
     *
     * @param application The Application object to convert
     * @return A string representing the application in CSV format
     */
    private static String applicationToCSV(Application application) {
        return String.join(",",
                application.getApplicationID(),
                application.getUser().getNRIC(),
                application.getProject().getProjectID(),
                application.getFlatType().toString(),
                application.getApplicationStatus().toString(),
                application.getWithdrawalStatus().toString());
    }
}