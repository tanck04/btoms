package controller;

public interface PasswordChangerInterface {
    public boolean changePassword(String nric, String hashedPassword);
}
