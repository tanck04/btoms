package controller;

import enums.FlatType;
import enums.Visibility;
import model.*;
import enums.MaritalStatus;
import repository.ApplicantRepository;
import repository.ApplicationRepository;
import repository.ProjectRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ApplicantController{
    private ApplicantRepository applicantRepository;
    private final ApplicationRepository applicationRepository = new ApplicationRepository();
    private final ProjectRepository projectRepository = new ProjectRepository();
    private ArrayList<Project> viewable_projects = new ArrayList<>();
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

    private List<Project> viewProject(Applicant applicant) {
        // Fetch the project details from the repository
        try{
            for (Project project : projectRepository.loadProjects()) {
                if (project.getVisibility() == Visibility.ON) {
                    viewable_projects.add(project);
                } else if (isApplyProject(project, applicant)) {
                    viewable_projects.add(project);
                }
            }
        }
        catch (Exception e) {
            System.out.println("Error loading projects: " + e.getMessage());
        }
        return viewable_projects;
    }
    private boolean isApplyProject(Project project, User user) {
        try {
            for (Application application : applicationRepository.loadApplications()) {
                if (application.getProject().equals(project) && application.getApplicant().equals(user)) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading applications: " + e.getMessage());
        }
        return false;
    }

    public void viewProject(User user) {
        // Assuming user is an instance of Applicant
        Applicant applicant = (Applicant) user;
        List<Project> projectsCanView = viewProject(applicant);

        // Header
        System.out.println("+------------------------------------------------------------------------------------------------------------------------------------------+");
        System.out.println("|                                                             Project List                                                                 |");
        System.out.println("+------------------------------------------------------------------------------------------------------------------------------------------+");
        System.out.printf("| %-10s | %-20s | %-15s | %-20s | %-22s |\n", "Project ID", "Project Name", "Neighbourhood", "App. Start", "App. End");
        System.out.println("+------------------------------------------------------------------------------------------------------------------------------------------+");

        // Project rows
        for (Project project : projectsCanView) {
            System.out.printf("| %-10s | %-20s | %-15s | %-20s | %-22s |\n",
                    project.getProjectID(),
                    project.getProjectName(),
                    project.getNeighborhood(),
                    project.getApplicationOpeningDate(),
                    project.getApplicationClosingDate()
            );

            // Flat types
            System.out.println("| Flat Types:                                                                                                                              |");
            for (Map.Entry<FlatType, Double> entry : project.getFlatTypePrices().entrySet()) {
                FlatType flatType = entry.getKey();
                Double price = entry.getValue();
                int units = project.getUnitsForFlatType(flatType);
                System.out.printf("|    - %-10s : $%-10.2f (%-3d units available)                                                                                      |\n",
                        flatType.toString(), price, units);
            }

            // Separator after each project
            System.out.println("+------------------------------------------------------------------------------------------------------------------------------------------+");
        }
    }



}
