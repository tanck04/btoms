package view;

import controller.*;
import model.User;

import java.util.Scanner;

public class ApplicantView implements MenuInterface {
    private final ApplicantController applicantController = new ApplicantController();
    private final ApplicationController applicationController = new ApplicationController();
    private final EnquiryController enquiryController = new EnquiryController();
    private final SecQuesController secQuesController = new SecQuesController();
    private final PasswordController passwordController = new PasswordController();
    private final Scanner scanner = new Scanner(System.in);

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
