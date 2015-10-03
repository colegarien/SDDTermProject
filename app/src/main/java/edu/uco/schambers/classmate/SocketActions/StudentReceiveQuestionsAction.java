package edu.uco.schambers.classmate.SocketActions;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;

import edu.uco.schambers.classmate.ListenerInterfaces.OnQuestionReceivedListener;
import edu.uco.schambers.classmate.Models.Questions.IQuestion;

/**
 * Created by Steven Chambers on 10/3/2015.
 */
public class StudentReceiveQuestionsAction extends SocketAction
{
    ObjectInputStream objectInputStream;
    DataOutputStream dataOutputStream;
    ServerSocket serverSocket;

    OnQuestionReceivedListener questionReceivedListener;

    boolean listeningForQuestions = true;
    boolean questionReceivedSuccessfully;

    public StudentReceiveQuestionsAction(OnQuestionReceivedListener listener)
    {
       this.questionReceivedListener = listener;
    }

    @Override
    void setUpSocket() throws IOException
    {
        serverSocket = new ServerSocket(QUESTIONS_PORT_NUMBER);

    }

    @Override
    void performAction() throws IOException
    {
        while (listeningForQuestions)
        {
            socket = serverSocket.accept();
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            try
            {
                IQuestion questionReceived =(IQuestion) objectInputStream.readObject();
                questionReceivedListener.onQuestionReceived(questionReceived);
            }
            catch (ClassNotFoundException e)
            {
                Log.d("SocketAction", "There was a problem decoding the serializable question. Exception: " + e.toString());
            }
            dataOutputStream.writeBoolean(questionReceivedSuccessfully);

        }

    }

    @Override
    void tearDownSocket() throws IOException
    {
        if(serverSocket != null)
        {
            serverSocket.close();
        }
        if(socket != null)
        {
            socket.close();
        }
        if(objectInputStream != null)
        {
            objectInputStream.close();
        }
        if(dataOutputStream != null)
        {
            dataOutputStream.close();
        }
    }
    public void stopListening()
    {
        listeningForQuestions = false;
    }
}
