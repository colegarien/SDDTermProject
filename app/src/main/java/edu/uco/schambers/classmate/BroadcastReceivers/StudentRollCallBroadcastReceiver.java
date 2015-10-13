package edu.uco.schambers.classmate.BroadcastReceivers;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import edu.uco.schambers.classmate.Fragments.StudentRollCall;
import edu.uco.schambers.classmate.ObservableManagers.IPAddressManager;
import edu.uco.schambers.classmate.Services.StudentRollCallService;

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
            if (!wifiInfo.isGroupOwner){
                IPAddressManager.getInstance().setGroupOwnerAddress(wifiInfo.groupOwnerAddress);

                Log.d("SocketAction", "Group owner address has retrieved");
                Activity activity = fragment.getActivity();

                Intent studentServiceIntent = new Intent(activity, StudentRollCallService.class);
                studentServiceIntent.putExtra("id", ((StudentRollCall)fragment).getUser().getId());
                activity.startService(studentServiceIntent);
            }
        }
    }

    @Override
    void onPeerDisconnected() {

    }

    @Override
    void onDeviceConfigChanged() {

    }
}
