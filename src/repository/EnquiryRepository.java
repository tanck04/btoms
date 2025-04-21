package repository;

import model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for managing Enquiry data persistence.
 * <p>
 * This class handles CRUD operations for Enquiry objects, storing and retrieving data
 * from a CSV file. It provides methods for creating new enquiries, loading existing enquiries,
 * searching by various criteria, and managing enquiry responses from officers.
 * </p>
 */
public class EnquiryRepository {
    /** The complete file path to the enquiry records file */
    private static final String FILE_PATH_ENQUIRY = "./src/repository/data/enquiry_records.csv";

    /** Repository instance for accessing project data */
    private static final ProjectRepository projectRepository = new ProjectRepository();

    /**
     * Generates the next sequential enquiry ID.
     * <p>
     * The format is "E" followed by a four-digit number (e.g., E0001, E0002).
     * </p>
     *
     * @return A new unique enquiry ID
     */
    public String generateNextEnquiryID() {
        int max = 0;
        // Load all enquiries directly from CSV
        List<Enquiry> enquiries = loadAllEnquiries();

        // Find the highest project number
        for (Enquiry enquiry : enquiries) {
            String existingID = enquiry.getEnquiryID();
            if (existingID.matches("E\\d+")) {
                int number = Integer.parseInt(existingID.substring(1));
                if (number > max) {
                    max = number;
                }
            }
        }

        int nextNumber = max + 1;
        return String.format("E%04d", nextNumber);  // e.g., E0001, E0002
    }

