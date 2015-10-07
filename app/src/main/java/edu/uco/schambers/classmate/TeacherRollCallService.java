package edu.uco.schambers.classmate;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class TeacherRollCallService extends Service {
    public TeacherRollCallService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
