package controller;

import model.User;
import view.ForgetPasswordView;

import java.util.Scanner;

import static controller.SignInController.userLoginRepository;

public class SecQuesController {
    public boolean haveSetSecQues(String nric) {
        String role = userLoginRepository.getUserTypeByNRIC(nric);
        RepositoryController repositoryController = new RepositoryController();
        CheckSecQuesInterface repository = (CheckSecQuesInterface) repositoryController.getRepository(role);
        if (repository == null) {
            return false;
        }

        return repository.checkHaveSecQues(nric);
    }

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
