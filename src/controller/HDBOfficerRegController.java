package controller;

import enums.OfficerRegStatus;
import entity.HDBOfficerRegistration;
import repository.HDBOfficerRegRepository;
import repository.ProjectRepository;

public class HDBOfficerRegController {
    public void approveRegistration(HDBOfficerRegistration reg) {
        reg.setStatus(OfficerRegStatus.APPROVED);
        ProjectRepository.addOfficerToProject(reg.getProjectID(), reg.getOfficerID());
        HDBOfficerRegRepository.saveAll();
    }

    public void rejectRegistration(HDBOfficerRegistration reg) {
        reg.setStatus(OfficerRegStatus.REJECTED);
        HDBOfficerRegRepository.saveAll();
    }
}
