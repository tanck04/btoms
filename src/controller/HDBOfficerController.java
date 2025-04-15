package controller;

import enums.FlatType;
import model.Applicant;
import model.Project;
import model.User;
import model.Officer;
import java.util.List;
import java.util.Map;

public class HDBOfficerController {
    public void viewProject(User user) {
        // Assuming user is an instance of Applicant
        Officer officer = (Officer) user;
        ViewProjectController viewController = new ViewProjectController();
        List<Project> projectsCanView = viewController.viewProject(officer);

        for (Project project : projectsCanView) {
            System.out.println("Project ID: " + project.getProjectID());
            System.out.println("Project Name: " + project.getProjectName());
            System.out.println("Neighbourhood: " + project.getNeighborhood());

            // Display flat type prices
            System.out.println("Flat Types Available:");
            for (Map.Entry<FlatType, Double> entry : project.getFlatTypePrices().entrySet()) {
                FlatType flatType = entry.getKey();
                Double price = entry.getValue();
                int units = project.getUnitsForFlatType(flatType);
                System.out.println("  - " + flatType + ": $" + price + " (" + units + " units available)");
            }

            System.out.println("Application Period: " + project.getApplicationOpeningDate() +
                    " to " + project.getApplicationClosingDate());
            System.out.println();
        }
    }

}
