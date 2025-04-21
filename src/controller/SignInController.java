package controller;

import model.User;
import repository.UserLoginRepository;
import view.MenuInterface;

import java.util.Scanner;

/**
 * Controller responsible for handling user sign-in operations.
 * Manages authentication, password verification, and initial user session setup.
 */
public class SignInController {
    /**
     * Repository for managing user login information.
     * Used throughout the class for user verification and role retrieval.
     */
    static UserLoginRepository userLoginRepository = new UserLoginRepository();

    /**
     * Authenticates a user with the provided credentials and manages the sign-in process.
     * This method handles:
     * <ul>
     *     <li>Role identification based on NRIC</li>
     *     <li>Credential verification</li>
     *     <li>First-time user password change</li>
     *     <li>Navigation to the appropriate user menu</li>
     * </ul>
     *
     * @param nric The user's National Registration Identity Card number
     * @param password The user's password
     * @return true if sign-in is successful, false otherwise
     */
    public static boolean signIn(String nric, String password) {
        String role = userLoginRepository.getUserTypeByNRIC(nric);

        if (role == null) {
            System.out.println("No role found for NRIC: " + nric + ". Returning to main menu.");
            return false;
        }

        RepositoryController repositoryController = new RepositoryController();
        VerificationInterface repository = (VerificationInterface) repositoryController.getRepository(role);

        User user = repository.verifyCredentials(nric, password);
        if(user == null){
            System.out.println("Wrong password. Returning to main menu.");
            System.out.println();
            return false;
        }
        else if (user != null && password.equals("password")){
            System.out.println("You are a new user with the default password, please change it");
            PasswordController pc = new PasswordController();
            System.out.println("Please enter a new password");
            Scanner scanner = new Scanner(System.in);
            String newPassword;
            do {
                newPassword = scanner.nextLine();
                if (newPassword.equals("password")) {
                    System.out.println("The password cannot be the default 'Password'. Please enter a new password:");
                }
            } while (newPassword.equals("password"));
            if(!pc.changePassword(nric, newPassword)){
                System.out.println("Something went wrong, please contact an administrator");
            }
            System.out.println("Your password has been successfully changed, proceeding to login...");
            System.out.println("\nWelcome, " + user.getName() + ".");
            ViewController viewController = new ViewController();
            MenuInterface view = (MenuInterface) viewController.getView(role);
            view.displayMenu(user);
            return true;
        }
        else{
            System.out.println("Login successful.");
            System.out.println("\nWelcome, " + user.getName() + ".");
            ViewController viewController = new ViewController();
            MenuInterface view = (MenuInterface) viewController.getView(role);
            view.displayMenu(user);
            return true;
        }
    }

    /**
     * Validates the format of a National Registration Identity Card (NRIC) number.
     * Valid format is a letter ('S' or 'T'), followed by 7 digits, ending with a capital letter.
     *
     * @param nric The NRIC string to validate
     * @return true if the NRIC format is valid, false otherwise
     */
    public static boolean isValidNRICFormat(String nric) {
        if (nric == null) {
            return false;
        }
        return nric.matches("^[ST]\\d{7}[A-Z]$");
    }
}