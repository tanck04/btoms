package repository;

import java.io.*;

public class UserLoginRepository {
private static final String FILE_PATH_USER_LOGIN = "./src/repository/data/user_login_records.csv";


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

