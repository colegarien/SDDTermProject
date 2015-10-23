package edu.uco.schambers.classmate.Models.Questions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp main on 10/11/2015.
 */
public class IQuestion3Choices implements IQuestion {

    private List<String> choiceList;
    private int responseIndex;



    public IQuestion3Choices()
    {
        choiceList = new ArrayList<>();
        choiceList.add("Nothing");
        choiceList.add("42");
        choiceList.add("Be happy");

        responseIndex = -1;
    }



    @Override
    public String getQuestionText()
    {
        return "What is the meaning of life?";
    }

    @Override
    public List<String> getQuestionChoices()
    {
        return choiceList;
    }


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
        return choiceList.get(responseIndex);
    }

}
