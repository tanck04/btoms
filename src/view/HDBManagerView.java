package view;

import controller.ApplicationController;
import controller.EnquiryController;
import controller.HDBOfficerRegController;
import controller.ProjectController;
import enums.Visibility;
import model.HDBManager;
import model.Project;

import java.util.Scanner;

public class HDBManagerView implements MenuInterface{
    private Scanner sc;
    private ProjectController projectController;
    private ApplicationController applicationController;
    private EnquiryController enquiryController;
    private HDBOfficerRegController hdbOfficerRegController;
  
    // Create a new BTO Project by prompting the manager for all required details.
    public void createProjectFlow(HDBManager manager) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter project name: ");
        String projectName = sc.nextLine().trim();

        System.out.print("Enter neighbourhood: ");
        String neighborhood = sc.nextLine().trim();

        System.out.print("Enter number of 2-Room units: ");
        int twoRoomUnits = sc.nextInt();
        sc.nextLine(); // consume the leftover newline

        System.out.print("Enter number of 3-Room units: ");
        int threeRoomUnits = sc.nextInt();
        sc.nextLine(); // consume the leftover newline

        System.out.print("Enter application opening date (DD-MM-YYYY): ");
        String openingDate = sc.nextLine().trim();

        System.out.print("Enter application closing date (DD-MM-YYYY): ");
        String closingDate = sc.nextLine().trim();

        System.out.print("Enter number of HDB Officer slots (max 10): ");
        int officerSlots = sc.nextInt();
        sc.nextLine(); // consume the leftover newline
        if (officerSlots > 10) {
            System.out.println("Maximum officer slots is 10. Setting slots to 10.");
            officerSlots = 10;
        }

        System.out.print("Set project visibility (ON/OFF): ");
        String visibilityInput = sc.nextLine().trim();
        Visibility visibility = Visibility.OFF;
        if (visibilityInput.equalsIgnoreCase("ON")) {
            visibility = Visibility.ON;
        }

        // Use ProjectController to construct and save the new project
        Project newProject = projectController.createNewProject(
                projectName, neighborhood, twoRoomUnits, threeRoomUnits,
                openingDate, closingDate, manager, officerSlots, visibility
        );

        // Confirm the result to the manager
        if (newProject != null) {
            System.out.println("Project created successfully with Project ID: "
                    + newProject.getProjectID());
        } else {
            System.out.println("Failed to create project. Please try again.");
        }
    }
}

