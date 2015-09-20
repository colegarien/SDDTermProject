package edu.uco.schambers.classmate.Models.Questions;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.uco.schambers.classmate.R;

/**
 * Created by Steven Chambers on 9/19/2015.
 */
public class DefaultMultiChoiceQuestion implements IQuestion
{
    private List<String> choiceList;
    private int responseIndex;

    public DefaultMultiChoiceQuestion()
    {
        choiceList = new ArrayList<>();
        choiceList.add("A");
        choiceList.add("B");
        choiceList.add("C");
        choiceList.add("D");
        responseIndex = -1;
    }

    @Override
    public String getQuestionText()
    {
        return "Your teacher has read a question and its choices aloud. Mark your response to their question below and press send.";
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
