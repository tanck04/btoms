package controller;

import enums.FlatType;
import repository.ApplicationRepository;
import repository.ProjectRepository;
import model.User;
import model.Applicant;
import model.Manager;
import model.Officer;
import model.Application;
import model.Project;
import enums.Visibility;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewProjectController {
    private final ProjectRepository projectRepository = new ProjectRepository();
    private final ApplicationRepository applicationRepository= new ApplicationRepository();
    ArrayList<Project> viewable_projects = new ArrayList<>();
    public List<Project> viewProject(User user) {
        // Fetch the project details from the repository
        if (user instanceof Manager) {
            try {
                for (Project project : projectRepository.loadProjects()) {
                    viewable_projects.add(project);
                }
            } catch (Exception e) {
                System.out.println("Error loading projects: " + e.getMessage());
            }
        }
        else if (user instanceof Officer) {
            try {
                for (Project project : projectRepository.loadProjects()) {
                    if (project.getVisibility() == Visibility.ON) {
                        viewable_projects.add(project);
                    } else if (project.getOfficerIDs().contains(user.getNRIC())) {
                        viewable_projects.add(project);
                    }

                }
            } catch (Exception e) {
                System.out.println("Error loading projects: " + e.getMessage());
            }
        }

        else if (user instanceof Applicant){
            try {
                for (Project project : projectRepository.loadProjects()) {
                    if (project.getVisibility() == Visibility.ON) {
                        viewable_projects.add(project);
                    } else if (isApplyProject(project, user)) {
                        viewable_projects.add(project);
                    }
                }
            } catch (Exception e) {
                System.out.println("Error loading projects: " + e.getMessage());
            }
        }
        return viewable_projects;
    }

    public boolean isApplyProject(Project project, User user) {
        try {
            for (Application application : applicationRepository.loadApplications()) {
                if (application.getProject().equals(project) && application.getApplicant().equals(user)) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading applications: " + e.getMessage());
        }
        return false;
    }
}