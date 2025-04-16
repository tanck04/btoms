package controller;

import enums.FlatType;
import enums.Visibility;
import model.*;
import repository.ProjectRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class HDBOfficerController {
    private final ProjectRepository projectRepository = new ProjectRepository();
    private List<Project> viewable_projects = new ArrayList<>();

    public void viewProject(User user) {
        Scanner scanner = new Scanner(System.in);
        Officer officer = (Officer) user;

        // Step 1: Show all projects
        List<Project> allProjects = listProject(officer, null, null);
        System.out.println("All Available Projects:");
        printProjectList(allProjects);

        // Step 2: Ask user if they want to filter
        System.out.print("\nWould you like to apply a filter? (yes/no): ");
        String response = scanner.nextLine().trim().toLowerCase();

        if (response.equals("yes")) {
            System.out.print("Enter neighbourhood to filter by (or leave blank): ");
            String neighbourhood = scanner.nextLine().trim();

            System.out.print("Enter flat type to filter by (e.g., TWO_ROOMS, THREE_ROOMS) or leave blank: ");
            String flatTypeInput = scanner.nextLine().trim();
            FlatType flatType = null;

            if (!flatTypeInput.isEmpty()) {
                try {
                    flatType = FlatType.valueOf(flatTypeInput.toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid flat type, ignoring flat type filter.");
                }
            }

            List<Project> filteredProjects = listProject(officer, neighbourhood, flatType);
            System.out.println("\nFiltered Projects:");
            printProjectList(filteredProjects);
        }
    }

    public void printProjectList(List<Project> projects) {
        // Assuming user is an instance of Manager

        // Header
        System.out.println("+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+");
        System.out.println("|                                                                                 Project List                                                                                 |");
        System.out.println("+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+");
        System.out.printf("| %-10s | %-20s | %-15s | %-12s | %-12s | %-10s | %-12s | %-15s |\n",
                "Project ID", "Project Name", "Neighbourhood", "App. Start", "App. End", "Visibility", "OfficerSlot", "Manager ID");
        System.out.println("+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+");

        // Project rows
        for (Project project : projects) {
            System.out.printf("| %-10s | %-20s | %-15s | %-12s | %-12s | %-10s | %-12d | %-15s |\n",
                    project.getProjectID(),
                    project.getProjectName(),
                    project.getNeighborhood(),
                    project.getApplicationOpeningDate(),
                    project.getApplicationClosingDate(),
                    project.getVisibility(),
                    project.getOfficerSlot(),
                    project.getManagerID()
            );

            // Officer IDs
            System.out.println("| Officer IDs: " + String.join(", ", project.getOfficerIDs()));
            // Flat types
            System.out.println("| Flat Types:");
            for (Map.Entry<FlatType, Double> entry : project.getFlatTypePrices().entrySet()) {
                FlatType flatType = entry.getKey();
                Double price = entry.getValue();
                int units = project.getUnitsForFlatType(flatType);
                System.out.printf("|    - %-10s : $%-10.2f (%-3d units available)\n",
                        flatType.toString(), price, units);
            }

            // Separator after each project
            System.out.println("+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+");
        }
    }

    public List<Project> listProject(Officer officer, String neighbourhoodFilter, FlatType flatTypeFilter) {
        try {
            return projectRepository.loadProjects().stream()
                    .filter(project ->
                            project.getVisibility() == Visibility.ON || project.getOfficerIDs().contains(officer.getNRIC())
                    )
                    .filter(project ->
                            neighbourhoodFilter == null || neighbourhoodFilter.isEmpty() ||
                                    project.getNeighborhood().equalsIgnoreCase(neighbourhoodFilter)
                    )
                    .filter(project ->
                            flatTypeFilter == null ||
                                    project.getFlatTypePrices().containsKey(flatTypeFilter)
                    )
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.out.println("Error loading projects: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}