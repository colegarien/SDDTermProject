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
import edu.uco.schambers.classmate.Models.Questions.IQuestion;

/**
 * Created by Steven Chambers on 10/3/2015.
 */
public class StudentSendQuestionAction extends SocketAction
{
    ObjectOutputStream objectOutputStream;
    DataInputStream dataInputStream;
    private String domain = "localhost";

    IQuestion questionToSend;
    OnQuestionReceivedListener questionReceivedListener;

    boolean questionSentSuccessfully;

    public StudentSendQuestionAction(IQuestion question, OnQuestionReceivedListener listener)
    {
        this.questionToSend = question;
        this.questionReceivedListener = listener;
    }

    @Override
    void setUpSocket() throws UnknownHostException, IOException
    {
        socket = new Socket(domain, QUESTIONS_PORT_NUMBER);
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        dataInputStream = new DataInputStream(socket.getInputStream());
    }

    @Override
    void performAction() throws IOException
    {
        if (questionToSend != null)
        {
            objectOutputStream.writeObject(questionToSend);
        }
        questionSentSuccessfully = dataInputStream.readBoolean();
        if(questionSentSuccessfully)
        {
            questionReceivedListener.onQuestionSentSuccessfully(domain,QUESTIONS_PORT_NUMBER);
        }
    }

    @Override
    void tearDownSocket() throws IOException
    {
        if(socket != null)
        {
            socket.close();
        }
        if(dataInputStream != null)
        {
            dataInputStream.close();
        }
        if(objectOutputStream != null)
        {
            objectOutputStream.close();
        }

    }
}
