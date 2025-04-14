package view;

import controller.HDBOfficerController;
import model.User;

import java.util.Scanner;
import java.util.Map;

public class HDBOfficerView implements MenuInterface{
    private final HDBOfficerController controller = new HDBOfficerController();
    private final Scanner scanner = new Scanner(System.in);
    public void displayMenu(User user){
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
        System.out.println("| 10. Logout                                    |");
        System.out.println("+-----------------------------------------------+");
        System.out.println();
        System.out.print("Enter your choice: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                break;
            case "2":
                break;
            case "3":
                break;
            case "4":
                break;
            case "5":
                break;
            case "6":
                break;
            case "7":
                break;
            case "8":
                break;
            case "9":
                break;
            case "10":
                break;
            default:
                break;
        }
    }
}
