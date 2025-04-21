package controller;
import model.User;

/**
 * Interface for credential verification functionality.
 * Defines a contract for verifying user credentials in the BTO application system.
 * Implementations should validate user credentials against repository data.
 */
public interface VerificationInterface {

    /**
     * Verifies user credentials by matching provided userID and password.
     * This method authenticates users during the sign-in process.
     *
     * @param userID The unique identifier of the user (typically NRIC)
     * @param password The password provided by the user
     * @return A User object if credentials are valid, null otherwise
     */
    public User verifyCredentials(String userID, String password);
}