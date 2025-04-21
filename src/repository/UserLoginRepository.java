package repository;

import java.io.*;

/**
 * Repository class for managing user login information and authentication.
 * <p>
 * This class provides methods for retrieving user type information and checking
 * user existence from a CSV file containing user login records. It supports the
 * application's authentication system by validating user identities based on their
 * National Registration Identity Card (NRIC) numbers.
 * </p>
 */
public class UserLoginRepository {
    /** The complete file path to the user login records file */
    private static final String FILE_PATH_USER_LOGIN = "./src/repository/data/user_login_records.csv";

    /**
     * Retrieves the user type associated with a given NRIC.
     * <p>
     * This method searches the login records file for a matching NRIC and returns
     * the corresponding user type (e.g., "Applicant", "Officer", "Manager").
     * </p>
     *
     * @param nric The National Registration Identity Card number to search for
     * @return The user type if found, or null if the NRIC is not found
     */
    public String getUserTypeByNRIC(String nric) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH_USER_LOGIN))) {
            String line;
            br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2 && parts[0].trim().equalsIgnoreCase(nric)) {
                    return parts[1].trim(); // return user type
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV: " + e.getMessage());
        }

        return null; // Not found
    }

    /**
     * Checks if a user with the given NRIC exists in the system.
     * <p>
     * This method searches the login records file to determine if a user with
     * the specified NRIC is registered in the system.
     * </p>
     *
     * @param nric The National Registration Identity Card number to check
     * @return true if the user exists, false otherwise
     */
    public boolean userExists(String nric) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH_USER_LOGIN))) {
            String line;
            br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1 && parts[0].trim().equalsIgnoreCase(nric)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV: " + e.getMessage());
        }

        return false;
    }
}