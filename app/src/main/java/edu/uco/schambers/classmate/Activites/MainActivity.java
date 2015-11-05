package edu.uco.schambers.classmate.Activites;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


//import edu.uco.schambers.classmate.BroadcastReceivers.CallForStudentQuestionResponseReceiver;
import edu.uco.schambers.classmate.BroadcastReceivers.WiFiDirectBroadcastReceiver;
import edu.uco.schambers.classmate.Fragments.Auth;

import edu.uco.schambers.classmate.Fragments.Debug;
import edu.uco.schambers.classmate.Fragments.Login;
import edu.uco.schambers.classmate.Fragments.ResetPassword;
import edu.uco.schambers.classmate.Fragments.StudentInterface;
import edu.uco.schambers.classmate.Fragments.StudentResponseFragment;
import edu.uco.schambers.classmate.Fragments.StudentRollCall;
import edu.uco.schambers.classmate.Fragments.TeacherInterface;
import edu.uco.schambers.classmate.Fragments.TeacherQuestionResults;
import edu.uco.schambers.classmate.Fragments.UserInformation;
import edu.uco.schambers.classmate.Models.Questions.IQuestion;
import edu.uco.schambers.classmate.ObservableManagers.ServiceDiscoveryManager;
import edu.uco.schambers.classmate.R;
import edu.uco.schambers.classmate.Services.StudentQuestionService;


