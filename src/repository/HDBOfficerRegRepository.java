package repository;

import enums.OfficerRegStatus;
import entity.HDBOfficerRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HDBOfficerRegRepository{
    public static List<HDBOfficerRegistration> registrations = new ArrayList<>();

    public static List<HDBOfficerRegistration> getPendingByProject(String projectID) {
        return registrations.stream()
                .filter(r -> r.getProjectID().equals(projectID) && r.getStatus() == OfficerRegStatus.PENDING)
                .collect(Collectors.toList());
    }
    public static void saveAll() {
        // Simulated CSV save
        System.out.println("📝 Saved all registration statuses.");
    }
}
