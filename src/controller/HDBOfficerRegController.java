package controller;

import enums.ApplicantAppStatus;
import enums.OfficerRegStatus;
import model.*;
import repository.ApplicationRepository;
import repository.OfficerRegRepository;
import repository.ProjectRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HDBOfficerRegController {
    private final OfficerRegRepository officerRegRepository = new OfficerRegRepository();
    private final ProjectRepository projectRepository = new ProjectRepository();


    public String generateNextRegistrationID() throws IOException {
        String lastID = officerRegRepository.getLastRegId();

        if (lastID.equals("R0001")) {
            return "R0002"; // Start from R0002 if R0001 already exists
        }

        // Extract numeric part: from "R0042" → "0042"
        String numberPart = lastID.substring(1);

        // Convert to int and increment
        int nextNumber = Integer.parseInt(numberPart) + 1;

        // Format back to "Rxxxx" with leading zeros
        return "R" + String.format("%04d", nextNumber);
    }

    public void createRegistration(User user) {
        Officer officer = (Officer) user;
        try {
            ProjectController projectController = new ProjectController();
            Scanner scanner = new Scanner(System.in);

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
                String regId = generateNextRegistrationID();
                OfficerRegistration newRegistration = new OfficerRegistration(regId, officer, project, OfficerRegStatus.PENDING);
                if (newRegistration.getStatus() == null){
                    newRegistration.setStatus(OfficerRegStatus.PENDING);
                }
                officerRegRepository.createNewOfficerReg(newRegistration);
                System.out.println("Registration submitted successfully! Registration ID: " + regId);
                break;
            }
        }catch (IOException e) {
            // Handle the IOException here (e.g., log or print the error message)
            System.out.println("Error occurred while creating registration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void viewRegistrationStatus(String nric) {
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
