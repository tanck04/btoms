package controller;

import enums.FlatType;
import model.Applicant;
import enums.MaritalStatus;
import model.Project;
import model.Application;
import model.User;
import repository.ApplicantRepository;
import repository.ApplicationRepository;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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

    public void viewProject(User user) {
        // Assuming user is an instance of Applicant
        Applicant applicant = (Applicant) user;
        ViewProjectController viewController = new ViewProjectController();
        List<Project> projectsCanView = viewController.viewProject(applicant);

        for (Project project : projectsCanView) {
            System.out.println("Project ID: " + project.getProjectID());
            System.out.println("Project Name: " + project.getProjectName());
            System.out.println("Neighbourhood: " + project.getNeighborhood());

            // Display flat type prices
            System.out.println("Flat Types Available:");
            for (Map.Entry<FlatType, Double> entry : project.getFlatTypePrices().entrySet()) {
                FlatType flatType = entry.getKey();
                Double price = entry.getValue();
                int units = project.getUnitsForFlatType(flatType);
                System.out.println("  - " + flatType + ": $" + price + " (" + units + " units available)");
            }

            System.out.println("Application Period: " + project.getApplicationOpeningDate() +
                               " to " + project.getApplicationClosingDate());
            System.out.println();
        }
    }


}
