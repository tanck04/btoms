package main;

import boundary.ApplicantView;
import boundary.HDBManagerView;
import repository.ApplicantRepository;
import repository.ProjectRepository;
import repository.ApplicationRepository;
import repository.HDBManagerRepository;
import java.util.Scanner;

public class BTOMain {
    public static void main(String[] args) {
        // Initialize repositories
        ProjectRepository projectRepo = new ProjectRepository();
        ApplicantRepository applicantRepo = new ApplicantRepository();
        ApplicationRepository applicationRepo = new ApplicationRepository();
        HDBManagerRepository managerRepo = new HDBManagerRepository();

        // Load data in the correct order
        projectRepo.loadFromCSV();
        applicantRepo.loadFromCSV();
        managerRepo.loadFromCSV();
        applicationRepo.loadFromCSV(); // This must come last

        System.out.println("===== BTO Application System =====");
        Scanner scanner = new Scanner(System.in);

        System.out.println("Select user type:");
        System.out.println("1. Applicant");
        System.out.println("2. HDB Manager");
        System.out.print("Enter your choice: ");

        String userTypeChoice = scanner.nextLine();

        if (userTypeChoice.equals("1")) {
            // Start the applicant flow
            runApplicantFlow();
        } else if (userTypeChoice.equals("2")) {
            // Start the HDB manager flow
            runHDBManagerFlow();
        } else {
            System.out.println("Invalid choice. Exiting system.");
        }

        System.out.println("\nThank you for using the BTO Application System!");
    }

    private static void runApplicantFlow() {
        // Create ApplicantView
        ApplicantView applicantView = new ApplicantView();

        // Start the main view loop
        boolean exit = false;
        while (!exit) {
            // Display the applicant menu
            applicantView.displayMenu();

            // Handle the user input
            String choice = applicantView.getUserInput();

            if (choice.equals("6")) {
                exit = true;
            } else {
                applicantView.handleUserInput(choice);
            }
        }
    }

    private static void runHDBManagerFlow() {
        // Create HDBManagerView
        HDBManagerView managerView = new HDBManagerView();
        Scanner scanner = new Scanner(System.in);

        // Start the main view loop
        boolean exit = false;
        while (!exit) {
            // Display the manager menu
            managerView.displayMenu();

            // Handle the user input
            String choice = scanner.nextLine();

            if (choice.equals("8")) {
                exit = true;
            } else {
                managerView.handleUserInput(choice);
            }
        }
    }
}