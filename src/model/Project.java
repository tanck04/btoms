package model;

import enums.FlatType;
import enums.Visibility;

import java.util.List;
import java.util.Map;

/**
 * Represents a BTO housing project in the system.
 * <p>
 * A Project contains information about a specific BTO housing development including
 * its unique identifier, name, location, available flat types with prices and quantities,
 * application period dates, associated manager and officers, and visibility status.
 * This class serves as the central model for BTO projects that applicants can apply for.
 * </p>
 */
public class Project {
    /** Unique identifier for this project */
    private String projectID;

    /** The name of this BTO housing project */
    private String projectName;

    /** The neighborhood or area where this project is located */
    private String neighborhood;

    /** Map of flat types to the number of available units for each type */
    private Map<FlatType, Integer> flatTypeUnits;

    /** Map of flat types to their respective selling prices */
    private Map<FlatType, Double> flatTypePrices;

    /** The date when applications for this project will start being accepted */
    private String applicationOpeningDate;

    /** The deadline date for submitting applications to this project */
    private String applicationClosingDate;

    /** The ID of the manager in charge of this project */
    private String managerID;

    /** The maximum number of officers that can be assigned to this project */
    private int officerSlot;

    /** List of officer IDs currently assigned to this project */
    private List<String> officerIDs;

    /** The current visibility status of this project (determines who can view/apply) */
    private Visibility visibility;

    /** Counter to track the total number of projects created in the system */
    private static int projectCount = 0;

