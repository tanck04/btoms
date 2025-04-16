package repository;

import controller.PasswordChangerInterface;
import controller.PasswordController;
import controller.VerificationInterface;
import enums.*;
import model.Applicant;
import model.Project;
import model.User;

import java.io.*;
import java.util.*;

public class ApplicantRepository implements VerificationInterface, PasswordChangerInterface {
    private static final String folder = "data";
    private static final String fileName = "applicant_records.csv";
    private static final String filePath = "./src/repository/" + folder + "/" + fileName;

    private Applicant createApplicantFromCSV(String[] parts) {
        try {
            String nric = parts[0];
            String name = parts[1];
            int age = Integer.parseInt(parts[2]);
            MaritalStatus maritalStatus = MaritalStatus.valueOf(parts[3].toUpperCase());
            String password = parts[4];

            return new Applicant(nric, name, password, age, maritalStatus);
        } catch (Exception e) {
            System.out.println("Error creating applicant from CSV data: " + e.getMessage());
            return null;
        }
    }

    public List<Applicant> loadApplicants() throws IOException {
        List<Applicant> applicants = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("Applicants file not found, returning empty list");
            return applicants;
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
                Applicant applicant = createApplicantFromCSV(data);
                if (applicant != null) {
                    applicants.add(applicant);
                }
            }
        }

        return applicants;
    }

    public Applicant findApplicantById(String nric) throws IOException {
        List<Applicant> applicants = loadApplicants();
        return applicants.stream()
                .filter(applicant -> applicant.getNRIC().equals(nric))
                .findFirst()
                .orElse(null);
    }

    public Applicant verifyCredentials(String id, String password) {
        PasswordController pc = new PasswordController();
        String hashedInputPassword = pc.hashPassword(password);

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                Applicant applicant = createApplicantFromCSV(parts);
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