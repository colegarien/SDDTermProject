package edu.uco.schambers.classmate.Services;

import android.app.IntentService;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pInfo;
import android.util.Log;

import java.net.InetAddress;

import edu.uco.schambers.classmate.ObservableManagers.IPAddressManager;
import edu.uco.schambers.classmate.SocketActions.SocketAction;
import edu.uco.schambers.classmate.SocketActions.StudentRollCallAction;

/**
 * Created by WenHsi on 10/6/2015.
 */
public class StudentRollCallService extends IntentService {

    private SocketAction socketAction;

    public StudentRollCallService() {
        super("StudentRollCallService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String studentId = intent.getStringExtra("id");
        socketAction = new StudentRollCallAction(studentId);

        Log.d("SocketAction", "Target is group owner");
        socketAction.execute();
    }

    @Override
    public void onDestroy()
    {
        //Signal that the service was stopped
        //serverResult.send(port, new Bundle());
        stopSelf();
    }
}
