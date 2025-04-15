package repository;

import model.Enquiry;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

//methods:
public class EnquiryRepository {
    private static final String FILE_PATH_ENQUIRY = "./src/repository/data/enquiry_records.csv";

    /**
     * Creates a new enquiry and appends it to the CSV file.
     *
     * @param enquiry The Enquiry object to be added.
     * @throws IOException if an error occurs while writing to the file.
     */
    public void createNewEnquiry(Enquiry enquiry) throws IOException {
        File file = new File(FILE_PATH_ENQUIRY);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            // Write a newline if file has content
            if (file.length() > 0) writer.newLine();

            String record = String.join(",",
                    enquiry.getEnquiryID(),
                    enquiry.getApplicantID(),
                    enquiry.getProjectID(),
                    enquiry.getEnquiryText(),
                    enquiry.getEnquiryReply(),
                    enquiry.getEnquiryStatus());
            writer.write(record);
        }
    }

    /**
     * Loads all enquiries from the CSV file.
     *
     * @return A list of Enquiry objects.
     * @throws IOException if an error occurs while reading the file.
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
                            parts[6].trim() // Assuming this is the replying officer ID
                    );
                    enquiries.add(enquiry);
                }
            }
        }catch (IOException e) {
            System.out.println("Failed to read enquiries: " + e.getMessage());
            // Optional: e.printStackTrace();
        }
        return enquiries;
    }

    /**
     * Finds an enquiry by its ID.
     *
     * @param enquiryID The ID to look for.
     * @return Enquiry if found, else null.
     * @throws IOException if reading fails.
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
     * @return The last enquiry ID (or "E0000" if none).
     * @throws IOException if reading fails.
     */
    public String getLastEnquiryId() throws IOException {
        List<Enquiry> all = loadAllEnquiries();
        if (all.isEmpty()) return "E0000";
        return all.get(all.size() - 1).getEnquiryID();
    }

    /**
     * Updates the reply and status of an enquiry.
     *
     * @param enquiryID Enquiry to update.
     * @param replyText Text to reply with.
     * @return true if successful.
     * @throws IOException if writing fails.
     */
    public boolean replyToEnquiry(String enquiryID, String replyText) throws IOException {
        List<Enquiry> enquiries = loadAllEnquiries();
        boolean updated = false;

        for (Enquiry e : enquiries) {
            if (e.getEnquiryID().equals(enquiryID)) {
                e.setEnquiryReply(replyText);
                e.setEnquiryStatus("REPLIED");
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
     * @param enquiryID The ID to remove.
     * @return true if deleted.
     * @throws IOException if writing fails.
     */
    public boolean removeEnquiryById(String enquiryID) throws IOException {
        List<Enquiry> enquiries = loadAllEnquiries();
        boolean removed = enquiries.removeIf(e -> e.getEnquiryID().equals(enquiryID));

        if (removed) {
            overwriteCSV(enquiries);
        }
        return removed;
    }

    /**
     * Gets all enquiries made by a specific applicant.
     *
     * @param applicantID The applicant ID.
     * @return List of Enquiries.
     * @throws IOException if reading fails.
     */
    public List<Enquiry> getEnquiriesByApplicantId(String applicantID) throws IOException {
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
     * @param projectID The project ID.
     * @return List of Enquiries.
     * @throws IOException if reading fails.
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
     * Helper to overwrite CSV file with given list of enquiries.
     */
    private void overwriteCSV(List<Enquiry> enquiries) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH_ENQUIRY))) {
            writer.write("Enquiry ID,Applicant ID,Project ID,Enquiry Text,Enquiry Reply,Enquiry Status");
            writer.newLine();
            for (Enquiry e : enquiries) {
                String record = String.join(",",
                        e.getEnquiryID(),
                        e.getApplicantID(),
                        e.getProjectID(),
                        e.getEnquiryText(),
                        e.getEnquiryReply(),
                        e.getEnquiryStatus());
                writer.write(record);
                writer.newLine();
            }
        }
    }
}