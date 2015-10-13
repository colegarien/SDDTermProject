/* Team 9Lives
 *
 * Author: Wenxi Zeng
 * Purpose:
 *   UI backend for student roll call module, used for checking and
 *   discovering Roll Call Wifi P2P service
 *
 * Edit: 9/24/2015
 *   added button onCreate method for activating Wifi discovery
 *
 * Edit: 9/27/2015
 *   default the check-in button to be disable
 *   Enable check-in button and display teacher's info when a roll call service is found
 */

package edu.uco.schambers.classmate.Fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import edu.uco.schambers.classmate.Activites.MainActivity;
import edu.uco.schambers.classmate.BroadcastReceivers.StudentRollCallBroadcastReceiver;
import edu.uco.schambers.classmate.Database.DataRepo;
import edu.uco.schambers.classmate.Database.TokenUtility;
import edu.uco.schambers.classmate.Database.User;
import edu.uco.schambers.classmate.ObservableManagers.ServiceDiscoveryManager;
import edu.uco.schambers.classmate.ObservableManagers.SocketResultManager;
import edu.uco.schambers.classmate.R;
import edu.uco.schambers.classmate.Services.StudentRollCallService;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StudentRollCall.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StudentRollCall#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudentRollCall extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //UI Components
    private Button btnCheckin;
    private TextView lblCheckinStatus;

    private SharedPreferences prefs;

    private Observer observer;
    private Observer socketResultObserver;

    private OnFragmentInteractionListener mListener;

    // for retrieving student name
    private SharedPreferences sp;
    public static final String MyPREFS = "MyPREFS";
    private String user_key;
    private DataRepo dr;
    private User user;
    private String token;

    public User getUser() {
        return user;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StudentRollCall.
     */
    // TODO: Rename and change types and number of parameters
    public static StudentRollCall newInstance(String param1, String param2) {
        StudentRollCall fragment = new StudentRollCall();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public StudentRollCall() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // Load preference for student roll call module
        prefs = this.getActivity().getSharedPreferences("studentRollCallPrefs", Context.MODE_PRIVATE);

        setHasOptionsMenu(true);

        initBroadcast();

        observer = new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                Map<String, String> record = (Map<String, String>)data;

                btnCheckin.setEnabled(true);
                lblCheckinStatus.setText("Professor " + record.get("buddyname") + "'s class has been found");
            }
        };

        ServiceDiscoveryManager.getInstance().addObserver(observer);

        socketResultObserver = new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                boolean result = (boolean) data;

                if (result){

                }
                else {

                }
            }
        };

        SocketResultManager.getInstance().addObserver(socketResultObserver);

        // Discover teacher coll roll service
        discoverService();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        final MenuItem mute = menu.add("Mute when checked-in");
        mute.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        final MenuItem vibrate = menu.add("Vibrate when checked-in");
        vibrate.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        // Get user preference
        String checkedInMode = prefs.getString("CheckedInMode", null);

        // If no preference found
        if (checkedInMode == null || checkedInMode.isEmpty()){
            SharedPreferences.Editor editor = prefs.edit();

            // set the mute as default mode
            editor.putString("CheckedInMode", "Mute");
            editor.commit();

            checkedInMode = "Mute";
        }

        // Set the visibility of mute and vibrate menu items
        if (checkedInMode.equalsIgnoreCase("Mute")) {
            mute.setVisible(false);
        }
        else {
            mute.setVisible(true);
        }
        vibrate.setVisible(!mute.isVisible());

        // Declare an editor for modify user's preference
        final SharedPreferences.Editor editor = prefs.edit();

        mute.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                vibrate.setVisible(true);
                mute.setVisible(false);

                editor.putString("CheckedInMode", "Mute");
                editor.commit();

                return true;
            }
        });

        vibrate.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                vibrate.setVisible(false);
                mute.setVisible(true);

                editor.putString("CheckedInMode", "Vibrate");
                editor.commit();

                return true;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_student_roll_call, container, false);
        try {
            initUI(rootView);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rootView;
    }


    private void initUI(final View rootView) throws JSONException {
        sp = getActivity().getSharedPreferences(MyPREFS, Context.MODE_PRIVATE);
        token = sp.getString("AUTH_TOKEN", null);
        user = TokenUtility.parseUserToken(token);

        btnCheckin = (Button) rootView.findViewById(R.id.btn_check_in);
        lblCheckinStatus = (TextView) rootView.findViewById(R.id.lbl_checkin_status);
        btnCheckin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mute or vibrate user's devices
                changeAudioSetting(prefs.getString("CheckedInMode", null));

                lblCheckinStatus.setText(getString(R.string.lbl_status_checked_in));

                connectToGroupOwner();
                Toast.makeText(getActivity(), "You've checked-in", Toast.LENGTH_SHORT).show();
            }
        });

        // Disable the button.
        // The check-in button can be enabled
        // only if there is a teacher roll call service found.
        btnCheckin.setEnabled(false);
    }

    private void changeAudioSetting(String audioMode){
        AudioManager am;
        am= (AudioManager) getActivity().getBaseContext().getSystemService(Context.AUDIO_SERVICE);

        if (audioMode == null){
            return;
        }

        if (audioMode.equalsIgnoreCase("Mute")){
            // If the device is in vibrate mode, reset it to normal first
            // Otherwise the vibrate and mute mode will be accumulated
            if (am.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE){
                am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            }

            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        }
        else if (audioMode.equalsIgnoreCase("Vibrate")){
            am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        }
    }

    private void discoverService(){
        Activity activity = getActivity();

        /// Start discovering teacher service
        if(activity instanceof MainActivity) {
            ((MainActivity) activity).discoverLocalService();
        }
    }

    private void connectToGroupOwner(){
        Activity activity = getActivity();

        /// Start discovering teacher service
        if(activity instanceof MainActivity) {
            ((MainActivity) activity).connectToPeer();
        }
    }

    private void initBroadcast(){
        Activity activity = getActivity();

        /// Start discovering teacher service
        if(activity instanceof MainActivity) {
            ((MainActivity) activity).setupBroadcastReceiver(new StudentRollCallBroadcastReceiver(this));
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDestroy() {
        // Unregister since the fragment is about to be closed.
        ServiceDiscoveryManager.getInstance().deleteObserver(observer);
        SocketResultManager.getInstance().deleteObserver(socketResultObserver);
        super.onDestroy();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
