package model;

import enums.MaritalStatus;
import enums.Role;

import java.util.ArrayList;
import java.util.List;

public class HDBManager extends User{
    public HDBManager(String userID, String name, Role role, String password, int age, MaritalStatus maritalStatus) {
        super(userID, name, Role.HDBMANAGER, password, age, maritalStatus);
    }
    private List<String> createdProjectIDs = new ArrayList<>();

    public void addProject(String projectID) {
        if (!createdProjectIDs.contains(projectID)) {
            createdProjectIDs.add(projectID);

        }
    }

    public List<String> getCreatedProjectIDs() {
        return createdProjectIDs;
    }
}
