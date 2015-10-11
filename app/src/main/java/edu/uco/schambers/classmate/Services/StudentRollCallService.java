package edu.uco.schambers.classmate.Services;

import android.app.IntentService;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pInfo;
import android.util.Log;

import java.net.InetAddress;

import edu.uco.schambers.classmate.SocketActions.SocketAction;
import edu.uco.schambers.classmate.SocketActions.StudentRollCallAction;

/**
 * Created by WenHsi on 10/6/2015.
 */
public class StudentRollCallService extends IntentService {

    private WifiP2pInfo wifiInfo;
    private SocketAction socketAction;

    public StudentRollCallService() {
        super("StudentRollCallService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        wifiInfo = (WifiP2pInfo) intent.getExtras().get("wifiInfo");

        Log.d("SocketAction", "Handling in Service");

        if(!wifiInfo.isGroupOwner){
            socketAction = new StudentRollCallAction();

            InetAddress targetIP = wifiInfo.groupOwnerAddress;
            socketAction.setTargetIP(targetIP);

            Log.d("SocketAction", "Target is group owner");
            socketAction.execute();
        }
    }

    @Override
    public void onDestroy()
    {
        //Signal that the service was stopped
        //serverResult.send(port, new Bundle());
        stopSelf();
    }
}
