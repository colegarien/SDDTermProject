package edu.uco.schambers.classmate.SocketActions;

import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by WenHsi on 10/6/2015.
 */
public class StudentRollCallAction extends SocketAction {

    ObjectOutputStream objectOutputStream;
    DataInputStream dataInputStream;

    private boolean result = false;

    @Override
    void setUpSocket() throws IOException {

        socket = new Socket(targetIP, ROLL_CALL_PORT_NUMBER);
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        dataInputStream = new DataInputStream(socket.getInputStream());
    }

    @Override
    void performAction() throws IOException {
        Log.d("SocketAction", "Sending message to server");

        objectOutputStream.writeObject("Test message for student roll call");
        result = dataInputStream.readBoolean();

        Log.d("SocketAction", String.valueOf(result));
    }

    @Override
    void tearDownSocket() throws IOException {
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
