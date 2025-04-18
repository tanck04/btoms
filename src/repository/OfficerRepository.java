package repository;

import controller.*;
import enums.MaritalStatus;
import model.Officer;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OfficerRepository implements PasswordChangerInterface, VerificationInterface, CheckSecQuesInterface, SecQuesChangerInterface {
    private static final String folder = "data";
    private static final String fileName = "officer_records.csv";
    private static final String filePath = "src/repository/" + folder + "/" + fileName;

    private Officer createOfficerFromCSV(String[] parts) {
        try {
            String nric = parts[0];
            String name = parts[1];
            int age = Integer.parseInt(parts[2]);
            MaritalStatus maritalStatus = MaritalStatus.valueOf(parts[3].toUpperCase());
            String password = parts[4];

            return new Officer(nric, name, password, age, maritalStatus);
        } catch (Exception e) {
            System.out.println("Error creating officer from CSV data: " + e.getMessage());
            return null;
        }
    }

    public List<Officer> loadOfficers() throws IOException {
        List<Officer> officers = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("Managers file not found, returning empty list");
            return officers;
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
                Officer officer = createOfficerFromCSV(data);
                if (officer != null) {
                    officers.add(officer);
                }
            }
        }

        return officers;
    }

    public Officer findOfficerById(String nric) throws IOException {
        List<Officer> officers = loadOfficers();
        return officers.stream()
                .filter(officer -> officer.getNRIC().equals(nric))
                .findFirst()
                .orElse(null);
    }

    public Officer verifyCredentials(String id, String password) {
        PasswordController pc = new PasswordController();
        String hashedInputPassword = pc.hashPassword(password);

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                Officer officer = createOfficerFromCSV(parts);
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

    @Override
    public boolean changeSecQuesAndAns(String nric, String newSecQues, String newSecAns) {
        List<String[]> records = new ArrayList<>();
        boolean secQuesUpdated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts[0].equals(nric)) {
                    // Ensure the CSV has enough columns for Question and Answer
                    if (parts.length < 7) {
                        parts = Arrays.copyOf(parts, 7); // Extend to at least 8 elements
                    }
                    // Update security question and answer
                    parts[5] = newSecQues;
                    parts[6] = newSecAns;
                    secQuesUpdated = true;
                }
                records.add(parts); // Add the record to the list
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
            return false; // Indicate failure
        }

        // Rewrite the file with updated records
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String[] record : records) {
                writer.write(String.join(",", record));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to the file: " + e.getMessage());
            return false; // Indicate failure
        }

        return secQuesUpdated;
    }

    @Override
    public boolean checkHaveSecQues(String nric) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 5 && parts[0].equals(nric) && !parts[5].isEmpty()) { // NRIC and Password
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String retrieveSecQues(String nric) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(nric) && !parts[5].isEmpty()) { // NRIC and Password
                    return parts[5];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Error retrieving security question"; // Default return value
    }

    @Override
    public boolean verifyAnsToSecQues(String nric, String answer) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(nric) && parts[6].equals(answer.toLowerCase())) { // Match NRIC and Answer
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
