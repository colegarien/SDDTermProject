package edu.uco.schambers.classmate.SocketActions;

import android.util.Log;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.uco.schambers.classmate.ListenerInterfaces.OnQuestionReceivedListener;
import edu.uco.schambers.classmate.ListenerInterfaces.OnQuestionSendErrorListener;
import edu.uco.schambers.classmate.Models.Questions.IQuestion;
import edu.uco.schambers.classmate.ObservableManagers.IPAddressManager;

/**
 * Created by Steven Chambers on 10/3/2015.
 */
public class StudentSendQuestionAction extends SocketAction
{
    ObjectOutputStream objectOutputStream;
    DataInputStream dataInputStream;
    private String domain = IPAddressManager.getInstance().getGroupOwnerAddress().getHostAddress();

    IQuestion questionToSend;
    OnQuestionReceivedListener questionReceivedListener;
    OnQuestionSendErrorListener questionSendErrorListener;

    boolean questionSentSuccessfully;

    public StudentSendQuestionAction(IQuestion question, OnQuestionReceivedListener listener)
    {
        this.questionToSend = question;
        this.questionReceivedListener = listener;
        this.questionSendErrorListener = (OnQuestionSendErrorListener) listener;
    }

    @Override
    void setUpSocket()
    {
        try
        {
            socket = new Socket(domain, QUESTIONS_PORT_NUMBER);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());
        }catch (IOException e)
        {
            questionSendErrorListener.onQuestionSendError("There was an error sending the question: Class session not found.");
        }
    }

    @Override
    void performAction()
    {
        try
        {
            if (questionToSend != null)
            {
                objectOutputStream.writeObject(questionToSend);
            }
            questionSentSuccessfully = dataInputStream.readBoolean();
        }catch(IOException e)
        {
            questionSendErrorListener.onQuestionSendError("There was an error sending the question: Please try again soon.");
        }

        if (questionSentSuccessfully)
        {
            questionReceivedListener.onQuestionSentSuccessfully(domain, QUESTIONS_PORT_NUMBER);
        }
    }

    @Override
    void tearDownSocket() throws IOException
    {
        if (socket != null)
        {
            socket.close();
        }
        if (dataInputStream != null)
        {
            dataInputStream.close();
        }
        if (objectOutputStream != null)
        {
            objectOutputStream.close();
        }

    }
}
