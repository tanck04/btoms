package controller;

import enums.ApplicantAppStatus;
import model.Application;
import model.Project;
import model.User;
import enums.FlatType;
import repository.ApplicationRepository;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReceiptController {

    public boolean generateReceipt(Application application) {
        if (application == null) {
            System.out.println("Error: No application provided to generate receipt.");
            return false;
        }

        try {
            User applicant = application.getUser();
            Project project = application.getProject();
            FlatType flatType = application.getFlatType();
            double price = project.getFlatTypePrices().get(flatType);
            String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            StringBuilder receipt = new StringBuilder();
            receipt.append("+----------------------------------------------------------------------------------------+\n");
            receipt.append("|                                FLAT BOOKING RECEIPT                                    |\n");
            receipt.append("+----------------------------------------------------------------------------------------+\n");

            receipt.append("| APPLICANT INFORMATION:                                                                 |\n");
            receipt.append(String.format("| Name          : %-20s NRIC           : %-33s|\n", applicant.getName(), applicant.getNRIC()));
            receipt.append(String.format("| Age           : %-20d Marital Status : %-33s|\n", applicant.getAge(), applicant.getMaritalStatus()));

            receipt.append("+----------------------------------------------------------------------------------------+\n");

            receipt.append("| PROPERTY DETAILS:                                                                      |\n");
            receipt.append(String.format("| Project ID    : %-20s Project Name   : %-33s|\n", project.getProjectID(), project.getProjectName()));
            receipt.append(String.format("| Neighbourhood : %-20s Flat Type      : %-33s|\n", project.getNeighborhood(), flatType.toString()));

            receipt.append("+----------------------------------------------------------------------------------------+\n");

            receipt.append("| FINANCIAL SUMMARY:                                                                     |\n");
            receipt.append(String.format("| Base Price:%-64s$%-10.2f |\n", "", price));

            receipt.append("+----------------------------------------------------------------------------------------+\n");

            receipt.append("| IMPORTANT INFORMATION:                                                                 |\n");
            receipt.append(String.format("| 1. This receipt confirms your flat booking under Application ID: %-22s|\n", application.getApplicationID()));
            receipt.append("| 2. Please keep this receipt for your records.                                          |\n");
            receipt.append("| 3. You will be contacted by HDB for the next steps in the purchase process.            |\n");
            receipt.append("| 4. Failure to make payments by due dates may result in cancellation of your booking.   |\n");

            receipt.append("+----------------------------------------------------------------------------------------+\n");



            // Print the receipt
            System.out.println(receipt.toString());
            return true;
        } catch (Exception e) {
            System.out.println("Error generating receipt: " + e.getMessage());
            return false;
        }
    }

    public void generateBookingReceipt(Application application, User user) {
        System.out.println("\n+----------------------------------------------------------------------------------------------------------------------------------+");
        System.out.println("|                                           GENERATING FLAT BOOKING RECEIPT                                                          |");
        System.out.println("+----------------------------------------------------------------------------------------------------------------------------------+");
        System.out.println("| Officer: " + user.getName() + " (ID: " + user.getNRIC() + ")");

        if (generateReceipt(application)) {
            System.out.println("\nReceipt generated successfully. The above receipt has been provided to the applicant.");
        } else {
            System.out.println("\nFailed to generate receipt. Please try again later.");
        }
    }

    // View a stored receipt
    public void viewReceiptByUser(User user) {
        // Try to regenerate the receipt if it's not found in memory
        try {

            ApplicationRepository applicationRepo = new ApplicationRepository();
            Application app = null;
            for (Application application: applicationRepo.loadApplications()){
                if (application.getUser() != null && application.getUser().getNRIC().equals(user.getNRIC())) {
                    app = application;
                }
            }

            if (app != null && app.getApplicationStatus() == ApplicantAppStatus.BOOKED) {
                System.out.println("Regenerating receipt for booked application...");
                generateReceipt(app);
            } else if (app == null) {
                System.out.println("Application not found in the system.");
            } else {
                System.out.println("Receipt can only be generated for BOOKED applications.");
            }
        } catch (IOException e) {
            System.out.println("Error retrieving application: " + e.getMessage());
        }
    }
}

