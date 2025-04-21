package view;

import model.User;
import java.io.IOException;

/**
 * Interface defining the menu display behavior for different user types.
 * <p>
 * This interface establishes a contract for all view classes in the application
 * that need to display menus. It ensures that each implementing class provides
 * a method to display an appropriate menu based on the user type (Applicant,
 * Officer, Manager).
 * </p>
 */
public interface MenuInterface {

    /**
     * Displays the appropriate menu for a specific user.
     * <p>
     * This method is responsible for presenting menu options tailored to the user's role,
     * handling user input, and delegating actions to the appropriate controllers based on
     * the user's selections.
     * </p>
     *
     * @param user The authenticated user for whom the menu should be displayed
     */
    public void displayMenu(User user);
}