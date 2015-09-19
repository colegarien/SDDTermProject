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
    private Context context;
    private List<String> choiceList;
    private int responseIndex;

    DefaultMultiChoiceQuestion(Context context)
    {
        this.context = context;
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
        return context.getResources().getString(R.string.default_simple_question_description);
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
}
