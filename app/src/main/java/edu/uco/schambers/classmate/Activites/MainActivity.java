package edu.uco.schambers.classmate.Activites;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pDevice;
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

import java.util.HashMap;
import java.util.Map;

import edu.uco.schambers.classmate.Fragments.Debug;
import edu.uco.schambers.classmate.Fragments.Login;
import edu.uco.schambers.classmate.Fragments.StudentInterface;
import edu.uco.schambers.classmate.Fragments.StudentResponseFragment;
import edu.uco.schambers.classmate.Fragments.TeacherInterface;
import edu.uco.schambers.classmate.Fragments.TeacherQuestionResults;
import edu.uco.schambers.classmate.Fragments.UserInformation;
import edu.uco.schambers.classmate.Models.Questions.IQuestion;
import edu.uco.schambers.classmate.R;
import edu.uco.schambers.classmate.Services.StudentQuestionService;


public class MainActivity extends Activity implements StudentResponseFragment.OnFragmentInteractionListener, Login.OnFragmentInteractionListener,
        StudentInterface.OnFragmentInteractionListener, TeacherInterface.OnFragmentInteractionListener, TeacherQuestionResults.OnFragmentInteractionListener, UserInformation.OnFragmentInteractionListener
{

    // For registering Application in the Wifi P2p framework
    private WifiP2pManager mManager;
    // For connecting with the Wifi P2p framework
    private Channel mChannel;

    // Service identity
    public static final String SERVICE_INSTANCE = "_test";
    // For creating service request, and initiate discovery
    private WifiP2pDnsSdServiceRequest serviceRequest;

    // Constants
    public static final String SERVICE_FOUND = "edu.uco.schambers.classmate.wifip2p.service_found";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        startFragmentAccordingToIntentAction(getIntent());

    }

    private void startFragmentAccordingToIntentAction(Intent intent)
    {
        FragmentTransaction trans = getFragmentManager().beginTransaction();
        switch(intent.getAction())
        {

            case StudentQuestionService.ACTION_REQUEST_QUESTION_RESPONSE:
                Bundle bundle= intent.getExtras();
                IQuestion question =(IQuestion) bundle.getSerializable(StudentResponseFragment.ARG_QUESTION);
                StudentResponseFragment studentResponseFragment= StudentResponseFragment.newInstance(question);
                trans.replace(R.id.fragment_container,studentResponseFragment);
                break;

            default:
                Fragment loginFragment = new Login();
                trans.replace(R.id.fragment_container, loginFragment);
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

        // Add the local service, sending the service info, network channel,
        // and listener that will be used to indicate success or failure of
        // the request.
        mManager.addLocalService(mChannel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // Command successful! Code isn't necessarily needed here,
                // Unless you want to update the UI or add logging statements.
                Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reasonCode) {
                // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
                String errorMessage = "";
                switch (reasonCode){
                    case WifiP2pManager.BUSY:
                        errorMessage = "Busy...";
                        break;
                    case WifiP2pManager.ERROR:
                        errorMessage = "An Error Occurred";
                        break;
                    case WifiP2pManager.P2P_UNSUPPORTED:
                        errorMessage = "P2P Unsupported";
                        break;
                }
                Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_SHORT).show();
            }
        });

        discoverLocalService();
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

                            Intent intent = new Intent(MainActivity.SERVICE_FOUND);

                            // Traverse all the key-value pair that sent from server
                            // and put them to intent.
                            for (Map.Entry<String,String> entry : records.entrySet()) {
                                String key = entry.getKey();
                                String value = entry.getValue();

                                intent.putExtra(key, value);
                            }

                            // Notify the observers to update their UI
                            broadcaster.sendBroadcast(intent);

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
                        Log.d("ServiceDiscovery", "Service discovery failed");
                    }
                });
    }
}
