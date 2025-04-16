package view;

import controller.ApplicantController;
import controller.ApplicationController;
import controller.ProjectController;
import enums.MaritalStatus;
import enums.Role;
import enums.FlatType;
import model.Applicant;
import model.Project;
import model.User;

import java.util.List;
import java.util.Scanner;
import java.util.Map;

public class ApplicantView implements MenuInterface {
    private final ApplicantController applicantController = new ApplicantController();
    private final ApplicationController applicationController = new ApplicationController();
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
            System.out.println("| 5. Enquiry (Submit, View, Edit, Delete)       |");
            System.out.println("| 6. Logout                                     |");
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
                    // controller.getAvailableProjects();
                    break;
                case "6":
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
