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
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import edu.uco.schambers.classmate.Activites.MainActivity;
import edu.uco.schambers.classmate.BroadcastReceivers.StudentRollCallBroadcastReceiver;
import edu.uco.schambers.classmate.ObservableManagers.ServiceDiscoveryManager;
import edu.uco.schambers.classmate.ObservableManagers.SocketResultManager;
import edu.uco.schambers.classmate.R;
import edu.uco.schambers.classmate.Services.StudentQuestionService;

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
    private TextView lblCheckinStatus;
    private ListView list;
    private ScrollView scrollView;

    private SharedPreferences prefs;

    private Observer observer;
    private Observer socketResultObserver;

    private OnFragmentInteractionListener mListener;

    // for indicate check in status
    private boolean isCheckingIn = false;

    // Discovered class list
    private ArrayList<ClassService> classes =new ArrayList<>();
    // Adapter of classes
    private ArrayAdapter<ClassService> classAdapter;

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

        observer = new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                Map<String, String> record = (Map<String, String>)data;

                ClassService classService = new ClassService(
                                                record.get("classname"),
                                                record.get("buddyname"),
                                                record.get("deviceaddress"),
                                                record.get("listenport")
                                        );

                if (!classes.contains(classService)){
                    classes.add(classService);
                }

                classAdapter.notifyDataSetChanged();

                if (classes.size() == 1){
                    lblCheckinStatus.setText("1 class has been found.");
                }
                else {
                    lblCheckinStatus.setText(classes.size() + " classes have been found. Scroll to view more");
                }

            }
        };

        ServiceDiscoveryManager.getInstance().addObserver(observer);

        socketResultObserver = new Observer() {
            @Override
            public void update(Observable observable, final Object data) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateCheckinStatus((boolean)data);
                        isCheckingIn = false;
                    }
                });
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

        final MenuItem refresh = menu.add("Refresh");
        refresh.setIcon(R.drawable.ic_action_refresh);
        refresh.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

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

        refresh.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                discoverService();

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

    public void reset(){
        lblCheckinStatus.setText("Class is over!");
        list.setEnabled(true);
        scrollView.setEnabled(true);

        classes.clear();
        classAdapter.notifyDataSetChanged();
    }

    private void initUI(final View rootView) throws JSONException {
        initListView(rootView);

        lblCheckinStatus = (TextView) rootView.findViewById(R.id.lbl_checkin_status);
    }

    private void initListView(View rootView){

        classes = new ArrayList<>();
        classAdapter=new ArrayAdapter<ClassService>(this.getActivity(),
                android.R.layout.two_line_list_item,
                classes){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                LayoutInflater inflater = (LayoutInflater)getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View listItemView = convertView;
                if (null == convertView) {
                    listItemView = inflater.inflate(
                            android.R.layout.simple_list_item_2,
                            parent,
                            false);
                }

                // The ListItemLayout must use the standard text item IDs.
                TextView lineOneView = (TextView)listItemView.findViewById(
                        android.R.id.text1);
                TextView lineTwoView = (TextView)listItemView.findViewById(
                        android.R.id.text2);
                lineOneView.setGravity(Gravity.CENTER );
                lineTwoView.setGravity(Gravity.CENTER);
                lineOneView.setPadding(0, 80, 0, 20);
                lineTwoView.setPadding(0, 20, 0, 80);
                ClassService item = getItem(position);

                lineOneView.setText("Class Name: " + item.getClassName());
                lineTwoView.setText("Professor Name: " + item.getProfessorName());

                return listItemView;
            }
        };

        list = (ListView) rootView.findViewById(R.id.list_class);
        list.setAdapter(classAdapter);

        scrollView = (ScrollView) rootView.findViewById(R.id.scrollView_class);

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                // Mute or vibrate user's devices
                changeAudioSetting(prefs.getString("CheckedInMode", null));

                lblCheckinStatus.setText(getString(R.string.lbl_status_checking_in));
                isCheckingIn = true;
                scrollView.setEnabled(false);
                list.setEnabled(false);

                initBroadcast();
                connectToGroupOwner(classes.get(position).getDeviceAddress());

                return false;
            }
        });
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

    private void connectToGroupOwner(String deviceAddress){
        Activity activity = getActivity();

        /// Start discovering teacher service
        if(activity instanceof MainActivity) {
            ((MainActivity) activity).connectToPeer(deviceAddress);
        }
    }

    private void initBroadcast(){
        Activity activity = getActivity();

        /// Start discovering teacher service
        if(activity instanceof MainActivity) {
            ((MainActivity) activity).setupBroadcastReceiver(new StudentRollCallBroadcastReceiver(this));
        }
    }

    private void updateCheckinStatus(boolean result){
        if (result){
            lblCheckinStatus.setText(getString(R.string.lbl_status_checked_in));
            Toast.makeText(getActivity(), "You've checked-in", Toast.LENGTH_SHORT).show();

            startStudentQuestionService();
        }
        else {
            lblCheckinStatus.setText(getString(R.string.lbl_status_failed));
            Toast.makeText(getActivity(), "Failed. Please try later", Toast.LENGTH_SHORT).show();
        }
    }

    private void startStudentQuestionService(){
        Intent i = new Intent(getActivity(), StudentQuestionService.class);
        i.setAction(StudentQuestionService.ACTION_START_SERVICE_STICKY);
        getActivity().startService(i);
    }

    public boolean allowBackPressed(){
        if (isCheckingIn){
            Toast.makeText(getActivity(), "You can't leave while checking-in", Toast.LENGTH_SHORT).show();
        }

        return  !isCheckingIn;
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

    public class ClassService{
        private String className;
        private String professorName;
        private String deviceAddress;
        private String devicePort;

        public ClassService(){}

        public ClassService(String className, String professorName, String deviceAddress, String devicePort) {
            this.className = className;
            this.professorName = professorName;
            this.deviceAddress = deviceAddress;
            this.devicePort = devicePort;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getProfessorName() {
            return professorName;
        }

        public void setProfessorName(String professorName) {
            this.professorName = professorName;
        }

        public String getDeviceAddress() {
            return deviceAddress;
        }

        public void setDeviceAddress(String deviceAddress) {
            deviceAddress = deviceAddress;
        }

        public String getDevicePort() {
            return devicePort;
        }

        public void setDevicePort(String devicePort) {
            devicePort = devicePort;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof ClassService) {
                ClassService other = (ClassService) o;
                return this.className.equals(other.className)
                        && this.professorName.equals(other.professorName)
                        && this.deviceAddress.equals(other.deviceAddress)
                        && this.devicePort.equals(other.devicePort);
            } else {
                return false;
            }
        }
    }
}
