package view;

public interface MenuInterface {
    void displayMenu();
    void handleUserInput(String input);
    void setController(Object controller);
    void setUserType(String userType);
    String getUserType();
    String getMenuName();
}
