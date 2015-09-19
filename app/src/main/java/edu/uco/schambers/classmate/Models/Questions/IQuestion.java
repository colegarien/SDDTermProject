package edu.uco.schambers.classmate.Models.Questions;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Steven Chambers on 9/19/2015.
 */
public interface IQuestion extends Serializable
{
    public String getQuestionText();
    public List<String> getQuestionChoices();
    public void answerQuestion(String answer);
    public boolean questionIsAnswered();
}
