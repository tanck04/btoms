package controller;

import model.*;
import repository.EnquiryRepository;
import repository.ProjectRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EnquiryController {
    EnquiryRepository enquiryRepository = new EnquiryRepository();
    ProjectRepository projectRepository = new ProjectRepository();
    public void viewEnquiry(User user) {
        List<Enquiry> enquiries = new ArrayList<>();
        if (user instanceof Manager){
            enquiries = enquiryRepository.loadAllEnquiries();
            }
        else if(user instanceof Officer){
            List<Project> projects = projectRepository.getProjectsByOfficerId(user.getNRIC());
            for (Project project : projects) {
                try {
                    List<Enquiry> enquiriesForOneProject = enquiryRepository.getEnquiriesByProject(project.getProjectID());
                    enquiries.addAll(enquiriesForOneProject);
                } catch (IOException e) {
                    System.out.println("Failed to load enquiries for project: " + project.getProjectID());
                }
            }
        }else if(user instanceof Applicant){
            enquiries = enquiryRepository.loadAllEnquiries();}

        System.out.println("+---------------------------------------------------------------------------------------------------------------------------+");
        System.out.println("|                                                  Enquiries List                                                          |");
        System.out.println("+---------------------------------------------------------------------------------------------------------------------------+");
        System.out.printf("| %-10s | %-12s | %-10s | %-40s | %-8s | %-12s | %-18s |\n",
            "Enquiry ID", "Applicant ID", "Project ID", "Enquiry Text", "Status", "Reply", "Replying Officer");
        System.out.println("+---------------------------------------------------------------------------------------------------------------------------+");

        for (Enquiry e : enquiries) {
        String reply = (e.getEnquiryReply() == null || e.getEnquiryReply().isBlank()) ? "N/A" : e.getEnquiryReply();
        String officerID = (e.getReplyingOfficerID() == null || e.getReplyingOfficerID().isBlank()) ? "N/A" : e.getReplyingOfficerID();

        System.out.printf("| %-10s | %-12s | %-10s | %-40s | %-8s | %-12s | %-18s |\n",
                e.getEnquiryID(),
                e.getApplicantID(),
                e.getProjectID(),
                truncate(e.getEnquiryText(), 40), // keep column alignment neat
                e.getEnquiryStatus(),
                truncate(reply, 12),
                officerID);
    }

    System.out.println("+---------------------------------------------------------------------------------------------------------------------------+");
}
    private String truncate(String text, int maxLength) {
        if (text == null) return "N/A";
        return text.length() > maxLength ? text.substring(0, maxLength - 3) + "..." : text;
    }
}

