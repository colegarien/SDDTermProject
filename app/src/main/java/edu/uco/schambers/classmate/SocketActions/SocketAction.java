package edu.uco.schambers.classmate.SocketActions;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Steven Chambers on 10/3/2015.
 */
public abstract class SocketAction
{
   static final int QUESTIONS_PORT_NUMBER = 8080;

   abstract void setUpSocket() throws  IOException;
   abstract void performAction() throws IOException;
   abstract void tearDownSocket() throws  IOException;
   void execute()
   {
      Thread executionThread = new Thread(new Runnable()
      {
         @Override
         public void run()
         {
            try
            {
               setUpSocket();
               performAction();
               tearDownSocket();
            }
            catch (UnknownHostException e)
            {
               Log.d("StudentSendAction", "Host not found. Exception: " + e.toString());
            }
            catch(IOException e)
            {
               Log.d("StudentSendAction", "IOException: " + e.toString());
            }
         }
      });
      executionThread.start();
   }
}
