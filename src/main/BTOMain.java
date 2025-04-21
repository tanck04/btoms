package main;

import controller.AuthenticationController;

/**
 * Main entry point for the BTO Housing System application.
 * <p>
 * This class initializes the application by starting the authentication controller
 * which handles user login and program flow control. The application manages
 * BTO (Build-To-Order) flat applications, processing, and administrative functions.
 * </p>
 */
public class BTOMain {
    /**
     * The main method that serves as the entry point for the application.
     * <p>
     * Creates and initializes an instance of AuthenticationController to
     * begin the user authentication process and application flow.
     * </p>
     *
     * @param args Command line arguments (not used in this application)
     */
    public static void main(String[] args) {
        AuthenticationController authenticationController = new AuthenticationController();
        authenticationController.start();
    }
}