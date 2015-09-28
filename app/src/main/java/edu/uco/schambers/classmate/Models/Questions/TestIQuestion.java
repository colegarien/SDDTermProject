package edu.uco.schambers.classmate.Models.Questions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp main on 9/23/2015.
 */
public class TestIQuestion implements IQuestion {

    private List<Choice> choiceList;
    private int responseIndex;
    private int totalAnswers;



    public TestIQuestion()
    {
        choiceList = new ArrayList<>();
        choiceList.add(new Choice("10 wood",4));
        choiceList.add(new Choice("15 wood",10));
        choiceList.add(new Choice("20 wood",3));
        choiceList.add(new Choice("9001 wood",7));
        responseIndex = -1;
    }

    public int getTotalAnswers() {
        totalAnswers = 0;
        for (Choice c : choiceList){
            totalAnswers += c.getChoiceAnswers();
        }
        return totalAnswers;
    }

    @Override
    public String getQuestionText()
    {
        return "How much wood could a woodchuck chuck if a woodchuck could chuck wood?";
    }

    @Override
    public List<String> getQuestionChoices()
    {
        return null;
    }

    public List<Choice> getQuestionChoicesChoiceClass() { return choiceList;}

    @Override
    public void answerQuestion(String answer)
    {
        responseIndex = choiceList.indexOf(answer);
    }

    @Override
    public boolean questionIsAnswered()
    {
        return responseIndex > -1;
    }

    @Override
    public String getAnswer()
    {
        return choiceList.get(responseIndex).getChoiceText();
    }

}
