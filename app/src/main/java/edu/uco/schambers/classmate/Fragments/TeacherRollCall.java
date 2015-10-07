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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import edu.uco.schambers.classmate.Activites.MainActivity;
import edu.uco.schambers.classmate.Database.DataRepo;
import edu.uco.schambers.classmate.Database.User;
import edu.uco.schambers.classmate.R;
import edu.uco.schambers.classmate.Services.TeacherRollCallService;
import edu.uco.schambers.classmate.SocketActions.SocketAction;

public class TeacherRollCall extends Fragment {
    public static final String ARG_TEACHER = "edu.uco.schambers.classmate.arq_teacher";
    private static final String CLASS_OPEN = "edu.uco.schambers.classmate.class_open";

    //UI Components
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_teacher_roll_call, container, false);
        initUI(rootView);
        return rootView;
    }


    private void initUI(final View rootView)
    {
        startBtn = (Button) rootView.findViewById(R.id.btn_start_roll_call);
        teacherText = (TextView) rootView.findViewById(R.id.txt_rc_teacher);

        sp = getActivity().getSharedPreferences(MyPREFS, Context.MODE_PRIVATE);
        user_key = sp.getString("USER_KEY", null);
        dr = new DataRepo(getActivity());
        user = dr.getUser(user_key);

        // intialize class_open to false
        sp.edit().putBoolean(CLASS_OPEN,false).apply();

        teacherText.setText(user.getName());

        classText = (EditText) rootView.findViewById(R.id.txt_rc_class);
        connectedList = (ListView) rootView.findViewById(R.id.list_connected);
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

                    // TODO: lock all input (like class)
                    startBtn.setText(getResources().getString(R.string.btn_roll_call_stop));
                    sp.edit().putBoolean(CLASS_OPEN,true).apply();
                }else{

                    Intent intent = TeacherRollCallService.getNewEndSessionIntent(activity);
                    activity.startService(intent);

                    if (activity instanceof MainActivity) {
                        ((MainActivity) activity).removeLocalService(SocketAction.ROLL_CALL_PORT_NUMBER, teacherText.getText().toString(), classText.getText().toString(), true);
                    }

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

}
