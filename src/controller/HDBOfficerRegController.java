package controller;

import enums.OfficerRegStatus;
import model.OfficerRegistration;
import model.Project;
import repository.OfficerRegRepository;
import repository.ProjectRepository;

import java.io.IOException;
import java.util.Scanner;

public class HDBOfficerRegController {
    private final OfficerRegRepository officerRegRepository = new OfficerRegRepository();

    public void approveRegistration(OfficerRegistration reg) {
        reg.setStatus(OfficerRegStatus.APPROVED);
        ProjectRepository.addOfficerToProject(reg.getProjectId(), reg.getNric());
        OfficerRegRepository.saveAll();
    }

    public void rejectRegistration(OfficerRegistration reg) {
        reg.setStatus(OfficerRegStatus.REJECTED);
        OfficerRegRepository.saveAll();
    }

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

    public void createRegistration(String nric) {
        try {
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
                Project project = ProjectRepository.getProjectById(projectId);
                if (project == null) {
                    System.out.println("The project ID you entered does not exist. Please try again.");
                    continue;
                }
                // need to check eligibility before allowing registration
                String regId = generateNextRegistrationID();
                OfficerRegistration newRegistration = new OfficerRegistration(regId, nric, projectId);
                officerRegRepository.createNewOfficerReg(newRegistration);
            }
        }catch (IOException e) {
            // Handle the IOException here (e.g., log or print the error message)
            System.out.println("Error occurred while creating registration: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
