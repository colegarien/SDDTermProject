/* Team 9Lives
 *
 * Author: Steven Chambers
 * Purpose:
 *   Abstract Class that outlines methods necessary
 *   for socket interactions
 *
 * Edit: 10/7/2015
 *   Cole Garien: modified port numbers to follow 4000 scheme
 *     and made ports public so can be used when starting wifi p2p
 *
 *
 */

package edu.uco.schambers.classmate.SocketActions;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public abstract class SocketAction
{
   public static final int ROLL_CALL_PORT_NUMBER = 4001;
   public static final int QUESTIONS_PORT_NUMBER = 4002;

   Socket socket;

   abstract void setUpSocket() throws  IOException;
   abstract void performAction() throws IOException;
   abstract void tearDownSocket() throws  IOException;
   public void execute()
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
               Log.d("SocketAction", "Host not found. Exception: " + e.toString());
            }
            catch(IOException e)
            {
               Log.d("SocketAction", "IOException: " + e.toString());
            }
         }
      });
      executionThread.start();
   }
}
