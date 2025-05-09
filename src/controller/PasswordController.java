package controller;

import model.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import static controller.SignInController.userLoginRepository;

/**
 * Controller class responsible for password management operations.
 * Handles password hashing, changing, and verification processes across all user types
 * in the BTO application system.
 */
public class PasswordController {

    /**
     * Hashes a plain-text password using SHA-256 algorithm.
     * Converts the resulting byte array to a hexadecimal string representation.
     *
     * @param password The plain-text password to be hashed
     * @return The hexadecimal string representation of the hashed password
     * @throws RuntimeException If the SHA-256 algorithm is not available
     */
    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Changes a user's password by hashing the new password and delegating to the
     * appropriate repository based on the user's role.
     *
     * @param nric The National Registration Identity Card (NRIC) of the user
     * @param newPassword The new plain-text password to be set
     * @return true if the password was successfully changed, false otherwise
     */
    public boolean changePassword(String nric, String newPassword) {
        String hashedPassword = hashPassword(newPassword);
        String role = userLoginRepository.getUserTypeByNRIC(nric);
        RepositoryController repositoryController = new RepositoryController();
        PasswordChangerInterface repository = (PasswordChangerInterface) repositoryController.getRepository(role);

        if (repository == null) {
            System.out.println("Invalid NRIC or password. Redirecting to main menu.");
            System.out.println();
            return false;
        }

        return repository.changePassword(nric, hashedPassword);
    }

    /**
     * Handles the complete password change workflow for a user.
     * Verifies the current password, collects and confirms the new password,
     * and processes the change if all validations pass.
     *
     * @param user The user object whose password is being changed
     */
    public void handlePasswordChange(User user){
        String nric = user.getNRIC();
        Scanner scanner = new Scanner(System.in);

        String role = userLoginRepository.getUserTypeByNRIC(nric);
        RepositoryController repositoryController = new RepositoryController();
        VerificationInterface repository = (VerificationInterface) repositoryController.getRepository(role);

        System.out.print("| Enter your current password: ");
        String currentPassword = scanner.nextLine();

        User verifiedUser = repository.verifyCredentials(nric, currentPassword);

        if (verifiedUser == null) {
            System.out.println("Incorrect current password. Password change aborted.");
            return;
        }

        System.out.print("| Enter new password: ");
        String newPassword = scanner.nextLine();

        System.out.print("| Confirm new password: ");
        String confirmPassword = scanner.nextLine();

        if (!newPassword.equals(confirmPassword)) {
            System.out.println("New passwords do not match. Password change aborted.");
            return;
        }

        boolean success = changePassword(nric, newPassword);

        if (success) {
            System.out.println("Password changed successfully.");
        } else {
            System.out.println("Failed to change password. Please try again.");
        }
    }
}