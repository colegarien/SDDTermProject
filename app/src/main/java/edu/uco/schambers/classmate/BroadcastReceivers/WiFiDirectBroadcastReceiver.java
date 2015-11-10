package edu.uco.schambers.classmate.BroadcastReceivers;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import org.json.JSONException;

import edu.uco.schambers.classmate.AdapterModels.TokenUtility;
import edu.uco.schambers.classmate.Fragments.StudentRollCall;
import edu.uco.schambers.classmate.ObservableManagers.IPAddressManager;
import edu.uco.schambers.classmate.R;
import edu.uco.schambers.classmate.Services.StudentRollCallService;

/**
 * Created by WenHsi on 10/6/2015.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    protected WifiP2pManager manager;
    protected WifiP2pManager.Channel channel;
    protected Activity activity;
    public static boolean connectedToGroupOwner = false;

    public void setManager(WifiP2pManager manager) {
        this.manager = manager;
    }

    public void setChannel(WifiP2pManager.Channel channel) {
        this.channel = channel;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public WiFiDirectBroadcastReceiver(){
        super();
    }

    public WiFiDirectBroadcastReceiver(Activity activity) {
        super();
        this.activity = activity;
    }

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,Activity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    void onWiFiDirectEnabled() {

    }

    void onWiFiDirectDisabled() {

    }

    void onPeerListChanged(WifiP2pDeviceList wifiP2pDeviceList) {

    }

    void onPeerConnected(NetworkInfo networkState, WifiP2pInfo wifiInfo, WifiP2pDevice device) {
        final Fragment f = activity.getFragmentManager().findFragmentById(R.id.fragment_container);

        if(f instanceof StudentRollCall) {
            if (!wifiInfo.isGroupOwner){
                IPAddressManager.getInstance().setGroupOwnerAddress(wifiInfo.groupOwnerAddress);

                Log.d("SocketAction", "Group owner address has retrieved");

                Intent studentServiceIntent = new Intent(activity, StudentRollCallService.class);
                try {
                    studentServiceIntent.putExtra("id", String.valueOf(TokenUtility.parseUserToken(activity).getpKey()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                activity.startService(studentServiceIntent);
            }
        }
    }

    void onPeerDisconnected() {
        final Fragment f = activity.getFragmentManager().findFragmentById(R.id.fragment_container);

        if(f instanceof StudentRollCall) {
            Log.d("SocketAction", "Disconnected");
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((StudentRollCall) f).reset();
                }
            });
        }

    }

    void onDeviceConfigChanged() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);

            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                onWiFiDirectEnabled();
            } else {
                onWiFiDirectDisabled();
            }

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            manager.requestPeers(channel, new WifiP2pManager.PeerListListener() {
                @Override
                public void onPeersAvailable(WifiP2pDeviceList peers) {

                    onPeerListChanged(peers);
                }
            });


        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            final NetworkInfo networkState = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            final WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);

            if(networkState.isConnected())
            {
                Log.d("SocketAction", "Peer is connected");

                connectedToGroupOwner = true;
                manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo info) {
                        onPeerConnected(networkState, info, device);
                    }
                });
            }
            else if(connectedToGroupOwner && networkState.getState() == NetworkInfo.State.DISCONNECTED)
            {
                connectedToGroupOwner = false;
                onPeerDisconnected();
            }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            onDeviceConfigChanged();
        }
    }
}
