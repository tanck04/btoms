package view;
import java.util.Scanner;
public class UserView {
    private ApplicantView applicantView;
    private HDBOfficerView hdbOfficerView;
    private HDBManagerView hdbManagerView;
    public UserView() {
        this.applicantView = new ApplicantView();
        this.hdbOfficerView = new HDBOfficerView();
    }

    public ApplicantView getApplicantView() {
        return applicantView;
    }

    public HDBOfficerView getHdbOfficerView() {
        return hdbOfficerView;
    }

    public HDBManagerView getHdbManagerView() {
        return hdbManagerView;
    }

//    public selectUserType() {
//        // Logic to select user type
//        // For example, you can prompt the user to choose between Applicant and HDB Officer
//        // and then call the respective view methods based on the selection.
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Please enter your user type: ");
//        while (true) {
//            System.out.println("1. Applicant");
//            System.out.println("2. HDB Officer");
//            System.out.println("3. HDB Manager");
//
//            System.out.print("Enter your choice: ");
//            int choice = scanner.nextInt();
//            if (choice == 1) {
//                applicantView.displayApplicantMenu();
//                break;
//            } else if (choice == 2) {
//                hdbOfficerView.displayHDBOfficerMenu();
//                break;
//            } else if (choice == 3) {
//                hdbManagerView.displayHDBManagerMenu();
//                break;
//            } else {
//                System.out.println("Invalid choice. Please try again.");
//            }
//        }
//    }
}

