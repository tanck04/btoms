package view;

import controller.CheckSecQuesInterface;

import java.util.Scanner;

public class ForgetPasswordView {
    public boolean displayMenu(String nric, CheckSecQuesInterface repository){
        String answer;
        Scanner scanner = new Scanner(System.in);
        System.out.println("| Security question: "+ repository.retrieveSecQues(nric));
        System.out.print("| Answer: ");
        answer = scanner.nextLine();
        return repository.verifyAnsToSecQues(nric,answer);
    }


}
