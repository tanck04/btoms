package repository;

import controller.PasswordChangerInterface;
import controller.PasswordController;
import controller.VerificationInterface;
import model.Applicant;
import model.Application;
import model.Manager;
import enums.MaritalStatus;
import model.Project;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ManagerRepository implements PasswordChangerInterface,VerificationInterface {
    private static final String folder = "data";
    private static final String fileName = "manager_records.csv";
    private static final String filePath = "./src/repository/" + folder + "/" + fileName;

    private Manager createManagerFromCSV(String[] parts) {
        try {
            String nric = parts[0];
            String name = parts[1];
            int age = Integer.parseInt(parts[2]);
            MaritalStatus maritalStatus = MaritalStatus.valueOf(parts[3].toUpperCase());
            String password = parts[4];

            return new Manager(nric, name, password, maritalStatus, age);
        } catch (Exception e) {
            System.out.println("Error creating manager from CSV data: " + e.getMessage());
            return null;
        }
    }

    public List<Manager> loadApplicants() throws IOException {
        List<Manager> managers = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("Managers file not found, returning empty list");
            return managers;
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
                Manager manager = createManagerFromCSV(data);
                if (manager != null) {
                    managers.add(manager);
                }
            }
        }

        return managers;
    }

    public Manager findManagerById(String nric) throws IOException {
        List<Manager> managers = loadApplicants();
        return managers.stream()
                .filter(manager -> manager.getNRIC().equals(nric))
                .findFirst()
                .orElse(null);
    }

    public Manager verifyCredentials(String id, String password) {
        PasswordController pc = new PasswordController();
        String hashedInputPassword = pc.hashPassword(password);

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                Manager hdbmanager = createManagerFromCSV(parts);
                if (hdbmanager != null && hdbmanager.getNRIC().equals(id)) {
                    // Check for default password OR hashed password match
                    if (hdbmanager.getPassword().equals("password") && password.equals("password")) {
                        return hdbmanager;
                    } else if (hdbmanager.getPassword().equals(hashedInputPassword)) {
                        return hdbmanager;
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

    public void updateManagerInCSV(Manager updatedManager, Project project) {
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

                if (line.startsWith(updatedManager.getNRIC() + ",")) {
                    // Get the existing line parts
                    String[] parts = line.split(",");

                    // Update the line with project information
                    // Format: NRIC,Name,Age,MaritalStatus,Password,[ProjectID]
                    StringBuilder updatedLine = new StringBuilder();
                    updatedLine.append(String.join(",", parts));

                    // Add project ID if it's not already in the line
                    if (parts.length < 6) {
                        updatedLine.append(",").append(project.getProjectID());
                    } else {
                        // Replace the last part with new project ID
                        updatedLine = new StringBuilder(line.substring(0, line.lastIndexOf(",")));
                        updatedLine.append(",").append(project.getProjectID());
                    }

                    updatedLines.add(updatedLine.toString());
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
            System.out.println("Manager updated with new project successfully.");
        } catch (IOException e) {
            System.out.println("Error writing updated CSV: " + e.getMessage());
        }
    }

}
