package edu.uco.schambers.classmate.SocketActions;

import android.util.Log;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

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
    boolean socketConnectionSuccess = true;

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
            socket = new Socket();
            socket.connect(new InetSocketAddress(domain, QUESTIONS_PORT_NUMBER), 1000);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());
        }catch (Exception e)
        {
            questionSendErrorListener.onQuestionSendError("There was an error sending the question: Class session not found.");
            socketConnectionSuccess = false;
        }
    }

    @Override
    void performAction()
    {
        try
        {
            if (socketConnectionSuccess && questionToSend != null)
            {
                objectOutputStream.writeObject(questionToSend);
                questionSentSuccessfully = dataInputStream.readBoolean();
            }
        }catch(Exception e)
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
