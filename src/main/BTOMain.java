package main;

import controller.ApplicantController;
import controller.ProjectController;
import enums.MaritalStatus;
import enums.Role;

import model.Applicant;
import model.HDBManager;
import model.Project;
import repository.ApplicantRepository;
import repository.ProjectRepository;

import java.util.Scanner;

public class BTOMain {
    public static void main(String[] args) {
        // Load repositories
        HDBManager manager = new HDBManager(
                "MGR001",           // userID
                "Alice Tan",        // name
                Role.HDBMANAGER,        // role
                "password123",      // password
                35,                 // age
                MaritalStatus.SINGLE // marital status
        );
        ApplicantRepository applicantRepository = new ApplicantRepository();
        applicantRepository.loadFromCSV();

        ProjectRepository projectRepository = new ProjectRepository();
        projectRepository.loadFromCSV();

        Scanner scanner = new Scanner(System.in);
        System.out.println("=== BTO Management System ===");
        System.out.println("1. Create a new applicant");
        System.out.println("2. Create a new project");
        System.out.print("Enter your choice (1 or 2): ");

        int choice = Integer.parseInt(scanner.nextLine().trim());

        HDBManager HDBManager;
        ProjectController projectController = null;
        if (choice == 1) {
            // Initialize ApplicantController and create a new applicant
            ApplicantController applicantController = new ApplicantController();
            System.out.println("=== Creating a new applicant ===");
            Applicant newApplicant = applicantController.createApplicantFromInput();

            if (newApplicant != null) {
                System.out.println("Applicant created with NRIC: " + newApplicant.getNRIC());
                System.out.println("Applicant name: " + newApplicant.getName());
                System.out.println("Applicant age: " + newApplicant.getAge());
                System.out.println("Marital status: " + newApplicant.getMaritalStatus());
            } else {
                System.out.println("Applicant creation was unsuccessful.");
            }
        } else if (choice == 2) {
            // Initialize ProjectController and create a new project
            projectController = new ProjectController();
            System.out.println("=== Creating a new project ===");
            HDBManager = new HDBManager(
                    "MGR001",           // userID
                    "Alice Tan",        // name
                    Role.HDBMANAGER,    // role
                    "password123",      // password
                    35,                 // age
                    MaritalStatus.SINGLE // marital status
            );

            Project newProject = projectController.createProject(HDBManager);

        if (newProject != null) {
            System.out.println("Project created with ID: " + newProject.getProjectID());
            System.out.println("Project name: " + newProject.getProjectName());
            System.out.println("Neighborhood: " + newProject.getNeighborhood());
        } else {
            System.out.println("Project creation was unsuccessful.");
        }
    } else {
            System.out.println("Invalid choice. Please restart the program.");
        }
    }
}
