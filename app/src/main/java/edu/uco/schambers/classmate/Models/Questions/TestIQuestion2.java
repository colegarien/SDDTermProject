package edu.uco.schambers.classmate.Models.Questions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp main on 10/11/2015.
 */
public class TestIQuestion2 implements IQuestion {

    private List<String> choiceList;
    private int responseIndex;



    public TestIQuestion2()
    {
        choiceList = new ArrayList<>();
        choiceList.add("10 wood");
        choiceList.add("20 wood");
        choiceList.add("30 wood");
        choiceList.add("9001 wood");
        choiceList.add("100 wood");
        responseIndex = -1;
    }



    @Override
    public String getQuestionText()
    {
        return "How much wood could a woodchuck chuck if a woodchuck could chuck wood?";
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
