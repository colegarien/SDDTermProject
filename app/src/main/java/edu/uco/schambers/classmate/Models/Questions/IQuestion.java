package edu.uco.schambers.classmate.Models.Questions;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Steven Chambers on 9/19/2015.
 */
public interface IQuestion extends Serializable
{
    String getQuestionText();
    List<String> getQuestionChoices();
    void answerQuestion(String answer);
    boolean questionIsAnswered();
    String getAnswer();

}
