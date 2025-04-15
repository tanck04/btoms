package repository;

import controller.PasswordChangerInterface;
import controller.PasswordController;
import controller.VerificationInterface;
import enums.ApplicantAppStatus;
import enums.WithdrawalStatus;
import enums.MaritalStatus;
import model.Applicant;
import model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ApplicantRepository extends Repository  implements VerificationInterface, PasswordChangerInterface {
    private static final String folder = "data";
    private static final String fileName = "applicant_records.csv";
    private static Boolean isRepoLoaded = true;
    public static HashMap<String, Applicant> APPLICANTS = new HashMap<>();
    private static final String filePath = "./src/repository/" + folder + "/" + fileName;
    @Override
    public boolean loadFromCSV() {
        try {
            loadApplicantsFromCSV(filePath, APPLICANTS);
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

        File directory = new File("./src/repository/" + folder);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) { // false = overwrite mode
            // Write header
            writer.write("NRIC,Name,Age,Marital Status,Password");
            writer.newLine();

            // Write all applicants from the HashMap
            for (Applicant applicant : applicantsMap.values()) {
                writer.write(applicantToCSV(applicant));
                writer.newLine();
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
                applicant.getPassword()
        );
    }

    private static void loadApplicantsFromCSV(String fileName, HashMap<String, Applicant> applicantsMap) {

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
            // Skip empty lines
            if (csv == null || csv.trim().isEmpty()) {
                return null;
            }

            String[] fields = csv.split(",");
            try {
                // Skip if fields array is too small or this looks like a header row
                if (fields.length < 5 || fields[0].equalsIgnoreCase("NRIC")) {
                    return null;
                }

                String nric = fields[0];
                String name = fields[1];
                int age = Integer.parseInt(fields[2]);
                MaritalStatus maritalStatus = MaritalStatus.valueOf(fields[3].toUpperCase());
                String password = fields[4];

                // Create applicant with all the correct values
                return new Applicant(nric, name, password, age, maritalStatus);
            } catch (Exception e) {
                System.out.println("Error parsing applicant data: " + csv + " - " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
    public static void saveNewApplicantToCSV(Applicant applicant) {

        File directory = new File("./src/repository/" + folder);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try {
            // Read existing content
            List<String> lines = new ArrayList<>();
            File file = new File(filePath);
            boolean fileExists = file.exists() && file.length() > 0;

            if (fileExists) {
                try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        lines.add(line);
                    }
                }
            }

            // Write all content including new applicant
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
                // Write header if file is new or empty
                if (!fileExists) {
                    writer.write("NRIC,Name,Age,Marital Status,Password");
                } else {
                    writer.write(lines.get(0)); // Write existing header
                }

                // Write existing applicants
                for (int i = 1; i < lines.size(); i++) {
                    writer.newLine();
                    writer.write(lines.get(i));
                }

                // Write the new applicant
                writer.newLine();
                writer.write(applicantToCSV(applicant));

                System.out.println("Applicant successfully saved to " + fileName);
            }
        } catch (IOException e) {
            System.out.println("Error saving applicant data: " + e.getMessage());
        }
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

    public Applicant verifyCredentials(String id, String password) {
        PasswordController pc = new PasswordController();
        String hashedInputPassword = pc.hashPassword(password);

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                Applicant applicant = csvToApplicant(line);
                if (applicant != null && applicant.getNRIC().equals(id)) {
                    // Check for default password OR hashed password match
                    if (applicant.getPassword().equals("password") && password.equals("password")) {
                        return applicant;
                    } else if (applicant.getPassword().equals(hashedInputPassword)) {
                        return applicant;
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
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
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

}