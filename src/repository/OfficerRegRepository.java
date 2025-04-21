package repository;

import enums.OfficerRegStatus;
import model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import helper.CSVUtil;

/**
 * Repository class for managing Officer Registration data persistence.
 * <p>
 * This class handles CRUD operations for OfficerRegistration objects, storing and retrieving data
 * from a CSV file. It provides methods for generating registration IDs, loading registrations,
 * creating new registrations, and updating existing ones.
 * </p>
 */
public class OfficerRegRepository {
    /** The complete file path to the officer registration records file */
    private static final String FILE_PATH_OFFICER_REGISTRATION = "./src/repository/data/officer_registration_records.csv";

    /** In-memory cache of officer registrations */
    public static List<OfficerRegistration> registrations = new ArrayList<>();

    /**
     * Generates the next sequential registration ID.
     * <p>
     * The format is "R" followed by a four-digit number (e.g., R0001, R0002).
     * </p>
     *
     * @return A new unique registration ID
     */
    public String generateNextRegistrationID() {
        int max = 0;
        try {
            // Load all registrations directly from CSV
            List<OfficerRegistration> registrations = loadAllOfficerReg();

            // Find the highest registration number
            for (OfficerRegistration registration : registrations) {
                String existingID = registration.getRegistrationId();
                if (existingID.matches("R\\d+")) {
                    int number = Integer.parseInt(existingID.substring(1));
                    if (number > max) {
                        max = number;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading projects: " + e.getMessage());
            // If we can't load existing projects, start from 1
        }

        int nextNumber = max + 1;
        return String.format("R%04d", nextNumber);  // e.g., R0001, R0002
    }

    /**
     * Gets all pending officer registrations for a specific project.
     * <p>
     * This method filters registrations by project ID and pending status.
     * </p>
     *
     * @param projectID The ID of the project to filter by
     * @return List of pending OfficerRegistration objects for the specified project
     */
    public static List<OfficerRegistration> getPendingByProject(String projectID) {
        try {
            // Create an instance to use the instance method
            OfficerRegRepository repository = new OfficerRegRepository();
            // Load registrations from CSV
            List<OfficerRegistration> allRegistrations = repository.loadAllOfficerReg();

            // Filter for pending registrations matching the project ID
            return allRegistrations.stream()
                    .filter(r -> r.getProject().getProjectID().equals(projectID) &&
                            r.getStatus() == OfficerRegStatus.PENDING)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("Error loading officer registrations: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Saves all registration data to persistent storage.
     * <p>
     * This is currently a simulated operation that logs a confirmation message.
     * </p>
     */
    public static void saveAll() {
        // Simulated CSV save
        System.out.println("Saved all registration statuses.");
    }

    /**
     * Creates an OfficerRegistration object from CSV record data.
     *
     * @param parts Array of strings representing fields from a CSV record
     * @return A new OfficerRegistration object or null if creation fails
     */
    private OfficerRegistration createRegFromCSV(String[] parts) {
        // Create repository instances
        OfficerRepository officerRepo = new OfficerRepository();
        ProjectRepository projectRepo = new ProjectRepository();
        // Create references
        Officer officer = null;
        Project project = null;
        try {
            String registrationID = parts[0];
            String officerID = parts[1];
            String projectID = parts[2];
            OfficerRegStatus officerRegStatus = OfficerRegStatus.valueOf(parts[3].toUpperCase());

            try{
                if (officerRepo.findOfficerById(officerID) != null) {
                    officer = officerRepo.findOfficerById(officerID);
                    project = projectRepo.findProjectById(projectID);
                }
            }catch (Exception e){
                System.out.println("Error finding officer: " + e.getMessage());
                return null;
            }

            return new OfficerRegistration(registrationID, officer, project, officerRegStatus);
        } catch (Exception e) {
            System.out.println("Error creating applicant from CSV data: " + e.getMessage());
            return null;
        }
    }

    /**
     * Loads all officer registrations from the CSV file.
     *
     * @return A list of OfficerRegistration objects
     * @throws IOException If an error occurs while reading the file
     */
    public List<OfficerRegistration> loadAllOfficerReg() throws IOException {
        List<OfficerRegistration> registrations = new ArrayList<>();
        File file = new File(FILE_PATH_OFFICER_REGISTRATION);

        if (!file.exists()) {
            System.out.println("Applicants file not found, returning empty list");
            return registrations;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH_OFFICER_REGISTRATION))) {
            String line;
            // Skip the header
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                // Skip the header row
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] data = line.split(",");
                OfficerRegistration registration = createRegFromCSV(data);
                if (registration != null) {
                    registrations.add(registration);
                }
            }
        } catch (IOException e){
            throw new IOException("Error reading Officer Registration data: " + e.getMessage());
        }
        return registrations;
    }

    /**
     * Creates a new officer registration and saves it to the CSV file.
     * <p>
     * If the status is null, it defaults to PENDING.
     * </p>
     *
     * @param officerReg The OfficerRegistration object to be saved
     */
    public void createNewOfficerReg(OfficerRegistration officerReg) {
        File file = new File(FILE_PATH_OFFICER_REGISTRATION);

        // Add null check for status here
        if (officerReg.getStatus() == null) {
            officerReg.setStatus(OfficerRegStatus.PENDING);
        }

        // Open the CSV file in append mode
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH_OFFICER_REGISTRATION, true))) {
            // If the file is not empty, write a newline first
            if (file.length() > 0) {
                writer.newLine();
            }

            // Format the registration data as CSV
            String registrationData = officerRegToCSV(officerReg);

            // Write the new registration data to the file
            writer.write(registrationData);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to save officer registration.");
        }
        try {
            CSVUtil.removeEmptyRows(FILE_PATH_OFFICER_REGISTRATION);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to clean up empty rows in officer registration file.");
        }
    }

    /**
     * Updates an existing officer registration record in the CSV file.
     * <p>
     * This method finds and replaces the record with matching registration ID.
     * </p>
     *
     * @param updatedOfficerReg The OfficerRegistration object with updated information
     */
    public static void updateOfficerRegInCSV(OfficerRegistration updatedOfficerReg) {
        File inputFile = new File(FILE_PATH_OFFICER_REGISTRATION);
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

                if (line.startsWith(updatedOfficerReg.getRegistrationId() + ",")) {
                    updatedLines.add(officerRegToCSV(updatedOfficerReg)); // Use helper method
                } else {
                    updatedLines.add(line); // Keep as is
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV for update: " + e.getMessage());
            return;
        }

        // Write updated content back to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH_OFFICER_REGISTRATION, false))) {
            for (String updatedLine : updatedLines) {
                writer.write(updatedLine);
                writer.newLine();
            }
            System.out.println("Updated project saved successfully.");
        } catch (IOException e) {
            System.out.println("Error writing updated CSV: " + e.getMessage());
        }
    }

    /**
     * Gets the most recent registration ID.
     *
     * @return The last registration ID (or "R0001" if none)
     * @throws IOException if reading fails
     */
    public String getLastRegId() throws IOException {
        List<OfficerRegistration> registrations = loadAllOfficerReg();
        if (registrations.isEmpty()) {
            return "R0001";
        }
        String lastRegId = registrations.get(registrations.size() - 1).getRegistrationId();
        return lastRegId;
    }

    /**
     * Gets all registrations for a specific officer.
     *
     * @param nric The NRIC of the officer to filter by
     * @return List of OfficerRegistration objects for the specified officer
     */
    public List<OfficerRegistration> getRegistrationByOfficerId(String nric) {
        try{
            List<OfficerRegistration> registrations = loadAllOfficerReg();
            List<OfficerRegistration> filteredRegistrations = new ArrayList<>();
            for (OfficerRegistration registration : registrations) {
                if (registration.getOfficer().getNRIC().equals(nric)) {
                    filteredRegistrations.add(registration);
                }
            }
            return filteredRegistrations;
        }catch (IOException e) {
            System.out.println("Error loading officer registrations: " + e.getMessage());
            return new ArrayList<>();
            // Optionally log or rethrow as a custom exception
        }
    }

    /**
     * Converts an OfficerRegistration object to a CSV record string.
     *
     * @param officerReg The OfficerRegistration object to convert
     * @return A string representing the officer registration in CSV format
     */
    private static String officerRegToCSV(OfficerRegistration officerReg) {
        return String.join(",",
                String.join(",",
                        officerReg.getRegistrationId(),
                        officerReg.getOfficer().getNRIC(),
                        officerReg.getProject().getProjectID(),
                        officerReg.getStatus().toString()));
    }
}