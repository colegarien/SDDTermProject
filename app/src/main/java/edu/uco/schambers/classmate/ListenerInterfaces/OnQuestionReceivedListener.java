package edu.uco.schambers.classmate.ListenerInterfaces;

import edu.uco.schambers.classmate.Models.Questions.IQuestion;

/**
 * Created by Steven Chambers on 10/3/2015.
 */
public interface OnQuestionReceivedListener
{
    void onQuestionReceived(IQuestion q);
    void onQuestionSentSuccessfully(String domain, int port);
}
