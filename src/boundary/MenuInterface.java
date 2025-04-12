package boundary;
import enums.Role;
public interface MenuInterface {
    void displayMenu();
    void handleUserInput(String input);
    Role getUserType();
}
