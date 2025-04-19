package controller;

import enums.ApplicantAppStatus;
import enums.OfficerRegStatus;
import model.*;
import repository.ApplicationRepository;
import repository.OfficerRegRepository;
import repository.ProjectRepository;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class HDBOfficerRegController {
    private final OfficerRegRepository officerRegRepository = new OfficerRegRepository();

    public Project getInChargeActiveProject(Officer officer) {
        ProjectRepository projectRepository = new ProjectRepository();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        sdf.setLenient(false);
        Date currentDate = new Date();

        try {
            for (Project project : projectRepository.loadProjects()) {
                try {
                    String closingDateStr = project.getApplicationClosingDate();

                    if (closingDateStr == null || closingDateStr.isEmpty()) {
                        System.out.println("Skipping project " + project.getProjectID() + ": Missing closing date.");
                        continue;
                    }

                    Date projectClosingDate = sdf.parse(closingDateStr);

                    if (project.getOfficerIDs().contains(officer.getNRIC()) && projectClosingDate.after(currentDate)) {
                        return project;
                    }
                } catch (java.text.ParseException e) {
                    System.out.println("Error parsing date for project " + project.getProjectID() + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading projects: " + e.getMessage());
        }
        return null;
    }

    public void createRegistration(User user) {
        Officer officer = (Officer) user;
            ProjectController projectController = new ProjectController();
            HDBOfficerController officerController = new HDBOfficerController();
            Scanner scanner = new Scanner(System.in);
            Project inChargeActiveProject = getInChargeActiveProject(officer);
            if (inChargeActiveProject != null) {
                System.out.println("You are in charge of an active project. You cannot register for another project.");
                return;
            }
            officerController.viewProject(officer);
            while (true) {
                System.out.println("Please enter the Project ID for which you wish to register (press 'x' to exit):");
                String projectId = scanner.nextLine();
                if (projectId.equals("x")) {
                    System.out.println("Exiting registration process.");
                    break;
                }

                if (projectId.isEmpty()) {
                    continue;
                }
                Project project = projectController.getProjectById(projectId);
                if (project == null) {
                    System.out.println("The project ID you entered does not exist. Please try again.");
                    continue;
                }
                // need to check eligibility before allowing registration
                if (ifAppliedProject(officer, project)) {
                    System.out.println("You have already applied for this project. Registration not allowed.");
                    continue;
                }
                String regId = officerRegRepository.generateNextRegistrationID();
                OfficerRegistration newRegistration = new OfficerRegistration(regId, officer, project, OfficerRegStatus.PENDING);
                if (newRegistration.getStatus() == null){
                    newRegistration.setStatus(OfficerRegStatus.PENDING);
                }
                officerRegRepository.createNewOfficerReg(newRegistration);
                System.out.println("Registration submitted successfully! Registration ID: " + regId);
                break;
            }
    }

    public void viewRegistrationStatus(User user) {
        Officer officer = (Officer) user;
        String nric = officer.getNRIC();
        List<OfficerRegistration> registrations = officerRegRepository.getRegistrationByOfficerId(nric);

        if (registrations.isEmpty()) {
            System.out.println("No registration found.");
            return;
        }

        System.out.println("+-----------------------------------------------------+");
        System.out.println("|                 Registration Record                 |");
        System.out.println("+-----------------------------------------------------+");
        System.out.printf("| %-15s | %-10s | %-20s |\n", "Registration ID", "Project ID", "Registration Status");
        System.out.println("+-----------------------------------------------------+");

        for (OfficerRegistration registration : registrations) {
            System.out.printf("| %-15s | %-10s | %-20s |\n",
                    registration.getRegistrationId(),
                    registration.getProject().getProjectID(),
                    registration.getStatus());
        }

        System.out.println("+-----------------------------------------------------+");
    }

    private boolean ifAppliedProject(Officer officer, Project project) {
        ApplicationRepository applicationRepo = new ApplicationRepository();
        List<Application> applications = new ArrayList<>();
        try{
            applications = applicationRepo.loadApplications();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (Application application : applications) {
            if (application.getUser().getNRIC().equals(officer.getNRIC()) &&
                    application.getProject().getProjectID().equals(project.getProjectID()) &&
                    application.getApplicationStatus() != ApplicantAppStatus.UNSUCCESSFUL){
                return true;
            }
        }
        return false;
    }


}

