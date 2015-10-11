package edu.uco.schambers.classmate.BroadcastReceivers;

import android.app.Fragment;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import edu.uco.schambers.classmate.Fragments.StudentRollCall;

/**
 * Created by WenHsi on 10/6/2015.
 */
public class StudentRollCallBroadcastReceiver extends WiFiDirectBroadcastReceiver {

    public StudentRollCallBroadcastReceiver(){ }

    public StudentRollCallBroadcastReceiver(Fragment fragment) {
        super(fragment);
    }

    public StudentRollCallBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,Fragment fragment) {
        super(manager, channel, fragment);
    }

    @Override
    void onWiFiDirectEnabled() {

    }

    @Override
    void onWiFiDirectDisabled() {

    }

    @Override
    void onPeerListChanged(WifiP2pDeviceList wifiP2pDeviceList) {

    }

    @Override
    void onPeerConnected(NetworkInfo networkState, WifiP2pInfo wifiInfo, WifiP2pDevice device) {
        if(fragment instanceof StudentRollCall) {
            Log.d("SocketAction", "Sending Wifi info");
            ((StudentRollCall) fragment).sendStudentInfoTo(wifiInfo);
        }
    }

    @Override
    void onPeerDisconnected() {

    }

    @Override
    void onDeviceConfigChanged() {

    }
}
