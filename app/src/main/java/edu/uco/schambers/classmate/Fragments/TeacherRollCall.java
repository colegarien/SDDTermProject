/* Team 9Lives
 *
 * Author: Cole Garien
 * Purpose:
 *   UI backend for teacher roll call module, used for starting and
 *   stopping the Roll Call Wifi P2P service
 *
 * Edit: 10/7/2015
 *   added button code for starting service,
 *    cleaned up default params1 and params2,
 *
 */

package edu.uco.schambers.classmate.Fragments;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import edu.uco.schambers.classmate.Activites.MainActivity;
import edu.uco.schambers.classmate.AdapterModels.DataRepo;
import edu.uco.schambers.classmate.AdapterModels.TokenUtility;
import edu.uco.schambers.classmate.AdapterModels.User;
import edu.uco.schambers.classmate.ObservableManagers.StudentAttendanceObservable;
import edu.uco.schambers.classmate.R;
import edu.uco.schambers.classmate.Services.TeacherRollCallService;
import edu.uco.schambers.classmate.SocketActions.SocketAction;

public class TeacherRollCall extends Fragment {
    public static final String ARG_TEACHER = "edu.uco.schambers.classmate.arq_teacher";
    private static final String CLASS_OPEN = "edu.uco.schambers.classmate.class_open";
    private static final String CLASS_NAME = "edu.uco.schambers.classmate.class_name";

    //UI Components
    private View rootView;
    private Button startBtn;
    private TextView teacherText;
    private EditText classText;
    private ListView connectedList;

    private OnFragmentInteractionListener mListener;

    // for retrieving teacher name
    public SharedPreferences sp;
    public static final String MyPREFS = "MyPREFS";
    public String user_key;
    private DataRepo dr;
    public User user;
    private String token;

    // for Service binding
    TeacherRollCallService teacherRollCallService;
    private boolean isBound = false;
    private Observer attendanceObserver;
    private ArrayAdapter<String> listAdapter;
    private ArrayList<String> student_info = new ArrayList<String>();

    // TODO: Get Class Name from DB/Dropdown Box
    public static TeacherRollCall newInstance() {
        TeacherRollCall fragment = new TeacherRollCall();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public TeacherRollCall() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

        attendanceObserver = new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                // TODO: switch over to student data-type
                if (data != null) {
                    student_info = (ArrayList<String>) data;

                    // TODO: display arraylist
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            listAdapter.clear();
                            listAdapter.addAll(student_info);
                            listAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        };
                StudentAttendanceObservable.getInstance().addObserver(attendanceObserver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_teacher_roll_call, container, false);
        try {
            initUI(rootView);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rootView;
    }

    @Override
    public void onStop(){
        super.onStop();
        // unbind from service it is bound
        unBindService();
        StudentAttendanceObservable.getInstance().deleteObserver(attendanceObserver);
    }

    @Override
    public void onPause(){
        super.onPause();
        // unbind from service it is bound
        unBindService();
        StudentAttendanceObservable.getInstance().deleteObserver(attendanceObserver);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // unbind from service it is bound
        unBindService();
        StudentAttendanceObservable.getInstance().deleteObserver(attendanceObserver);
    }

    private void unBindService(){
        if(isBound){
            getActivity().unbindService(serviceConnection);
            isBound = false;
        }
    }
    private void bindService(){
        Intent intent = TeacherRollCallService.getNewStartSessionIntent(getActivity(), teacherText.getText().toString());
        getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void initUI(final View rootView) throws JSONException {
        startBtn = (Button) rootView.findViewById(R.id.btn_start_roll_call);
        teacherText = (TextView) rootView.findViewById(R.id.txt_rc_teacher);

        sp = getActivity().getSharedPreferences(MyPREFS, Context.MODE_PRIVATE);
        token = sp.getString("AUTH_TOKEN", null);
        user = TokenUtility.parseUserToken(token);

        // initialize class_open to false if doesn't exist
        if(!sp.contains(CLASS_OPEN))
            sp.edit().putBoolean(CLASS_OPEN, false).apply();

        // set the button's text properly
        if (sp.getBoolean(CLASS_OPEN,false)) {
            startBtn.setText(getResources().getString(R.string.btn_roll_call_stop));
        }else{
            startBtn.setText(getResources().getString(R.string.btn_roll_call_start));
        }

        teacherText.setText(user.getName().toString());

        // Persistent Class Name
        classText = (EditText) rootView.findViewById(R.id.txt_rc_class);
        classText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // update class name
                sp.edit().putString(CLASS_NAME, classText.getText().toString()).apply();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        if(!sp.contains(CLASS_NAME))
            sp.edit().putString(CLASS_NAME,"").apply();
        classText.setText(sp.getString(CLASS_NAME,""));

        listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
        connectedList = (ListView) rootView.findViewById(R.id.list_connected);
        connectedList.setAdapter(listAdapter);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();

                if (!sp.getBoolean(CLASS_OPEN,false)) {

                    // TODO: change teacherText over to Teacher object
                    Intent intent = TeacherRollCallService.getNewStartSessionIntent(activity, teacherText.getText().toString());
                    activity.startService(intent);

                    if (activity instanceof MainActivity) {
                        ((MainActivity) activity).addLocalService(SocketAction.ROLL_CALL_PORT_NUMBER, teacherText.getText().toString(), classText.getText().toString(), true);
                    }

                    // bind the service
                    bindService();

                    // TODO: lock all input (like class)
                    startBtn.setText(getResources().getString(R.string.btn_roll_call_stop));
                    sp.edit().putBoolean(CLASS_OPEN,true).apply();
                }else{

                    Intent intent = TeacherRollCallService.getNewEndSessionIntent(activity);
                    activity.startService(intent);

                    if (activity instanceof MainActivity) {
                        ((MainActivity) activity).removeLocalService(SocketAction.ROLL_CALL_PORT_NUMBER, teacherText.getText().toString(), classText.getText().toString(), true);
                    }

                    // unbind the service
                    unBindService();

                    // TODO: unlock inputs (like class)
                    startBtn.setText(getResources().getString(R.string.btn_roll_call_start));
                    sp.edit().putBoolean(CLASS_OPEN,false).apply();
                }
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    // define callbacks for service binding, passed to bindService()
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to TeacherRollCallService, cast the IBinder and get TeacherRollCallService instance
            TeacherRollCallService.LocalBinder binder = (TeacherRollCallService.LocalBinder) service;
            teacherRollCallService = binder.getService();
            isBound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };

}
