package controller;

import model.Project;
import model.Applicant;
import repository.ApplicantRepository;
import repository.ProjectRepository;

import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class ViewController {

    /**
     * Displays all available projects to the user
     * @return list of available projects
     */
    public Map<String, Project> getAvailableProjects() {
        return ProjectRepository.PROJECTS;
    }

    public Map<String, Applicant> getAllApplicants() {
        return ApplicantRepository.APPLICANTS;
    }
}
