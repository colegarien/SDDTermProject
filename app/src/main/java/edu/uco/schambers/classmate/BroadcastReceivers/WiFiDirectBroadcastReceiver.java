package edu.uco.schambers.classmate.BroadcastReceivers;

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

/**
 * Created by WenHsi on 10/6/2015.
 */
public abstract class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    protected WifiP2pManager manager;
    protected WifiP2pManager.Channel channel;
    protected Fragment fragment;
    private boolean connectedToGroupOwner = false;

    public void setManager(WifiP2pManager manager) {
        this.manager = manager;
    }

    public void setChannel(WifiP2pManager.Channel channel) {
        this.channel = channel;
    }

    public void setActivity(Fragment fragment) {
        this.fragment = fragment;
    }

    public WiFiDirectBroadcastReceiver(){
        super();
    }

    public WiFiDirectBroadcastReceiver(Fragment fragment) {
        super();
        this.fragment = fragment;
    }

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,Fragment fragment) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.fragment = fragment;
    }

    abstract void onWiFiDirectEnabled();
    abstract void onWiFiDirectDisabled();
    abstract void onPeerListChanged(WifiP2pDeviceList wifiP2pDeviceList);
    abstract void onPeerConnected(NetworkInfo networkState, WifiP2pInfo wifiInfo, WifiP2pDevice device);
    abstract void onPeerDisconnected();
    abstract void onDeviceConfigChanged();

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
