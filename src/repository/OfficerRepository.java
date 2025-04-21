package repository;

import controller.*;
import enums.MaritalStatus;
import model.Officer;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Repository class for managing Officer data persistence.
 * <p>
 * This class handles CRUD operations for Officer objects, storing and retrieving data
 * from a CSV file. It implements multiple interfaces to support authentication,
 * password management, and security question functionality for officers.
 * </p>
 */
public class OfficerRepository implements PasswordChangerInterface, VerificationInterface, CheckSecQuesInterface, SecQuesChangerInterface {
    /** The folder where data files are stored */
    private static final String folder = "data";

    /** The filename for officer records */
    private static final String fileName = "officer_records.csv";

    /** The complete file path to the officer records file */
    private static final String filePath = "src/repository/" + folder + "/" + fileName;

    /**
     * Creates an Officer object from CSV record data.
     *
     * @param parts Array of strings representing fields from a CSV record
     * @return A new Officer object or null if creation fails
     */
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

    /**
     * Loads all officers from the CSV file.
     *
     * @return A list of Officer objects
     * @throws IOException If an error occurs while reading the file
     */
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

    /**
     * Finds an officer by their NRIC (National Registration Identity Card) number.
     *
     * @param nric The NRIC to search for
     * @return The found Officer or null if not found
     * @throws IOException If an error occurs while reading the file
     */
    public Officer findOfficerById(String nric) throws IOException {
        List<Officer> officers = loadOfficers();
        return officers.stream()
                .filter(officer -> officer.getNRIC().equals(nric))
                .findFirst()
                .orElse(null);
    }

    /**
     * Verifies officer credentials for authentication.
     *
     * @param id The NRIC of the officer
     * @param password The password to verify
     * @return The authenticated Officer or null if authentication fails
     */
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

    /**
     * Changes an officer's password.
     *
     * @param nric The NRIC of the officer
     * @param newHashedPassword The new hashed password
     * @return true if password was successfully updated, false otherwise
     */
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

    /**
     * Changes an officer's security question and answer.
     *
     * @param nric The NRIC of the officer
     * @param newSecQues The new security question
     * @param newSecAns The new security answer
     * @return true if security question/answer was successfully updated, false otherwise
     */
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
                        parts = Arrays.copyOf(parts, 7); // Extend to at least 7 elements
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

    /**
     * Checks if an officer has set up a security question.
     *
     * @param nric The NRIC of the officer
     * @return true if the officer has a security question, false otherwise
     */
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

    /**
     * Retrieves an officer's security question.
     *
     * @param nric The NRIC of the officer
     * @return The security question or an error message if not found
     */
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

    /**
     * Verifies if the provided answer matches the officer's security question answer.
     *
     * @param nric The NRIC of the officer
     * @param answer The answer to verify
     * @return true if the answer is correct, false otherwise
     */
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