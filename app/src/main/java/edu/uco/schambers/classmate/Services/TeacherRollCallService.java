/* Team 9Lives
 *
 * Author: Cole Garien
 * Purpose:
 *   Background Service for managing the classroom session
 *   Main function is to monitor Students attendance during class
 *
 * Edit: 10/7/2015
 *
 *
 */

package edu.uco.schambers.classmate.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import edu.uco.schambers.classmate.SocketActions.SocketAction;

public class TeacherRollCallService extends Service {

    private SocketAction listenForStudents;

    public TeacherRollCallService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
