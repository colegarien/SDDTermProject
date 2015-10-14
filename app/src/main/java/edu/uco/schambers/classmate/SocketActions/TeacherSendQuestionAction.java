package edu.uco.schambers.classmate.SocketActions;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;

import edu.uco.schambers.classmate.ListenerInterfaces.OnQuestionReceivedListener;
import edu.uco.schambers.classmate.Models.Questions.IQuestion;
import edu.uco.schambers.classmate.ObservableManagers.IPAddressManager;

/**
 * Created by Steven Chambers on 10/3/2015.
 */
public class TeacherSendQuestionAction extends SocketAction
{
    //ObjectOutputStream objectOutputStream;
    //DataInputStream dataInputStream;
    //private String domain = "localhost";

    IQuestion questionToSend;
    OnQuestionReceivedListener questionReceivedListener;


    ArrayList<Socket> classSocketList;//ArrayList<Socket> of student ips
    ArrayList<ObjectOutputStream> objectOutputStreams;//ArrayList<ObjectOutputStream> student output streams
    ArrayList<DataInputStream> dataInputStreams;//ArrayList<ObjectOutputStream> student output streams

    boolean questionSentSuccessfully;

    public TeacherSendQuestionAction(IQuestion question, OnQuestionReceivedListener listener)
    {
        this.questionToSend = question;
        this.questionReceivedListener = listener;
    }

    @Override
    void setUpSocket() throws UnknownHostException, IOException
    {
        //socket = new Socket(domain, QUESTIONS_PORT_NUMBER);

        //Setup classSocketList and corresponding dataInput/objectOut streams for
        // students listed in IpAddressManager.
        generateLists();
    }

    private void generateLists() throws IOException {
        //TODO: refactor, consider data storage location, consider data type
        //Will Clean this up later
        classSocketList = new ArrayList<>();
        dataInputStreams = new ArrayList<>();
        objectOutputStreams = new ArrayList<>();

        //ArrayList<String> classIPList = new ArrayList<>();
        ArrayList<InetAddress> classIPList = IPAddressManager.getInstance().getStudentAddresses();
        //Iterator

        Iterator<InetAddress> ipIterator = classIPList.iterator();
        while (ipIterator.hasNext()){
            InetAddress ip = ipIterator.next();
            Socket ts = new Socket(ip, QUESTIONS_PORT_NUMBER);
            classSocketList.add(ts);
            objectOutputStreams.add(new ObjectOutputStream(ts.getOutputStream()));
            dataInputStreams.add(new DataInputStream(ts.getInputStream()));
        }
    }

    /*private void verifySocketStreams(){
        Iterator<Socket> classSocketIterator = classSocketList.iterator();

        Socket ts;
        while(classSocketIterator.hasNext()){
            ts = classSocketIterator.next();
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());

        }
    }*/

    @Override
    void performAction() throws IOException
    {
        if (questionToSend != null)
        {
            //objectOutputStream.writeObject(questionToSend);
            sendQuestions();
        }

        //questionsSentSuccessfully = checkAllQuestionsSent();
        //questionSentSuccessfully = dataInputStream.readBoolean();

        if(questionSentSuccessfully)
        {
            questionReceivedListener.onQuestionSentSuccessfully("student"
                    ,QUESTIONS_PORT_NUMBER);
        }
    }

    private void sendQuestions() throws IOException {
        //TODO: modify lists to be iterable and/or encapsulate data another way
        //for each loop, dynamically allocate/create I/O streams

        Socket ts;
        ObjectOutputStream oos;
        DataInputStream dis;
        for (int i = 0; i<classSocketList.size(); i++){
            ts = classSocketList.get(i); //don't need, life cycle of socket streams tied to sockets?
            oos = objectOutputStreams.get(i);
            dis = dataInputStreams.get(i);
            oos.writeObject(questionToSend);
            questionSentSuccessfully = dis.readBoolean();
        }
    }

    @Override
    void tearDownSocket() throws IOException
    {
        //Refactor methods to reduce repeated code
        if(classSocketList != null)
        {
            for (Socket s :
                    classSocketList) {
                if (s != null){
                    s.close();
                }

            }
            //socket.close();
        }
        if(dataInputStreams != null)
        {
            for (DataInputStream dis :
                    dataInputStreams) {
                if (dis != null){
                    dis.close();
                }

            }
        }
        if(objectOutputStreams != null)
        {
            for (ObjectOutputStream oos :
                    objectOutputStreams) {
                if (oos != null){
                    oos.close();
                }

            }

        }

    }
}
