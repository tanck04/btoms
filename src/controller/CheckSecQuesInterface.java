package controller;

/**
 * Interface for security question verification operations.
 * Defines methods for handling security questions and answers,
 * which are used in the user authentication and password recovery process.
 */
public interface CheckSecQuesInterface {

    /**
     * Checks if a user has set up a security question.
     *
     * @param nric The NRIC (National Registration Identity Card) of the user to check
     * @return true if the user has set up a security question, false otherwise
     */
    public boolean checkHaveSecQues(String nric);

    /**
     * Retrieves the security question associated with a specific user.
     *
     * @param nric The NRIC of the user whose security question to retrieve
     * @return The security question as a String, or null if no question is found
     */
    public String retrieveSecQues(String nric);

    /**
     * Verifies if the provided answer matches the stored answer for the user's security question.
     *
     * @param nric The NRIC of the user whose answer to verify
     * @param answer The answer provided by the user
     * @return true if the answer is correct, false otherwise
     */
    public boolean verifyAnsToSecQues(String nric, String answer);
}