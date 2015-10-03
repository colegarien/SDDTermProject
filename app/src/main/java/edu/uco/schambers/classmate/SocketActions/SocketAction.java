package edu.uco.schambers.classmate.SocketActions;

/**
 * Created by Steven Chambers on 10/3/2015.
 */
public abstract class SocketAction
{
   abstract void setUpSocket();
   abstract void performAction();
   abstract void tearDownSocket();
   void execute()
   {
      setUpSocket();
      performAction();
      tearDownSocket();
   }
}