public class MainActivity extends Activity implements StudentResponseFragment.OnFragmentInteractionListener, Login.OnFragmentInteractionListener,
        StudentInterface.OnFragmentInteractionListener, TeacherInterface.OnFragmentInteractionListener, TeacherQuestionResults.OnFragmentInteractionListener,
        UserInformation.OnFragmentInteractionListener, Auth.OnFragmentInteractionListener, ResetPassword.OnFragmentInteractionListener
{

    // For registering Application in the Wifi P2p framework
    private WifiP2pManager mManager;
    // For connecting with the Wifi P2p framework
    private Channel mChannel;
    public static final String MyPREFS = "MyPREFS";
    public SharedPreferences sp;
    public SharedPreferences.Editor editor;

    // Service identity
    public static final String SERVICE_INSTANCE = "_test";
    // For creating service request, and initiate discovery
    private WifiP2pDnsSdServiceRequest serviceRequest;

    // BroadcastReceiver
    private BroadcastReceiver wifiReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        // This method removes all the remembered groups from device.
        // It prevents the situation that being as group owner once will be as group owner forever.
        // Solves the problem of check-in indefinitely..
        this.deletePersistentGroups();

        startFragmentAccordingToIntentAction(getIntent());

    }

    private void startFragmentAccordingToIntentAction(Intent intent)
    {
        FragmentTransaction trans = getFragmentManager().beginTransaction();
        switch(intent.getAction())
        {

            case StudentQuestionService.ACTION_REQUEST_QUESTION_RESPONSE:
                StudentResponseFragment studentResponseFragment= StudentResponseFragment.newInstance();
                trans.replace(R.id.fragment_container,studentResponseFragment);
                break;

            default:
                Fragment authFragment = new Auth();
                trans.replace(R.id.fragment_container, authFragment);
                break;

        }
        trans.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == R.id.action_debug)
        {
            FragmentTransaction trans = getFragmentManager().beginTransaction();
            Fragment debugFragment = new Debug();
            trans.replace(R.id.fragment_container, debugFragment).addToBackStack(null);
            trans.commit();
            return true;
        }
        if(id == R.id.action_logout)
        {
            sp = this.getSharedPreferences(MyPREFS, Context.MODE_PRIVATE);
            editor = sp.edit();
            editor.remove("AUTH_TOKEN");
            editor.clear();
            editor.commit();

            FragmentTransaction trans = getFragmentManager().beginTransaction();
            Fragment AuthFragment = new Auth();
            trans.replace(R.id.fragment_container, AuthFragment).addToBackStack(null);
            trans.commit();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onFragmentInteraction(Uri uri)
    {
        //todo
    }

    // For Adding Local WIFI P2P Services
    public void addLocalService(int port, String buddyName, String className, boolean isTeacher){
        //  Create a string map containing information about your service.
        Map<String, String> record = new HashMap<>();
        record.put("listenport", String.valueOf(port));
        // name of person running the service
        record.put("buddyname", buddyName);
        // name of the class the service is for
        record.put("classname", className);
        // is this server teacher run?
        record.put("isteacher", Boolean.toString(isTeacher));
        // service is visible
        record.put("available", "visible");

        // Service information.  Pass it an instance name, service type
        // _protocol._transportlayer , and the map containing
        // information other devices will want once they connect to this one.
        WifiP2pDnsSdServiceInfo serviceInfo =
                WifiP2pDnsSdServiceInfo.newInstance(SERVICE_INSTANCE, "_presence._tcp", record);

        // Add the local service, sending the service info, network channel,
        // and listener that will be used to indicate success or failure of
        // the request.
        mManager.addLocalService(mChannel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // Command successful! Code isn't necessarily needed here,
                // Unless you want to update the UI or add logging statements.
                Log.d("ServiceCreation", "Service creation successful");

                // Create group. This method default the host device as group owner
                mManager.createGroup(mChannel, null);
            }

            @Override
            public void onFailure(int reasonCode) {
                // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
                String errorMessage = reportErrorMessage(reasonCode);

                Log.d("ServiceCreation", "Error: " + errorMessage);
            }
        });

    }

    public void discoverLocalService(){

        final LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(this);
        // String map containing information about your service.
        final HashMap<String, String> records = new HashMap<>();

        //Register listeners for DNS-SD services. These are callbacks invoked
        //by the system when a service is actually discovered.
        mManager.setDnsSdResponseListeners(mChannel,
                new WifiP2pManager.DnsSdServiceResponseListener() {
                    @Override
                    public void onDnsSdServiceAvailable(String instanceName, String registrationType, WifiP2pDevice srcDevice) {
                        // A service has been discovered. Is this our app?
                        if (instanceName.equalsIgnoreCase(SERVICE_INSTANCE)){

                            records.put("deviceaddress", srcDevice.deviceAddress);
                            // Notify the observers to update their UI
                            ServiceDiscoveryManager.getInstance().directNotifyObservers(records);

                            Log.d("ServiceDiscovery", "Service found");
                        }

                    }
                },

                new WifiP2pManager.DnsSdTxtRecordListener() {
                    @Override
                    public void onDnsSdTxtRecordAvailable(String fullDomainName, Map<String, String> txtRecordMap, WifiP2pDevice srcDevice) {
                        // Get all the information that sent from server.
                        records.putAll(txtRecordMap);
                    }
                });

        // After attaching listeners, create a service request and initiate discovery
        serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        mManager.addServiceRequest(mChannel, serviceRequest,
                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d("ServiceDiscovery", "Added service discovery request");
                    }

                    @Override
                    public void onFailure(int reason) {
                        reportErrorMessage(reason);

                        Log.d("ServiceDiscovery", "Failed adding service discovery request");
                    }
                });
        mManager.discoverServices(mChannel,
                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d("ServiceDiscovery", "Service discovery initiated");
                    }

                    @Override
                    public void onFailure(int reason) {
                        reportErrorMessage(reason);

                        Log.d("ServiceDiscovery", "Service discovery failed");
                    }
                });
    }

    private String reportErrorMessage(int reason){
        String errorMessage = "";
        switch (reason) {
            case WifiP2pManager.BUSY:
                errorMessage = "Busy. Try again later";
                break;
            case WifiP2pManager.ERROR:
                errorMessage = "Internal Error. Try reopening app to solve the problem";
                break;
            case WifiP2pManager.P2P_UNSUPPORTED:
                errorMessage = "P2P Unsupported";
                break;
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();

        return errorMessage;
    }

    // used for removing a local service
    public void removeLocalService(int port, String buddyName, String className, boolean isTeacher){
        //  Create a string map containing information about your service.
        Map<String, String> record = new HashMap<String, String>();
        record.put("listenport", String.valueOf(port));
        // name of person running the service
        record.put("buddyname", buddyName);
        // name of the class the service is for
        record.put("classname", className);
        // is this server teacher run?
        record.put("isteacher", Boolean.toString(isTeacher));
        // service is visible
        record.put("available", "visible");

        // Service information.  Pass it an instance name, service type
        // _protocol._transportlayer , and the map containing
        // information other devices will want once they connect to this one.
        WifiP2pDnsSdServiceInfo serviceInfo =
                WifiP2pDnsSdServiceInfo.newInstance(SERVICE_INSTANCE, "_presence._tcp", record);

        // Remove the local service, sending the service info, network channel,
        // and listener that will be used to indicate success or failure of
        // the request.
        mManager.removeLocalService(mChannel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // Command successful! Code isn't necessarily needed here,
                // Unless you want to update the UI or add logging statements.

                // removeGroup broadcasts the disconnect state to all the connected peers
                mManager.removeGroup(mChannel, null);
                Log.d("ServiceRemoval", "Service removal successful");
            }

            @Override
            public void onFailure(int reasonCode) {
                // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
                String errorMessage = reportErrorMessage(reasonCode);

                Log.d("ServiceRemoval", "Error: " + errorMessage);
            }
        });
    }

    public void connectToPeer(String deviceAddress) {

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        //config.groupOwnerIntent = 15;

        if (serviceRequest != null)
            mManager.removeServiceRequest(mChannel, serviceRequest, null);

        mManager.connect(mChannel, config,
                new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        Log.d("ServiceDiscovery", "Connecting to service");
                    }

                    @Override
                    public void onFailure(int errorCode) {
                        reportErrorMessage(errorCode);
                        Log.d("ServiceDiscovery", "Failed connecting to service");
                    }
                });

    }

    public void setupBroadcastReceiver(WiFiDirectBroadcastReceiver receiver){
        receiver.setChannel(mChannel);
        receiver.setManager(mManager);
        wifiReceiver = receiver;

        IntentFilter wifiReceiverIntentFilter;wifiReceiverIntentFilter = new IntentFilter();
        wifiReceiverIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        wifiReceiverIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        wifiReceiverIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        wifiReceiverIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        registerReceiver(wifiReceiver, wifiReceiverIntentFilter);
    }

    private void deletePersistentGroups(){
        try {
            Method[] methods = WifiP2pManager.class.getMethods();
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getName().equals("deletePersistentGroup")) {
                    // Delete any persistent group
                    for (int netid = 0; netid < 32; netid++) {
                        methods[i].invoke(mManager, mChannel, netid, null);
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        if (wifiReceiver != null) {
            unregisterReceiver(wifiReceiver);
        }

        if (mManager != null && mChannel != null){
            mManager.requestGroupInfo(mChannel, new WifiP2pManager.GroupInfoListener() {
                @Override
                public void onGroupInfoAvailable(WifiP2pGroup group) {
                    if (group != null && group.isGroupOwner()) {
                        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
                            @Override
                            public void onSuccess() {
                                Log.d("ServiceDiscovery", "Removing Wifi p2p group");
                            }

                            @Override
                            public void onFailure(int reason) {
                                reportErrorMessage(reason);

                                Log.d("ServiceDiscovery", "Failed *Removing Wifi p2p group");
                            }
                        });
                    }
                }
            });

        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed(){
        Fragment f = getFragmentManager().findFragmentById(R.id.fragment_container);

        // Back button is disabled if student roll call fragment is processing check-in
        if (f instanceof StudentRollCall && !((StudentRollCall) f).allowBackPressed()){
            return;
        }

        super.onBackPressed();
    }
}
