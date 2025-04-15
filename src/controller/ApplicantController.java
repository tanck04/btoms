package controller;

import model.Applicant;
import enums.MaritalStatus;
import model.Project;
import model.Application;
import model.User;
import repository.ApplicantRepository;
import repository.ApplicationRepository;

import java.io.IOException;

public class ApplicantController{
    private ApplicantRepository applicantRepository;
    private final ApplicationRepository applicationRepository = new ApplicationRepository();

    // Method to check application status
    public void checkApplicationStatus(User user) {
        // Logic to check application status
        try {
            for (Application application : applicationRepository.loadApplications()) {
                if (application.getApplicant().getNRIC().equals(user.getNRIC())) {
                    System.out.println("Application ID: " + application.getApplicationID());
                    System.out.println("Project ID: " + application.getProject().getProjectID());
                    System.out.println("Flat Type: " + application.getFlatType());
                    System.out.println("Application Status: " + application.getApplicationStatus());
                    System.out.println("Withdrawal Status: " + application.getWithdrawalStatus());
                    System.out.println();
                }
                // Add logic to check and display application status
            }
        } catch (IOException e) {
            System.out.println("Error loading applications: " + e.getMessage());
        }
    }

    public Applicant getApplicantById(String applicantID) {
        try {
            return applicantRepository.findApplicantById(applicantID);
        } catch (IOException e) {
            System.out.println("Error retrieving project: " + e.getMessage());
            return null;
        }
    }


}
