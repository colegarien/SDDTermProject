/* Team 9Lives
 *
 * Author: Cole Garien
 * Purpose:
 *   Main ServerSocket that will deal with incoming and outgoing
 *   communications to a students phone (dealing with roll call packets)
 *
 * Edit: 10/7/2015
 *
 *
 */

package edu.uco.schambers.classmate.SocketActions;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;

import edu.uco.schambers.classmate.Fragments.TeacherRollCall;
import edu.uco.schambers.classmate.ListenerInterfaces.OnStudentConnectListener;

public class TeacherRollCallAction extends SocketAction{
    ObjectInputStream objectInputStream;
    DataOutputStream dataOutputStream;
    ServerSocket serverSocket;

    boolean listeningForStudents = true;
    boolean receiveSuccess = false;

    private OnStudentConnectListener studentConnectListener;

    public TeacherRollCallAction(OnStudentConnectListener studentConnectListener){
        this.studentConnectListener = studentConnectListener;
    }

    @Override
    void setUpSocket() throws IOException {
        serverSocket = new ServerSocket(ROLL_CALL_PORT_NUMBER);
    }

    @Override
    void performAction() throws IOException {
        while(listeningForStudents){
            receiveSuccess = false;

            socket = serverSocket.accept();
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            try
            {
                // TODO: use Student object (check with someone on status, may do myself)
                String student =(String) objectInputStream.readObject();
                studentConnectListener.onStudentConnect(student);
                receiveSuccess = true;
            }
            catch (ClassNotFoundException e)
            {
                Log.d("SocketAction", "There was a problem decoding the serializable student. Exception: " + e.toString());
            }
            // confirmation of reading
            dataOutputStream.writeBoolean(receiveSuccess);
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
