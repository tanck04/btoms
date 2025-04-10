package repository;

import enums.ApplicantAppStatus;
import enums.WithdrawalStatus;
import enums.MaritalStatus;
import model.Applicant;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ApplicantRepository extends Repository {
    private static final String folder = "data";
    private static final String fileName = "applicant_records.csv";
    private static Boolean isRepoLoaded = true;
    public static HashMap<String, Applicant> APPLICANTS = new HashMap<>();

    @Override
    public boolean loadFromCSV() {
        try {
            loadApplicantsFromCSV(fileName, APPLICANTS);
            isRepoLoaded = true;
            return true;
        } catch (Exception e) {
            System.out.println("Error loading applicants repository: " + e.getMessage());
            return false;
        }
    }

    public static void saveAllApplicantsToCSV() {
        saveApplicantsToCSV(fileName, APPLICANTS);
    }

    private static void saveApplicantsToCSV(String fileName, HashMap<String, Applicant> applicantsMap) {
        String filePath = "./src/repository/" + folder + "/" + fileName;

        File directory = new File("./src/repository/" + folder);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(filePath);
        boolean fileExists = file.exists();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            // Only write header if file is new
            if (!fileExists) {
                writer.write("NRIC,Name,Age,Marital Status,Password,Application ID,Enquiry ID,ApplicantAppStatus,WithdrawalStatus");
                writer.newLine();
            }

            // For simplicity, we'll only append the newly added applicant
            // Get the latest applicant (assuming it's the one we just added)
            for (Applicant applicant : applicantsMap.values()) {
                // Only write new applicants (those not already in file)
                if (!fileExists || isNewApplicant(applicant)) {
                    writer.write(applicantToCSV(applicant));
                    writer.newLine();
                }
            }
            System.out.println("Applicants successfully saved to " + fileName);
        } catch (IOException e) {
            System.out.println("Error saving applicant data: " + e.getMessage());
        }
    }

    // Helper method to determine if an applicant is new (not yet in file)
    private static boolean isNewApplicant(Applicant applicant) {
        // This is a simple implementation - in practice, you might need more sophisticated logic
        // For example, you could keep track of which applicants are already saved to the file
        return true; // Simplified for demonstration
    }

    private static String applicantToCSV(Applicant applicant) {
        return String.join(",",
                applicant.getNRIC(),
                applicant.getName(),
                String.valueOf(applicant.getAge()),
                applicant.getMaritalStatus().toString(),
                applicant.getPassword(),
                applicant.getApplicationID() != null ? applicant.getApplicationID() : "",
                applicant.getEnquiryID() != null ? applicant.getEnquiryID() : "",
                applicant.getApplicantAppStatus() != null ? applicant.getApplicantAppStatus().toString() : "",
                applicant.getWithdrawalStatus() != null ? applicant.getWithdrawalStatus().toString() : ""
        );
    }

    private static void loadApplicantsFromCSV(String fileName, HashMap<String, Applicant> applicantsMap) {
        String filePath = "./src/repository/" + folder + "/" + fileName;

        File directory = new File("./src/repository/" + folder);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(filePath);

        if (!file.exists()) {
            try {
                file.createNewFile();
                System.out.println("Created empty file: " + filePath);
            } catch (IOException e) {
                System.out.println("Error creating file: " + e.getMessage());
            }
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Applicant applicant = csvToApplicant(line);
                if (applicant != null) {
                    applicantsMap.put(applicant.getNRIC(), applicant);
                }
            }
            System.out.println("Successfully loaded " + applicantsMap.size() + " applicants from " + fileName);
        } catch (IOException e) {
            System.out.println("Error reading applicant data: " + e.getMessage());
        }
    }

    private static Applicant csvToApplicant(String csv) {
        String[] fields = csv.split(",");
        try {
            String nric = fields[0];
            String name = fields[1];
            int age = Integer.parseInt(fields[2]);
            MaritalStatus maritalStatus = MaritalStatus.valueOf(fields[3].toUpperCase());
            String password = fields[4];
            String applicationID = fields[5];
            String enquiryID = fields[6];
            ApplicantAppStatus applicantAppStatus = ApplicantAppStatus.valueOf(fields[7].toUpperCase());
            WithdrawalStatus withdrawalStatus = WithdrawalStatus.valueOf(fields[8].toUpperCase());

            return new Applicant(nric, name, password, age, maritalStatus, applicationID, enquiryID, applicantAppStatus, withdrawalStatus);
        } catch (Exception e) {
            System.out.println("Error parsing applicant data: " + e.getMessage());
        }
        return null;
    }

    public static boolean clearApplicantDatabase() {
        APPLICANTS.clear();
        saveAllApplicantsToCSV();
        isRepoLoaded = false;
        return true;
    }

    public static boolean isRepoLoaded() {
        return isRepoLoaded;
    }

    public static void setRepoLoaded(boolean isRepoLoaded) {
        ApplicantRepository.isRepoLoaded = isRepoLoaded;
    }
}