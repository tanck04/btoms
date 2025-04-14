package controller;


import model.User;
import repository.UserLoginRepository;
import view.MenuInterface;

import java.io.IOException;
import java.util.Scanner;

public class SignInController {
    static UserLoginRepository userLoginRepository = new UserLoginRepository();

    public static boolean signIn(String nric, String password) {
        String role = userLoginRepository.getUserTypeByNRIC(nric);
        RepositoryController repositoryController = new RepositoryController();
        VerificationInterface repository = (VerificationInterface) repositoryController.getRepository(role);

        if(repository == null){
            System.out.println("Invalid ID or password. Returning to main menu.");
            System.out.println();
            return false;
        }
        User user = repository.verifyCredentials(nric, password);
        if(user == null){
            System.out.println("Invalid ID or password. Returning to main menu.");
            System.out.println();
            return false;
        }
        else if (user != null && password.equals("password")){
            System.out.println("You are a new user with the default password, please change it");
            PasswordController pc = new PasswordController();
            System.out.println("Please enter a new password");
            @SuppressWarnings("resource")
            Scanner scanner = new Scanner(System.in);
            String newPassword;
            do {
                newPassword = scanner.nextLine();
                if (newPassword.equals("password")) {
                    System.out.println("The password cannot be the default 'Password'. Please enter a new password:");
                }
            } while (newPassword.equals("password"));
            if(!pc.changePassword(nric, newPassword)){
                System.out.println("Something went wrong, please contact an administrator");
            }
            System.out.println("Your password has been successfully changed, proceeding to login...");
            ViewController viewController = new ViewController();
            MenuInterface view = (MenuInterface) viewController.getView(role);
            try{
                view.displayMenu(user);
            } catch(IOException e){
                System.out.println("Menu did not work ");
            }
            return true;


        }
        else{
            ViewController viewController = new ViewController();
            MenuInterface view = (MenuInterface) viewController.getView(role);
            try{
                view.displayMenu(user);
            } catch(IOException e){
                System.out.println("Menu did not work ");
            }
            return true;
        }

    }
}
