package view;

import controller.*;
import enums.FlatType;
import model.*;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class OfficerView implements MenuInterface{
    private final HDBOfficerController officerController = new HDBOfficerController();
    private final HDBOfficerRegController officerRegController = new HDBOfficerRegController();
    private final EnquiryController enquiryController = new EnquiryController();
    private final ApplicationController applicationController = new ApplicationController();
    private final SecQuesController secQuesController = new SecQuesController();
    private final PasswordController passwordController = new PasswordController();
    private final Scanner scanner = new Scanner(System.in);
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
                    officerRegController.viewRegistrationStatus(user.getNRIC());
                    break;
                case "8":
                    enquiryController.viewEnquiry(user);
                    enquiryController.replyToEnquiry(user);
                    break;
                case "9":
                    officerController.BookBTO(user);
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