    /**
     * Constructs a new Project with all required attributes.
     *
     * @param projectID              The unique identifier for this project
     * @param projectName            The name of this BTO housing project
     * @param neighborhood           The neighborhood or area where this project is located
     * @param flatTypeUnits          Map of flat types to the number of available units for each type
     * @param flatTypePrices         Map of flat types to their respective selling prices
     * @param applicationOpeningDate The date when applications for this project will start being accepted
     * @param applicationClosingDate The deadline date for submitting applications
     * @param managerID              The ID of the manager in charge of this project
     * @param officerSlot            The maximum number of officers that can be assigned to this project
     * @param officerIDs             List of officer IDs currently assigned to this project
     * @param visibility             The current visibility status of this project
     */
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
        this.flatTypeUnits = flatTypeUnits;
        this.flatTypePrices = flatTypePrices;
        this.applicationOpeningDate = applicationOpeningDate;
        this.applicationClosingDate = applicationClosingDate;
        this.managerID = managerID;
        this.officerSlot = officerSlot;
        this.officerIDs = officerIDs;
        this.visibility = visibility;
        projectCount++;
    }

    /**
     * Gets the unique identifier for this project.
     *
     * @return The project's unique ID
     */
    public String getProjectID() {
        return projectID;
    }

    /**
     * Sets the unique identifier for this project.
     *
     * @param projectID The new project ID to set
     */
    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    /**
     * Gets the name of this BTO housing project.
     *
     * @return The project name
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Sets the name of this BTO housing project.
     *
     * @param projectName The new project name to set
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * Gets the neighborhood where this project is located.
     *
     * @return The neighborhood name
     */
    public String getNeighborhood() {
        return neighborhood;
    }

    /**
     * Sets the neighborhood where this project is located.
     *
     * @param neighborhood The new neighborhood to set
     */
    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    /**
     * Gets the map of flat types to their available unit counts.
     *
     * @return A map with flat types as keys and available unit counts as values
     */
    public Map<FlatType, Integer> getFlatTypeUnits() {
        return flatTypeUnits;
    }

    /**
     * Sets the map of flat types to their available unit counts.
     *
     * @param flatTypeUnits The new flat type units map to set
     */
    public void setFlatTypeUnits(Map<FlatType, Integer> flatTypeUnits) {
        this.flatTypeUnits = flatTypeUnits;
    }

    /**
     * Gets the map of flat types to their prices.
     *
     * @return A map with flat types as keys and prices as values
     */
    public Map<FlatType, Double> getFlatTypePrices() {
        return flatTypePrices;
    }

    /**
     * Sets the map of flat types to their prices.
     *
     * @param flatTypePrices The new flat type prices map to set
     */
    public void setFlatTypePrices(Map<FlatType, Double> flatTypePrices) {
        this.flatTypePrices = flatTypePrices;
    }

    /**
     * Gets the date when applications for this project will start being accepted.
     *
     * @return The application opening date as a string
     */
    public String getApplicationOpeningDate() {
        return applicationOpeningDate;
    }

    /**
     * Sets the date when applications for this project will start being accepted.
     *
     * @param applicationOpeningDate The new application opening date to set
     */
    public void setApplicationOpeningDate(String applicationOpeningDate) {
        this.applicationOpeningDate = applicationOpeningDate;
    }

    /**
     * Gets the deadline date for submitting applications to this project.
     *
     * @return The application closing date as a string
     */
    public String getApplicationClosingDate() {
        return applicationClosingDate;
    }

    /**
     * Sets the deadline date for submitting applications to this project.
     *
     * @param applicationClosingDate The new application closing date to set
     */
    public void setApplicationClosingDate(String applicationClosingDate) {
        this.applicationClosingDate = applicationClosingDate;
    }

    /**
     * Gets the ID of the manager in charge of this project.
     *
     * @return The manager's ID
     */
    public String getManagerID() {
        return managerID;
    }

    /**
     * Sets the ID of the manager in charge of this project.
     *
     * @param managerID The new manager ID to set
     */
    public void setManagerID(String managerID) {
        this.managerID = managerID;
    }

    /**
     * Gets the maximum number of officers that can be assigned to this project.
     *
     * @return The number of officer slots available
     */
    public int getOfficerSlot() {
        return officerSlot;
    }

    /**
     * Sets the maximum number of officers that can be assigned to this project.
     *
     * @param officerSlot The new officer slot count to set
     */
    public void setOfficerSlot(int officerSlot) {
        this.officerSlot = officerSlot;
    }

    /**
     * Gets the list of officer IDs currently assigned to this project.
     *
     * @return A list of officer IDs
     */
    public List<String> getOfficerIDs() {
        return officerIDs;
    }

    /**
     * Sets the list of officer IDs currently assigned to this project.
     *
     * @param officerIDs The new list of officer IDs to set
     */
    public void setOfficerIDs(List<String> officerIDs) {
        this.officerIDs = officerIDs;
    }

    /**
     * Gets the visibility status of this project.
     *
     * @return The current visibility status
     */
    public Visibility getVisibility() {
        return visibility;
    }

    /**
     * Sets the visibility status of this project.
     *
     * @param visibility The new visibility status to set
     */
    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    /**
     * Sets the number of available units for a specific flat type.
     *
     * @param flatType The flat type to update
     * @param units The number of units available for this flat type
     */
    public void setUnitsForFlatType(FlatType flatType, int units) {
        flatTypeUnits.put(flatType, units);
    }

    /**
     * Gets the number of available units for a specific flat type.
     *
     * @param flatType The flat type to query
     * @return The number of available units for the specified flat type, or 0 if none are available
     */
    public int getUnitsForFlatType(FlatType flatType) {
        return flatTypeUnits.getOrDefault(flatType, 0);
    }

    /**
     * Sets the price for a specific flat type.
     *
     * @param flatType The flat type to update
     * @param price The new price for this flat type
     */
    public void setPriceForFlatType(FlatType flatType, double price) {
        flatTypePrices.put(flatType, price);
    }

    /**
     * Gets the price for a specific flat type.
     *
     * @param flatType The flat type to query
     * @return The price for the specified flat type, or 0.0 if no price is set
     */
    public double getPriceForFlatType(FlatType flatType) {
        return flatTypePrices.getOrDefault(flatType, 0.0);
    }

    /**
     * Gets the total number of projects created in the system.
     *
     * @return The current project count
     */
    public static int getProjectCount() {
        return projectCount;
    }
}