    /**
     * Creates a new enquiry and appends it to the CSV file.
     *
     * @param enquiry The Enquiry object to be added
     * @return true if the enquiry was successfully created, false otherwise
     * @throws IOException if an error occurs while writing to the file
     */
    public boolean createNewEnquiry(Enquiry enquiry) throws IOException {
        File file = new File(FILE_PATH_ENQUIRY);
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

            String enquiryData = enquiryToCSV(enquiry);

            writer.write(enquiryData);
        }
        return true;
    }

    /**
     * Converts an Enquiry object to a CSV record string.
     *
     * @param enquiry The Enquiry object to convert
     * @return A string representing the enquiry in CSV format
     */
    private static String enquiryToCSV(Enquiry enquiry) {
        return String.join(",",
                enquiry.getEnquiryID(),
                enquiry.getApplicantID(),
                enquiry.getProjectID(),
                enquiry.getEnquiryText(),
                enquiry.getEnquiryReply(),
                enquiry.getEnquiryStatus(),
                enquiry.getReplyingOfficerID());
    }

    /**
     * Loads all enquiries from the CSV file.
     *
     * @return A list of Enquiry objects
     */
    public List<Enquiry> loadAllEnquiries() {
        List<Enquiry> enquiries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_ENQUIRY))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] parts = line.split(",", -1);
                if (parts.length >= 7) {
                    Enquiry enquiry = new Enquiry(
                            parts[0].trim(),
                            parts[1].trim(),
                            parts[2].trim(),
                            parts[3].trim(),
                            parts[4].trim(),
                            parts[5].trim(),
                            parts[6].trim() // Replying officer ID
                    );
                    enquiries.add(enquiry);
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to read enquiries: " + e.getMessage());
        }
        return enquiries;
    }

    /**
     * Finds an enquiry by its ID.
     *
     * @param enquiryID The ID to look for
     * @return Enquiry if found, else null
     * @throws IOException if reading fails
     */
    public Enquiry getEnquiryById(String enquiryID) throws IOException {
        for (Enquiry e : loadAllEnquiries()) {
            if (e.getEnquiryID().equals(enquiryID)) return e;
        }
        return null;
    }

    /**
     * Gets the most recent enquiry ID.
     *
     * @return The last enquiry ID (or "E0000" if none)
     * @throws IOException if reading fails
     */
    public String getLastEnquiryId() throws IOException {
        List<Enquiry> all = loadAllEnquiries();
        if (all.isEmpty()) return "E0000";
        return all.get(all.size() - 1).getEnquiryID();
    }

    /**
     * Updates the reply and status of an enquiry.
     *
     * @param enquiryID The ID of the enquiry to update
     * @param replyText The text to reply with
     * @param officerId The ID of the officer making the reply
     * @return true if update was successful
     * @throws IOException if writing fails
     */
    public boolean replyToEnquiry(String enquiryID, String replyText, String officerId) throws IOException {
        List<Enquiry> enquiries = loadAllEnquiries();
        boolean updated = false;

        for (Enquiry e : enquiries) {
            if (e.getEnquiryID().equals(enquiryID)) {
                e.setEnquiryReply(replyText);
                e.setEnquiryStatus("REPLIED");
                e.setReplyingOfficerID(officerId);
                updated = true;
                break;
            }
        }

        if (updated) {
            overwriteCSV(enquiries);
        }
        return updated;
    }

    /**
     * Deletes an enquiry by its ID.
     *
     * @param enquiryID The ID of the enquiry to remove
     * @return true if deleted successfully
     * @throws IOException if writing fails
     */
    public boolean removeEnquiryById(String enquiryID) throws IOException {
        List<Enquiry> enquiries = loadAllEnquiries();
        boolean removed = enquiries.removeIf(e -> e.getEnquiryID().equals(enquiryID));

        if (removed) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH_ENQUIRY))) {
                // Write header
                writer.write("EnquiryID,ApplicantNRIC,ProjectID,EnquiryText,EnquiryResponse,EnquiryStatus,ResponderNRIC");
                writer.newLine();

                for (Enquiry e : enquiries) {
                    writer.write(enquiryToCSV(e));
                    writer.newLine();
                }
            }
        }
        return true;
    }

    /**
     * Gets all enquiries made by a specific applicant.
     *
     * @param applicantID The ID of the applicant
     * @return List of enquiries made by the applicant
     */
    public List<Enquiry> getEnquiriesByApplicantId(String applicantID) {
        List<Enquiry> result = new ArrayList<>();
        for (Enquiry e : loadAllEnquiries()) {
            if (e.getApplicantID().equals(applicantID)) {
                result.add(e);
            }
        }
        return result;
    }

    /**
     * Gets all enquiries made for a specific project.
     *
     * @param projectID The ID of the project
     * @return List of enquiries for the project
     * @throws IOException if reading fails
     */
    public List<Enquiry> getEnquiriesByProject(String projectID) throws IOException {
        List<Enquiry> result = new ArrayList<>();
        for (Enquiry e : loadAllEnquiries()) {
            if (e.getProjectID().equals(projectID)) {
                result.add(e);
            }
        }
        return result;
    }

    /**
     * Gets all enquiries that have been replied to by a specific officer.
     *
     * @param officerID The ID of the officer
     * @return List of enquiries replied to by the officer
     * @throws IOException if reading fails
     */
    public List<Enquiry> getEnquiriesRepliedByOfficer(String officerID) throws IOException {
        List<Enquiry> filteredEnquiries = new ArrayList<>();
        List<Enquiry> allEnquiries = loadAllEnquiries();

        for (Enquiry enquiry : allEnquiries) {
            if ("REPLIED".equalsIgnoreCase(enquiry.getEnquiryStatus()) &&
                    enquiry.getReplyingOfficerID() != null &&
                    enquiry.getReplyingOfficerID().equalsIgnoreCase(officerID)) {
                filteredEnquiries.add(enquiry);
            }
        }

        return filteredEnquiries;
    }

    /**
     * Gets all enquiries that are either pending or have been replied to by a specific officer.
     *
     * @param officerID The ID of the officer
     * @return List of pending or replied enquiries for the officer
     * @throws IOException if reading fails
     */
    public List<Enquiry> getPendingOrRepliedEnquiriesForOfficer(String officerID) throws IOException {
        List<Enquiry> filteredEnquiries = new ArrayList<>();
        List<Enquiry> allEnquiries = loadAllEnquiries();

        for (Enquiry enquiry : allEnquiries) {
            if (enquiry.getReplyingOfficerID() != null &&
                    enquiry.getReplyingOfficerID().equalsIgnoreCase(officerID) &&
                    (enquiry.getEnquiryStatus().equalsIgnoreCase("PENDING") ||
                            enquiry.getEnquiryStatus().equalsIgnoreCase("REPLIED"))) {
                filteredEnquiries.add(enquiry);
            }
        }

        return filteredEnquiries;
    }

    /**
     * Helper method to overwrite CSV file with a given list of enquiries.
     *
     * @param enquiries The list of enquiries to write to the file
     * @throws IOException if writing fails
     */
    private void overwriteCSV(List<Enquiry> enquiries) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH_ENQUIRY))) {
            writer.write("EnquiryID,ApplicantNRIC,ProjectID,EnquiryText,EnquiryResponse,EnquiryStatus,ResponderNRIC");
            writer.newLine();
            for (Enquiry e : enquiries) {
                String record = String.join(",",
                        e.getEnquiryID(),
                        e.getApplicantID(),
                        e.getProjectID(),
                        e.getEnquiryText(),
                        e.getEnquiryReply(),
                        e.getEnquiryStatus(),
                        e.getReplyingOfficerID());
                writer.write(record);
                writer.newLine();
            }
        }
    }

    /**
     * Updates an enquiry with a reply text and sets its status to replied.
     *
     * @param enquiryID The ID of the enquiry to update
     * @param replyText The text of the reply
     * @param officerId The ID of the officer making the reply
     * @throws IOException if writing fails
     */
    public void insertInquiryTextByEnquiryId(String enquiryID, String replyText, String officerId) throws IOException {
        List<Enquiry> enquiries = loadAllEnquiries();
        boolean updated = false;

        for (Enquiry e : enquiries) {
            if (e.getEnquiryID().equals(enquiryID)) {
                e.setEnquiryReply(replyText);
                e.setEnquiryStatus("REPLIED");
                e.setReplyingOfficerID(officerId); // Set the replying officer ID
                updated = true;
                break;
            }
        }

        if (updated) {
            overwriteCSV(enquiries);
        }
    }

    /**
     * Gets enquiries based on user type.
     * <p>
     * - Managers see all enquiries.
     * - Officers see enquiries for projects they are assigned to.
     * - Applicants see only their own enquiries.
     * </p>
     *
     * @param user The user requesting the enquiries
     * @return List of enquiries relevant to the user type
     */
    public List<Enquiry> getEnquiriesByUserType(User user) {
        List<Enquiry> enquiriesByUserType = new ArrayList<>();
        if (user instanceof Manager) {
            enquiriesByUserType = loadAllEnquiries();
        } else if (user instanceof Officer) {
            List<Project> projects = projectRepository.getProjectsByOfficerId(user.getNRIC());
            for (Project project : projects) {
                try {
                    List<Enquiry> enquiriesForOneProject = getEnquiriesByProject(project.getProjectID());
                    enquiriesByUserType.addAll(enquiriesForOneProject);
                } catch (IOException e) {
                    System.out.println("Failed to load enquiries for project: " + project.getProjectID());
                }
            }
        } else if (user instanceof Applicant) {
            enquiriesByUserType = getEnquiriesByApplicantId(user.getNRIC());
        }
        return enquiriesByUserType;
    }

    /**
     * Updates an existing enquiry in the CSV file.
     * <p>
     * This method finds and replaces the record with matching enquiry ID.
     * </p>
     *
     * @param updatedEnquiry The Enquiry object with updated information
     * @return true if update was successful, false otherwise
     * @throws IOException if reading or writing fails
     */
    public boolean updateEnquiry(Enquiry updatedEnquiry) throws IOException {
        File inputFile = new File(FILE_PATH_ENQUIRY);
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

                if (line.startsWith(updatedEnquiry.getEnquiryID() + ",")) {
                    updatedLines.add(enquiryToCSV(updatedEnquiry)); // Use helper method
                } else {
                    updatedLines.add(line); // Keep as is
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV for update: " + e.getMessage());
            return false;
        }

        // Write updated content back to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH_ENQUIRY, false))) {
            for (String updatedLine : updatedLines) {
                writer.write(updatedLine);
                writer.newLine();
            }
            System.out.println("Updated enquiry saved successfully.");
        } catch (IOException e) {
            System.out.println("Error writing updated CSV: " + e.getMessage());
        }
        return true;
    }
}