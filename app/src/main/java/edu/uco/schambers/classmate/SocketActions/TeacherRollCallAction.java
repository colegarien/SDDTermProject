package edu.uco.schambers.classmate.SocketActions;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;

import edu.uco.schambers.classmate.Fragments.TeacherRollCall;

/**
 * Created by Cole Garien on 10/7/2015.
 */
public class TeacherRollCallAction extends SocketAction{
    ObjectInputStream objectInputStream;
    DataOutputStream dataOutputStream;
    ServerSocket serverSocket;

    boolean listeningForStudents = true;

    public TeacherRollCallAction(){

    }

    @Override
    void setUpSocket() throws IOException {
        serverSocket = new ServerSocket(ROLL_CALL_PORT_NUMBER);
    }

    @Override
    void performAction() throws IOException {
        while(listeningForStudents){
            socket = serverSocket.accept();
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            // TODO: add code for receiving student check-in
        }
    }

    @Override
    void tearDownSocket() throws IOException {
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
        listeningForStudents = false;
    }
}
