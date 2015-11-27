/* Team 9Lives
 *
 * Author: Cole Garien
 * Purpose:
 *   UI frontend for teacher roll call module, used for starting and
 *   stopping the Roll Call Wifi P2P service along with tracking the
 *   students' attendance as they check in
 *
 * Edit: 11/22/2015
 *   Added functionality for tracking attendance
 *   Added interaction with cloud adapter for saving attednace
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import edu.uco.schambers.classmate.Activites.MainActivity;
import edu.uco.schambers.classmate.Adapter.AttendanceAdapter;
import edu.uco.schambers.classmate.Adapter.Callback;
import edu.uco.schambers.classmate.Adapter.ClassAdapter;
import edu.uco.schambers.classmate.Adapter.EnrollmentAdapter;
import edu.uco.schambers.classmate.Adapter.HttpResponse;
import edu.uco.schambers.classmate.AdapterModels.*;
import edu.uco.schambers.classmate.AdapterModels.Class;
import edu.uco.schambers.classmate.ObservableManagers.StudentAttendanceObservable;
import edu.uco.schambers.classmate.R;
import edu.uco.schambers.classmate.Services.TeacherRollCallService;
import edu.uco.schambers.classmate.SocketActions.SocketAction;

public class TeacherRollCall extends Fragment {
    public static final String ARG_TEACHER = "edu.uco.schambers.classmate.arq_teacher";
    private static final String CLASS_OPEN = "edu.uco.schambers.classmate.class_open";
    private static final String CLASS_INDEX = "edu.uco.schambers.classmate.rc_class_index";

    //UI Components
    private View rootView;
    private Button startBtn;
    private TextView teacherText;
    private Spinner classSpinner;
    private ListView connectedList;

    private OnFragmentInteractionListener mListener;

    // for retrieving teacher name
    public SharedPreferences sp;
    public static final String MyPREFS = "MyPREFS";
    public String user_key;
    public User user;
    private String token;

    // for getting class list
    private ClassAdapter classAdapter = new ClassAdapter();
    // for getting student lists
    private EnrollmentAdapter enrollmentAdapter = new EnrollmentAdapter();
    private ArrayList<StudentByClass> studentByClass = new ArrayList<StudentByClass>();
    // for saving student attendance
    private AttendanceAdapter attendanceAdapter = new AttendanceAdapter();



    // for Service binding
    TeacherRollCallService teacherRollCallService;
    private boolean isBound = false;
    private Observer attendanceObserver;
    private ArrayAdapter<String> listAdapter;

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

        // Observer that creates an attendance record for a student
        //   as they check into the calls session
        attendanceObserver = new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                if (data != null) {
                    // current student PK's
                    ArrayList<String> student_pks = (ArrayList<String>) data;
                    // add students that need to be added
                    for (String pk : student_pks){
                        for (StudentByClass stu : studentByClass){
                            if ((""+stu.getId()).equals(pk)) {
                                teacherRollCallService.addStudent(stu);
                            }
                        }
                    }

                    // Modifications to UI must be done on the main thread
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            // refresh the list adapter with currently checked-in students
                            listAdapter.clear();
                            for (StudentByClass stu : teacherRollCallService.getStudentByClass()) {
                                listAdapter.add(stu.getName());
                            }
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

        // teacher classes setup
        final List<SpinnerItem> spinnerArray =  new ArrayList<>();
        final ArrayAdapter<SpinnerItem> dropAdapter = new ArrayAdapter<SpinnerItem>(
                getActivity(), android.R.layout.simple_spinner_item, spinnerArray);
        dropAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Drop Box contents loaded from the cloud database
        classAdapter.professorClasses(user.getpKey(), new Callback<ArrayList<edu.uco.schambers.classmate.AdapterModels.Class>>() {
            @Override
            public void onComplete(ArrayList<Class> result) throws Exception {
                spinnerArray.clear();
                spinnerArray.add(new SpinnerItem("Select Course..", "-1"));
                for (Class classItem : result) {
                    spinnerArray.add(new SpinnerItem(classItem.getClass_name(), Integer.toString(classItem.getId())));
                }
                dropAdapter.notifyDataSetChanged();
                classSpinner.setSelection(sp.getInt(CLASS_INDEX, 0));
            }
        });

        classSpinner = (Spinner) rootView.findViewById(R.id.spinner_rc_class);
        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        classSpinner.setAdapter(dropAdapter);

        // reset drop-down if class is not running
        if(!sp.getBoolean(CLASS_OPEN, false) || !sp.contains(CLASS_INDEX))
            sp.edit().putInt(CLASS_INDEX, 0).apply();
        else if(sp.getBoolean(CLASS_OPEN,false))
            classSpinner.setEnabled(false);
        classSpinner.setSelection(sp.getInt(CLASS_INDEX, 0));

        listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
        connectedList = (ListView) rootView.findViewById(R.id.list_connected);
        connectedList.setAdapter(listAdapter);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();

                if (!sp.getBoolean(CLASS_OPEN,false)) {
                    // if a class has been selected
                    if (classSpinner.getSelectedItemPosition() > 0) {
                        Intent intent = TeacherRollCallService.getNewStartSessionIntent(activity, teacherText.getText().toString());
                        activity.startService(intent);

                        if (activity instanceof MainActivity) {
                            ((MainActivity) activity).addLocalService(SocketAction.ROLL_CALL_PORT_NUMBER, teacherText.getText().toString(), classSpinner.getSelectedItem().toString(), true);
                        }

                        // bind the service
                        bindService();

                        // save selected class
                        sp.edit().putInt(CLASS_INDEX, classSpinner.getSelectedItemPosition()).apply();

                        startBtn.setText(getResources().getString(R.string.btn_roll_call_stop));
                        classSpinner.setEnabled(false);
                        sp.edit().putBoolean(CLASS_OPEN, true).apply();

                        // get student List for current class
                        try {
                            int tmpid =Integer.parseInt(((SpinnerItem) classSpinner.getSelectedItem()).getValue());
                            Date tmpdate = new Date();
                            Log.d("takeRollCall", "id: "+tmpid);
                            Log.d("takeRollCall", "date: "+ tmpdate);
                            attendanceAdapter.takeRollCall(Integer.parseInt(((SpinnerItem) classSpinner.getSelectedItem()).getValue()), new Date(),new Callback<HttpResponse>() {
                                @Override
                                public void onComplete(HttpResponse result) throws Exception {
                                    //logging HTTP responses
                                    Log.d("TakeRollCall", "HttpResponse: "+result.getResponse());
                                    Log.d("TakeRollCall", "HttpResponse Code: "+result.getHttpCode());
                                }
                            });
                            enrollmentAdapter.getStudentsByClass(Integer.parseInt(((SpinnerItem) classSpinner.getSelectedItem()).getValue()), new Callback<ArrayList<StudentByClass>>() {
                                @Override
                                public void onComplete(ArrayList<StudentByClass> result) throws Exception {
                                    studentByClass.clear();
                                    studentByClass.addAll(result);
                                }
                            });
                        }catch(JSONException e){
                            Log.d("TeacherRollCall", e.getMessage());
                        }

                    }else{
                        Toast.makeText(getActivity().getApplicationContext(), "Select a Course!",Toast.LENGTH_SHORT).show();
                    }
                }else{

                    Intent intent = TeacherRollCallService.getNewEndSessionIntent(activity);
                    activity.startService(intent);

                    if (activity instanceof MainActivity) {
                        ((MainActivity) activity).removeLocalService(SocketAction.ROLL_CALL_PORT_NUMBER, teacherText.getText().toString(), classSpinner.getSelectedItem().toString(), true);
                    }

                    // unbind the service
                    unBindService();

                    startBtn.setText(getResources().getString(R.string.btn_roll_call_start));
                    classSpinner.setEnabled(true);
                    sp.edit().putBoolean(CLASS_OPEN,false).apply();

                    // save the attendance
                    try {
                        attendanceAdapter.saveAttendance(teacherRollCallService.getStudentAttendance(),new Callback<HttpResponse>() {
                            @Override
                            public void onComplete(HttpResponse result) throws Exception {
                                //log HTTP responses
                                Log.d("SaveAttendance", "HttpResponse: "+result.getResponse());
                                Log.d("SaveAttendance", "HttpResponse Code: "+result.getHttpCode());
                            }
                        });
                    }catch(JSONException e){
                        Log.d("TeacherRollCall", e.getMessage());
                    }
                }
            }
        });
    }

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
