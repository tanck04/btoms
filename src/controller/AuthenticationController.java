package controller;

import java.util.Scanner;

public class AuthenticationController {
    private final SignInController loginController = new SignInController();

    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("===================================================================");
        System.out.println();
        System.out.println(" ███████████  ███████████    ███████    ██████   ██████  █████████ ");
        System.out.println("░░███░░░░░███░█░░░███░░░█  ███░░░░░███ ░░██████ ██████  ███░░░░░███");
        System.out.println(" ░███    ░███░   ░███  ░  ███     ░░███ ░███░█████░███ ░███    ░░░ ");
        System.out.println(" ░██████████     ░███    ░███      ░███ ░███░░███ ░███ ░░█████████ ");
        System.out.println(" ░███░░░░░███    ░███    ░███      ░███ ░███ ░░░  ░███  ░░░░░░░░███");
        System.out.println(" ░███    ░███    ░███    ░░███     ███  ░███      ░███  ███    ░███");
        System.out.println(" ███████████     █████    ░░░███████░   █████     █████░░█████████ ");
        System.out.println("░░░░░░░░░░░     ░░░░░       ░░░░░░░    ░░░░░     ░░░░░  ░░░░░░░░░  ");
        System.out.println();
        System.out.println("        Welcome to Build-To-Order Management System (BTOMS)        ");
        System.out.println("===================================================================");
        System.out.println();
        System.out.println("How can we assist you today?");

        System.out.println("===================================");
        System.out.println("|           MAIN MENU             |");
        System.out.println("===================================");
        System.out.println("|  1. Sign In                     |");
        System.out.println("|  2. Forget Password             |");
        System.out.println("|  3. Exit                        |");
        System.out.println("===================================");

        int choice = -1;
        boolean validInput = false;

        while (!validInput) {
            System.out.print("Please select an option (1-3): ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // Clear newline character

                if (choice >= 1 && choice <= 4) {
                    validInput = true; // Valid input and within range
                } else {
                    System.out.println("Invalid choice. Please enter a number between 1 and 4.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number between 1 and 4.");
                scanner.nextLine(); // Clear invalid input
            }
        }


        switch (choice) {
            case 1:
                System.out.println("");
                System.out.println("Please enter your credentials");
                System.out.println("+------------------------------+");
                System.out.print("| NRIC: ");
                String nric = scanner.nextLine();
                System.out.print("| Password:    ");
                String password = scanner.nextLine();
                System.out.println("+------------------------------+");
                System.out.println("\nAttempting login with provided credentials...");
                SignInController.signIn(nric, password);
                break;
            case 2:
                break;
            case 3:
                System.out.println("Thank you for using our system. Goodbye!");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                break;
        }
    }

}
