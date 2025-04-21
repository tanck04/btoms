package controller;

import java.util.Scanner;

import static controller.SignInController.userLoginRepository;

/**
 * The main authentication controller for the Build-To-Order Management System (BTOMS).
 * This class manages the authentication flow including sign-in, password recovery,
 * and system navigation. It serves as the entry point for user interaction with the system.
 */
public class AuthenticationController {
    private final SignInController signInController = new SignInController();

    /**
     * Starts the authentication process and displays the main menu.
     * This method runs in a continuous loop until the user chooses to exit,
     * providing options for signing in, recovering passwords, and exiting the system.
     */
    public void start() {
        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("===================================================================");
            System.out.println();
            System.out.println(" ███████████  ███████████    ███████    ██████   ██████  █████████ ");
            System.out.println("░░███░░░░░███░█░░░███░░░█  ███░░░░░███ ░░██████ ██████  ███░░░░░███");
            System.out.println(" ░███    ░███░   ░███  ░  ███     ░░███ ░███░█████░███ ░███    ░░░ ");
            System.out.println(" ░██████████     ░███    ░███      ░███ ░███░░███ ░███ ░░█████████ ");
            System.out.println(" ░███░░░░░███    ░███    ░███      ░███ ░███ ░░░  ░███  ░░░░░░░░███");
            System.out.println(" ░███    ░███    ░███    ░░███     ███  ░███      ░███  ███    ░███");
            System.out.println(" ███████████     █████    ░░░███████░   █████     █████░░█████████ ");
            System.out.println("░░░░░░░░░░░     ░░░░░       ░░░░░░░    ░░░░░     ░░░░░  ░░░░░░░░░  ");
            System.out.println();
            System.out.println("        Welcome to Build-To-Order Management System (BTOMS)        ");
            System.out.println("===================================================================");
            System.out.println();
            System.out.println("How can we assist you today?");

            System.out.println("===================================");
            System.out.println("|           MAIN MENU             |");
            System.out.println("===================================");
            System.out.println("|  1. Sign In                     |");
            System.out.println("|  2. Forget Password             |");
            System.out.println("|  3. Exit                        |");
            System.out.println("===================================");

            int choice = -1;
            boolean validInput = false;

            while (!validInput) {
                System.out.print("Please select an option (1-3): ");

                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                    scanner.nextLine(); // Clear newline character

                    if (choice >= 1 && choice <= 4) {
                        validInput = true; // Valid input and within range
                    } else {
                        System.out.println("Invalid choice. Please enter a number between 1 and 3.");
                    }
                } else {
                    System.out.println("Invalid input. Please enter a number between 1 and 3.");
                    scanner.nextLine(); // Clear invalid input
                }
            }

            switch (choice) {
                case 1:
                    processSignIn(scanner);
                    break;
                case 2:
                    processPasswordRecovery(scanner);
                    break;
                case 3:
                    System.out.println("Thank you for using our system. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Please enter a number between 1 and 3.");
                    break;
            }
        }
    }

    /**
     * Handles the user sign-in process.
     * Validates the NRIC format and existence in the system before attempting login.
     *
     * @param scanner The Scanner object to capture user input
     */
    private void processSignIn(Scanner scanner) {
        System.out.println("\nPlease enter your credentials");
        System.out.println("+------------------------------+");

        String nric;
        do {
            System.out.print("| NRIC: ");
            nric = scanner.nextLine();

            if (!SignInController.isValidNRICFormat(nric)) {
                System.out.println("Invalid NRIC format.");
                nric = null; // reset so loop continues
            } else if (!userLoginRepository.userExists(nric)) {
                System.out.println("NRIC not found in the system. Please try again.");
                nric = null; // reset so loop continues
            }

        } while (nric == null);

        System.out.print("| Password: ");
        String password = scanner.nextLine();
        System.out.println("+------------------------------+");
        System.out.println("\nAttempting login with provided credentials...");
        SignInController.signIn(nric, password);
    }

    /**
     * Handles the password recovery process.
     * Verifies the user's identity using security questions before allowing password reset.
     *
     * @param scanner The Scanner object to capture user input
     */
    private void processPasswordRecovery(Scanner scanner) {
        boolean haveSetUpSecQues = false;
        boolean answer = false;
        System.out.println("\nAccount Recovery Page");
        System.out.println("+-------------------------------------+");

        String nric;
        do {
            System.out.print("| NRIC: ");
            nric = scanner.nextLine();

            if (!SignInController.isValidNRICFormat(nric)) {
                System.out.println("Invalid NRIC format. Please try again.");
                System.out.println("*NRIC should start with S/T, followed by 7 digits, and end with a capital letter (e.g., S1234567A).");
                nric = null; // reset so loop continues
            } else if (!userLoginRepository.userExists(nric)) {
                System.out.println("NRIC not found in the system. Please try again.");
                nric = null; // reset so loop continues
            }
        } while (nric == null);

        SecQuesController secQuesController = new SecQuesController();
        haveSetUpSecQues = secQuesController.haveSetSecQues(nric);

        if (!haveSetUpSecQues) {
            System.out.println("+---------------------------------------------------------------------------------------+");
            System.out.println("| Sorry, security question not set yet, please contact an administrator for assistance  |");
            System.out.println("+---------------------------------------------------------------------------------------+");
            return;
        }

        answer = secQuesController.askSecQues(nric);
        if (answer) {
            processPasswordReset(scanner, nric);
        } else {
            System.out.println("+-------------------------------------+");
            System.out.println("| Wrong answer to security question.  |");
            System.out.println("+-------------------------------------+");
        }
    }

    /**
     * Handles the password reset process after successful security question verification.
     * Ensures the new password is not the default password.
     *
     * @param scanner The Scanner object to capture user input
     * @param nric The user's NRIC for which the password is being reset
     */
    private void processPasswordReset(Scanner scanner, String nric) {
        String newPassword;
        System.out.println("+-------------------------------------+");
        System.out.println("|            Correct answer           |");
        System.out.println("+-------------------------------------+");
        System.out.print("| New password: ");

        do {
            newPassword = scanner.nextLine();
            if (newPassword.equals("password")) {
                System.out.println("The password cannot be the default 'password'. Please enter a new password:");
            }
        } while (newPassword.equals("password"));

        PasswordController passwordController = new PasswordController();
        if (passwordController.changePassword(nric, newPassword)) {
            System.out.println("+-------------------------------------+");
            System.out.println("|    Password successfully changed    |");
            System.out.println("+-------------------------------------+\n");
        } else {
            System.out.println("Something went wrong. Contact Administrator");
        }
    }
}