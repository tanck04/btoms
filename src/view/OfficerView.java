package view;

import controller.*;
import model.*;

import java.util.Scanner;

/**
 * View class responsible for displaying the officer user interface.
 * <p>
 * This class implements the MenuInterface and provides a command-line interface
 * for officer users. It offers various functionality including viewing projects,
 * submitting and managing applications, handling enquiries, registering for projects,
 * booking flats for applicants, and managing account security.
 * </p>
 */
public class OfficerView implements MenuInterface{
    /** Controller for HDB officer-specific operations */
    private final HDBOfficerController officerController = new HDBOfficerController();

    /** Controller for officer registration operations */
    private final HDBOfficerRegController officerRegController = new HDBOfficerRegController();

    /** Controller for enquiry management */
    private final EnquiryController enquiryController = new EnquiryController();

    /** Controller for application-specific operations */
    private final ApplicationController applicationController = new ApplicationController();

    /** Controller for security question operations */
    private final SecQuesController secQuesController = new SecQuesController();

    /** Controller for password management */
    private final PasswordController passwordController = new PasswordController();

    /** Scanner for handling user input */
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Displays the main menu for officer users and processes their selections.
     * <p>
     * This method presents a menu with options for viewing projects, submitting applications,
     * checking application status, requesting withdrawals, managing enquiries, registering for
     * projects, viewing registration status, replying to enquiries, booking flats for applicants,
     * and managing account security. It continues to display the menu until the user chooses to logout.
     * </p>
     *
     * @param user The currently authenticated User object
     */
    public void displayMenu(User user) {
        boolean running = true;
        while (running){
            System.out.println();
            System.out.println("+-----------------------------------------------+");
            System.out.println("|                 Officer Menu                  |");
            System.out.println("+-----------------------------------------------+");
            System.out.println("| 1. View Projects                              |");
            System.out.println("| 2. Submit Application                         |");
            System.out.println("| 3. View Application Status                    |");
            System.out.println("| 4. Request Withdrawal for Application         |");
            System.out.println("| 5. Enquiry (Submit, View, Edit, Delete)       |");
            System.out.println("| 6. Register to Join a Project                 |");
            System.out.println("| 7. View Registration Status                   |");
            System.out.println("| 8. View and Reply to Enquiries                |");
            System.out.println("| 9. Book a Flat for Applicant                  |"); //need to generate receipt after that
            System.out.println("| 10. Set Security Question for Recovery        |");
            System.out.println("| 11. Change Password                           |");
            System.out.println("| 12. Logout                                    |");
            System.out.println("+-----------------------------------------------+");
            System.out.println();
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    officerController.viewProject(user);
                    break;
                case "2":
                    officerController.submitApplication(user);
                    break;
                case "3":
                    officerController.checkApplicationStatus(user);
                    break;
                case "4":
                    applicationController.requestWithdrawal(user);
                    break;
                case "5":
                    enquiryController.handleEnquiries(user, true);
                    break;
                case "6":
                    officerRegController.createRegistration(user);
                    break;
                case "7":
                    officerRegController.viewRegistrationStatus(user);
                    break;
                case "8":
                    enquiryController.viewEnquiry(user);
                    enquiryController.replyToEnquiry(user);
                    break;
                case "9":
                    officerController.bookBTO(user);
                    break;
                case "10":
                    secQuesController.changeSecurityQuestionAndAnswer(user);
                    break;
                case "11":
                    passwordController.handlePasswordChange(user);
                    break;
                case "12":
                    System.out.println("Logging out...");
                    running = false;
                    break;
                default:
                    break;
            }
        }
    }
}