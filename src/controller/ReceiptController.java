package controller;

import model.Application;
import model.Project;
import model.User;
import enums.FlatType;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReceiptController {
    private Map<String, String> receiptMap = new HashMap<>(); // Key: Application ID, Value: Receipt Content

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

            // Build receipt content as a string
            StringBuilder receipt = new StringBuilder();
            receipt.append("\n+----------------------------------------------------------------------------------------------------------------------+\n");
            receipt.append("|                                           FLAT BOOKING RECEIPT                                                      |\n");
            receipt.append("+----------------------------------------------------------------------------------------------------------------------+\n");
            receipt.append(String.format("| Current Date: %-94s |\n", currentDate));
            receipt.append("| APPLICANT INFORMATION:                                                                                              |\n");
            receipt.append(String.format("| Name: %-30s NRIC: %-15s                                                |\n", applicant.getName(), applicant.getNRIC()));
            receipt.append(String.format("| Age: %-4d Marital Status: %-12s                                                              |\n", applicant.getAge(), applicant.getMaritalStatus()));
            receipt.append("+----------------------------------------------------------------------------------------------------------------------+\n");
            receipt.append("| PROPERTY DETAILS:                                                                                                   |\n");
            receipt.append(String.format("| Project ID: %-12s Project Name: %-25s Neighborhood: %-15s         |\n", project.getProjectID(), project.getProjectName(), project.getNeighborhood()));
            receipt.append(String.format("| Flat Type: %-12s                                                                                     |\n", flatType));
            receipt.append("+----------------------------------------------------------------------------------------------------------------------+\n");
            receipt.append("| FINANCIAL SUMMARY:                                                                                                  |\n");
            receipt.append(String.format("| Base Price:                                                                                     $%-10.2f |\n", price));
            receipt.append("+----------------------------------------------------------------------------------------------------------------------+\n");
            receipt.append("| IMPORTANT INFORMATION:                                                                                              |\n");
            receipt.append(String.format("| 1. This receipt confirms your flat booking under Application ID: %-45s |\n", application.getApplicationID()));
            receipt.append("| 2. Please keep this receipt for your records.                                                                       |\n");
            receipt.append("| 3. You will be contacted by HDB for the next steps in the purchase process.                                         |\n");
            receipt.append("| 4. Failure to make payments by due dates may result in cancellation of your booking.                                |\n");
            receipt.append("+----------------------------------------------------------------------------------------------------------------------+\n");

            // Store the receipt content
            receiptMap.put(application.getApplicationID(), receipt.toString());

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
    public void viewReceiptByApplicationID(String applicationID) {
        if (receiptMap.containsKey(applicationID)) {
            System.out.println(receiptMap.get(applicationID));
        } else {
            System.out.println("No receipt found for Application ID: " + applicationID);
        }
    }
}

