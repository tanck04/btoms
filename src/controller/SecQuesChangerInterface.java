package controller;

/**
 * Interface for components that provide security question update functionality.
 * Defines a contract for changing a user's security question and answer.
 */
public interface SecQuesChangerInterface {

    /**
     * Changes the security question and answer for a user.
     * Implementations should validate the user exists before making changes.
     *
     * @param nric The unique identifier (NRIC) of the user whose security question is being changed
     * @param newSecQues The new security question to be set
     * @param newSecAns The new answer for the security question
     * @return true if the security question and answer were successfully changed, false otherwise
     */
    public boolean changeSecQuesAndAns(String nric, String newSecQues, String newSecAns);
}