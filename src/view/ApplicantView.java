package view;

import controller.*;
import model.User;

import java.util.Scanner;

/**
 * View class responsible for displaying the applicant user interface.
 * <p>
 * This class implements the MenuInterface and provides a command-line interface
 * for applicant users. It offers various functionality including viewing projects,
 * submitting applications, checking application status, managing enquiries,
 * and handling account security.
 * </p>
 */
public class ApplicantView implements MenuInterface {
    /** Controller for applicant-specific operations */
    private final ApplicantController applicantController = new ApplicantController();

    /** Controller for application-specific operations */
    private final ApplicationController applicationController = new ApplicationController();

    /** Controller for enquiry management */
    private final EnquiryController enquiryController = new EnquiryController();

    /** Controller for security question operations */
    private final SecQuesController secQuesController = new SecQuesController();

    /** Controller for password management */
    private final PasswordController passwordController = new PasswordController();

    /** Scanner for handling user input */
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Displays the main menu for applicant users and processes their selections.
     * <p>
     * This method presents a menu with options for viewing projects, submitting applications,
     * checking application status, requesting withdrawals, managing enquiries, setting security
     * questions, and changing passwords. It continues to display the menu until the user
     * chooses to logout.
     * </p>
     *
     * @param user The currently authenticated User object
     */
    @Override
    public void displayMenu(User user) {
        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println("+-----------------------------------------------+");
            System.out.println("|                 Applicant Menu                |");
            System.out.println("+-----------------------------------------------+");
            System.out.println("| 1. View Projects                              |");
            System.out.println("| 2. Submit Application                         |");
            System.out.println("| 3. View Application Status                    |");
            System.out.println("| 4. Request Withdrawal for Application         |");
            System.out.println("| 5. Enquiry (View, Submit, Edit, Delete)       |");
            System.out.println("| 6. Set Security Question for Recovery         |");
            System.out.println("| 7. Change Password                            |");
            System.out.println("| 8. Logout                                     |");
            System.out.println("+-----------------------------------------------+");
            System.out.println();
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    applicantController.viewProject(user);
                    break;
                case "2":
                    applicantController.submitApplication(user);
                    break;
                case "3":
                    applicantController.checkApplicationStatus(user);
                    break;
                case "4":
                    applicationController.requestWithdrawal(user);
                    break;
                case "5":
                    enquiryController.handleEnquiries(user, true);
                    break;
                case "6":
                    secQuesController.changeSecurityQuestionAndAnswer(user);
                    break;
                case "7":
                    passwordController.handlePasswordChange(user);
                    break;
                case "8":
                    System.out.println("Logging out...");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }

            // Add a small pause before showing the menu again (except when logging out)
            if (running) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }
    }
}