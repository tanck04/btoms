package repository;

import controller.*;
import model.Manager;
import enums.MaritalStatus;
import model.Project;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Repository class for managing Manager data persistence.
 * <p>
 * This class handles CRUD operations for Manager objects, storing and retrieving data
 * from a CSV file. It implements multiple interfaces to support authentication,
 * password management, and security question functionality for managers.
 * </p>
 */
public class ManagerRepository implements PasswordChangerInterface,VerificationInterface, CheckSecQuesInterface, SecQuesChangerInterface {
    /** The folder where data files are stored */
    private static final String folder = "data";

    /** The filename for manager records */
    private static final String fileName = "manager_records.csv";

    /** The complete file path to the manager records file */
    private static final String filePath = "./src/repository/" + folder + "/" + fileName;

    /**
     * Creates a Manager object from CSV record data.
     *
     * @param parts Array of strings representing fields from a CSV record
     * @return A new Manager object or null if creation fails
     */
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

    /**
     * Loads all managers from the CSV file.
     *
     * @return A list of Manager objects
     * @throws IOException If an error occurs while reading the file
     */
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

    /**
     * Finds a manager by their NRIC (National Registration Identity Card) number.
     *
     * @param nric The NRIC to search for
     * @return The found Manager or null if not found
     * @throws IOException If an error occurs while reading the file
     */
    public Manager findManagerById(String nric) throws IOException {
        List<Manager> managers = loadApplicants();
        return managers.stream()
                .filter(manager -> manager.getNRIC().equals(nric))
                .findFirst()
                .orElse(null);
    }

    /**
     * Verifies manager credentials for authentication.
     *
     * @param id The NRIC of the manager
     * @param password The password to verify
     * @return The authenticated Manager or null if authentication fails
     */
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

    /**
     * Changes a manager's password.
     *
     * @param nric The NRIC of the manager
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
     * Updates a manager's record with a project assignment.
     *
     * @param updatedManager The Manager object with updated information
     * @param project The Project to assign to the manager
     */
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

    /**
     * Changes a manager's security question and answer.
     *
     * @param nric The NRIC of the manager
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
                    if (parts.length < 8) {
                        parts = Arrays.copyOf(parts, 8); // Extend to at least 8 elements
                    }
                    // Update security question and answer
                    parts[6] = newSecQues;
                    parts[7] = newSecAns;
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
     * Checks if a manager has set up a security question.
     *
     * @param nric The NRIC of the manager
     * @return true if the manager has a security question, false otherwise
     */
    @Override
    public boolean checkHaveSecQues(String nric) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 6 && parts[0].equals(nric) && !parts[6].isEmpty()) { // NRIC and Password
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves a manager's security question.
     *
     * @param nric The NRIC of the manager
     * @return The security question or an error message if not found
     */
    @Override
    public String retrieveSecQues(String nric) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(nric) && !parts[6].isEmpty()) { // NRIC and Password
                    return parts[6];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Error retrieving security question"; // Default return value
    }

    /**
     * Verifies if the provided answer matches the manager's security question answer.
     *
     * @param nric The NRIC of the manager
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
                if (parts[0].equals(nric) && parts[7].equals(answer.toLowerCase())) { // Match ID and Answer
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}