package controller;

import model.User;
import view.ForgetPasswordView;

import java.util.Scanner;

import static controller.SignInController.userLoginRepository;

/**
 * Controller class for managing security questions functionality.
 * Provides methods to check, ask, and change security questions for users
 * as part of the account security and password recovery system.
 */
public class SecQuesController {

    /**
     * Checks if a user has set their security question.
     *
     * @param nric The user's NRIC (National Registration Identity Card) number
     * @return true if the user has set their security question, false otherwise
     */
    public boolean haveSetSecQues(String nric) {
        String role = userLoginRepository.getUserTypeByNRIC(nric);
        RepositoryController repositoryController = new RepositoryController();
        CheckSecQuesInterface repository = (CheckSecQuesInterface) repositoryController.getRepository(role);
        if (repository == null) {
            return false;
        }

        return repository.checkHaveSecQues(nric);
    }

    /**
     * Displays the security question for a user and prompts for answer.
     * Used during the password recovery process.
     *
     * @param nric The user's NRIC number
     * @return true if the user successfully answers their security question, false otherwise
     */
    public boolean askSecQues(String nric) {
        String role = userLoginRepository.getUserTypeByNRIC(nric);
        RepositoryController repositoryController = new RepositoryController();
        CheckSecQuesInterface repository = (CheckSecQuesInterface) repositoryController.getRepository(role);
        if (repository == null) {
            return false;
        }
        ForgetPasswordView forgetPasswordView = new ForgetPasswordView();
        return forgetPasswordView.displayMenu(nric, repository);
    }

    /**
     * Changes a user's security question and answer.
     *
     * @param nric The user's NRIC number
     * @param secQues The new security question
     * @param secQuesAnswer The answer to the new security question
     * @return true if the security question was changed successfully, false otherwise
     */
    public boolean changeSecQues(String nric, String secQues, String secQuesAnswer) {
        String role = userLoginRepository.getUserTypeByNRIC(nric);
        RepositoryController repositoryController = new RepositoryController();
        SecQuesChangerInterface repository = (SecQuesChangerInterface) repositoryController.getRepository(role);

        if (repository == null) {
            System.out.println("+------------------------------------------------+");
            System.out.println("| Invalid NRIC or role. Cannot change questions. |");
            System.out.println("+------------------------------------------------+\n");
            return false;
        }

        boolean isChanged = repository.changeSecQuesAndAns(nric, secQues, secQuesAnswer);

        if (isChanged) {
            System.out.println("\n+------------------------------------------------+");
            System.out.println("| Security question set successfully.            |");
            System.out.println("+------------------------------------------------+");
        } else {
            System.out.println("+------------------------------------------------+");
            System.out.println("| Failed to change security question.            |");
            System.out.println("+------------------------------------------------+");
        }

        return isChanged;
    }

    /**
     * Interactive method that prompts the user to set a new security question and answer.
     * Collects input from the console and attempts to update the user's security question.
     *
     * @param user The user object for whom to change security question
     */
    public void changeSecurityQuestionAndAnswer(User user) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("| Please enter a security question: ");
        String secQues = scanner.nextLine();
        System.out.print("| Please enter the answer: ");
        String secQuesAns = scanner.nextLine();
        if(!changeSecQues(user.getNRIC(), secQues, secQuesAns)){
            System.out.println("Sorry, fail to set security question, please contact an administrator");
        }
    }
}