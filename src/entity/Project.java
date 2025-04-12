package entity;

import enums.FlatType;
import enums.Visibility;

import java.util.List;
import java.util.Map;

public class Project {
    private String projectID;
    private String projectName;
    private String neighborhood;
    private Map<FlatType, Integer> flatTypeUnits; // Number of units for each FlatType
    private Map<FlatType, Double> flatTypePrices; // Selling price for each FlatType
    private String applicationOpeningDate;
    private String applicationClosingDate;
    private String managerID;
    private int officerSlot;
    private List<String> officerIDs;
    private Visibility visibility;
    private static int projectCount = 0; // Static variable to keep track of project count
    public Project(String projectID,
                   String projectName,
                   String neighborhood,
                   Map<FlatType, Integer> flatTypeUnits,
                   Map<FlatType, Double> flatTypePrices,
                   String applicationOpeningDate,
                   String applicationClosingDate,
                   String managerID,
                   int officerSlot,
                   List<String> officerIDs,
                   Visibility visibility) {
        this.projectID = projectID;
        this.projectName = projectName;
        this.neighborhood = neighborhood;
        this.flatTypeUnits = flatTypeUnits;     // Fixed
        this.flatTypePrices = flatTypePrices;   // Fixed
        this.applicationOpeningDate = applicationOpeningDate;
        this.applicationClosingDate = applicationClosingDate;
        this.managerID = managerID;
        this.officerSlot = officerSlot;
        this.officerIDs = officerIDs;
        this.visibility = visibility;
        projectCount++;// Use parameter instead of hardcoding
    }

    // Getter and Setter methods
    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public Map<FlatType, Integer> getFlatTypeUnits() {
        return flatTypeUnits;
    }

    public void setFlatTypeUnits(Map<FlatType, Integer> flatTypeUnits) {
        this.flatTypeUnits = flatTypeUnits;
    }

    public Map<FlatType, Double> getFlatTypePrices() {
        return flatTypePrices;
    }

    public void setFlatTypePrices(Map<FlatType, Double> flatTypePrices) {
        this.flatTypePrices = flatTypePrices;
    }

    public String getApplicationOpeningDate() {
        return applicationOpeningDate;
    }

    public void setApplicationOpeningDate(String applicationOpeningDate) {
        this.applicationOpeningDate = applicationOpeningDate;
    }

    public String getApplicationClosingDate() {
        return applicationClosingDate;
    }

    public void setApplicationClosingDate(String applicationClosingDate) {
        this.applicationClosingDate = applicationClosingDate;
    }

    public String getManagerID() {
        return managerID;
    }

    public void setManagerID(String managerID) {
        this.managerID = managerID;
    }

    public int getOfficerSlot() {
        return officerSlot;
    }

    public void setOfficerSlot(int officerSlot) {
        this.officerSlot = officerSlot;
    }

    public List<String> getOfficerIDs() {
        return officerIDs;
    }

    public void setOfficerIDs(List<String> officerIDs) {
        this.officerIDs = officerIDs;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    // Methods to manage flat type units and prices
    public void setUnitsForFlatType(FlatType flatType, int units) {
        flatTypeUnits.put(flatType, units);
    }

    public int getUnitsForFlatType(FlatType flatType) {
        return flatTypeUnits.getOrDefault(flatType, 0);
    }

    public void setPriceForFlatType(FlatType flatType, double price) {
        flatTypePrices.put(flatType, price);
    }

    public double getPriceForFlatType(FlatType flatType) {
        return flatTypePrices.getOrDefault(flatType, 0.0);
    }

    public static int getProjectCount() {
        return projectCount;
    }
}
