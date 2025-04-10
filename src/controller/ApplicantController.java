package controller;

import model.Applicant;
import enums.MaritalStatus;
import model.Applicant;
import repository.ApplicantRepository;

import java.util.Scanner;
public class ApplicantController {
    // This class is responsible for handling the logic related to the applicant's actions
    // such as applying for a flat, checking application status, etc.



        // Existing methods...

        /**
         * Creates a new applicant from user input and saves it to the repository
         * @return the created Applicant object if successful, null otherwise
         */
        public Applicant createApplicantFromInput() {
            Scanner scanner = new Scanner(System.in);

            try {
                System.out.print("Enter NRIC: ");
                String nric = scanner.nextLine().trim();

                // Check if applicant already exists
                if (ApplicantRepository.APPLICANTS.containsKey(nric)) {
                    System.out.println("An applicant with this NRIC already exists.");
                    return null;
                }

                System.out.print("Enter Name: ");
                String name = scanner.nextLine().trim();

                System.out.print("Enter Password: ");
                String password = scanner.nextLine().trim();

                System.out.print("Enter Age: ");
                int age = Integer.parseInt(scanner.nextLine().trim());

                System.out.print("Enter Marital Status (SINGLE/MARRIED): ");
                MaritalStatus maritalStatus = MaritalStatus.valueOf(scanner.nextLine().trim().toUpperCase());

                // Create the Applicant object
                Applicant newApplicant = new Applicant(nric, name, password, age, maritalStatus);

                // Add to repository and save to CSV
                ApplicantRepository.APPLICANTS.put(nric, newApplicant);
                ApplicantRepository.saveAllApplicantsToCSV();

                System.out.println("Applicant created successfully!");
                return newApplicant;

            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Error creating applicant: " + e.getMessage());
            }

            return null;
        }

    // Method to check application status
    public void checkApplicationStatus() {
        // Logic to check application status
    }

    // Method to update personal details
    public void updatePersonalDetails() {
        // Logic to update personal details
    }

    // Method to view available flats
    public void viewAvailableFlats() {
        // Logic to view available flats
    }
}
