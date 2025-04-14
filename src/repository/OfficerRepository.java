package repository;

import controller.PasswordChangerInterface;
import controller.PasswordController;
import controller.VerificationInterface;
import enums.MaritalStatus;
import model.Officer;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OfficerRepository extends Repository implements PasswordChangerInterface, VerificationInterface {
    private static final String folder = "data";
    private static final String fileName = "officer_records.csv";
    private static boolean isRepoLoaded = false;
    public static HashMap<String, Officer> OFFICERS = new HashMap<>();
    private static final String FILE_PATH_OFFICER = "src/repository/" + folder + "/" + fileName;

    @Override
    public boolean loadFromCSV() {
        try {
            loadOfficersFromCSV(FILE_PATH_OFFICER, OFFICERS);
            isRepoLoaded = true;
            return true;
        } catch (Exception e) {
            System.out.println("Error loading HDB Officer repository: " + e.getMessage());
            return false;
        }
    }

    private static void loadOfficersFromCSV(String fileName, HashMap<String, Officer> officersMap) {
        File file = new File(FILE_PATH_OFFICER);
        if (!file.exists()) {
            System.out.println("File not found: " + FILE_PATH_OFFICER);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_OFFICER))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                // Skip the header row
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                Officer officer = csvToOfficer(line);
                if (officer != null) {
                    officersMap.put(officer.getNRIC(), officer);
                }
            }
            System.out.println("Successfully loaded " + officersMap.size() + " officers from " + fileName);
        } catch (IOException e) {
            System.out.println("Error reading officer data: " + e.getMessage());
        }
    }

    private static Officer csvToOfficer(String csv) {
        String[] fields = csv.split(",");
        try {
            // Skip if this looks like a header row
            if (fields[1].equalsIgnoreCase("Name")) {
                return null;
            }
            String nric = fields[0];
            String name = fields[1];
            int age = Integer.parseInt(fields[2]);
            MaritalStatus maritalStatus = MaritalStatus.valueOf(fields[3].toUpperCase());
            String password = fields[4];

            return new Officer(nric, name, password, age, maritalStatus);
        } catch (Exception e) {
            System.out.println("Error parsing officer data: " + csv + " - " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Officer verifyCredentials(String id, String password) {
        PasswordController pc = new PasswordController();
        String hashedInputPassword = pc.hashPassword(password);

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_OFFICER))) {
            String line;
            reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                Officer officer = csvToOfficer(line);
                if (officer != null && officer.getNRIC().equals(id)) {
                    // Check for default password OR hashed password match
                    if (officer.getPassword().equals("password") && password.equals("password")) {
                        return officer;
                    } else if (officer.getPassword().equals(hashedInputPassword)) {
                        return officer;
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
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_OFFICER))) {
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH_OFFICER))) {
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
    public static boolean isRepoLoaded() {
        return isRepoLoaded;
    }

    public static void setRepoLoaded(boolean repoLoaded) {
        isRepoLoaded = repoLoaded;
    }
}
