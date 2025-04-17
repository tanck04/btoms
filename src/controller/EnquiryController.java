package controller;

import model.*;
import org.w3c.dom.ls.LSOutput;
import repository.EnquiryRepository;
import repository.ProjectRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EnquiryController {
    EnquiryRepository enquiryRepository = new EnquiryRepository();
    ProjectRepository projectRepository = new ProjectRepository();
    public void viewEnquiry(User user) {
        List<Enquiry> enquiries = enquiryRepository.getEnquiriesByUserType(user);

        // check if enquiry list is empty
        if (enquiries.isEmpty()) {
            System.out.println("No enquiries found.");
            return;
        }

        String separator = "+------------+--------------+------------+----------------------------------+----------------------------------+----------+";

        System.out.println("+-------------------------------------------------------------------------------------------------------------------------+");
        System.out.println("|                                                 Enquiries List                                                          |");
        System.out.println(separator);
        System.out.printf("| %-10s | %-12s | %-10s | %-32s | %-32s | %-8s |\n",
                "Enquiry ID", "Applicant ID", "Project ID", "Enquiry", "Reply", "Status");
        System.out.println(separator);

        for (Enquiry e : enquiries) {
            String enquiry = (e.getEnquiryText() == null) ? "N/A" : e.getEnquiryText();
            String reply = (e.getEnquiryReply() == null || e.getEnquiryReply().isBlank()) ? "N/A" : e.getEnquiryReply();

            // Wrap text to fit within 32-character columns
            List<String> enquiryLines = wrapText(enquiry, 32);
            List<String> replyLines = wrapText(reply, 32);
            int maxLines = Math.max(enquiryLines.size(), replyLines.size());

            for (int i = 0; i < maxLines; i++) {
                String enquiryLine = i < enquiryLines.size() ? enquiryLines.get(i) : "";
                String replyLine = i < replyLines.size() ? replyLines.get(i) : "";

                if (i == 0) {
                    System.out.printf("| %-10s | %-12s | %-10s | %-32s | %-32s | %-8s |\n",
                            e.getEnquiryID(),
                            e.getApplicantID(),
                            e.getProjectID(),
                            enquiryLine,
                            replyLine,
                            e.getEnquiryStatus());
                } else {
                    System.out.printf("| %-10s | %-12s | %-10s | %-32s | %-32s | %-8s |\n",
                            "", "", "", enquiryLine, replyLine, "");
                }
            }

            System.out.println(separator);
        }
    }
    public void replyToEnquiry(User user){
        List<Enquiry> enquiries = enquiryRepository.getEnquiriesByUserType(user);

        // check if enquiry list is empty
        if (enquiries.isEmpty()) {
            System.out.println("Enquiry empty");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("Do you want to reply to an enquiry? (yes/no): ");
        String response = scanner.nextLine().trim().toLowerCase();

        if (response.equals("yes")) {
            try {
                System.out.print("Enter Enquiry ID: ");
                String enquiryID = scanner.nextLine().trim();

                // Check if enquiry exists
                Enquiry enquiry = enquiryRepository.getEnquiryById(enquiryID);
                if (enquiry == null) {
                    System.out.println("Enquiry not found. Please check the ID and try again.");
                    return;
                }

                // Check if already replied (optional)
                if ("REPLIED".equalsIgnoreCase(enquiry.getEnquiryStatus())) {
                    System.out.println("This enquiry has already been replied to.");
                    return;
                }

                System.out.print("Enter your reply: ");
                String replyText = scanner.nextLine().trim();

                boolean success = enquiryRepository.replyToEnquiry(enquiryID, replyText, user.getNRIC());

                if (success) {
                    System.out.println("Reply submitted successfully.");
                } else {
                    System.out.println("Failed to submit reply. Enquiry ID might be incorrect.");
                }
            } catch (IOException e) {
                System.out.println("An error occurred while replying to the enquiry: " + e.getMessage());
            }
        }

    }

    public void submitEnquiry(User user) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Project ID: ");
        String projectID = scanner.nextLine().trim();

        System.out.print("Enter your enquiry: ");
        String enquiryText = scanner.nextLine().trim();

        while (enquiryText.isEmpty()) {
            System.out.println("Enquiry text cannot be empty. Please enter your enquiry:");
            enquiryText = scanner.nextLine().trim();
        }

        String enquiryID = generateNewEnquiryID();

        try {
            Enquiry newEnquiry = new Enquiry(enquiryID, user.getNRIC(), projectID, enquiryText, null, "PENDING", null);
            boolean success = enquiryRepository.createNewEnquiry(newEnquiry);
            if (success) {
                System.out.println("Enquiry submitted successfully.");
            } else {
                System.out.println("Failed to submit enquiry. Please try again.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while submitting the enquiry: " + e.getMessage());
        }
    }

    public void editEnquiry(User user) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Enquiry ID to edit: ");
        String enquiryID = scanner.nextLine().trim();

        try {
            Enquiry enquiry = enquiryRepository.getEnquiryById(enquiryID);

            if (enquiry == null) {
                System.out.println("Enquiry not found.");
                return;
            }

            // Only allow applicant to edit their own enquiry
            if (!enquiry.getApplicantID().equals(user.getNRIC())) {
                System.out.println("You can only edit your own enquiries.");
                return;
            }

            // Only allow edit if status is PENDING
            if (!"PENDING".equalsIgnoreCase(enquiry.getEnquiryStatus())) {
                System.out.println("You can only edit enquiries that are still pending.");
                return;
            }

            System.out.println("Current enquiry: " + enquiry.getEnquiryText());
            System.out.print("Enter updated enquiry text: ");
            String updatedText = scanner.nextLine().trim();

            if (updatedText.isEmpty()) {
                System.out.println("Updated enquiry cannot be empty.");
                return;
            }

            // Update the enquiry object
            enquiry.setEnquiryText(updatedText);

            boolean success = enquiryRepository.updateEnquiry(enquiry);
            if (success) {
                System.out.println("Enquiry updated successfully.");
            } else {
                System.out.println("Failed to update enquiry.");
            }

        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    public void deleteEnquiry(User user) {
        try {
            Scanner scanner = new Scanner(System.in);
            // Ask the user for the enquiry ID
            System.out.print("Enter the Enquiry ID to delete: ");
            String enquiryID = scanner.nextLine().trim();

            Enquiry enquiry = enquiryRepository.getEnquiryById(enquiryID);

            if (!enquiry.getApplicantID().equals(user.getNRIC())) {
                System.out.println("You can only edit your own enquiries.");
                return;
            }

            // Call the removeEnquiryById method from the repository
            boolean isDeleted = enquiryRepository.removeEnquiryById(enquiry.getEnquiryID());

            // Return a success or failure message
            if (isDeleted) {
                System.out.println("Enquiry with ID " + enquiryID + " has been successfully deleted.");
            } else {
                System.out.println("Enquiry with ID " + enquiryID + " not found.");
            }
        } catch (IOException e) {
            // Handle any potential IOException
            System.out.println("An error occurred while deleting the enquiry: " + e.getMessage());
        }
    }

    private List<String> wrapText(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            lines.add("");
            return lines;
        }

        while (text.length() > maxWidth) {
            int breakIndex = text.lastIndexOf(' ', maxWidth);
            if (breakIndex == -1) breakIndex = maxWidth;
            lines.add(text.substring(0, breakIndex));
            text = text.substring(breakIndex).trim();
        }

        lines.add(text);
        return lines;
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "N/A";
        return text.length() > maxLength ? text.substring(0, maxLength - 3) + "..." : text;
    }

    private String generateNewEnquiryID() {
        try{
            String lastRecordId = enquiryRepository.getLastEnquiryId();
            String numberPart = lastRecordId.substring(1);
            int nextNumber = Integer.parseInt(numberPart) + 1;
            return "E" + String.format("%04d", nextNumber);
        }catch(IOException e){
            System.out.println("Error generating new enquiry ID: " + e.getMessage());
            return null;
        }
    }
}

