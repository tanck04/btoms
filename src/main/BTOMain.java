package main;

import controller.AuthenticationController;

public class BTOMain {
    public static void main(String[] args) {
        AuthenticationController authenticationController = new AuthenticationController();
        authenticationController.start();
    }
}