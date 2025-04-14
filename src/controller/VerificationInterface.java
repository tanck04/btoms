package controller;
import model.User;

public interface VerificationInterface {
    public User verifyCredentials(String userID, String password);
}
