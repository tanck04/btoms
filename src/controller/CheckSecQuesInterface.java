package controller;

public interface CheckSecQuesInterface {
    public boolean checkHaveSecQues(String nric);

    public String retrieveSecQues(String nric);

    public boolean verifyAnsToSecQues(String nric, String answer);
}
