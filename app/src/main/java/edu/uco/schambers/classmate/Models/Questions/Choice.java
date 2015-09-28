package edu.uco.schambers.classmate.Models.Questions;

/**
 * Created by hp main on 9/23/2015.
 */
public class Choice {
    private String choiceText;
    private int choiceAnswers;

    public Choice(String s, int i) {
        choiceText = s;
        choiceAnswers=i;
    }

    public String getChoiceText() {
        return choiceText;
    }

    public void setChoiceText(String choiceText) {
        this.choiceText = choiceText;
    }

    public int getChoiceAnswers() {
        return choiceAnswers;
    }

    public void setChoiceAnswers(int choiceAnswers) {
        this.choiceAnswers = choiceAnswers;
    }
}
