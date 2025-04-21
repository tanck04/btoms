package view;

import controller.CheckSecQuesInterface;

import java.util.Scanner;

/**
 * View class for handling the password recovery process through security questions.
 * <p>
 * This class provides a user interface for the password recovery flow, displaying
 * the user's security question and validating their answer against stored security
 * credentials. It serves as part of the application's password recovery mechanism.
 * </p>
 */
public class ForgetPasswordView {

    /**
     * Displays the security question for a user and verifies their answer.
     * <p>
     * This method retrieves and displays the security question associated with the
     * provided NRIC, collects the user's answer, and verifies it against the stored
     * security answer using the provided repository, if the user has it set up beforehand.
     * </p>
     *
     * @param nric The National Registration Identity Card number of the user
     * @param repository The repository implementation that provides security question functionality
     * @return true if the provided answer matches the stored answer, false otherwise
     */
    public boolean displayMenu(String nric, CheckSecQuesInterface repository) {
        String answer;
        Scanner scanner = new Scanner(System.in);
        System.out.println("| Security question: " + repository.retrieveSecQues(nric));
        System.out.print("| Answer: ");
        answer = scanner.nextLine();
        return repository.verifyAnsToSecQues(nric, answer);
    }
}