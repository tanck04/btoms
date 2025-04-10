/**
 * Abstract class representing a repository.
 * Provides a framework for managing repository data, including loading data
 * from a CSV file, checking the load status, and clearing repository data.
 */
package repository;

public abstract class Repository {
    /**
     * A static flag indicating whether the repository data has been loaded.
     */
    private static boolean isRepoLoad = false;

    /**
     * Static method to load the repository data.
     * Calls the subclass-specific `loadData` implementation.
     *
     * @param repository An instance of a subclass of Repository
     * @return boolean indicating success or failure of the load operation
     */
    public static boolean loadRepository(Repository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("Repository instance cannot be null.");
        }
        isRepoLoad = repository.loadFromCSV();
        return isRepoLoad;
    }

    /**
     * Checks if the repository is loaded.
     *
     * @return boolean indicating if the repository is loaded
     */
    public static boolean isRepoLoad() {
        return isRepoLoad;
    }

    /**
     * Abstract method for loading data.
     * Each subclass must implement its own data loading logic.
     *
     * @return boolean indicating success or failure of the load operation
     */
    public abstract boolean loadFromCSV();

    /**
     * Clears repository data.
     */
    public void clearRepository() {
        isRepoLoad = false;
        System.out.println("Repository cleared.");
    }
}
