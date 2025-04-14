package controller;

import model.Applicant;
import enums.MaritalStatus;
import repository.ApplicantRepository;

public class ApplicantController{

    /**
     * Creates a new applicant and saves it to the repository
     * @return the created Applicant object if successful, null otherwise
     */
    public Applicant createApplicant(String nric, String name, String password, int age, MaritalStatus maritalStatus) {
        try {
            // Check if applicant already exists
            if (ApplicantRepository.APPLICANTS.containsKey(nric)) {
                System.out.println("Applicant with NRIC " + nric + " already exists.");
                return null;
            }

            // Create the Applicant object
            Applicant newApplicant = new Applicant(nric, name, password, age, maritalStatus);

            // Add to repository and save to CSV
            ApplicantRepository.APPLICANTS.put(nric, newApplicant);
            ApplicantRepository.saveNewApplicantToCSV(newApplicant);

            return newApplicant;
        } catch (Exception e) {
            System.out.println("Error in controller: " + e.getMessage());
            return null;
        }
    }

    // Method to check application status
    public void checkApplicationStatus() {
        // Logic to check application status
        System.out.println("Checking application status - Implementation pending");
    }


}
