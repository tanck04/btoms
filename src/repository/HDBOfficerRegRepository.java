package repository;

import enums.OfficerRegStatus;
import model.HDBOfficerRegistration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import helper.CSVUtil;

public class HDBOfficerRegRepository{
    private static final String FILE_PATH_OFFICER_REGISTRATION = "./src/repository/data/officer_registration_records.csv";
    public static List<HDBOfficerRegistration> registrations = new ArrayList<>();

    public static List<HDBOfficerRegistration> getPendingByProject(String projectID) {
        return registrations.stream()
                .filter(r -> r.getProjectId().equals(projectID) && r.getStatus() == OfficerRegStatus.PENDING)
                .collect(Collectors.toList());
    }
    public static void saveAll() {
        // Simulated CSV save
        System.out.println("📝 Saved all registration statuses.");
    }
    public void createNewOfficerReg(HDBOfficerRegistration officerReg) {
        File file = new File(FILE_PATH_OFFICER_REGISTRATION);

        // Open the CSV file in append mode
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH_OFFICER_REGISTRATION, true))) {
            // If the file is not empty, write a newline first
            if (file.length() > 0) {
                writer.newLine();
            }

            // Format the registration data as CSV
            String registrationData = String.join(",",
                    officerReg.getRegistrationId(),
                    officerReg.getNric(),
                    officerReg.getProjectId(),
                    officerReg.getStatus().toString());

            // Write the new registration data to the file
            writer.write(registrationData);
            writer.flush();
        }catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to save officer registration.");
        }
        try {
            CSVUtil.removeEmptyRows(FILE_PATH_OFFICER_REGISTRATION);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to clean up empty rows in officer registration file.");
        }
    }
    public List<HDBOfficerRegistration> loadAllOfficerReg() throws IOException {
        List<HDBOfficerRegistration> registrations = new ArrayList<>();
        BufferedReader reader = null;

        try{
            reader = new BufferedReader(new FileReader(FILE_PATH_OFFICER_REGISTRATION));
            String line;

            // Skip the header
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length == 4) {
                    String registrationId = fields[0];
                    String nric = fields[1];
                    String projectId = fields[2];
                    OfficerRegStatus status = OfficerRegStatus.valueOf(fields[3]);

                    HDBOfficerRegistration registration = new HDBOfficerRegistration(registrationId, nric, projectId);
                    registrations.add(registration);
                }
            }
        } catch (IOException e){
            throw new IOException("Error reading Officer Registration data: " + e.getMessage());
        }finally{
            if (reader != null) {
                reader.close();
            }
        }
        return registrations;
    }
    public String getLastRegId() throws IOException {
        List<HDBOfficerRegistration> registrations = loadAllOfficerReg();
        if (registrations.isEmpty()) {
            return "R0001";
        }
        String lastRegId = registrations.get(registrations.size() - 1).getRegistrationId();
        return lastRegId;
    }

    public HDBOfficerRegistration getRegistrationById(String regId) throws IOException {
        List<HDBOfficerRegistration> Registrations = loadAllOfficerReg();

        for (HDBOfficerRegistration registration : Registrations) {
            if (registration.getRegistrationId().equals(regId)) {
                return registration;
            }
        }
        return null;
    }
}

