package edu.uco.schambers.classmate.Models.Questions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Perrofrijole on 10/27/2015.
 */
public class DefaultUnanswerdQuestion implements IQuestion {
    private List<String> choiceList;

    public DefaultUnanswerdQuestion() {
        choiceList = new ArrayList<>();
        choiceList.add("N/A");
    }

    @Override
    public String getQuestionText() {
        return "This question was not answered.";
    }

    @Override
    public List<String> getQuestionChoices() {
        return choiceList;
    }

    @Override
    public void answerQuestion(String answer) {
        //shouldn't happen
    }

    @Override
    public boolean questionIsAnswered() {
        return false;
    }

    @Override
    public String getAnswer() {
        return choiceList.get(0);
    }
}
