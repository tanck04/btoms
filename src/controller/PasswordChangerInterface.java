package controller;

/**
 * Interface defining password change functionality in the BTO system.
 * This interface defines the contract for components that need to
 * implement password changing capabilities for users in the system.
 */
public interface PasswordChangerInterface {

    /**
     * Changes a user's password to the provided hashed password.
     *
     * @param nric The National Registration Identity Card (NRIC) of the user whose password is to be changed
     * @param hashedPassword The new password in hashed format to be stored
     * @return true if the password was changed successfully, false otherwise
     */
    public boolean changePassword(String nric, String hashedPassword);
}