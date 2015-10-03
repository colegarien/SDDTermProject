package edu.uco.schambers.classmate.SocketActions;

import android.util.Log;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.uco.schambers.classmate.Models.Questions.IQuestion;

/**
 * Created by Steven Chambers on 10/3/2015.
 */
public class StudentSendQuestionAction extends SocketAction
{
    Socket socket;
    ObjectOutputStream objectOutputStream;
    DataInputStream dataInputStream;

    IQuestion questionToSend;

    boolean questionSentSuccessfully;

    @Override
    void setUpSocket() throws UnknownHostException, IOException
    {
        socket = new Socket("localhost", QUESTIONS_PORT_NUMBER);
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

    public void setQuestionToSend(IQuestion questionToSend)
    {
        this.questionToSend = questionToSend;
    }

    public boolean isQuestionSentSuccessfully()
    {
        return questionSentSuccessfully;
    }
}
