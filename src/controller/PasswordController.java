package controller;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static controller.SignInController.userLoginRepository;

public class PasswordController {
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
}
